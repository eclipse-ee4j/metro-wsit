/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.protocol.AcknowledgementData;
import com.sun.xml.ws.rx.rm.protocol.CloseSequenceData;
import com.sun.xml.ws.rx.rm.protocol.CloseSequenceResponseData;
import com.sun.xml.ws.rx.rm.protocol.CreateSequenceData;
import com.sun.xml.ws.rx.rm.protocol.CreateSequenceResponseData;
import com.sun.xml.ws.rx.rm.protocol.TerminateSequenceData;
import com.sun.xml.ws.rx.rm.protocol.TerminateSequenceResponseData;
import com.sun.xml.ws.rx.util.Communicator;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import jakarta.xml.soap.Detail;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public abstract class WsrmProtocolHandler {

    private static final Logger LOGGER = Logger.getLogger(WsrmProtocolHandler.class);

    public static WsrmProtocolHandler getInstance(RmConfiguration configuration, Communicator communicator, RuntimeContext rc) {
        switch (configuration.getRuntimeVersion()) {
            case WSRM200502:
                return new Wsrm200502ProtocolHandler(configuration, rc, communicator);
            case WSRM200702:
                return new Wsrm200702ProtocolHandler(configuration, rc, communicator);
            default:
                return null;
        }
    }

    protected final RmRuntimeVersion rmVersion;
    protected final Communicator communicator;

    protected final AddressingVersion addressingVersion;
    protected final SOAPVersion soapVersion;

    public abstract void appendSequenceHeader(@NotNull Message jaxwsMessage, @NotNull ApplicationMessage message) throws RxRuntimeException;

    public abstract void appendAcknowledgementHeaders(@NotNull Packet packet, @NotNull AcknowledgementData ackData) throws RxRuntimeException;

    public abstract AcknowledgementData getAcknowledgementData(Message jaxwsMessage) throws RxRuntimeException;

    public abstract void loadAcknowledgementData(@NotNull ApplicationMessage message, @NotNull Message jaxwsMessage) throws RxRuntimeException;

    public abstract void loadSequenceHeaderData(@NotNull ApplicationMessage message, @NotNull Message jaxwsMessage) throws RxRuntimeException;

    public abstract CreateSequenceData toCreateSequenceData(@NotNull Packet packet) throws RxRuntimeException;

    public abstract Packet toPacket(@NotNull CreateSequenceData data, @Nullable Packet requestPacket) throws RxRuntimeException;

    public abstract CreateSequenceResponseData toCreateSequenceResponseData(@NotNull Packet packet) throws RxRuntimeException;

    public abstract Packet toPacket(@NotNull CreateSequenceResponseData data, @NotNull Packet requestPacket, boolean clientSideResponse) throws RxRuntimeException;

    public abstract CloseSequenceData toCloseSequenceData(@NotNull Packet packet) throws RxRuntimeException;

    public abstract Packet toPacket(@NotNull CloseSequenceData data, @Nullable Packet requestPacket) throws RxRuntimeException;

    public abstract CloseSequenceResponseData toCloseSequenceResponseData(@NotNull Packet packet) throws RxRuntimeException;

    public abstract Packet toPacket(@NotNull CloseSequenceResponseData data, @NotNull Packet requestPacket, boolean clientSideResponse) throws RxRuntimeException;

    public abstract TerminateSequenceData toTerminateSequenceData(@NotNull Packet packet) throws RxRuntimeException;

    public abstract Packet toPacket(@NotNull TerminateSequenceData data, @Nullable Packet requestPacket) throws RxRuntimeException;

    public abstract TerminateSequenceResponseData toTerminateSequenceResponseData(@NotNull Packet packet) throws RxRuntimeException;

    public abstract Packet toPacket(@NotNull TerminateSequenceResponseData data, @NotNull Packet requestPacket, boolean clientSideResponse) throws RxRuntimeException;

    public abstract Header createSequenceFaultElementHeader(QName subcode, Detail detail);

    public abstract Packet createEmptyAcknowledgementResponse(AcknowledgementData ackData, Packet requestPacket) throws RxRuntimeException;

    public final boolean containsProtocolMessage(@NotNull Packet packet) {
        assert packet != null;

        return (packet.getMessage() == null) ? false : rmVersion.protocolVersion.isProtocolAction(getWsaAction(packet.getMessage()));
    }

    public final boolean containsProtocolRequest(@NotNull Packet packet) {
        assert packet != null;

        return (packet.getMessage() == null) ? false : rmVersion.protocolVersion.isProtocolRequest(getWsaAction(packet.getMessage()));
    }

    public final boolean containsProtocolResponse(@NotNull Packet packet) {
        assert packet != null;

        return (packet.getMessage() == null) ? false : rmVersion.protocolVersion.isProtocolResponse(getWsaAction(packet.getMessage()));
    }

    protected WsrmProtocolHandler(@NotNull RmRuntimeVersion rmVersion, @NotNull RmConfiguration configuration, @NotNull Communicator communicator) {
        assert rmVersion != null;
        assert rmVersion == configuration.getRuntimeVersion();
        assert configuration != null;
        assert communicator != null;

        this.rmVersion = rmVersion;
        this.communicator = communicator;

        this.addressingVersion = configuration.getAddressingVersion();
        this.soapVersion = configuration.getSoapVersion();
    }

    protected final Header createHeader(Object jaxbHeaderContent) {
        return Headers.create(rmVersion.getJaxbContext(addressingVersion), jaxbHeaderContent);
    }

    protected final <T> T readHeaderAsUnderstood(@NotNull String nsUri, @NotNull String name, @NotNull Message message) throws RxRuntimeException {
        assert nsUri != null;
        assert name != null;
        assert message != null;

        Header header = message.getHeaders().get(nsUri, name, true);
        if (header == null) {
            return null;
        }
        try {
            @SuppressWarnings(value = "unchecked")
            T result = (T) header.readAsJAXB(getJaxbUnmarshaller());
            return result;
        } catch (JAXBException ex) {
            throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRM_1122_ERROR_MARSHALLING_RM_HEADER(nsUri + "#" + name), ex));
        }
    }

    protected final String getWsaAction(@NotNull Message message) {
        return AddressingUtils.getAction(message.getHeaders(), addressingVersion, soapVersion);
    }

    protected final JAXBRIContext getJaxbContext() {
        return rmVersion.getJaxbContext(addressingVersion);
    }

    protected final Unmarshaller getJaxbUnmarshaller() throws RxRuntimeException {
        return rmVersion.createUnmarshaller(addressingVersion);
    }

    /**
     * Unmarshalls underlying JAXWS {@link Message} using JAXB context of a configured RM version
     *
     * @return message content unmarshalled JAXB bean
     *
     * @throws com.sun.xml.ws.rm.RxException in case the message unmarshalling failed
     */
    protected final <T> T unmarshallMessage(@NotNull Message message) throws RxRuntimeException {
        assert message != null;

        try {
            @SuppressWarnings("unchecked") T result = (T) message.readPayloadAsJAXB(getJaxbUnmarshaller());
            return result;
        } catch (JAXBException e) {
            throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRM_1123_ERROR_UNMARSHALLING_MESSAGE(), e));
        }
    }
}
