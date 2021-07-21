/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.message.jaxws;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.commons.xmlutil.Converter;
import com.sun.xml.ws.rx.message.RxMessage;
import com.sun.xml.ws.rx.message.RxMessageBase;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * JAX-WS specific application message
 *
 */
public class JaxwsMessage extends RxMessageBase {
    public static class JaxwsMessageState implements RxMessage.State {

        private final byte[] data;
        private final String wsaAction;
        private final String correlationId;

        private JaxwsMessageState(JaxwsMessage message) {
            this.data = message.toBytes();
            this.wsaAction = message.getWsaAction();
            this.correlationId = message.getCorrelationId();
        }

        public JaxwsMessage toMessage() {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            return JaxwsMessage.newInstance(bais,correlationId, wsaAction);
        }

        @Override
        public String toString() {
            return "JaxwsMessageState" +
                    "{\n\twsaAction=" + wsaAction +
                    ",\n\tcorrelationId=" + correlationId +
                    ",\n\tmessage data=\n" + Converter.messageDataToString(data, Converter.UTF_8) +
                    "\n}";
        }
    }
    //
    private final SerializableMessage jaxwsMessage;

    public JaxwsMessage(@NotNull Packet packet, @NotNull String correlationId) {
        super(correlationId);

        assert packet != null;
        assert packet.getMessage() != null;

        this.jaxwsMessage = new SerializableMessage(packet, null);
    }

    private JaxwsMessage(@NotNull SerializableMessage jaxwsMessage,@NotNull String correlationId) {
        super(correlationId);

        this.jaxwsMessage = jaxwsMessage;
    }

    public @NotNull Message getJaxwsMessage() {
        return jaxwsMessage.getMessage();
    }

    public @NotNull Packet getPacket() {
        return jaxwsMessage.getPacket();
    }

    public void setPacket(Packet newPacket) {
        // FIXME once this method is not needed, remove it and make packet attribute final
        jaxwsMessage.setPacket(newPacket);
    }

    @Override
    public byte[] toBytes() {
        return jaxwsMessage.toBytes();
    }

    /**
     * Returns WS-Addressing action header value - used in ServerTube as a workaround
     *
     * FIXME remove when no longer needed
     *
     * @return WS-Addressing action header value
     */
    public String getWsaAction() {
        return jaxwsMessage.getWsaAction();
    }

    public JaxwsMessageState getState() {
        return new JaxwsMessageState(this);
    }

    public static JaxwsMessage newInstance(@NotNull InputStream dataStream, @NotNull String correlationId, @NotNull String wsaAction) {
        SerializableMessage jaxwsMessage = SerializableMessage.newInstance(dataStream, wsaAction);
        return new JaxwsMessage(jaxwsMessage, correlationId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("JAX-WS Message { ");
        sb.append("correlationId=[ ").append(this.getCorrelationId()).append(" ], ");
        sb.append("wsaAction=[ ").append(this.jaxwsMessage.getWsaAction());
        sb.append(" ] }");
        return super.toString();
    }
}
