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
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.commons.xmlutil.Converter;
import com.sun.xml.ws.rx.message.RxMessage;
import com.sun.xml.ws.rx.message.jaxws.SerializableMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * JAX-WS specific application message
 *
 */
public class JaxwsApplicationMessage extends ApplicationMessageBase {
    public static class JaxwsApplicationMessageState implements RxMessage.State {

        private static final long serialVersionUID = 3877515996105255338L;
        private final String sequenceId;
        private final long messageNumber;
        private final int nextResendCount;
        private final String correlationId;
        private final String wsaAction;
        private final byte[] data;

        private JaxwsApplicationMessageState(JaxwsApplicationMessage message) {
            this.data = message.toBytes();
            this.nextResendCount = message.getNextResendCount();
            this.correlationId = message.getCorrelationId();
            this.wsaAction = message.getWsaAction();
            this.sequenceId = message.getSequenceId();
            this.messageNumber = message.getMessageNumber();
        }

        @Override
        public JaxwsApplicationMessage toMessage() {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            return JaxwsApplicationMessage.newInstance(bais, nextResendCount, correlationId, wsaAction, sequenceId, messageNumber);
            // closing ByteArrayInputStream has no effect, so ignoring the redundant call
        }

        @Override
        public String toString() {
            return "JaxwsApplicationMessageState" +
                    "{\n\tsequenceId=" + sequenceId +
                    ",\n\tmessageNumber=" + messageNumber +
                    ",\n\tnextResendCount=" + nextResendCount +
                    ",\n\tcorrelationId=" + correlationId +
                    ",\n\twsaAction=" + wsaAction +
                    ",\n\tmessage data=\n" + Converter.messageDataToString(data, Converter.UTF_8) +
                    "\n}";
        }
    }
    //
    private final SerializableMessage jaxwsMessage;

    public JaxwsApplicationMessage(@NotNull Packet packet, @NotNull String correlationId) {
        super(correlationId);

        assert packet != null;
        assert packet.getMessage() != null;

        this.jaxwsMessage = new SerializableMessage(packet, null);
    }

    private JaxwsApplicationMessage(@NotNull SerializableMessage jaxwsMessage, int initialResendCounterValue, @NotNull String correlationId, @NotNull String sequenceId, long messageNumber) {
        super(initialResendCounterValue, correlationId, sequenceId, messageNumber, null);

        this.jaxwsMessage = jaxwsMessage;
    }

    public
    @NotNull
    Message getJaxwsMessage() {
        return jaxwsMessage.getMessage();
    }

    public
    @NotNull
    Packet getPacket() {
        return jaxwsMessage.getPacket();
    }

    void setPacket(Packet newPacket) {
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

    @Override
    public JaxwsApplicationMessageState getState() {
        return new JaxwsApplicationMessageState(this);
    }

    public static JaxwsApplicationMessage newInstance(@NotNull InputStream dataStream, int initialResendCounterValue, @NotNull String correlationId, @NotNull String wsaAction, @NotNull String sequenceId, long messageNumber) {
        SerializableMessage jaxwsMessage = SerializableMessage.newInstance(dataStream, wsaAction);
        return new JaxwsApplicationMessage(jaxwsMessage, initialResendCounterValue, correlationId, sequenceId, messageNumber);
    }

    public static JaxwsApplicationMessage newInstance(@NotNull Packet packet, int initialResendCounterValue, @NotNull String correlationId, @NotNull String wsaAction, @NotNull String sequenceId, long messageNumber) {
        SerializableMessage jaxwsMessage = new SerializableMessage(packet, wsaAction);
        return new JaxwsApplicationMessage(jaxwsMessage, initialResendCounterValue, correlationId, sequenceId, messageNumber);
    }

    @Override
    public String toString() {
        String sb = "JAX-WS Application Message { " + "sequenceId=[ " + this.getSequenceId() + " ], " +
                "messageNumber=[ " + this.getMessageNumber() + " ], " +
                "correlationId=[ " + this.getCorrelationId() + " ], " +
                "nextResendCount=[ " + this.getNextResendCount() + " ], " +
                "wsaAction=[ " + this.jaxwsMessage.getWsaAction() +
                " ] }";
        return sb;
    }
}
