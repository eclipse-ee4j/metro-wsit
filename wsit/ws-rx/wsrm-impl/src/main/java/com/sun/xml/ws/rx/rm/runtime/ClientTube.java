/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.oracle.webservices.oracle_internal_api.rm.OutboundDelivered;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.metro.dev.MetroClientTubelineAssemblyContext;
import com.sun.xml.ws.commons.MaintenanceTaskExecutor;
import com.sun.xml.ws.commons.VolatileReference;
import com.sun.xml.ws.commons.ha.HaContext;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.mc.dev.ProtocolMessageHandler;
import com.sun.xml.ws.rx.mc.dev.WsmcRuntimeProvider;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.protocol.*;
import com.sun.xml.ws.rx.rm.runtime.LocalIDManager.BoundMessage;
import com.sun.xml.ws.rx.rm.runtime.delivery.DeliveryQueueBuilder;
import com.sun.xml.ws.rx.rm.runtime.delivery.PostmanPool;
import com.sun.xml.ws.rx.rm.runtime.sequence.*;
import com.sun.xml.ws.rx.rm.runtime.sequence.invm.InMemoryLocalIDManager;
import com.sun.xml.ws.rx.rm.runtime.sequence.persistent.JDBCLocalIDManager;
import com.sun.xml.ws.rx.rm.runtime.transaction.TransactionException;
import com.sun.xml.ws.rx.util.Communicator;
import com.sun.xml.ws.security.secconv.SecureConversationInitiator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * Attaches additional RM-specific headers to each request message and ensures the reliable delivery of the message (in
 * case of any problems with sending the message, the exception is evaluated and the message is scheduled for a resend
 * if possible.
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
final class ClientTube extends AbstractFilterTubeImpl {
    //
    private static final Logger LOGGER = Logger.getLogger(ClientTube.class);
    private static final Lock INIT_LOCK = new ReentrantLock();
    //
    private final RuntimeContext rc;
    private final WSEndpointReference rmSourceReference;
    //
    private volatile VolatileReference<String> outboundSequenceId;
    
    private volatile VolatileReference<Set<String>> processedLocalIDs;
    
    private volatile VolatileReference<LocalIDManager> localIDManager;

    ClientTube(ClientTube original, TubeCloner cloner) {
        super(original, cloner);
        this.rc = original.rc;

        this.rmSourceReference = original.rmSourceReference;
        this.outboundSequenceId = original.outboundSequenceId;
        this.processedLocalIDs = original.processedLocalIDs;
        this.localIDManager = original.localIDManager;
    }

    ClientTube(RmConfiguration configuration, ClientTubelineAssemblyContext context) throws RxRuntimeException {
        super(context.getTubelineHead()); // cannot use context.getTubelineHead as McClientTube might have been created in RxTubeFactory

        this.outboundSequenceId = new VolatileReference<String>(null);
        this.processedLocalIDs = new VolatileReference<Set<String>>(null);
        this.localIDManager = new VolatileReference<LocalIDManager>(null);

        // the legacy way of getting the scInitiator, works for Metro SC impl
        SecureConversationInitiator scInitiator = context.getImplementation(SecureConversationInitiator.class);
        if (scInitiator == null) {
            // TODO P3 remove this condition and remove context.getScInitiator() method
            scInitiator = ((MetroClientTubelineAssemblyContext) context).getScInitiator();
        }
        
        // the SPI way of getting the scInitiator, it works for JRF OWSM SC integration
        // TODO consider using the SPI way for Metro SC impl as well
        if (scInitiator == null) {
            scInitiator = context.getContainer().getSPI(SecureConversationInitiator.class);
        }

        if (scInitiator == null) {
            LOGGER.fine("No SecureConversationInitiator");
        } else {
            LOGGER.fine("SecureConversationInitiator: " + scInitiator.getClass().getName());
        }

        this.rc = RuntimeContext.builder(
                configuration,
                Communicator.builder("rm-client-tube-communicator")
                .tubelineHead(super.next)
                .secureConversationInitiator(scInitiator)
                .addressingVersion(configuration.getAddressingVersion())
                .soapVersion(configuration.getSoapVersion())
                .jaxbContext(configuration.getRuntimeVersion().getJaxbContext(configuration.getAddressingVersion()))
                .container(context.getContainer())
                .build()).build();

        DeliveryQueueBuilder outboundQueueBuilder = DeliveryQueueBuilder.getBuilder(
                rc.configuration,
                PostmanPool.INSTANCE.getPostman(),
                new ClientSourceDeliveryCallback(rc));

        DeliveryQueueBuilder inboundQueueBuilder = null;
        if (rc.configuration.requestResponseOperationsDetected()) {
            inboundQueueBuilder = DeliveryQueueBuilder.getBuilder(
                    rc.configuration,
                    PostmanPool.INSTANCE.getPostman(),
                    new ClientDestinationDeliveryCallback(rc));
        }

        if (configuration.getRmFeature().isPersistenceEnabled()) {
            localIDManager.value = new JDBCLocalIDManager();
        } else {
            localIDManager.value = InMemoryLocalIDManager.getInstance();
        }

        SequenceManager sequenceManager = SequenceManagerFactory.INSTANCE.createSequenceManager(
                configuration.getRmFeature().isPersistenceEnabled(),
                context.getAddress().getURI().toString(),
                inboundQueueBuilder,
                outboundQueueBuilder,
                rc.configuration,
                context.getContainer(),
                localIDManager.value);
        rc.setSequenceManager(sequenceManager);

        // TODO P3 we should also take into account addressable clients
        final WsmcRuntimeProvider wsmcRuntimeProvider = context.getImplementation(WsmcRuntimeProvider.class);
        if (configuration.isMakeConnectionSupportEnabled()) {
            assert wsmcRuntimeProvider != null;

            this.rmSourceReference = wsmcRuntimeProvider.getWsmcAnonymousEndpointReference();
            wsmcRuntimeProvider.registerProtocolMessageHandler(createRmProtocolMessageHandler(rc));
        } else {
            this.rmSourceReference = configuration.getAddressingVersion().anonymousEpr;
        }
    }

    @Override
    public ClientTube copy(TubeCloner cloner) {
        LOGGER.entering();
        try {
            return new ClientTube(this, cloner);
        } finally {
            LOGGER.exiting();
        }
    }
    
    @Override
    public NextAction processRequest(Packet request) {
        LOGGER.entering();

        if (rc.transactionHandler.userTransactionAvailable()
                && rc.transactionHandler.transactionExists()) {
            String errorMessage = LocalizationMessages.WSRM_5002_CLIENTTUBE_PROCESSING_CANNNOT_HAVE_TRANSACTION();
            LOGGER.severe(errorMessage);
            throw new TransactionException(errorMessage);
        }

        try {
            HaContext.initFrom(request);
            if (HaContext.failoverDetected()) {
                rc.sequenceManager().invalidateCache();
            }

            String userStateId = request.getUserStateId();
            if(userStateId != null) {
                rc.setUserStateID(userStateId);
            }

            // set up with LocalID in OutboundDelivered
            String localID = null;
            boolean existingLocalID = false;
            boolean sendWithExistingSeqIdAndMsgNumber = false;
            long outboundMessageNumber = 0;
            OutboundDelivered outboundDelivered = request.getSatellite(OutboundDelivered.class);
            if (outboundDelivered != null) {
                localID = outboundDelivered.getMessageIdentity();
                if (localID != null) {
                    BoundMessage boundMessage = localIDManager.value.getBoundMessage(localID);
                    if (boundMessage != null) {
                        existingLocalID = true;

                        boolean validSequence = false;
                        try {
                            Sequence existingSequence = rc.sequenceManager().getOutboundSequence(boundMessage.sequenceID);
                            if (existingSequence != null) {
                                if ( Sequence.State.CREATED.equals(existingSequence.getState())) {
                                    validSequence = true;
                                }
                            }
                        } catch (Throwable t) {
                            // In case we have a problem, take it as the sequence it is looking for is invalid
                        }
                        
                        if (validSequence) {
                            // use the existing localID to send message
                            sendWithExistingSeqIdAndMsgNumber = true;
                            outboundSequenceId.value = boundMessage.sequenceID;
                            outboundMessageNumber = boundMessage.messageNumber;
                        } else {
                            InvalidSequenceException ex = new InvalidSequenceException(
                                    "Refused to redeliver the message identified by localID ("+ localID +
                                    ") because the bound sequence (" + boundMessage.sequenceID +
                                    ") is not in active state.", boundMessage.sequenceID);
                            LOGGER.logSevereException(ex);
                            return doThrow(ex);
                        }
                    }
                }
            }

            try {
                INIT_LOCK.lock();
                if (outboundSequenceId.value == null) { // RM session not initialized yet - need to synchronize
                    openRmSession(request);
                }
            } finally {
                INIT_LOCK.unlock();
            }
            assert outboundSequenceId != null;

            JaxwsApplicationMessage tempMessage;
            if (!sendWithExistingSeqIdAndMsgNumber) {
                tempMessage = new JaxwsApplicationMessage(
                        request,
                        request.getMessage().getID(rc.addressingVersion, rc.soapVersion));
                boolean persistenceEnabled = rc.configuration.getRmFeature().isPersistenceEnabled();
                rc.sourceMessageHandler.registerMessage(tempMessage,
                        outboundSequenceId.value, !persistenceEnabled);
            } else {
                // create message with previous sequence id and message number
                tempMessage = JaxwsApplicationMessage.newInstance(
                        request,
                        1,
                        request.getMessage().getID(rc.addressingVersion, rc.soapVersion),
                        null,
                        outboundSequenceId.value,
                        outboundMessageNumber);
                // no need to persistence the message
                
                // set DestinationAddress 
                rc.communicator.setDestinationAddressFrom(request); // set the actual destination endpoint from the first packed
            }
            final JaxwsApplicationMessage message = tempMessage;
 
            if (localID != null) {
                // persist the localID if it is a new one
                if (!existingLocalID) {
                    localIDManager.value.createLocalID(localID, message.getSequenceId(), message.getMessageNumber());
                }
                // book keeping this localID for clean up 
                if (processedLocalIDs.value == null) {
                    processedLocalIDs.value = new HashSet<String>();
                }
                processedLocalIDs.value.add(localID);
            }
            
            synchronized (message.getCorrelationId()) {
                // this synchronization is needed so that all 3 operations occur before
                // AbstractResponseHandler.getParentFiber() is invoked on the response thread
                rc.suspendedFiberStorage.register(message.getCorrelationId(), Fiber.current());
                return doSuspend(new Runnable() {
                    @Override
                    public void run() {
                        rc.sourceMessageHandler.putToDeliveryQueue(message);
                    }
                });
            }
        } catch (DuplicateMessageRegistrationException ex) {
            // TODO P2 duplicate message exception handling
            LOGGER.logSevereException(ex);
            return doThrow(ex);
        } catch (RxRuntimeException ex) {
            LOGGER.logSevereException(ex);
            return doThrow(ex);
        } finally {
            HaContext.clear();
            LOGGER.exiting();
        }
    }

    @Override
    public NextAction processResponse(Packet repsonse) {
        LOGGER.entering();
        try {
            return super.processResponse(repsonse);
        } finally {
            LOGGER.exiting();
        }
    }

    @Override
    public NextAction processException(Throwable throwable) {
        LOGGER.entering();
        try {
            if (throwable instanceof RxRuntimeException) {
                // try to close current RM session in case of an unhandled RX exception
                closeRmSession();
            }

            return super.processException(throwable);
        } finally {
            LOGGER.exiting();
        }
    }

    @Override
    public void preDestroy() {
        LOGGER.entering();
        try {
            cleanupPersistedLocalIDs();
            closeRmSession();
        } finally {
            try {
                rc.close();
            } finally {
                super.preDestroy();
                LOGGER.exiting();
            }
        }
    }
    
    private void cleanupPersistedLocalIDs() {
        if (processedLocalIDs.value != null) {
            localIDManager.value.removeLocalIDs(processedLocalIDs.value.iterator());
        }
    }

    static ProtocolMessageHandler createRmProtocolMessageHandler(final RuntimeContext rc) {

        final RmProtocolVersion rmVersion = rc.configuration.getRuntimeVersion().protocolVersion;

        return new ProtocolMessageHandler() {

            Collection<String> SUPPORTED_WSA_ACTIONS = Collections.unmodifiableCollection(Arrays.asList(new String[]{
                        rmVersion.ackRequestedAction,
                        rmVersion.closeSequenceAction,
                        // rmVersion.closeSequenceResponseAction,
                        // rmVersion.createSequenceAction,
                        // rmVersion.createSequenceResponseAction,
                        // rmVersion.lastAction,
                        rmVersion.sequenceAcknowledgementAction,
                        rmVersion.terminateSequenceAction, // rmVersion.terminateSequenceResponseAction,
                    // rmVersion.wsrmFaultAction
                    }));

            public Collection<String> getSuportedWsaActions() {
                return SUPPORTED_WSA_ACTIONS;
            }

            public void processProtocolMessage(Packet protocolMessagePacket) {
                if (rc.protocolHandler.containsProtocolMessage(protocolMessagePacket)) {
                    LOGGER.finer("Processing RM protocol response message.");

                    final String wsaAction = rc.communicator.getWsaAction(protocolMessagePacket);
                    if (rmVersion.ackRequestedAction.equals(wsaAction) || rmVersion.sequenceAcknowledgementAction.equals(wsaAction)) {
                        AcknowledgementData ackData = rc.protocolHandler.getAcknowledgementData(protocolMessagePacket.getMessage());
                        rc.destinationMessageHandler.processAcknowledgements(ackData);
                    } else if (rmVersion.closeSequenceAction.equals(wsaAction)) {
                        handleCloseSequenceAction(protocolMessagePacket);
                    } else if (rmVersion.terminateSequenceAction.equals(wsaAction)) {
                        handleTerminateSequenceAction(protocolMessagePacket);
                    } else {
                        throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRM_1134_UNSUPPORTED_PROTOCOL_MESSAGE(wsaAction)));
                    }
                } else {
                    LOGGER.severe(LocalizationMessages.WSRM_1120_RESPONSE_NOT_IDENTIFIED_AS_PROTOCOL_MESSAGE());
                }
            }

            private void handleCloseSequenceAction(Packet protocolMessagePacket) {
                CloseSequenceData requestData = rc.protocolHandler.toCloseSequenceData(protocolMessagePacket);
                rc.destinationMessageHandler.processAcknowledgements(requestData.getAcknowledgementData());

                try {
                    // TODO P2 pass last message id into closeSequence method, so that sequence can allocate new unacked messages if necessary
                    Sequence closedSequence = rc.sequenceManager().closeSequence(requestData.getSequenceId());

                    final CloseSequenceResponseData.Builder responseBuilder = CloseSequenceResponseData.getBuilder(closedSequence.getId());
                    responseBuilder.acknowledgementData(rc.destinationMessageHandler.getAcknowledgementData(closedSequence.getId()));
                    closedSequence.clearAckRequestedFlag();

                    CloseSequenceResponseData responseData = responseBuilder.build();
                    Packet responsePacket = rc.protocolHandler.toPacket(responseData, protocolMessagePacket, true);
                    responsePacket.setIsProtocolMessage();
                    rc.communicator.sendAsync(responsePacket, null);
                } catch (UnknownSequenceException ex) {
                    LOGGER.warning(LocalizationMessages.WSRM_1124_NO_SUCH_SEQUENCE_ID_REGISTERED(requestData.getSequenceId()), ex);
                    rc.communicator.sendAsync(ex.toRequest(rc), null);
                }
            }

            private void handleTerminateSequenceAction(Packet protocolMessagePacket) {
                TerminateSequenceData requestData = rc.protocolHandler.toTerminateSequenceData(protocolMessagePacket);
                rc.destinationMessageHandler.processAcknowledgements(requestData.getAcknowledgementData());

                try {
                    // TODO P2 pass last message id into terminateSequence method - so that it can implement proper incomplete sequence behavior
                    rc.sequenceManager().terminateSequence(requestData.getSequenceId());

                    TerminateSequenceResponseData tsrData = TerminateSequenceResponseData.getBuilder(requestData.getSequenceId()).build();
                    Packet tsrPacket = rc.protocolHandler.toPacket(tsrData, protocolMessagePacket, true);
                    tsrPacket.setIsProtocolMessage();
                    rc.communicator.sendAsync(tsrPacket, null);
                } catch (UnknownSequenceException ex) {
                    LOGGER.warning(LocalizationMessages.WSRM_1124_NO_SUCH_SEQUENCE_ID_REGISTERED(requestData.getSequenceId()), ex);
                    rc.communicator.sendAsync(ex.toRequest(rc), null);
                }
            }
        };
    }

    private void openRmSession(Packet request) {
        rc.communicator.setDestinationAddressFrom(request); // set the actual destination endpoint from the first packed
        
        createSequences(request);

        ClientAckRequesterTask cart = new ClientAckRequesterTask(rc, outboundSequenceId.value);
        MaintenanceTaskExecutor.register(cart, cart.getExecutionDelay(), cart.getExecutionDelayTimeUnit(), request.component);
    }

    private void closeRmSession() {
        if (outboundSequenceId.value == null || !rc.sequenceManager().isValid(outboundSequenceId.value)) { // RM session is not valid (e.g. has not been started yet
            return;
        }

        final String inboundSequenceId = rc.getBoundSequenceId(outboundSequenceId.value);
        try {
            if (inboundSequenceId != null) {
                waitForMissingAcknowledgements(inboundSequenceId, rc.configuration.getRmFeature().getCloseSequenceOperationTimeout());
            }
        } catch (RuntimeException ex) {
            LOGGER.warning(LocalizationMessages.WSRM_1103_RM_SEQUENCE_NOT_TERMINATED_NORMALLY(), ex);
        }

        try {
            sendCloseSequenceRequest();
        } catch (RuntimeException ex) {
            LOGGER.warning(LocalizationMessages.WSRM_1103_RM_SEQUENCE_NOT_TERMINATED_NORMALLY(), ex);
        } finally {
            closeSequences();
        }

        try {
            waitForMissingAcknowledgements(outboundSequenceId.value, rc.configuration.getRmFeature().getCloseSequenceOperationTimeout());
        } catch (RuntimeException ex) {
            LOGGER.warning(LocalizationMessages.WSRM_1103_RM_SEQUENCE_NOT_TERMINATED_NORMALLY(), ex);
        }

        try {
            terminateOutboundSequence();
        } catch (RuntimeException ex) {
            LOGGER.warning(LocalizationMessages.WSRM_1103_RM_SEQUENCE_NOT_TERMINATED_NORMALLY(), ex);
        } finally {
            // TODO P2 pass last message id into terminateSequence method
            rc.sequenceManager().terminateSequence(outboundSequenceId.value);
        }

        if (inboundSequenceId != null) {
            try {
                waitForInboundSequenceStateChange(inboundSequenceId, rc.configuration.getRmFeature().getCloseSequenceOperationTimeout(), Sequence.State.TERMINATING);
            } catch (RuntimeException ex) {
                LOGGER.warning(LocalizationMessages.WSRM_1103_RM_SEQUENCE_NOT_TERMINATED_NORMALLY(), ex);
            } finally {
                if (rc.sequenceManager().isValid(inboundSequenceId)) {
                    try {
                        rc.sequenceManager().terminateSequence(inboundSequenceId);
                    } catch (UnknownSequenceException ignored) { /* ignored - most likely terminated externally in the meanwhile */ }
                }
            }
        }
    }

    private void createSequences(Packet appRequest) throws RxRuntimeException, DuplicateSequenceException {
        final CreateSequenceData.Builder csBuilder = CreateSequenceData.getBuilder(this.rmSourceReference.toSpec());

        try {
            csBuilder.strType(rc.communicator.tryStartSecureConversation(appRequest));
        } catch (WSTrustException ex) {
            LOGGER.severe(LocalizationMessages.WSRM_1121_SECURE_CONVERSATION_INIT_FAILED(), ex);
        }

        if (rc.configuration.requestResponseOperationsDetected() &&
                !rc.configuration.getRmFeature().isOfferElementGenerationDisabled()) {
            csBuilder.offeredInboundSequenceId(rc.sequenceManager().generateSequenceUID());
            // TODO P2 add offered sequence expiration configuration
        }

        final String messageName = "CreateSequence";

        CreateSequenceData requestData = csBuilder.build();
        Packet request = rc.protocolHandler.toPacket(requestData, null);
        request.setIsProtocolMessage();
        if (rc.getUserStateID() != null) {
            request.setUserStateId(rc.getUserStateID());
        }

        Packet response = sendSessionControlMessage(messageName, request);
        CreateSequenceResponseData responseData = rc.protocolHandler.
                toCreateSequenceResponseData(verifyResponse(response, messageName, Level.SEVERE));

        if (requestData.getOfferedSequenceId() != null && responseData.getAcceptedSequenceAcksTo() == null) {
            // WS-I RSP R0010, R0011 - we must not fail in case of the Offer element has not been accepted by RMD
            // This behavior was detected when testing with IBM endpoint in a test scenario in which the first
            // CreateSequenceResponse + Accept was dropped. The IBM endpoint returns only CSR without Accept.
            // For now, we will do one more round and try to send a completely new offered sequence Id

            csBuilder.offeredInboundSequenceId(rc.sequenceManager().generateSequenceUID());
            // TODO P2 add offered sequence expiration configuration

            requestData = csBuilder.build();
            request = rc.protocolHandler.toPacket(requestData, null);

            response = sendSessionControlMessage(messageName, request);
            responseData = rc.protocolHandler.
                    toCreateSequenceResponseData(verifyResponse(response, messageName, Level.SEVERE));
        }

        if (responseData.getAcceptedSequenceAcksTo() != null) {
            if (!rc.communicator.getDestinationAddress().getURI().toString()
                    .equals(new WSEndpointReference(responseData.getAcceptedSequenceAcksTo()).getAddress())) {
                throw new RxRuntimeException(LocalizationMessages.WSRM_1116_ACKS_TO_NOT_EQUAL_TO_ENDPOINT_DESTINATION(responseData.getAcceptedSequenceAcksTo().toString(), rc.communicator.getDestinationAddress()));
            }
        }

        String outboundSeqId = responseData.getSequenceId();
        String outboundSeqSTRId = (requestData.getStrType() != null) ? requestData.getStrType().getId() : null;
        long outboundSeqExpTime = (responseData.getDuration() == Sequence.NO_EXPIRY) ? Sequence.NO_EXPIRY : responseData.getDuration() + rc.sequenceManager().currentTimeInMillis();
        Sequence outboundSequence =
                rc.sequenceManager().createOutboundSequence(outboundSeqId, outboundSeqSTRId, outboundSeqExpTime);
        this.outboundSequenceId.value = outboundSequence.getId();

        String offeredSeqId = requestData.getOfferedSequenceId();
        String offeredSeqSTRId = (requestData.getStrType() != null) ? requestData.getStrType().getId() : null;
        long offeredSeqExpTime = (responseData.getDuration() == Sequence.NO_EXPIRY) ? Sequence.NO_EXPIRY : responseData.getDuration() + rc.sequenceManager().currentTimeInMillis();
        if (offeredSeqId != null) {
            Sequence inboundSequence =
                    rc.sequenceManager().createInboundSequence(offeredSeqId, offeredSeqSTRId, offeredSeqExpTime);

            rc.sequenceManager().bindSequences(outboundSequenceId.value, inboundSequence.getId());
            rc.sequenceManager().bindSequences(inboundSequence.getId(), outboundSequenceId.value);
        }
    }

    private boolean sendCloseSequenceRequest() {
        CloseSequenceData.Builder dataBuilder = CloseSequenceData.getBuilder(
                outboundSequenceId.value,
                rc.sequenceManager().getOutboundSequence(outboundSequenceId.value).getLastMessageNumber());
        dataBuilder.acknowledgementData(rc.sourceMessageHandler.getAcknowledgementData(outboundSequenceId.value));

        final Packet request = rc.protocolHandler.toPacket(dataBuilder.build(), null);
        request.setIsProtocolMessage();
        if (rc.getUserStateID() != null) {
            request.setUserStateId(rc.getUserStateID());
        }
        final String messageName = "CloseSequence";
        final Packet response = verifyResponse(sendSessionControlMessage(messageName, request), messageName, Level.WARNING);

        final String responseAction = rc.communicator.getWsaAction(response);
        if (rc.rmVersion.protocolVersion.closeSequenceResponseAction.equals(responseAction)) {
            final CloseSequenceResponseData responseData = rc.protocolHandler.toCloseSequenceResponseData(response);
            rc.destinationMessageHandler.processAcknowledgements(responseData.getAcknowledgementData());
            if (!outboundSequenceId.value.equals(responseData.getSequenceId())) {
                LOGGER.warning(LocalizationMessages.WSRM_1119_UNEXPECTED_SEQUENCE_ID_IN_CLOSE_SR(responseData.getSequenceId(), outboundSequenceId));
            }

            return true;
        }

        return false;
    }

    private void closeSequences() {
        String boundSequenceId = rc.getBoundSequenceId(outboundSequenceId.value);
        try {
            rc.sequenceManager().closeSequence(outboundSequenceId.value);
        } finally {
            if (boundSequenceId != null) {
                try {
                    waitForInboundSequenceStateChange(boundSequenceId, rc.configuration.getRmFeature().getCloseSequenceOperationTimeout(), Sequence.State.CLOSED);
                } catch (RuntimeException ex) {
                    LOGGER.warning(LocalizationMessages.WSRM_1103_RM_SEQUENCE_NOT_TERMINATED_NORMALLY(), ex);
                } finally {
                    if (rc.sequenceManager().isValid(boundSequenceId)) {
                        try {
                            rc.sequenceManager().closeSequence(boundSequenceId);
                        } catch (UnknownSequenceException ignored) { /* ignored - most likely terminated externally in the meanwhile */ }
                    }
                }
            }
        }
    }

    private void terminateOutboundSequence() {
        TerminateSequenceData.Builder dataBuilder = TerminateSequenceData.getBuilder(
                outboundSequenceId.value,
                rc.sequenceManager().getOutboundSequence(outboundSequenceId.value).getLastMessageNumber());
        dataBuilder.acknowledgementData(rc.sourceMessageHandler.getAcknowledgementData(outboundSequenceId.value));

        final Packet request = rc.protocolHandler.toPacket(dataBuilder.build(), null);
        request.setIsProtocolMessage();
        if (rc.getUserStateID() != null) {
            request.setUserStateId(rc.getUserStateID());
        }

        final String messageName = "TerminateSequence";
        final Packet response = verifyResponse(sendSessionControlMessage(messageName, request), messageName, Level.FINE);

        if (response.getMessage() != null) {
            final String responseAction = rc.communicator.getWsaAction(response);

            if (rc.rmVersion.protocolVersion.terminateSequenceResponseAction.equals(responseAction)) {
                TerminateSequenceResponseData responseData = rc.protocolHandler.toTerminateSequenceResponseData(response);

                rc.destinationMessageHandler.processAcknowledgements(responseData.getAcknowledgementData());

                if (!outboundSequenceId.value.equals(responseData.getSequenceId())) {
                    LOGGER.warning(LocalizationMessages.WSRM_1117_UNEXPECTED_SEQUENCE_ID_IN_TERMINATE_SR(responseData.getSequenceId(), outboundSequenceId.value));
                }

            } else if (rc.rmVersion.protocolVersion.terminateSequenceAction.equals(responseAction)) {
                TerminateSequenceData responseData = rc.protocolHandler.toTerminateSequenceData(response);

                rc.destinationMessageHandler.processAcknowledgements(responseData.getAcknowledgementData());

                if (responseData.getSequenceId() != null) {
                    final String expectedInboundSequenceId = rc.getBoundSequenceId(outboundSequenceId.value);
                    if (!areEqual(expectedInboundSequenceId, responseData.getSequenceId())) {
                        LOGGER.warning(LocalizationMessages.WSRM_1117_UNEXPECTED_SEQUENCE_ID_IN_TERMINATE_SR(responseData.getSequenceId(), expectedInboundSequenceId));
                    }
                    try {
                        // TODO P2 pass last message id into terminateSequence method
                        rc.sequenceManager().terminateSequence(responseData.getSequenceId());
                    } catch (UnknownSequenceException ex) {
                        LOGGER.warning(LocalizationMessages.WSRM_1124_NO_SUCH_SEQUENCE_ID_REGISTERED(responseData.getSequenceId()), ex);
                        rc.communicator.sendAsync(ex.toRequest(rc), null);
                    }
                }
            }
        }
    }

    private Packet sendSessionControlMessage(final String messageName, final Packet request) throws RxRuntimeException {
        int attempt = 0;
        Packet response = null;
        while (true) {
            if (attempt > rc.configuration.getRmFeature().getMaxRmSessionControlMessageResendAttempts()) {
                throw new RxRuntimeException(LocalizationMessages.WSRM_1128_MAX_RM_SESSION_CONTROL_MESSAGE_RESEND_ATTEMPTS_REACHED(messageName));
            }
            try {
                Packet requestCopy = request.copy(true);
                requestCopy.setIsProtocolMessage();
                response = rc.communicator.send(requestCopy);
                break;
            } catch (RuntimeException ex) {
                if (!Utilities.isResendPossible(ex)) {
                    throw new RxRuntimeException(LocalizationMessages.WSRM_1106_SENDING_RM_SESSION_CONTROL_MESSAGE_FAILED(messageName), ex);
                } else {
                    LOGGER.warning(LocalizationMessages.WSRM_1106_SENDING_RM_SESSION_CONTROL_MESSAGE_FAILED(messageName), ex);
                }
            }
            attempt++;
        }
        return response;
    }

    private static boolean areEqual(String s1, String s2) {
        if (s1 == null) {
            return s2 == null;
        } else {
            return s1.equals(s2);
        }
    }

    private void waitForMissingAcknowledgements(final String sequenceId, final long timeoutInMillis) {
        final CountDownLatch doneSignal = new CountDownLatch(1);
        ScheduledFuture<?> taskHandle = rc.scheduledTaskManager.startTask(new Runnable() {

            public void run() {
                try {
                    if (!rc.sequenceManager().getSequence(sequenceId).hasUnacknowledgedMessages()) {
                        doneSignal.countDown();
                    }
                } catch (UnknownSequenceException ex) {
                    LOGGER.severe(LocalizationMessages.WSRM_1111_WAITING_FOR_SEQ_ACKS_UNEXPECTED_EXCEPTION(sequenceId), ex);
                    doneSignal.countDown();
                }
            }
        }, 10, 10);

        try {
            if (timeoutInMillis > 0) {
                boolean waitResult = doneSignal.await(timeoutInMillis, TimeUnit.MILLISECONDS);
                if (!waitResult) {
                    LOGGER.info(LocalizationMessages.WSRM_1112_WAITING_FOR_SEQ_ACKS_TIMED_OUT(sequenceId, timeoutInMillis));
                }
            } else {
                doneSignal.await();
            }
        } catch (InterruptedException ex) {
            LOGGER.fine(LocalizationMessages.WSRM_1113_WAITING_FOR_SEQ_ACKS_INTERRUPTED(sequenceId), ex);
        } finally {
            taskHandle.cancel(true);
        }
    }

    private void waitForInboundSequenceStateChange(final String sequenceId, final long timeoutInMillis, final Sequence.State waitForState) {
        final CountDownLatch stateChangedSignal = new CountDownLatch(1);
        ScheduledFuture<?> taskHandle = rc.scheduledTaskManager.startTask(new Runnable() {

            public void run() {
                try {
                    if (rc.sequenceManager().getSequence(sequenceId).getState() == waitForState) {
                        stateChangedSignal.countDown();
                    }
                } catch (UnknownSequenceException ex) {
                    LOGGER.fine(LocalizationMessages.WSRM_1124_NO_SUCH_SEQUENCE_ID_REGISTERED(sequenceId), ex);
                    stateChangedSignal.countDown();
                }
            }
        }, 10, 10);

        try {
            if (timeoutInMillis > 0) {
                boolean waitResult = stateChangedSignal.await(timeoutInMillis, TimeUnit.MILLISECONDS);
                if (!waitResult) {
                    LOGGER.info(LocalizationMessages.WSRM_1157_WAITING_FOR_SEQ_STATE_CHANGE_TIMED_OUT(sequenceId, waitForState, timeoutInMillis));
                }
            } else {
                stateChangedSignal.await();
            }
        } catch (InterruptedException ex) {
            LOGGER.fine(LocalizationMessages.WSRM_1158_WAITING_FOR_SEQ_STATE_CHANGE_INTERRUPTED(sequenceId, waitForState), ex);
        } finally {
            taskHandle.cancel(true);
        }
    }

    private Packet verifyResponse(final Packet response, final String requestId, Level logLevel) throws RxRuntimeException {
        String logMessage = null;
        if (response == null || response.getMessage() == null) {
            logMessage = LocalizationMessages.WSRM_1114_NULL_RESPONSE_ON_PROTOCOL_MESSAGE_REQUEST(requestId);
        } else {
            final String responseAction = rc.communicator.getWsaAction(response);
            if (response.getMessage().isFault() || rc.rmVersion.protocolVersion.isFault(responseAction)) {
                logMessage = LocalizationMessages.WSRM_1115_PROTOCOL_MESSAGE_REQUEST_REFUSED(requestId);
                // FIXME P2 pass fault data into exception
            }
        }

        if (logMessage != null) {
            if (logLevel == Level.SEVERE) {
                throw LOGGER.logSevereException(new RxRuntimeException(logMessage));
            } else {
                LOGGER.log(logLevel, logMessage);
            }
        }

        return response;
    }
}
