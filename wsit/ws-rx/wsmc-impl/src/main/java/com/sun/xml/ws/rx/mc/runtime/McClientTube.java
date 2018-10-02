/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.runtime;

import com.sun.xml.ws.rx.mc.dev.WsmcRuntimeProvider;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.api.server.Container;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.mc.localization.LocalizationMessages;
import com.sun.xml.ws.rx.mc.dev.ProtocolMessageHandler;
import com.sun.xml.ws.rx.util.Communicator;
import com.sun.xml.ws.rx.util.SuspendedFiberStorage;
import java.util.UUID;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class McClientTube extends AbstractFilterTubeImpl implements WsmcRuntimeProvider {
    //

    private static final Logger LOGGER = Logger.getLogger(McClientTube.class);
    //
    private final McConfiguration configuration;
    private final Header wsmcAnnonymousReplyToHeader;
    private final Header wsmcAnnonymousFaultToHeader;
    private final Communicator communicator;
    private final SuspendedFiberStorage suspendedFiberStorage;
    private final MakeConnectionSenderTask mcSenderTask;
    private final WSEndpointReference wsmcAnonymousEndpointReference;

    McClientTube(McConfiguration configuration, Tube tubelineHead, Container container) throws RxRuntimeException {
        super(tubelineHead);

        this.configuration = configuration;

        this.communicator = Communicator.builder("mc-client-tube-communicator")
                .tubelineHead(super.next)
                .addressingVersion(configuration.getAddressingVersion())
                .soapVersion(configuration.getSoapVersion())
                .jaxbContext(configuration.getRuntimeVersion().getJaxbContext(configuration.getAddressingVersion()))
                .container(container)
                .build();
        
        final String wsmcAnonymousAddress = configuration.getRuntimeVersion().getAnonymousAddress(UUID.randomUUID().toString());
        this.wsmcAnonymousEndpointReference = new WSEndpointReference(wsmcAnonymousAddress, configuration.getAddressingVersion());
        this.wsmcAnnonymousReplyToHeader = wsmcAnonymousEndpointReference.createHeader(configuration.getAddressingVersion().replyToTag);
        this.wsmcAnnonymousFaultToHeader = wsmcAnonymousEndpointReference.createHeader(configuration.getAddressingVersion().faultToTag);

        this.suspendedFiberStorage = new SuspendedFiberStorage();
        this.mcSenderTask = new MakeConnectionSenderTask(
                communicator,
                suspendedFiberStorage,
                wsmcAnonymousAddress,
                wsmcAnnonymousReplyToHeader,
                wsmcAnnonymousFaultToHeader,
                configuration);
    }

    McClientTube(McClientTube original, TubeCloner cloner) {
        super(original, cloner);

        this.configuration = original.configuration;

        this.wsmcAnnonymousReplyToHeader = original.wsmcAnnonymousReplyToHeader;
        this.wsmcAnnonymousFaultToHeader = original.wsmcAnnonymousFaultToHeader;
        this.communicator = original.communicator;
        this.suspendedFiberStorage = original.suspendedFiberStorage;
        this.mcSenderTask = original.mcSenderTask;

        this.wsmcAnonymousEndpointReference = original.wsmcAnonymousEndpointReference;
    }

    @Override
    public AbstractTubeImpl copy(TubeCloner cloner) {
        LOGGER.entering();
        try {
            return new McClientTube(this, cloner);
        } finally {
            LOGGER.exiting();
        }
    }

    @Override
    public NextAction processRequest(Packet request) {
        if (!mcSenderTask.isRunning() && !mcSenderTask.wasShutdown()) { // first packet
            communicator.setDestinationAddressFrom(request);  // setting actual destination endpoint
            mcSenderTask.start(); // starting the McSenderTask
        }

        final Message message = request.getMessage();
        if (!message.hasHeaders()) {
            throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSMC_0102_NO_SOAP_HEADERS()));
        }


        if (needToSetWsmcAnnonymousHeaders(request)) { // annonymous request
            setMcAnnonymousHeaders(
                    message.getHeaders(),
                    configuration.getAddressingVersion(),
                    wsmcAnnonymousReplyToHeader,
                    wsmcAnnonymousFaultToHeader);

            String correlationId = message.getID(configuration.getAddressingVersion(), configuration.getSoapVersion());
            Fiber.CompletionCallback responseHandler;
            if (request.expectReply != null && request.expectReply) { // most likely Req-Resp MEP
                responseHandler = new RequestResponseMepHandler(configuration, mcSenderTask, suspendedFiberStorage, correlationId);
            } else { // most likely One-Way MEP
                responseHandler = new OneWayMepHandler(configuration, mcSenderTask, suspendedFiberStorage, correlationId);
            }

            suspendedFiberStorage.register(correlationId, Fiber.current());
            communicator.sendAsync(request, responseHandler);

            return super.doSuspend();
        } else { // not annonymous request - don't care
            return super.processRequest(request);
        }
    }

    @Override
    public NextAction processResponse(Packet response) {
        return super.processResponse(response);
    }

    @Override
    public NextAction processException(Throwable t) {
        return super.processException(t);
    }

    @Override
    public void preDestroy() {
        mcSenderTask.shutdown();
        communicator.close();

        super.preDestroy();
    }

    /**
     *  @see WsmcRuntimeProvider#getWsmcAnonymousEndpointReference()
     */
    public final WSEndpointReference getWsmcAnonymousEndpointReference() {
        return wsmcAnonymousEndpointReference;
    }

    /**
     *  @see WsmcRuntimeProvider#registerProtocolMessageHandler(com.sun.xml.ws.rx.mc.dev.ProtocolMessageHandler) 
     */
    public final void registerProtocolMessageHandler(ProtocolMessageHandler handler) {
        mcSenderTask.register(handler);
    }

    /**
     * Method check if the WS-A {@code ReplyTo} header is present or not. If it is not present, or if it is present but is annonymous,
     * method returns true. If the WS-A {@code ReplyTo} header is present and non-annonymous, mehod returns false.
     */
    private boolean needToSetWsmcAnnonymousHeaders(final Packet request) {
        Header replyToHeader = request.getMessage().getHeaders().get(configuration.getAddressingVersion().replyToTag, false);
        if (replyToHeader != null) {
            try {
                return replyToHeader.readAsEPR(configuration.getAddressingVersion()).isAnonymous();
            } catch (XMLStreamException ex) {
                throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSMC_0103_ERROR_RETRIEVING_WSA_REPLYTO_CONTENT(), ex));
            }
        }

        // this request seems to be one-way, need to check if there are is an RM AckRequest set on it.
        // FIXME: this should be made in a RM-agnostic way
        return isBooleanFlagSet(request, McConfiguration.ACK_REQUESTED_HEADER_SET);
    }

    static void setMcAnnonymousHeaders(final MessageHeaders headers, AddressingVersion av, Header wsmcReplyToHeader, Header wsmcFaultToHeader) {
        headers.remove(av.replyToTag);
        headers.add(wsmcReplyToHeader);

        if (headers.remove(av.faultToTag) != null) {
            headers.add(wsmcFaultToHeader);
        }

    }

    private Boolean isBooleanFlagSet(Packet packet, String flag) {
        Boolean value = Boolean.class.cast(packet.invocationProperties.get(flag));
        return value != null && value.booleanValue();
    }
}
