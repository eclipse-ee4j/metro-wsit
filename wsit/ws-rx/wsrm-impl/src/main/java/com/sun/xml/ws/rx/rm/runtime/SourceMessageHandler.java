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
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.protocol.AcknowledgementData;
import com.sun.xml.ws.rx.rm.runtime.sequence.DuplicateMessageRegistrationException;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.runtime.sequence.SequenceManager;
import com.sun.xml.ws.rx.rm.runtime.sequence.UnknownSequenceException;
import java.util.logging.Level;

/**
 * Handles outgoing application messages. This class encapsulates 
 * RM Source logic that is independent on of the actual delivery mechanism
 * or framework (such as JAX-WS fibers).
 *
 */
class SourceMessageHandler implements MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(SourceMessageHandler.class);

    private volatile SequenceManager sequenceManager;

    SourceMessageHandler(@Nullable SequenceManager sequenceManager) {
        this.sequenceManager = sequenceManager;
    }

    void setSequenceManager(SequenceManager sequenceManager) {
        this.sequenceManager = sequenceManager;
    }
    /**
     * Registers outgoing message with the provided outbound sequence and
     * sets sequenceId and messageNumber properties on the outgoing message.
     *
     * Once the message is registered and properties are set, the message is placed into
     * a delivery queue and delivery callback is invoked.
     *
     * @throws UnknownSequenceException if no such sequence exits for a given sequence identifier
     */
    public void registerMessage(@NotNull ApplicationMessage outMessage,
            @NotNull String outboundSequenceId, boolean storeMessage)
            throws DuplicateMessageRegistrationException,
            UnknownSequenceException {
        assert sequenceManager != null;
        assert outMessage != null;
        assert outboundSequenceId != null;

        final Sequence outboundSequence = sequenceManager.getOutboundSequence(outboundSequenceId);
        outboundSequence.registerMessage(outMessage, storeMessage);
    }

    /**
     * Attaches RM acknowledgement information such as inbound sequence acknowledgements
     * (if there is an inbound sequence bound to the specified outbound sequence) or
     * outbound sequence acknowledgement requested flag to the outgoing message.
     *
     * @throws UnknownSequenceException if no such sequence exits for a given sequence identifier
     */
    public void attachAcknowledgementInfo(@NotNull ApplicationMessage outMessage) throws UnknownSequenceException {
        assert sequenceManager != null;
        assert outMessage != null;
        assert outMessage.getSequenceId() != null;

        // inbound sequence acknowledgements
        outMessage.setAcknowledgementData(getAcknowledgementData(outMessage.getSequenceId()));
    }

    /**
     * Retrieves acknowledgement information for a given outbound (and inbound) sequence
     *
     * @param outboundSequenceId outbound sequence identifier
     * @return acknowledgement information for a given outbound sequence
     * @throws UnknownSequenceException if no such sequence exits for a given sequence identifier
     */
    public AcknowledgementData getAcknowledgementData(String outboundSequenceId) throws UnknownSequenceException {
        assert sequenceManager != null;

        AcknowledgementData.Builder ackDataBuilder = AcknowledgementData.getBuilder();
        Sequence inboundSequence = sequenceManager.getBoundSequence(outboundSequenceId);
        if (inboundSequence != null) {
            /**
             * If inbound sequence exists, we are not checking if inboundSequence.isAckRequested() is true.
             * Instead, we are allways attaching inbound sequence acknowledegements (even if not requested by the other side)
             * This is to avoid potential locks in InOrder delivery/redelivery scenarios.
             *
             * For example, following could happen on the client side with InOrder enabled
             * if we strictly checked for inboundSequence.isAckRequested() to be true:
             *
             * 0. response to a previous client request arrives, endpoint is waiting for an acknowledgement
             * 1. client request is put to delivery queue
             * 2. acknowledgements are attached to the client request and ackRequested flag is cleared on inbound sequence
             * 3. client request gets lost.
             * 4. client request is scheduled for a resend
             * 5. client request is put to delivery queue
             * 6. this time, ackRequested flag is clear, so we will not append any acknowledgements
             * 7. client request is processed on the endpoint and response is put to the endpoint's source delivery queue
             * 8. since there was no acknowledgement of the previous response, the new response is blocekd in the delivery queue forever
             *
             * After step 8., communication between client and endpoint might freeze in a deadlock unless
             * another means of communicating the sequence acknowledgements from client to the endpoint 
             * are established.
             */

            ackDataBuilder.acknowledgements(inboundSequence.getId(), inboundSequence.getAcknowledgedMessageNumbers(), inboundSequence.isClosed());
            inboundSequence.clearAckRequestedFlag();
        }
        // outbound sequence ack requested flag
        final Sequence outboundSequence = sequenceManager.getOutboundSequence(outboundSequenceId);
        if (outboundSequence.hasUnacknowledgedMessages()) {
            ackDataBuilder.ackReqestedSequenceId(outboundSequenceId);
            outboundSequence.updateLastAcknowledgementRequestTime();
        }
        final AcknowledgementData acknowledgementData = ackDataBuilder.build();
        return acknowledgementData;
    }

    public void putToDeliveryQueue(ApplicationMessage message) throws RxRuntimeException {
        assert sequenceManager != null;

        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(String.format("Putting a message with number [ %d ] to the delivery queue of a sequence [ %s ]", message.getMessageNumber(), message.getSequenceId()));
        }
        sequenceManager.getOutboundSequence(message.getSequenceId()).getDeliveryQueue().put(message);
    }
}
