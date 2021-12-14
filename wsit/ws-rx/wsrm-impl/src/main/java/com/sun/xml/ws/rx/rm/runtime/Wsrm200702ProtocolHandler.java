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

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.mc.dev.AdditionalResponses;
import com.sun.xml.ws.rx.rm.protocol.AcknowledgementData;
import com.sun.xml.ws.rx.rm.protocol.CloseSequenceData;
import com.sun.xml.ws.rx.rm.protocol.CloseSequenceResponseData;
import com.sun.xml.ws.rx.rm.protocol.CreateSequenceData;
import com.sun.xml.ws.rx.rm.protocol.CreateSequenceResponseData;
import com.sun.xml.ws.rx.rm.protocol.TerminateSequenceData;
import com.sun.xml.ws.rx.rm.protocol.TerminateSequenceResponseData;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.AckRequestedElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.CloseSequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.CloseSequenceResponseElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.CreateSequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.CreateSequenceResponseElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.SequenceAcknowledgementElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.SequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.TerminateSequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.TerminateSequenceResponseElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.UsesSequenceSTR;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.AckRange;
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
final class Wsrm200702ProtocolHandler extends WsrmProtocolHandler {

    private static final Logger LOGGER = Logger.getLogger(Wsrm200702ProtocolHandler.class);
    private final RuntimeContext rc;

    Wsrm200702ProtocolHandler(RmConfiguration configuration, RuntimeContext rc, Communicator communicator) {
        super(RmRuntimeVersion.WSRM200702, configuration, communicator);

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
        Packet packet = communicator.createRequestPacket(requestPacket, new CreateSequenceElement(data), rmVersion.protocolVersion.createSequenceAction, true);

        if (data.getStrType() != null) {
            UsesSequenceSTR usesSequenceSTR = new UsesSequenceSTR();
            usesSequenceSTR.getOtherAttributes().put(communicator.soapMustUnderstandAttributeName, "true");
            packet.getMessage().getHeaders().add(Headers.create(getJaxbContext(), usesSequenceSTR));
        }

        return packet;
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
        CloseSequenceElement csElement = unmarshallMessage(message);
        final CloseSequenceData.Builder dataBuilder = csElement.toDataBuilder();

        dataBuilder.acknowledgementData(getAcknowledgementData(message));

        return dataBuilder.build();
    }

    @Override
    public Packet toPacket(CloseSequenceData data, @Nullable Packet requestPacket) throws RxRuntimeException {
        Packet packet = communicator.createRequestPacket(requestPacket, new CloseSequenceElement(data), rmVersion.protocolVersion.closeSequenceAction, true);

        if (data.getAcknowledgementData() != null) {
            appendAcknowledgementHeaders(packet, data.getAcknowledgementData());
        }

        return packet;
    }

    @Override
    public CloseSequenceResponseData toCloseSequenceResponseData(Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();
        CloseSequenceResponseElement csrElement = unmarshallMessage(message); // consuming message here
        final CloseSequenceResponseData.Builder dataBuilder = csrElement.toDataBuilder();

        dataBuilder.acknowledgementData(getAcknowledgementData(message));

        return dataBuilder.build();
    }

    @Override
    public Packet toPacket(CloseSequenceResponseData data, @NotNull Packet requestPacket, boolean clientSideResponse) throws RxRuntimeException {
        Packet packet = communicator.createResponsePacket(requestPacket, new CloseSequenceResponseElement(data), rmVersion.protocolVersion.closeSequenceResponseAction, clientSideResponse);

        if (data.getAcknowledgementData() != null) {
            appendAcknowledgementHeaders(packet, data.getAcknowledgementData());
        }

        return packet;
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


        TerminateSequenceResponseElement tsrElement = unmarshallMessage(message);
        final TerminateSequenceResponseData.Builder dataBuilder = tsrElement.toDataBuilder();

        dataBuilder.acknowledgementData(getAcknowledgementData(message));

        return dataBuilder.build();
    }

    @Override
    public Packet toPacket(final TerminateSequenceResponseData data, @NotNull final Packet requestPacket, boolean clientSideResponse) throws RxRuntimeException {
        final Packet packet = communicator.createResponsePacket(requestPacket, new TerminateSequenceResponseElement(data), rmVersion.protocolVersion.terminateSequenceResponseAction, clientSideResponse);

        if (data.getAcknowledgementData() != null) {
            appendAcknowledgementHeaders(packet, data.getAcknowledgementData());
        }

        // Create additional TerminateSequence "response" message for the outbound sequence if:
        // - there is a bound outbound sequence and
        // - AdditionalResponses property set is available in the req/resp packet => we are on the server side and MakeConnection is used
        //
        // TODO this will need to be fixed once true support for addressable clients is implemented
        if (data.getBoundSequenceId() != null) {
            final AdditionalResponses ar = packet.getSatellite(AdditionalResponses.class);
            if (ar != null) { // Make connection is available and ready to process multiple response packets
                final TerminateSequenceData tsData = TerminateSequenceData.getBuilder(data.getBoundSequenceId(), data.getBoundSequenceLastMessageId()).build();
                ar.getAdditionalResponsePacketQueue().offer(toPacket(tsData, requestPacket));
            }
        }

        return packet;
    }

    @Override
    public void appendSequenceHeader(@NotNull Message jaxwsMessage, @NotNull ApplicationMessage message) throws RxRuntimeException {
        assert message != null;
        assert message.getSequenceId() != null;
        assert jaxwsMessage != null;

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

            packet.invocationProperties.put(RmConfiguration.ACK_REQUESTED_HEADER_SET, Boolean.TRUE);
        }

        // sequence acknowledgement header
        if (ackData.getAcknowledgedSequenceId() != null) {
            SequenceAcknowledgementElement ackElement = new SequenceAcknowledgementElement();
            ackElement.setId(ackData.getAcknowledgedSequenceId());

            final List<AckRange> ackedRanges = ackData.getAcknowledgedRanges();
            if (ackedRanges.isEmpty()) {
                ackElement.setNone(new SequenceAcknowledgementElement.None());
            } else {
                for (Sequence.AckRange range : ackedRanges) {
                    ackElement.addAckRange(range.lower, range.upper);
                }                
            }

            if (ackData.isFinalAcknowledgement()) {
                ackElement.setFinal(new SequenceAcknowledgementElement.Final());
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

        SequenceElement sequenceElement = readHeaderAsUnderstood(rmVersion.protocolVersion.protocolNamespaceUri, "Sequence", jaxwsMessage);
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
            if (ackElement.getNone() == null) {
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
            }
            ackDataBuilder.acknowledgements(ackElement.getId(), ranges, ackElement.getFinal() != null);
            // TODO handle remaining buffer in the header
            // ackElement.getBufferRemaining();
        }
        return ackDataBuilder.build();
    }

    @Override
    public Header createSequenceFaultElementHeader(QName subcode, Detail detail) {
        return Headers.create(rmVersion.getJaxbContext(addressingVersion),
                new com.sun.xml.ws.rx.rm.protocol.wsrm200702.SequenceFaultElement(subcode, detail));
    }
    
    @Override
    public Packet createEmptyAcknowledgementResponse(AcknowledgementData ackData, Packet requestPacket) throws RxRuntimeException {
        if (ackData.getAckReqestedSequenceId() != null || ackData.getAcknowledgedSequenceId() != null) {
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
