/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.rm.faults.AbstractSoapFaultException;
import com.sun.xml.ws.rx.rm.faults.AbstractSoapFaultException.Code;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.runtime.ApplicationMessage;
import com.sun.xml.ws.rx.rm.runtime.delivery.DeliveryQueueBuilder;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.AckRange;
import com.sun.xml.ws.rx.util.TimeSynchronizer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Outbound sequence implementation
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class OutboundSequence extends AbstractSequence {

    public static final long INITIAL_LAST_MESSAGE_ID = Sequence.MIN_MESSAGE_ID - 1;
    private static final Logger LOGGER = Logger.getLogger(OutboundSequence.class);

    public OutboundSequence(SequenceData data, DeliveryQueueBuilder deliveryQueueBuilder, TimeSynchronizer timeSynchronizer) {
        super(data, deliveryQueueBuilder, timeSynchronizer);
    }

    @Override
    public void registerMessage(ApplicationMessage message, boolean storeMessageFlag) throws DuplicateMessageRegistrationException, AbstractSoapFaultException {
        this.getState().verifyAcceptingMessageRegistration(getId(), Code.Sender);

        if (message.getSequenceId() != null) {
            throw new IllegalArgumentException(String.format(
                    "Cannot register message: Application message has been already registered on a sequence [ %s ].",
                    message.getSequenceId()));
        }

        message.setSequenceData(this.getId(), generateNextMessageId());
        if (storeMessageFlag) {
            data.attachMessageToUnackedMessageNumber(message);
        }
    }

    private long generateNextMessageId() throws MessageNumberRolloverException, IllegalStateException {
        long nextId = data.incrementAndGetLastMessageNumber(true);

        if (nextId > Sequence.MAX_MESSAGE_ID) {
            throw LOGGER.logSevereException(new MessageNumberRolloverException(getId(), nextId));
        }

        return nextId;
    }

    @Override
    public void acknowledgeMessageNumber(long messageId) {
        throw new UnsupportedOperationException(LocalizationMessages.WSRM_1101_UNSUPPORTED_OPERATION(this.getClass().getName()));
    }

    @Override
    public void acknowledgeMessageNumbers(List<AckRange> ranges) throws AbstractSoapFaultException {
        this.getState().verifyAcceptingAcknowledgement(getId(), Code.Sender);

        if (ranges == null || ranges.isEmpty()) {
            return;
        }

        AckRange.sort(ranges);

        // check proper bounds of acked ranges
        AckRange lastAckRange = ranges.get(ranges.size() - 1);
        if (data.getLastMessageNumber() < lastAckRange.upper) {
            throw new InvalidAcknowledgementException(this.getId(), lastAckRange.upper, ranges);
        }

        final Collection<Long> unackedMessageNumbers = data.getUnackedMessageNumbers();
        if (unackedMessageNumbers.isEmpty()) {
            // we have checked the ranges are ok and there's nothing to acknowledge.
            return;
        }

        // acknowledge messages
        Iterator<AckRange> rangeIterator = ranges.iterator();
        AckRange currentRange = rangeIterator.next();

        for (long unackedMessageNumber : unackedMessageNumbers) {
            if (unackedMessageNumber >= currentRange.lower && unackedMessageNumber <= currentRange.upper) {
                data.markAsAcknowledged(unackedMessageNumber);
            } else if (rangeIterator.hasNext()) {
                currentRange = rangeIterator.next();
            } else {
                break; // no more acked ranges
            }
        }

        this.getDeliveryQueue().onSequenceAcknowledgement();
    }
}
