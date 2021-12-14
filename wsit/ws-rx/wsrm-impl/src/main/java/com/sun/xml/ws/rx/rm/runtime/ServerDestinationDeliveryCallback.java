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

import com.oracle.webservices.oracle_internal_api.rm.InboundAccepted;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.commons.ha.HaContext;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.protocol.AcknowledgementData;
import com.sun.xml.ws.rx.rm.runtime.delivery.Postman;
import com.sun.xml.ws.rx.util.AbstractResponseHandler;
import java.util.concurrent.TimeUnit;

/**
 *
 */
class ServerDestinationDeliveryCallback implements Postman.Callback {

    private static class ResponseCallbackHandler extends AbstractResponseHandler implements Fiber.CompletionCallback {

        /**
         * The property with this key may be set by JCaps in the message context to indicate
         * whether the message that was delivered to the application endpoint should be
         * acknowledged or not.
         *
         * The property value may be "true" or "false", "true" is default.
         *
         * Introduction of this property is required as a temporary workaround for missing
         * concept of distinguishing between system and application errors in JAXWS RI.
         * The workaround should be removed once the missing concept is introduced.
         */
        private static final String RM_ACK_PROPERTY_KEY = "RM_ACK";
        //
        private final JaxwsApplicationMessage request;
        private final RuntimeContext rc;

        public ResponseCallbackHandler(JaxwsApplicationMessage request, RuntimeContext rc) {
            super(rc.suspendedFiberStorage, request.getCorrelationId());
            this.request = request;
            this.rc = rc;
        }

        @Override
        public void onCompletion(Packet response) {
            try {
                HaContext.initFrom(response);
                /*
                  This if clause is a part of the RM-JCaps private contract. JCaps may decide
                  that the request it received should be resent and thus it should not be acknowledged.

                  For more information, see documentation of RM_ACK_PROPERTY_KEY constant field.
                 */
                String rmAckPropertyValue = (String) response.invocationProperties.remove(RM_ACK_PROPERTY_KEY);
                if (rmAckPropertyValue == null || Boolean.parseBoolean(rmAckPropertyValue)) {
                    //mark request as acknowledged here if InboundAcceptedImpl is not in use
                    //internalRmFeatureExists means InboundAcceptedImpl is in use
                    boolean internalRmFeatureExists = (rc.configuration.getInternalRmFeature() != null);
                    if (!internalRmFeatureExists) {
                        rc.destinationMessageHandler.acknowledgeApplicationLayerDelivery(request);
                    }
                } else {
                    /*
                      Private contract between Metro RM and Sun JavaCAPS (BPM) team
                      to let them control the acknowledgement of the message.
                      Does not apply to anyone else.
                     */
                    LOGGER.finer(String.format("Value of the '%s' property is '%s'. The request has not been acknowledged.", RM_ACK_PROPERTY_KEY, rmAckPropertyValue));
                    RedeliveryTaskExecutor.deliverUsingCurrentThread(
                            request,
                            rc.configuration.getRmFeature().getRetransmissionBackoffAlgorithm().getDelayInMillis(request.getNextResendCount(), rc.configuration.getRmFeature().getMessageRetransmissionInterval()),
                            TimeUnit.MILLISECONDS,
                            rc.destinationMessageHandler);
                    return;
                }

                if (response.getMessage() == null) {
                    //was one-way request - create empty acknowledgement message if needed
                    AcknowledgementData ackData = rc.destinationMessageHandler.getAcknowledgementData(request.getSequenceId());
                    if (ackData.getAckReqestedSequenceId() != null || ackData.containsSequenceAcknowledgementData()) {
                        //create acknowledgement response only if there is something to send in the SequenceAcknowledgement header
                        response = rc.communicator.setEmptyResponseMessage(response, request.getPacket(), rc.rmVersion.protocolVersion.sequenceAcknowledgementAction);
                        rc.protocolHandler.appendAcknowledgementHeaders(response, ackData);
                    }

                    resumeParentFiber(response);
                } else {
                    //two-way, response message itself (bytes) is stored as part of registerMessage
                    //to support Microsoft replay
                    JaxwsApplicationMessage message =
                            new JaxwsApplicationMessage(response, getCorrelationId());
                    rc.sourceMessageHandler.registerMessage(message,
                            rc.getBoundSequenceId(request.getSequenceId()),
                            true);
                    rc.sourceMessageHandler.putToDeliveryQueue(message);
                }

                // TODO handle RM faults
            } catch (final Throwable t) {
                onCompletion(t);
            } finally {
                HaContext.clear();
            }
        }

        @Override
        public void onCompletion(Throwable error) {
            //Resume original Fiber with Throwable.
            //No retry attempts to send request to application layer.
            resumeParentFiber(error);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(ServerDestinationDeliveryCallback.class);
    private final RuntimeContext rc;

    public ServerDestinationDeliveryCallback(RuntimeContext rc) {
        this.rc = rc;
    }

    @Override
    public void deliver(ApplicationMessage message) {
        if (message instanceof JaxwsApplicationMessage) {
            deliver((JaxwsApplicationMessage) message);
        } else {
            throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRM_1141_UNEXPECTED_MESSAGE_CLASS(
                    message.getClass().getName(),
                    JaxwsApplicationMessage.class.getName())));
        }
    }

    private void deliver(JaxwsApplicationMessage message) {
        Fiber.CompletionCallback responseCallback = new ResponseCallbackHandler(message, rc);
        Packet request = message.getPacket().copy(true);

        boolean internalRmFeatureExists = (rc.configuration.getInternalRmFeature() != null);
        if (internalRmFeatureExists) {
            InboundAccepted inboundAccepted = new InboundAcceptedImpl(message, rc);
            request.addSatellite(inboundAccepted);
        }

        rc.communicator.sendAsync(request, responseCallback, null);
    }
    
    @Override
    public RuntimeContext getRuntimeContext() {
        return rc;
    }
}
