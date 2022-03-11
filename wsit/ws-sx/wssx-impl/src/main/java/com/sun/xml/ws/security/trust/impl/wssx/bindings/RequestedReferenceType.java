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
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2006.07.12 at 02:51:23 PM PDT
//


package com.sun.xml.ws.security.trust.impl.wssx.bindings;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;


/**
 * <p>Java class for RequestedReferenceType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RequestedReferenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}SecurityTokenReference"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestedReferenceType", propOrder = {
    "securityTokenReference"
})
public class RequestedReferenceType {

    @XmlElement(name = "SecurityTokenReference", namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", required = true)
    protected SecurityTokenReferenceType securityTokenReference;

    /**
     * Gets the value of the securityTokenReference property.
     *
     * @return
     *     possible object is
     *     {@link SecurityTokenReferenceType }
     *
     */
    public SecurityTokenReferenceType getSecurityTokenReference() {
        return securityTokenReference;
    }

    /**
     * Sets the value of the securityTokenReference property.
     *
     * @param value
     *     allowed object is
     *     {@link SecurityTokenReferenceType }
     *
     */
    public void setSecurityTokenReference(SecurityTokenReferenceType value) {
        this.securityTokenReference = value;
    }

}
