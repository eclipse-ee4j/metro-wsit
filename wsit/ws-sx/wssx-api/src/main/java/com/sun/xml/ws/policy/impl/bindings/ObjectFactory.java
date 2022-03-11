/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2006.02.24 at 05:55:09 PM PST
//


package com.sun.xml.ws.policy.impl.bindings;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.ws.policy.impl.bindings.OperatorContentType;
import com.sun.xml.ws.policy.impl.bindings.Policy;
import com.sun.xml.ws.policy.impl.bindings.PolicyAttachment;
import com.sun.xml.ws.policy.impl.bindings.PolicyReference;
import com.sun.xml.ws.policy.impl.bindings.UsingPolicy;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.sun.xml.ws.policy.impl.bindings package.
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

    private final static QName _ExactlyOne_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/09/policy", "ExactlyOne");
    private final static QName _All_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/09/policy", "All");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.sun.xml.ws.policy.impl.bindings
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UsingPolicy }
     *
     */
    public UsingPolicy createUsingPolicy() {
        return new UsingPolicy();
    }

    /**
     * Create an instance of {@link PolicyReference }
     *
     */
    public PolicyReference createPolicyReference() {
        return new PolicyReference();
    }

    /**
     * Create an instance of {@link PolicyAttachment }
     *
     */
    public PolicyAttachment createPolicyAttachment() {
        return new PolicyAttachment();
    }

    /**
     * Create an instance of {@link AppliesTo }
     *
     */
    public AppliesTo createAppliesTo() {
        return new AppliesTo();
    }

    /**
     * Create an instance of {@link OperatorContentType }
     *
     */
    public OperatorContentType createOperatorContentType() {
        return new OperatorContentType();
    }

    /**
     * Create an instance of {@link Policy }
     *
     */
    public Policy createPolicy() {
        return new Policy();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperatorContentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/09/policy", name = "ExactlyOne")
    public JAXBElement<OperatorContentType> createExactlyOne(OperatorContentType value) {
        return new JAXBElement<>(_ExactlyOne_QNAME, OperatorContentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperatorContentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/09/policy", name = "All")
    public JAXBElement<OperatorContentType> createAll(OperatorContentType value) {
        return new JAXBElement<>(_All_QNAME, OperatorContentType.class, null, value);
    }

}
