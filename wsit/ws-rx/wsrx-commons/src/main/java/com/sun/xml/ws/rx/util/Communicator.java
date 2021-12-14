/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.util;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import org.glassfish.jaxb.runtime.api.JAXBRIContext;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.message.RelatesToHeader;
import com.sun.xml.ws.security.secconv.SecureConversationInitiator;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;

/**
 * Transmits standalone protocol messages over the wire. Provides also some additional
 * utility methods for creating and unmarshalling JAXWS {@link Message} and {@link Header}
 * objects.
 *
 * <b>
 * WARNING: This class is a private utility class used by WS-RX implementation.
 * Any usage outside the intended scope is strongly discouraged. The API exposed
 * by this class may be changed, replaced or removed without any advance notice.
 * </b>
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public final class Communicator {

    public static final class Builder {

        private final String name;
        private Tube tubelineHead;
        private SecureConversationInitiator scInitiator;
        private AddressingVersion addressingVersion = AddressingVersion.W3C;
        private SOAPVersion soapVersion = SOAPVersion.SOAP_12;
        private JAXBRIContext jaxbContext;
        private Container container;

        private Builder(String name) {
            this.name = name;
        }

        public Builder container(final Container value) {
            this.container = value;

            return this;
        }

        public Builder addressingVersion(AddressingVersion value) {
            this.addressingVersion = value;

            return this;
        }

        public Builder jaxbContext(JAXBRIContext value) {
            this.jaxbContext = value;

            return this;
        }

        public Builder secureConversationInitiator(SecureConversationInitiator value) {
            this.scInitiator = value;

            return this;
        }

        public Builder soapVersion(SOAPVersion value) {
            this.soapVersion = value;

            return this;
        }

        public Builder tubelineHead(Tube value) {
            this.tubelineHead = value;

            return this;
        }

        public Communicator build() {
            if (tubelineHead == null) {
                throw new IllegalStateException("Cannot create communicator instance: tubeline head has not been set.");
            }
            if (jaxbContext == null) {
                throw new IllegalStateException("Cannot create communicator instance: JAXB context has not been set.");
            }

            return new Communicator(name, tubelineHead, scInitiator, addressingVersion, soapVersion, jaxbContext, container);
        }
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }
    // TODO P2 introduce an inner builder class
    private static final Logger LOGGER = Logger.getLogger(Communicator.class);
    public final QName soapMustUnderstandAttributeName;
    //
    private final SecureConversationInitiator scInitiator;
    //
    private final AddressingVersion addressingVersion;
    private final SOAPVersion soapVersion;
    private final JAXBRIContext jaxbContext;
    private final Container container;
    //
    private BindingProvider proxy;
    private FiberExecutor fiberExecutor;
    private volatile EndpointAddress destinationAddress;

    private Communicator(
            @NotNull String name,
            @NotNull Tube tubeline,
            @Nullable SecureConversationInitiator scInitiator,
            @NotNull AddressingVersion addressingVersion,
            @NotNull SOAPVersion soapVersion,
            @NotNull JAXBRIContext jaxbContext,
            @NotNull Container container) {
        this.destinationAddress = null;
        this.fiberExecutor = new FiberExecutor(name, tubeline);
        this.scInitiator = scInitiator;
        this.addressingVersion = addressingVersion;
        this.soapVersion = soapVersion;
        this.soapMustUnderstandAttributeName = new QName(soapVersion.nsUri, "mustUnderstand");
        this.jaxbContext = jaxbContext;
        this.container = container;
    }

    public Packet createRequestPacket(Object jaxbElement, String wsaAction, boolean expectReply) {
        Message message = Messages.create(jaxbContext, jaxbElement, soapVersion);

        return createRequestPacket(message, wsaAction, expectReply);
    }

    public Packet createRequestPacket(Message message, String wsaAction, boolean expectReply) {
        if (destinationAddress == null) {
            throw new IllegalStateException("Destination address is not defined in this communicator instance");
        }

        final Packet packet = createRequestPacket(message, expectReply);

        AddressingUtils.fillRequestAddressingHeaders(
                message.getHeaders(),
                packet,
                addressingVersion,
                soapVersion,
                !expectReply,
                wsaAction);

        return packet;
    }

    public Packet createRequestPacket(Packet originalRequestPacket, Object jaxbElement, String wsaAction, boolean expectReply) {
        if (originalRequestPacket != null) { // server side request transferred as a response
            Packet request = createResponsePacket(originalRequestPacket, jaxbElement, wsaAction, false);

            final MessageHeaders requestHeaders = request.getMessage().getHeaders();
            if (expectReply) { // attach wsa:ReplyTo header from the original request
                final String endpointAddress = AddressingUtils.getTo(originalRequestPacket.getMessage().getHeaders(), addressingVersion, soapVersion);
                requestHeaders.add(createReplyToHeader(endpointAddress));
            }
            requestHeaders.remove(addressingVersion.relatesToTag);

            return request;
        } else {
            Message message = Messages.create(jaxbContext, jaxbElement, soapVersion);
            return createRequestPacket(message, wsaAction, expectReply);
        }
    }

    private Header createReplyToHeader(String address) {
        WSEndpointReference wsepr = new WSEndpointReference(address, addressingVersion);
        return wsepr.createHeader(addressingVersion.replyToTag);
    }

    /**
     * Creates a new empty request packet
     *
     * @return a new empty request packet
     */
    public Packet createEmptyRequestPacket(boolean expectReply) {
        if (destinationAddress == null) {
            throw new IllegalStateException("Destination address is not defined in this communicator instance");
        }

        return createRequestPacket(null, expectReply);
    }

    /**
     * Creates a new empty request packet with an empty message that has WS-A action set
     *
     * @return a new empty request packet
     */
    public Packet createEmptyRequestPacket(String requestWsaAction, boolean expectReply) {
        return createRequestPacket(Messages.createEmpty(soapVersion), requestWsaAction, expectReply);
    }

    /**
     * Creates new response packet based for the supplied request packet
     * with the provided response WS-Addressing action set.
     *
     * @param requestPacket original request the newly created response belongs to
     * @param responseWsaAction WS-Addressing action header value to be set
     * @param isClientResponse determines whether the response is technically a client request
     *
     * @return newly created response packet
     */
    public Packet createResponsePacket(@NotNull Packet requestPacket, Object jaxbElement, String responseWsaAction, boolean isClientResponse) {
        if (!isClientResponse) { // server side response
            return requestPacket.createServerResponse(
                    Messages.create(jaxbContext, jaxbElement, soapVersion),
                    addressingVersion,
                    soapVersion,
                    responseWsaAction);
        } else { // client side response transferred as a request
            Packet response = createRequestPacket(jaxbElement, responseWsaAction, false);
            response.getMessage().getHeaders().add(new RelatesToHeader(
                    addressingVersion.relatesToTag,
                    AddressingUtils.getMessageID(requestPacket.getMessage().getHeaders(), addressingVersion, soapVersion)));
            return response;
        }
    }

    /**
     * Creates new response packet based for the supplied request packet
     * with the provided response WS-Addressing action set.
     *
     * @param requestPacket original request the newly created response belongs to
     * @param responseWsaAction WS-Addressing action header value to be set
     *
     * @return newly created response packet
     */
    public Packet createResponsePacket(Packet requestPacket, Message message, String responseWsaAction) {
        if (requestPacket != null) { // server side response
            return requestPacket.createServerResponse(
                    message,
                    addressingVersion,
                    soapVersion,
                    responseWsaAction);
        } else { // client side response transferred as a request
            return createRequestPacket(message, responseWsaAction, false);
        }
    }

    /**
     * Creates an empty (no SOAP body payload) new response packet based for the
     * supplied request packet with the provided response WS-Addressing action set.
     *
     * @param requestPacket original request the newly created response belongs to
     * @param responseWsaAction WS-Addressing action header value to be set
     *
     * @return newly created empty (no SOAP body payload) response packet
     */
    public Packet createEmptyResponsePacket(Packet requestPacket, String responseWsaAction) {
        if (requestPacket != null) { // server side response
            return requestPacket.createServerResponse(
                    Messages.createEmpty(soapVersion),
                    addressingVersion,
                    soapVersion,
                    responseWsaAction);
        } else { // client side response transferred as a request
            return createEmptyRequestPacket(responseWsaAction, false);
        }
    }

    /**
     * Creates a null (no message) response packet based for the supplied request packet.
     *
     * @param requestPacket original request the newly created response belongs to
     *
     * @return newly created null (no message) response packet
     */
    public Packet createNullResponsePacket(Packet requestPacket) {
        if (requestPacket.transportBackChannel != null) {
            requestPacket.transportBackChannel.close();
        }

        final Packet packet = createPacket(null);
        packet.invocationProperties.putAll(requestPacket.invocationProperties);
        return packet;
    }

    private Packet createPacket(final Message message) {
        final Packet packet = message == null ? new Packet() : new Packet(message);
        packet.component = container;
        packet.proxy = proxy;
        return packet;
    }

    private Packet createRequestPacket(final Message message, final boolean expectReply) {
        final Packet packet = createPacket(message);
        packet.setState(Packet.State.ClientRequest);
        packet.endpointAddress = destinationAddress;
        packet.expectReply = expectReply;
        return packet;
    }

    /**
     * Creates a new JAX-WS {@link Message} object that doesn't have any payload
     * and sets it as the current packet content as a request message.
     *
     * @param wsaAction WS-Addressing action header to set
     *
     * @return the updated {@link Packet} instance
     */
    public Packet setEmptyRequestMessage(Packet request, String wsaAction) {
        Message message = Messages.createEmpty(soapVersion);
        request.setMessage(message);
        AddressingUtils.fillRequestAddressingHeaders(
                message.getHeaders(),
                request,
                addressingVersion,
                soapVersion,
                false,
                wsaAction);


        return request;
    }

    /**
     * Overwrites the {@link Message} of the response packet with a newly created empty {@link Message} instance.
     * Unlike {@link Packet#setMessage(Message)}, this method fills in the {@link Message}'s WS-Addressing headers
     * correctly, based on the provided request packet WS-Addressing headers.
     *
     */
    public Packet setEmptyResponseMessage(Packet response, Packet request, String wsaAction) {
        Message message = Messages.createEmpty(soapVersion);
        response.setResponseMessage(request, message, addressingVersion, soapVersion, wsaAction);
        return response;
    }

    /**
     * Returns the value of WS-Addressing {@code Action} header of a message stored
     * in the {@link Packet}.
     *
     * @param packet JAX-WS RI packet
     * @return Value of WS-Addressing {@code Action} header, {@code null} if the header is not present
     */
    public String getWsaAction(Packet packet) {
        if (packet == null || packet.getMessage() == null) {
            return null;
        }

        return AddressingUtils.getAction(packet.getMessage().getHeaders(), addressingVersion, soapVersion);
    }

    /**
     * Returns the value of WS-Addressing {@code To} header of a message stored
     * in the {@link Packet}.
     *
     * @param packet JAX-WS RI packet
     * @return Value of WS-Addressing {@code To} header, {@code null} if the header is not present
     */
    public String getWsaTo(Packet packet) {
        if (packet == null || packet.getMessage() == null) {
            return null;
        }

        return AddressingUtils.getTo(packet.getMessage().getHeaders(), addressingVersion, soapVersion);
    }

    /**
     * If security is enabled, tries to initate secured conversation and obtain the security token reference.
     *
     * @return security token reference of the initiated secured conversation, or {@code null} if there is no SC configured
     */
    public SecurityTokenReferenceType tryStartSecureConversation(Packet request) throws WSTrustException {
        if (scInitiator == null) {
            return null;
        }

        Packet emptyPacket = createEmptyRequestPacket(false);
        emptyPacket.invocationProperties.putAll(request.invocationProperties);

        @SuppressWarnings("unchecked")
        JAXBElement<SecurityTokenReferenceType> strElement = scInitiator.startSecureConversation(emptyPacket);

        return (strElement != null) ? strElement.getValue() : null;
    }

    /**
     * Sends the request {@link Packet} and returns the corresponding response {@link Packet}.
     * This method should be used for Req-Res MEP
     *
     * @param request {@link Packet} containing the message to be send
     * @return response {@link Message} wrapped in a response {@link Packet} received
     */
    public Packet send(@NotNull Packet request) {
        if (fiberExecutor == null) {
            LOGGER.fine("Cannot send messages: this Communicator instance has been closed");
            return null;
        }

        return fiberExecutor.runSync(request);
    }

    /**
     * Asynchronously sends the request {@link Packet}
     *
     * @param request {@link Packet} containing the message to be send
     * @param completionCallbackHandler completion callback handler to process the response.
     *        May be {@code null}. In such case a generic completion callback handler will be used.
     */
    public void sendAsync(@NotNull final Packet request,
            @Nullable final Fiber.CompletionCallback completionCallbackHandler) {
        sendAsync(request, completionCallbackHandler, null);
    }

    public void sendAsync(@NotNull final Packet request,
            @Nullable final Fiber.CompletionCallback completionCallbackHandler,
            @Nullable FiberContextSwitchInterceptor interceptor) {
        if (fiberExecutor == null) {
            LOGGER.fine("Cannot send messages: this Communicator instance has been closed");
            return;
        }

        if (completionCallbackHandler != null) {
            fiberExecutor.start(request, completionCallbackHandler, interceptor);
        } else {
            fiberExecutor.start(request, new Fiber.CompletionCallback() {

                @Override
                public void onCompletion(Packet response) {
                    // do nothing
                }

                @Override
                public void onCompletion(Throwable error) {
                    LOGGER.warning("Unexpected exception occured", error);
                }
            }, interceptor);
        }
    }

    /**
     * Provides the destination endpoint reference this {@link Communicator} is pointing to.
     * May return {@code null} (typically when used on the server side).
     *
     * @return destination endpoint reference or {@code null} in case the destination address has
     *         not been specified when constructing this {@link Communicator} instance.
     */
    public
    @Nullable
    EndpointAddress getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(EndpointAddress newValue) {
        this.destinationAddress = newValue;
    }

    // This is called by RM and MC when they start a session.
    public void setDestinationAddressFrom(Packet packet) {
        this.destinationAddress = packet.endpointAddress;
        // The proxy needs to be set so upper layers can get Container/Component.
        if (this.proxy == null) {
            this.proxy = packet.proxy;
        } else if (this.proxy != packet.proxy) {
            throw new WebServiceException("internal error: proxy should be the same");
        }
    }

    public AddressingVersion getAddressingVersion() {
        return addressingVersion;
    }

    public SOAPVersion getSoapVersion() {
        return soapVersion;
    }

    public void close() {
        final FiberExecutor fe = this.fiberExecutor;
        if (fe != null) {
            fe.close();
            this.fiberExecutor = null;
        }
    }

    public boolean isClosed() {
        return this.fiberExecutor == null;
    }

    public Component getContainer() {
        return container;
    }
}
