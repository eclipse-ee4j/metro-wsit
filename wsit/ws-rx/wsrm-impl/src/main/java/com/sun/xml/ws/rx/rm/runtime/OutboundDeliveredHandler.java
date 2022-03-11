/*
 * Copyright (c) 2013, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import com.oracle.webservices.oracle_internal_api.rm.OutboundDelivered;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.rm.protocol.AcknowledgementData;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.AckRange;

/**
 * Handles OutboundDelivered object that comes with the application
 * request message at the ClientTube. Provides methods that store,
 * retrieve and remove OutboundDelivered to/from a Map.
 *
 */
class OutboundDeliveredHandler {
    private static final Logger LOGGER = Logger.getLogger(OutboundDeliveredHandler.class);

    /*
     * Key class for the Map.
     */
    private static class MessageInfo {
        private final String sequenceId;
        private final long messageNumber;

        private MessageInfo(String seqId, long msgNumber) {
            this.sequenceId = seqId;
            this.messageNumber = msgNumber;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + (int) (messageNumber ^ (messageNumber >>> 32));
            result = prime * result
                    + ((sequenceId == null) ? 0 : sequenceId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MessageInfo other = (MessageInfo) obj;
            if (messageNumber != other.messageNumber)
                return false;
            if (sequenceId == null) {
                return other.sequenceId == null;
            } else return sequenceId.equals(other.sequenceId);
        }
    }

    private ConcurrentHashMap<MessageInfo, OutboundDelivered> map =
            new ConcurrentHashMap<>();

    /**
     * Store OutboundDelivered in a map for later retrieval
     * (keyed by sequenceId+messageNumber).
     * @param sequenceId Sequence ID
     * @param messageNumber Message number
     * @param outboundDelivered OutboundDelivered to put away
     */
    void store(String sequenceId, long messageNumber, OutboundDelivered outboundDelivered) {
        MessageInfo messageInfo = new MessageInfo(sequenceId, messageNumber);
        map.put(messageInfo, outboundDelivered);
    }

    /**
     * Retrieve OutboundDelivered that was put away in store.
     * @param sequenceId Sequence ID
     * @param messageNumber Message number
     * @return OutboundDelivered that was put away before using add
     */
    OutboundDelivered retrieve(String sequenceId, long messageNumber) {
        MessageInfo messageInfo = new MessageInfo(sequenceId, messageNumber);
        return map.get(messageInfo);
    }

    /**
     * Remove OutboundDelivered once it is used so that it is not
     * found again for use.
     * @param sequenceId Sequence ID
     * @param messageNumber Message number
     */
    void remove(String sequenceId, long messageNumber) {
        MessageInfo messageInfo = new MessageInfo(sequenceId, messageNumber);
        map.remove(messageInfo);
    }

    /**
     * Looks at all the ack'ed message numbers for the sequence and
     * invokes OutboundDelivered.setDelivered(true) as appropriate.
     * @param acknowledgementData AcknowledgementData to be processed
     */
    void processAcknowledgements(AcknowledgementData acknowledgementData) {
        String seqId = acknowledgementData.getAcknowledgedSequenceId();
        final List<AckRange> listOfAckRange = acknowledgementData.getAcknowledgedRanges();
        for (AckRange ackRange : listOfAckRange) {
            List<Long> messageNumbers = ackRange.rangeValues();
            for(long messageNumber : messageNumbers) {
                OutboundDelivered outboundDelivered = retrieve(seqId, messageNumber);
                if (outboundDelivered != null) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("Invoking outboundDelivered.setDelivered(true) for " +
                                "seq id:"+seqId+" and " +
                                "message number:"+messageNumber);
                    }
                    outboundDelivered.setDelivered(Boolean.TRUE);
                    remove(seqId, messageNumber);
                }
            }
        }
    }
}
