/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.commons.DelayedTaskManager;
import com.sun.xml.ws.commons.DelayedTaskManager.DelayedTask;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class ClientAckRequesterTask implements DelayedTask {

    private static final Logger LOGGER = Logger.getLogger(ClientAckRequesterTask.class);
    //
    private final RuntimeContext rc;
    private final String outboundSequenceId;
    private final long acknowledgementRequestInterval;

    public ClientAckRequesterTask(RuntimeContext rc, String outboundSequenceId) {
        this.rc = rc;
        this.acknowledgementRequestInterval = rc.configuration.getRmFeature().getAckRequestTransmissionInterval();
        this.outboundSequenceId = outboundSequenceId;
    }

    public void run(DelayedTaskManager manager) {
        LOGGER.entering(outboundSequenceId);
        try {
            if (rc.communicator.isClosed()) {
                // Our communication channel has been closed - let the task die
                return;
            }

            if (rc.sequenceManager().isValid(outboundSequenceId)) {
                final Sequence sequence = rc.sequenceManager().getOutboundSequence(outboundSequenceId);
                if (!sequence.isClosed() && !sequence.isExpired()) {
                    try {
                        if (sequence.isStandaloneAcknowledgementRequestSchedulable(acknowledgementRequestInterval)) {
                            requestAcknowledgement();
                            sequence.updateLastAcknowledgementRequestTime();
                        }
                    } finally {
                        LOGGER.finer(String.format("Scheduling next run for an outbound sequence with id [ %s ]", outboundSequenceId));
                        manager.register(this, getExecutionDelay(), getExecutionDelayTimeUnit());
                    }
                }
            }
            // else sequence is no longer valid / ready to accept acknowledgements - let the task die
        } finally {
            LOGGER.exiting(outboundSequenceId);
        }
    }

    private void requestAcknowledgement() {
        Packet request = rc.communicator.createEmptyRequestPacket(rc.rmVersion.protocolVersion.ackRequestedAction, true);
        request.setIsProtocolMessage();
        if (rc.getUserStateID() != null) {
            request.setUserStateId(rc.getUserStateID());
        }

        JaxwsApplicationMessage requestMessage = new JaxwsApplicationMessage(
                request,
                request.getMessage().getID(rc.addressingVersion, rc.soapVersion));

        // setting sequence id and fake message number so source message handler can attach a proper sequence acknowledgement info
        requestMessage.setSequenceData(outboundSequenceId, 0);

        rc.sourceMessageHandler.attachAcknowledgementInfo(requestMessage);
        rc.protocolHandler.appendAcknowledgementHeaders(requestMessage.getPacket(), requestMessage.getAcknowledgementData());

        rc.communicator.sendAsync(request, new Fiber.CompletionCallback() {

            public void onCompletion(Packet response) {
                if (response == null || response.getMessage() == null) {
                    LOGGER.warning(LocalizationMessages.WSRM_1108_NULL_RESPONSE_FOR_ACK_REQUEST());
                    return;
                }

                try {
                    if (rc.protocolHandler.containsProtocolMessage(response)) {
                        LOGGER.finer("Processing RM protocol response message.");
                        JaxwsApplicationMessage message = new JaxwsApplicationMessage(response, "");
                        rc.protocolHandler.loadAcknowledgementData(message, message.getJaxwsMessage());

                        rc.destinationMessageHandler.processAcknowledgements(message.getAcknowledgementData());

                        rc.outboundDeliveredHandler.processAcknowledgements(message.getAcknowledgementData());
                    } else {
                        LOGGER.severe(LocalizationMessages.WSRM_1120_RESPONSE_NOT_IDENTIFIED_AS_PROTOCOL_MESSAGE());
                    }

                    if (response.getMessage().isFault()) {
                        LOGGER.warning(LocalizationMessages.WSRM_1109_SOAP_FAULT_RESPONSE_FOR_ACK_REQUEST());
                    }
                } finally {
                    response.getMessage().consume();
                }
            }

            public void onCompletion(Throwable error) {
                LOGGER.warning(LocalizationMessages.WSRM_1127_UNEXPECTED_EXCEPTION_WHEN_SENDING_ACK_REQUEST(), error);
            }
        });
    }

    public long getExecutionDelay() {
        return acknowledgementRequestInterval;
    }

    public TimeUnit getExecutionDelayTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    public String getName() {
        return "client acknowledgement requester task";
    }
}
