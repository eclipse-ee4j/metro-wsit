/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v10.types;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.sun.xml.ws.tx.coord.v10.types package.
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

    private final static QName _Register_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "Register");
    private final static QName _RegisterResponse_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "RegisterResponse");
    private final static QName _CreateCoordinationContext_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "CreateCoordinationContext");
    private final static QName _CreateCoordinationContextResponse_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "CreateCoordinationContextResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.sun.xml.ws.tx.coord.v10.types
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CoordinationContextType }
     *
     */
    public CoordinationContextType createCoordinationContextType() {
        return new CoordinationContextType();
    }

    /**
     * Create an instance of {@link CreateCoordinationContextType }
     *
     */
    public CreateCoordinationContextType createCreateCoordinationContextType() {
        return new CreateCoordinationContextType();
    }

    /**
     * Create an instance of {@link Expires }
     *
     */
    public Expires createExpires() {
        return new Expires();
    }

    /**
     * Create an instance of {@link RegisterType }
     *
     */
    public RegisterType createRegisterType() {
        return new RegisterType();
    }

    /**
     * Create an instance of {@link RegisterResponseType }
     *
     */
    public RegisterResponseType createRegisterResponseType() {
        return new RegisterResponseType();
    }

    /**
     * Create an instance of {@link CreateCoordinationContextResponseType }
     *
     */
    public CreateCoordinationContextResponseType createCreateCoordinationContextResponseType() {
        return new CreateCoordinationContextResponseType();
    }

    /**
     * Create an instance of {@link CoordinationContext }
     *
     */
    public CoordinationContext createCoordinationContext() {
        return new CoordinationContext();
    }

    /**
     * Create an instance of {@link CoordinationContextType.Identifier }
     *
     */
    public CoordinationContextType.Identifier createCoordinationContextTypeIdentifier() {
        return new CoordinationContextType.Identifier();
    }

    /**
     * Create an instance of {@link CreateCoordinationContextType.CurrentContext }
     *
     */
    public CreateCoordinationContextType.CurrentContext createCreateCoordinationContextTypeCurrentContext() {
        return new CreateCoordinationContextType.CurrentContext();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", name = "Register")
    public JAXBElement<RegisterType> createRegister(RegisterType value) {
        return new JAXBElement<>(_Register_QNAME, RegisterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", name = "RegisterResponse")
    public JAXBElement<RegisterResponseType> createRegisterResponse(RegisterResponseType value) {
        return new JAXBElement<>(_RegisterResponse_QNAME, RegisterResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateCoordinationContextType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", name = "CreateCoordinationContext")
    public JAXBElement<CreateCoordinationContextType> createCreateCoordinationContext(CreateCoordinationContextType value) {
        return new JAXBElement<>(_CreateCoordinationContext_QNAME, CreateCoordinationContextType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateCoordinationContextResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", name = "CreateCoordinationContextResponse")
    public JAXBElement<CreateCoordinationContextResponseType> createCreateCoordinationContextResponse(CreateCoordinationContextResponseType value) {
        return new JAXBElement<>(_CreateCoordinationContextResponse_QNAME, CreateCoordinationContextResponseType.class, null, value);
    }

}
