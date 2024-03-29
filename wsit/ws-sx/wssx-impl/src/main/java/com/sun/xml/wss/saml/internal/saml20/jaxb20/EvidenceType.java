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
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-3509
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2006.09.12 at 08:57:41 PM IST
//


package com.sun.xml.wss.saml.internal.saml20.jaxb20;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EvidenceType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="EvidenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded"&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}AssertionIDRef"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}AssertionURIRef"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}Assertion"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}EncryptedAssertion"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlRootElement(name="Evidence")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EvidenceType", propOrder = {
    "assertionIDRefOrAssertionURIRefOrAssertion"
})
public class EvidenceType {

    @XmlElementRefs({
        @XmlElementRef(name = "AssertionURIRef", namespace = "urn:oasis:names:tc:SAML:2.0:assertion", type = JAXBElement.class),
        @XmlElementRef(name = "EncryptedAssertion", namespace = "urn:oasis:names:tc:SAML:2.0:assertion", type = JAXBElement.class),
        @XmlElementRef(name = "Assertion", namespace = "urn:oasis:names:tc:SAML:2.0:assertion", type = JAXBElement.class),
        @XmlElementRef(name = "AssertionIDRef", namespace = "urn:oasis:names:tc:SAML:2.0:assertion", type = JAXBElement.class)
    })
    protected List<JAXBElement<?>> assertionIDRefOrAssertionURIRefOrAssertion;

    /**
     * Gets the value of the assertionIDRefOrAssertionURIRefOrAssertion property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assertionIDRefOrAssertionURIRefOrAssertion property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssertionIDRefOrAssertionURIRefOrAssertion().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link EncryptedElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link AssertionType }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     *
     */
    public List<JAXBElement<?>> getAssertionIDRefOrAssertionURIRefOrAssertion() {
        if (assertionIDRefOrAssertionURIRefOrAssertion == null) {
            assertionIDRefOrAssertionURIRefOrAssertion = new ArrayList<>();
        }
        return this.assertionIDRefOrAssertionURIRefOrAssertion;
    }

}
