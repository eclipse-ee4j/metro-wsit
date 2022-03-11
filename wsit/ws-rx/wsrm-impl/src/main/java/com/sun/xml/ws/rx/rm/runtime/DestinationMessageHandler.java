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
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.faults.WsrmRequiredException;
import com.sun.xml.ws.rx.rm.protocol.AcknowledgementData;
import com.sun.xml.ws.rx.rm.runtime.sequence.DuplicateMessageRegistrationException;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.AckRange;
import com.sun.xml.ws.rx.rm.runtime.sequence.SequenceManager;
import com.sun.xml.ws.rx.rm.runtime.sequence.UnknownSequenceException;
import java.util.List;
import java.util.logging.Level;

/**
 * Handles incomming application messages. This class encapsulates
 * RM Source logic that is independent on of tha actual delivery mechanism
 * or framework (such as JAX-WS fibers).
 *
 */
class DestinationMessageHandler implements MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(DestinationMessageHandler.class);
    //
    private volatile SequenceManager sequenceManager;

    DestinationMessageHandler(@Nullable SequenceManager sequenceManager) {
        this.sequenceManager = sequenceManager;
    }

    void setSequenceManager(SequenceManager sequenceManager) {
        this.sequenceManager = sequenceManager;
    }

    /**
     * Registers incoming message with the given inbound sequence and
     * processes any acknowledgement information that the message carries.
     *
     * Once the message is registered and ack information processed, the message
     * is placed into a delivery queue and delivery callback is invoked
     */
    public void registerMessage(@NotNull ApplicationMessage inMessage, boolean storeMessage) throws DuplicateMessageRegistrationException, UnknownSequenceException, WsrmRequiredException {
        assert sequenceManager != null;
        assert inMessage != null;

        final String inboundSequenceId = inMessage.getSequenceId();
        if (inboundSequenceId == null) {
            throw new WsrmRequiredException();
        }
        final Sequence inboundSequence = sequenceManager.getInboundSequence(inboundSequenceId);

        // register and possibly store message in the unacked message sequence queue
        inboundSequence.registerMessage(inMessage, storeMessage);

        inboundSequence.setAckRequestedFlag(); // simulate acknowledgement request for new each message
    }

    public void processAcknowledgements(@Nullable AcknowledgementData acknowledgementData) throws UnknownSequenceException {
        processAcknowledgements(acknowledgementData, false);
    }

    void processAcknowledgements(@Nullable AcknowledgementData acknowledgementData, boolean doNotSetAckRequestedFlag) throws UnknownSequenceException {
        assert sequenceManager != null;

        if (acknowledgementData == null) {
            return;
        }

        if (acknowledgementData.getAcknowledgedSequenceId() != null) { // process outbound sequence acknowledgements
            final List<AckRange> acknowledgedRanges = acknowledgementData.getAcknowledgedRanges();
            if (!acknowledgedRanges.isEmpty()) {
                Sequence outboundSequence = sequenceManager.getOutboundSequence(acknowledgementData.getAcknowledgedSequenceId());
                if (!outboundSequence.isClosed()) { // we ignore acknowledgments on closed sequences
                    outboundSequence.acknowledgeMessageNumbers(acknowledgedRanges);
                }
            }
        }

        if (acknowledgementData.getAckReqestedSequenceId() != null && !doNotSetAckRequestedFlag) { // process inbound sequence ack requested flag
            final Sequence inboundSequence = sequenceManager.getInboundSequence(acknowledgementData.getAckReqestedSequenceId());
            inboundSequence.setAckRequestedFlag();
        }
    }

    /**
     * Retrieves acknowledgement information for a given outbound (and inbound) sequence
     *
     * @param inboundSequenceId inbound sequence identifier
     * @return acknowledgement information for a given outbound sequence
     * @throws UnknownSequenceException if no such sequence exits for a given sequence identifier
     */
    public AcknowledgementData getAcknowledgementData(String inboundSequenceId) throws UnknownSequenceException {
        return getAcknowledgementData(inboundSequenceId, false, false);
    }

    AcknowledgementData getAcknowledgementData(String inboundSequenceId, boolean isRespondingToAckRequested, boolean doNotClearAckRequestedFlag) throws UnknownSequenceException {
        assert sequenceManager != null;

        AcknowledgementData.Builder ackDataBuilder = AcknowledgementData.getBuilder();
        final Sequence inboundSequence = sequenceManager.getInboundSequence(inboundSequenceId);
        if (isRespondingToAckRequested || inboundSequence.isAckRequested() || inboundSequence.isClosed()) {
            ackDataBuilder.acknowledgements(inboundSequence.getId(), inboundSequence.getAcknowledgedMessageNumbers(), inboundSequence.isClosed());
            if (!doNotClearAckRequestedFlag) {
                inboundSequence.clearAckRequestedFlag();
            }
        }

        // outbound sequence ack requested flag
        Sequence outboundSequence = sequenceManager.getBoundSequence(inboundSequenceId);
        if (outboundSequence != null && outboundSequence.hasUnacknowledgedMessages()) {
            ackDataBuilder.ackReqestedSequenceId(outboundSequence.getId());
            outboundSequence.updateLastAcknowledgementRequestTime();
        }

        return ackDataBuilder.build();
    }

    public void acknowledgeApplicationLayerDelivery(ApplicationMessage inMessage) throws UnknownSequenceException {
        assert sequenceManager != null;

        sequenceManager.getInboundSequence(inMessage.getSequenceId()).acknowledgeMessageNumber(inMessage.getMessageNumber());
    }

    @Override
    public void putToDeliveryQueue(ApplicationMessage message) throws RxRuntimeException {
        assert sequenceManager != null;

        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(String.format("Putting a message with number [ %d ] to the delivery queue of a sequence [ %s ]", message.getMessageNumber(), message.getSequenceId()));
        }

        sequenceManager.getInboundSequence(message.getSequenceId()).getDeliveryQueue().put(message);
    }
}
