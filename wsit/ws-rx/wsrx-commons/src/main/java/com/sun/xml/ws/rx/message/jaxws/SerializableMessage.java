/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.message.jaxws;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.commons.xmlutil.Converter;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.localization.LocalizationMessages;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class SerializableMessage {

    private static final Logger LOGGER = Logger.getLogger(SerializableMessage.class);

    private @Nullable Packet packet;
    private @NotNull final Message message;
    private @Nullable final String wsaAction;

    public SerializableMessage(Packet packet, String wsaAction) {
        this.packet = packet;
        this.message = packet.getMessage();
        this.wsaAction = wsaAction;
    }

    public SerializableMessage(Message message, String wsaAction) {
        assert message != null;

        this.packet = null;
        this.message = message;
        this.wsaAction = wsaAction;
    }

    public Message getMessage() {
        return message;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet newPacket) {
        newPacket.setMessage(message);
        this.packet = newPacket;
    }

    public String getWsaAction() {
        return wsaAction;
    }

    public byte[] toBytes() {
        try {
            return Converter.toBytes(message.copy(), Converter.UTF_8);
        } catch (XMLStreamException ex) {
            throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRX_1001_UNABLE_TO_SERIALIZE_MSG_TO_XML_STREAM(), ex));
        }
    }

    public static SerializableMessage newInstance(@NotNull InputStream dataStream, String wsaAction) {
        Message m;
        try {
            m = Converter.toMessage(dataStream, Converter.UTF_8);
        } catch (XMLStreamException ex) {
            throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRX_1002_UNABLE_TO_DESERIALIZE_MSG_FROM_XML_STREAM(), ex));
        }
        return new SerializableMessage(m, wsaAction);
    }
}
