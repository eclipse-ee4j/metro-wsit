/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.oracle.webservices.oracle_internal_api.rm.OutboundDelivered;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.commons.ha.HaContext;
import com.sun.xml.ws.rx.RxException;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.runtime.delivery.Postman;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.util.AbstractResponseHandler;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

class ClientSourceDeliveryCallback implements Postman.Callback {
    private static final Logger LOGGER = Logger.getLogger(ClientSourceDeliveryCallback.class);

    private static class ResponseCallbackHandler extends AbstractResponseHandler implements Fiber.CompletionCallback {
        private final JaxwsApplicationMessage request;
        private final RuntimeContext rc;

        public ResponseCallbackHandler(JaxwsApplicationMessage request, RuntimeContext rc) {
            super(rc.suspendedFiberStorage, request.getCorrelationId());
            this.request = request;
            this.rc = rc;
        }

        @Override
        public void onCompletion(Packet response) {
            try {
                HaContext.initFrom(response);

                if (response.getMessage() != null) {
                    JaxwsApplicationMessage message = new JaxwsApplicationMessage(response, getCorrelationId());
                    rc.protocolHandler.loadSequenceHeaderData(message, message.getJaxwsMessage());
                    rc.protocolHandler.loadAcknowledgementData(message, message.getJaxwsMessage());

                    rc.destinationMessageHandler.processAcknowledgements(message.getAcknowledgementData());

                    invokeOutboundDeliveredTrueIfRequestAcked();

                    if (rc.configuration.getRuntimeVersion().protocolVersion.isFault(message.getWsaAction())) {
                        // TODO handle RM faults
                        LOGGER.severe(LocalizationMessages.WSRM_5003_RECEIVED_WSRM_FAULT_RESPONSE(message.getWsaAction()));
                    }

                    if (message.getSequenceId() != null) { //two-way
                        boolean persistenceEnabled = rc.configuration.getRmFeature().isPersistenceEnabled();
                        rc.destinationMessageHandler.registerMessage(message, !persistenceEnabled);
                        rc.destinationMessageHandler.putToDeliveryQueue(message); //resuming parent fiber there
                    } else { //one-way response likely with empty soap body but with ack header
                        resumeParentFiber(response);
                    }
                } else { //maybe HTTP 202 in response to a one-way request
                    final int nextResendCount = request.getNextResendCount();
                    if (!rc.configuration.getRmFeature().canRetransmitMessage(nextResendCount)) {
                        invokeOutboundDeliveredFalse();
                        resumeParentFiber(new RxRuntimeException((LocalizationMessages.WSRM_1159_MAX_MESSAGE_RESEND_ATTEMPTS_REACHED())));
                        return;
                    }

                    RedeliveryTaskExecutor.deliverUsingCurrentThread(
                            request,
                            rc.configuration.getRmFeature().getRetransmissionBackoffAlgorithm().getDelayInMillis(nextResendCount, rc.configuration.getRmFeature().getMessageRetransmissionInterval()),
                            TimeUnit.MILLISECONDS,
                            rc.sourceMessageHandler);
                }
            } catch (RxRuntimeException | RxException ex) {
                onCompletion(ex);
            } finally {
                HaContext.clear();
            }
        }

        @Override
        public void onCompletion(Throwable error) {
            if (Utilities.isResendPossible(error)) {
                final int nextResendCount = request.getNextResendCount();
                if (!rc.configuration.getRmFeature().canRetransmitMessage(nextResendCount)) {
                    invokeOutboundDeliveredFalse();
                    resumeParentFiber(new RxRuntimeException((LocalizationMessages.WSRM_1159_MAX_MESSAGE_RESEND_ATTEMPTS_REACHED())));
                    return;
                }

                try {
                    HaContext.initFrom(request.getPacket());

                    RedeliveryTaskExecutor.deliverUsingCurrentThread(
                            request,
                            rc.configuration.getRmFeature().getRetransmissionBackoffAlgorithm().getDelayInMillis(nextResendCount, rc.configuration.getRmFeature().getMessageRetransmissionInterval()),
                            TimeUnit.MILLISECONDS,
                            rc.sourceMessageHandler);
                } finally {
                    HaContext.clear();
                }
            } else {
                invokeOutboundDeliveredFalse();
                resumeParentFiber(error);
            }
        }

        private void invokeOutboundDeliveredTrueIfRequestAcked() {
            String seqId = request.getSequenceId();
            long messageNumber = request.getMessageNumber();
            OutboundDelivered outboundDelivered = retrieveOutboundDelivered(seqId, messageNumber);

            if (outboundDelivered != null) {
                Sequence outboundSequence = rc.sequenceManager().getOutboundSequence(seqId);
                boolean isRequestAcked = outboundSequence.isAcknowledged(messageNumber);
                if (isRequestAcked) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("Invoking outboundDelivered.setDelivered(true) for " +
                                "seq id:"+outboundSequence.getId()+" and " +
                                "message number:"+messageNumber);
                    }
                    outboundDelivered.setDelivered(Boolean.TRUE);
                    rc.outboundDeliveredHandler.remove(seqId, messageNumber);
                } else {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("isRequestAcked found false, cannot invoke outboundDelivered.setDelivered(true)");
                    }
                }
            }
        }

        private void invokeOutboundDeliveredFalse() {
            String seqId = request.getSequenceId();
            long messageNumber = request.getMessageNumber();
            OutboundDelivered outboundDelivered = retrieveOutboundDelivered(seqId, messageNumber);
            if (outboundDelivered != null) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine("Invoking outboundDelivered.setDelivered(false)");
                }
                outboundDelivered.setDelivered(Boolean.FALSE);
                rc.outboundDeliveredHandler.remove(seqId, messageNumber);
            }
        }

        private OutboundDelivered retrieveOutboundDelivered(String seqId, long messageNumber) {
            OutboundDelivered outboundDelivered = rc.outboundDeliveredHandler.retrieve(seqId, messageNumber);
            if (outboundDelivered == null && LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Could not get OutboundDelivered from OutboundDeliveredHandler");
            }
            return outboundDelivered;
        }
    }

    private final RuntimeContext rc;

    public ClientSourceDeliveryCallback(RuntimeContext rc) {
        this.rc = rc;
    }

    @Override
    public void deliver(ApplicationMessage message) {
        if (message instanceof JaxwsApplicationMessage) {
            deliver((JaxwsApplicationMessage) message);
        } else {
            throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRM_1141_UNEXPECTED_MESSAGE_CLASS(
                    message.getClass().getName(),
                    JaxwsApplicationMessage.class.getName())));
        }
    }

    private void deliver(JaxwsApplicationMessage message) {
        LOGGER.entering(message);
        try {
            rc.sourceMessageHandler.attachAcknowledgementInfo(message);

            Packet outboundPacketCopy = message.getPacket().copy(true);

            OutboundDelivered outboundDelivered =
                    outboundPacketCopy.getSatellite(OutboundDelivered.class);
            if (outboundDelivered != null) {
                String seqId = message.getSequenceId();
                long msgNumber = message.getMessageNumber();
                rc.outboundDeliveredHandler.store(seqId, msgNumber, outboundDelivered);
                outboundPacketCopy.removeSatellite(outboundDelivered);
            } else {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine("OutboundDelivered satellite property was not found");
                }
            }

            rc.protocolHandler.appendSequenceHeader(outboundPacketCopy.getMessage(), message);
            rc.protocolHandler.appendAcknowledgementHeaders(outboundPacketCopy, message.getAcknowledgementData());

            Fiber.CompletionCallback responseCallback = new ResponseCallbackHandler(message, rc);

            rc.communicator.sendAsync(outboundPacketCopy, responseCallback);
        } finally {
            LOGGER.exiting();
        }
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return rc;
    }
}
