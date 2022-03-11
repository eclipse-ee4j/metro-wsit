/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.message.RelatesToHeader;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.protocol.AcknowledgementData;
import com.sun.xml.ws.rx.rm.protocol.CloseSequenceData;
import com.sun.xml.ws.rx.rm.protocol.CloseSequenceResponseData;
import com.sun.xml.ws.rx.rm.protocol.CreateSequenceData;
import com.sun.xml.ws.rx.rm.protocol.CreateSequenceResponseData;
import com.sun.xml.ws.rx.rm.protocol.TerminateSequenceData;
import com.sun.xml.ws.rx.rm.protocol.TerminateSequenceResponseData;
import com.sun.xml.ws.rx.rm.runtime.sequence.DuplicateMessageRegistrationException;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.protocol.wsrm200502.AckRequestedElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200502.CreateSequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200502.CreateSequenceResponseElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200502.SequenceAcknowledgementElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200502.SequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200502.SequenceElement.LastMessage;
import com.sun.xml.ws.rx.rm.protocol.wsrm200502.TerminateSequenceElement;
import com.sun.xml.ws.rx.util.Communicator;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;
import jakarta.xml.soap.Detail;

/**
 *
 */
final class Wsrm200502ProtocolHandler extends WsrmProtocolHandler {

    private static final Logger LOGGER = Logger.getLogger(Wsrm200502ProtocolHandler.class);
    private final RuntimeContext rc;

    Wsrm200502ProtocolHandler(RmConfiguration configuration, RuntimeContext rc, Communicator communicator) {
        super(RmRuntimeVersion.WSRM200502, configuration, communicator);

        assert rc != null;

        this.rc = rc;
    }

    @Override
    public CreateSequenceData toCreateSequenceData(@NotNull Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();
        CreateSequenceElement csElement = unmarshallMessage(message);

        // TODO process UsesSequenceSTR

        return csElement.toDataBuilder().build();
    }

    @Override
    public Packet toPacket(CreateSequenceData data, @Nullable Packet requestPacket) throws RxRuntimeException {

        return communicator.createRequestPacket(requestPacket, new CreateSequenceElement(data), rmVersion.protocolVersion.createSequenceAction, true);
    }

    @Override
    public CreateSequenceResponseData toCreateSequenceResponseData(Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();
        CreateSequenceResponseElement csrElement = unmarshallMessage(message);

        return csrElement.toDataBuilder().build();
    }

    @Override
    public Packet toPacket(CreateSequenceResponseData data, @NotNull Packet requestPacket, boolean clientSideResponse) throws RxRuntimeException {
        return communicator.createResponsePacket(requestPacket, new CreateSequenceResponseElement(data), rmVersion.protocolVersion.createSequenceResponseAction, clientSideResponse);
    }

    @Override
    public CloseSequenceData toCloseSequenceData(Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();

        try {
            ApplicationMessage lastAppMessage = new ApplicationMessageBase("") {

                @Override
                public State getState() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
            loadSequenceHeaderData(lastAppMessage, message);
            loadAcknowledgementData(lastAppMessage, message);

            // simulating last message delivery
            Sequence inboundSequence = rc.sequenceManager().getInboundSequence(lastAppMessage.getSequenceId());
            try {
                inboundSequence.registerMessage(lastAppMessage, false);
            } catch (Exception ex) {
                throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRM_1146_UNEXPECTED_ERROR_WHILE_REGISTERING_MESSAGE(), ex));
            }
            inboundSequence.acknowledgeMessageNumber(lastAppMessage.getMessageNumber());
            inboundSequence.setAckRequestedFlag();

            CloseSequenceData.Builder dataBuilder = CloseSequenceData.getBuilder(lastAppMessage.getSequenceId(), lastAppMessage.getMessageNumber());
            dataBuilder.acknowledgementData(lastAppMessage.getAcknowledgementData());
            return dataBuilder.build();
        } finally {
            message.consume();
        }
    }

    @Override
    public Packet toPacket(CloseSequenceData data, @Nullable Packet requestPacket) throws RxRuntimeException {
        Packet packet;
        if (requestPacket != null) {
            packet = communicator.createEmptyResponsePacket(requestPacket, rmVersion.protocolVersion.closeSequenceAction);
        } else {
            packet = communicator.createEmptyRequestPacket(rmVersion.protocolVersion.closeSequenceAction, false);
        }
        final Message message = packet.getMessage();

        ApplicationMessage lastAppMessage = new ApplicationMessageBase("") {

            @Override
            public State getState() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        try {
            rc.sequenceManager().getOutboundSequence(data.getSequenceId()).registerMessage(lastAppMessage, false);
        } catch (DuplicateMessageRegistrationException | IllegalStateException ex) {
            LOGGER.logSevereException(ex);
        }

        SequenceElement sequenceElement = new SequenceElement();
        sequenceElement.setId(lastAppMessage.getSequenceId());
        sequenceElement.setMessageNumber(lastAppMessage.getMessageNumber());
        sequenceElement.setLastMessage(new LastMessage());

        message.getHeaders().add(createHeader(sequenceElement));

        appendAcknowledgementHeaders(packet, data.getAcknowledgementData());

        return packet;
    }

    @Override
    public CloseSequenceResponseData toCloseSequenceResponseData(Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();
        try {
            AcknowledgementData ackData = getAcknowledgementData(message);

            CloseSequenceResponseData.Builder dataBuilder = CloseSequenceResponseData.getBuilder(ackData.getAcknowledgedSequenceId());

            dataBuilder.acknowledgementData(ackData);

            return dataBuilder.build();
        } finally {
            message.consume();
        }
    }

    @Override
    public Packet toPacket(CloseSequenceResponseData data, @NotNull Packet requestPacket, boolean clientSideResponse) throws RxRuntimeException {
        /*
          Server-side Replay model (https://www.wso2.org/library/2792#Server) requirements:

          D) If there is no response to a given request – i.e. the request is a one-way message,
             then the Server MUST respond with an acknowledgement that includes the request message
             in one of the ranges.

          ...

          F) The Server SHOULD respond to an incoming LastMessage with a LastMessage for the Offered Sequence
         */
        Sequence boundSequence = rc.sequenceManager().getBoundSequence(data.getSequenceId());
        if (boundSequence != null) {
            // Apply requirement D)
            CloseSequenceData closeSequenceData = CloseSequenceData.getBuilder(boundSequence.getId(), boundSequence.getLastMessageNumber()).acknowledgementData(data.getAcknowledgementData()).build();
            return toPacket(closeSequenceData, requestPacket);
        } else {
            // Apply requirement F)
            return createEmptyAcknowledgementResponse(data.getAcknowledgementData(), requestPacket);
        }
    }

    @Override
    public TerminateSequenceData toTerminateSequenceData(Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();
        TerminateSequenceElement tsElement = unmarshallMessage(message);
        final TerminateSequenceData.Builder dataBuilder = tsElement.toDataBuilder();

        dataBuilder.acknowledgementData(getAcknowledgementData(message));

        return dataBuilder.build();
    }

    @Override
    public Packet toPacket(TerminateSequenceData data, @Nullable Packet requestPacket) throws RxRuntimeException {
        Packet packet = communicator.createRequestPacket(requestPacket, new TerminateSequenceElement(data), rmVersion.protocolVersion.terminateSequenceAction, true);

        if (data.getAcknowledgementData() != null) {
            appendAcknowledgementHeaders(packet, data.getAcknowledgementData());
        }

        return packet;
    }

    @Override
    public TerminateSequenceResponseData toTerminateSequenceResponseData(Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();
        try {
            TerminateSequenceResponseData.Builder dataBuilder = TerminateSequenceResponseData.getBuilder(""/*TODO*/);
            dataBuilder.acknowledgementData(getAcknowledgementData(message));

            return dataBuilder.build();
        } finally {
            message.consume();
        }
    }

    @Override
    public Packet toPacket(TerminateSequenceResponseData data, @NotNull Packet requestPacket, boolean clientSideResponse) throws RxRuntimeException {
        if (data.getBoundSequenceId() != null) {
            // Send terminate sequence. RM v1.0 does not define TerminateSequenceResponse.
            TerminateSequenceData tsData = TerminateSequenceData
                    .getBuilder(data.getBoundSequenceId(), data.getBoundSequenceLastMessageId())
                    .acknowledgementData(data.getAcknowledgementData())
                    .build();

            Packet packet = toPacket(tsData, requestPacket);

            if (!clientSideResponse) {
                // Add relatesTo. See https://java.net/jira/browse/WSIT-1669
                packet.getMessage().getHeaders().add(new RelatesToHeader(
                        addressingVersion.relatesToTag,
                        AddressingUtils.getMessageID(requestPacket.getMessage().getHeaders(), addressingVersion, soapVersion)));
            }

            return packet;
        } else {
            requestPacket.transportBackChannel.close();
            return communicator.createNullResponsePacket(requestPacket);
        }
    }

    @Override
    public void appendSequenceHeader(@NotNull Message jaxwsMessage, @NotNull ApplicationMessage message) throws RxRuntimeException {
        assert message != null;
        assert message.getSequenceId() != null;

        SequenceElement sequenceHeaderElement = new SequenceElement();
        sequenceHeaderElement.setId(message.getSequenceId());
        sequenceHeaderElement.setMessageNumber(message.getMessageNumber());
        final String muTrue = SOAPVersion.SOAP_12.equals(soapVersion) ? "true" : "1";
        sequenceHeaderElement.getOtherAttributes().put(communicator.soapMustUnderstandAttributeName, muTrue);
        jaxwsMessage.getHeaders().addOrReplace(createHeader(sequenceHeaderElement));
    }

    @Override
    public void appendAcknowledgementHeaders(@NotNull Packet packet, @NotNull AcknowledgementData ackData) {
        assert packet != null;
        assert packet.getMessage() != null;
        assert ackData != null;


        Message jaxwsMessage = packet.getMessage();
        // ack requested header
        if (ackData.getAckReqestedSequenceId() != null) {
            AckRequestedElement ackRequestedElement = new AckRequestedElement();
            ackRequestedElement.setId(ackData.getAckReqestedSequenceId());

            // MU attribute removed to comply with WS-I RSP R0540 - see WSIT issue #1318
            // ackRequestedElement.getOtherAttributes().put(communicator.soapMustUnderstandAttributeName, "true");
            jaxwsMessage.getHeaders().addOrReplace(createHeader(ackRequestedElement));
        }

        // sequence acknowledgement header
        if (ackData.containsSequenceAcknowledgementData()) {
            SequenceAcknowledgementElement ackElement = new SequenceAcknowledgementElement();
            ackElement.setId(ackData.getAcknowledgedSequenceId());

                for (Sequence.AckRange range : ackData.getAcknowledgedRanges()) {
                    ackElement.addAckRange(range.lower, range.upper);
                }

// TODO decide whether we will advertise remaining buffer
//        if (configuration.getDestinationBufferQuota() != Configuration.UNSPECIFIED) {
//            ackElement.setBufferRemaining(-1/*calculate remaining quota*/);
//        }

            // MU attribute removed to comply with WS-I RSP R0540 - see WSIT issue #1318
            // ackElement.getOtherAttributes().put(communicator.soapMustUnderstandAttributeName, "true");
            jaxwsMessage.getHeaders().addOrReplace(createHeader(ackElement));
        }
    }

    @Override
    public void loadSequenceHeaderData(@NotNull ApplicationMessage message, @NotNull Message jaxwsMessage) throws RxRuntimeException {
        assert message != null;
        assert message.getSequenceId() == null; // not initialized yet

        SequenceElement sequenceElement = readHeaderAsUnderstood(RmRuntimeVersion.WSRM200502.protocolVersion.protocolNamespaceUri, "Sequence", jaxwsMessage);
        if (sequenceElement != null) {
            message.setSequenceData(sequenceElement.getId(), sequenceElement.getMessageNumber());
        }
    }

    @Override
    public void loadAcknowledgementData(@NotNull ApplicationMessage message, @NotNull Message jaxwsMessage) throws RxRuntimeException {
        assert message != null;
        assert message.getAcknowledgementData() == null; // not initialized yet

        message.setAcknowledgementData(getAcknowledgementData(jaxwsMessage));
    }

    @Override
    public AcknowledgementData getAcknowledgementData(Message jaxwsMessage) throws RxRuntimeException {
        assert jaxwsMessage != null;

        AcknowledgementData.Builder ackDataBuilder = AcknowledgementData.getBuilder();
        AckRequestedElement ackRequestedElement = readHeaderAsUnderstood(rmVersion.protocolVersion.protocolNamespaceUri, "AckRequested", jaxwsMessage);
        if (ackRequestedElement != null) {
            ackDataBuilder.ackReqestedSequenceId(ackRequestedElement.getId());
        }
        SequenceAcknowledgementElement ackElement = readHeaderAsUnderstood(rmVersion.protocolVersion.protocolNamespaceUri, "SequenceAcknowledgement", jaxwsMessage);
        if (ackElement != null) {
            List<Sequence.AckRange> ranges = new LinkedList<>();
            if (!ackElement.getNack().isEmpty()) {
                List<BigInteger> nacks = new ArrayList<>(ackElement.getNack());
                Collections.sort(nacks);
                long lastLowerBound = 1;
                for (BigInteger nackId : nacks) {
                    if (lastLowerBound == nackId.longValue()) {
                        lastLowerBound++;
                    } else {
                        ranges.add(new Sequence.AckRange(lastLowerBound, nackId.longValue() - 1));
                        lastLowerBound = nackId.longValue() + 1;
                    }
                }

                long lastMessageId = rc.sequenceManager().getSequence(ackElement.getId()).getLastMessageNumber();
                if (lastLowerBound <= lastMessageId) {
                    ranges.add(new Sequence.AckRange(lastLowerBound, lastMessageId));
                }

            } else if (ackElement.getAcknowledgementRange() != null && !ackElement.getAcknowledgementRange().isEmpty()) {
                for (SequenceAcknowledgementElement.AcknowledgementRange rangeElement : ackElement.getAcknowledgementRange()) {
                    ranges.add(new Sequence.AckRange(rangeElement.getLower().longValue(), rangeElement.getUpper().longValue()));
                }
            }
            // TODO handle final and remaining buffer in the header
            // ackElement.getBufferRemaining();
            ackDataBuilder.acknowledgements(ackElement.getId(), ranges, false);
        }
        return ackDataBuilder.build();
    }

    @Override
    public Header createSequenceFaultElementHeader(QName subcode, Detail detail) {
        return Headers.create(rmVersion.getJaxbContext(addressingVersion),
                new com.sun.xml.ws.rx.rm.protocol.wsrm200502.SequenceFaultElement(subcode));
    }

    @Override
    public Packet createEmptyAcknowledgementResponse(AcknowledgementData ackData, Packet requestPacket) throws RxRuntimeException {
        if (ackData.getAckReqestedSequenceId() != null || ackData.containsSequenceAcknowledgementData()) {
            // create acknowledgement response only if there is something to send in the SequenceAcknowledgement header
            Packet response = rc.communicator.createEmptyResponsePacket(requestPacket, rc.rmVersion.protocolVersion.sequenceAcknowledgementAction);
            response = rc.communicator.setEmptyResponseMessage(response, requestPacket, rc.rmVersion.protocolVersion.sequenceAcknowledgementAction);
            appendAcknowledgementHeaders(response, ackData);
            return response;
        } else {
            return rc.communicator.createNullResponsePacket(requestPacket);
        }
    }
}
