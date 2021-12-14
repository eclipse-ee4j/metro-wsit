/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.servicechannel.stubs;

import com.sun.xml.ws.transport.tcp.servicechannel.ServiceChannelException.ServiceChannelExceptionBean;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.sun.xml.ws.transport.tcp.servicechannel.stubs package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _InitiateSessionResponse_QNAME = new QName("http://servicechannel.tcp.transport.ws.xml.sun.com/", "initiateSessionResponse");
    private final static QName _CloseSession_QNAME = new QName("http://servicechannel.tcp.transport.ws.xml.sun.com/", "closeSession");
    private final static QName _CloseChannelResponse_QNAME = new QName("http://servicechannel.tcp.transport.ws.xml.sun.com/", "closeChannelResponse");
    private final static QName _CloseChannel_QNAME = new QName("http://servicechannel.tcp.transport.ws.xml.sun.com/", "closeChannel");
    private final static QName _OpenChannel_QNAME = new QName("http://servicechannel.tcp.transport.ws.xml.sun.com/", "openChannel");
    private final static QName _InitiateSession_QNAME = new QName("http://servicechannel.tcp.transport.ws.xml.sun.com/", "initiateSession");
    private final static QName _OpenChannelResponse_QNAME = new QName("http://servicechannel.tcp.transport.ws.xml.sun.com/", "openChannelResponse");
    private final static QName _ServiceChannelException_QNAME = new QName("http://servicechannel.tcp.transport.ws.xml.sun.com/", "ServiceChannelException");
    private final static QName _CloseSessionResponse_QNAME = new QName("http://servicechannel.tcp.transport.ws.xml.sun.com/", "closeSessionResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.sun.xml.ws.transport.tcp.servicechannel.stubs
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OpenChannelResponse }
     *
     */
    public OpenChannelResponse createOpenChannelResponse() {
        return new OpenChannelResponse();
    }

    /**
     * Create an instance of {@link ServiceChannelExceptionBean }
     *
     */
    public ServiceChannelExceptionBean createServiceChannelExceptionBean() {
        return new ServiceChannelExceptionBean();
    }

    /**
     * Create an instance of {@link CloseChannelResponse }
     *
     */
    public CloseChannelResponse createCloseChannelResponse() {
        return new CloseChannelResponse();
    }

    /**
     * Create an instance of {@link InitiateSessionResponse }
     *
     */
    public InitiateSessionResponse createInitiateSessionResponse() {
        return new InitiateSessionResponse();
    }

    /**
     * Create an instance of {@link OpenChannel }
     *
     */
    public OpenChannel createOpenChannel() {
        return new OpenChannel();
    }

    /**
     * Create an instance of {@link InitiateSession }
     *
     */
    public InitiateSession createInitiateSession() {
        return new InitiateSession();
    }

    /**
     * Create an instance of {@link CloseChannel }
     *
     */
    public CloseChannel createCloseChannel() {
        return new CloseChannel();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InitiateSessionResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", name = "initiateSessionResponse")
    public JAXBElement<InitiateSessionResponse> createInitiateSessionResponse(InitiateSessionResponse value) {
        return new JAXBElement<>(_InitiateSessionResponse_QNAME, InitiateSessionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CloseChannelResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", name = "closeChannelResponse")
    public JAXBElement<CloseChannelResponse> createCloseChannelResponse(CloseChannelResponse value) {
        return new JAXBElement<>(_CloseChannelResponse_QNAME, CloseChannelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CloseChannel }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", name = "closeChannel")
    public JAXBElement<CloseChannel> createCloseChannel(CloseChannel value) {
        return new JAXBElement<>(_CloseChannel_QNAME, CloseChannel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OpenChannel }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", name = "openChannel")
    public JAXBElement<OpenChannel> createOpenChannel(OpenChannel value) {
        return new JAXBElement<>(_OpenChannel_QNAME, OpenChannel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InitiateSession }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", name = "initiateSession")
    public JAXBElement<InitiateSession> createInitiateSession(InitiateSession value) {
        return new JAXBElement<>(_InitiateSession_QNAME, InitiateSession.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OpenChannelResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", name = "openChannelResponse")
    public JAXBElement<OpenChannelResponse> createOpenChannelResponse(OpenChannelResponse value) {
        return new JAXBElement<>(_OpenChannelResponse_QNAME, OpenChannelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceChannelExceptionBean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", name = "ServiceChannelException")
    public JAXBElement<ServiceChannelExceptionBean> createServiceChannelExceptionBean(ServiceChannelExceptionBean value) {
        return new JAXBElement<>(_ServiceChannelException_QNAME, ServiceChannelExceptionBean.class, null, value);
    }
}
