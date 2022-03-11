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

import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for PolicyReference element declaration.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <element name="PolicyReference">
 *   <complexType>
 *     <complexContent>
 *       <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         <attribute name="Digest" type="{http://www.w3.org/2001/XMLSchema}base64Binary" />
 *         <attribute name="DigestAlgorithm" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *         <attribute name="URI" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       </restriction>
 *     </complexContent>
 *   </complexType>
 * </element>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "PolicyReference")
public class PolicyReference {

    @XmlAttribute(name = "Digest")
    protected byte[] digest;
    @XmlAttribute(name = "DigestAlgorithm")
    protected String digestAlgorithm;
    @XmlAttribute(name = "URI")
    protected String uri;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    /**
     * Gets the value of the digest property.
     *
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDigest() {
        return digest;
    }

    /**
     * Sets the value of the digest property.
     *
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDigest(byte[] value) {
        this.digest = value;
    }

    /**
     * Gets the value of the digestAlgorithm property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    /**
     * Sets the value of the digestAlgorithm property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDigestAlgorithm(String value) {
        this.digestAlgorithm = value;
    }

    /**
     * Gets the value of the uri property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getURI() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setURI(String value) {
        this.uri = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
