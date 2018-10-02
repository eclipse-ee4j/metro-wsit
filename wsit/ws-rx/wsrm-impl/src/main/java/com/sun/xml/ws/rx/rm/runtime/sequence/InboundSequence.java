/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.rm.faults.AbstractSoapFaultException.Code;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.runtime.ApplicationMessage;
import com.sun.xml.ws.rx.rm.runtime.delivery.DeliveryQueueBuilder;
import com.sun.xml.ws.rx.util.TimeSynchronizer;
import java.util.List;

/**
 * Inbound sequence implementation
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class InboundSequence extends AbstractSequence {
    private static final Logger LOGGER = Logger.getLogger(InboundSequence.class);
    public static final long INITIAL_LAST_MESSAGE_ID = Sequence.UNSPECIFIED_MESSAGE_ID;

    public InboundSequence(SequenceData data, DeliveryQueueBuilder deliveryQueueBuilder, TimeSynchronizer timeSynchronizer) {
        super(data, deliveryQueueBuilder, timeSynchronizer);
    }

    public void registerMessage(ApplicationMessage message, boolean storeMessageFlag) throws DuplicateMessageRegistrationException, IllegalStateException {
        this.getState().verifyAcceptingMessageRegistration(getId(), Code.Receiver);

        if (!this.getId().equals(message.getSequenceId())) {
            throw LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSRM_1149_DIFFERENT_MSG_SEQUENCE_ID(
                    message.getSequenceId(),
                    this.getId())));
        }

        data.registerReceivedUnackedMessageNumber(message.getMessageNumber());
        if (storeMessageFlag) {
            data.attachMessageToUnackedMessageNumber(message);
        }
    }

    public void acknowledgeMessageNumbers(List<AckRange> ranges) {
        throw new UnsupportedOperationException(String.format("This operation is not supported on %s class", this.getClass().getName()));
    }

    public void acknowledgeMessageNumber(long messageId) throws IllegalStateException {
        this.getState().verifyAcceptingAcknowledgement(getId(), Code.Receiver);

        data.markAsAcknowledged(messageId);

        this.getDeliveryQueue().onSequenceAcknowledgement();
    }
}
