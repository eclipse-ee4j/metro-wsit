/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b24-ea3
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2006.02.03 at 04:10:33 PM IST
//


package com.sun.xml.security.core.xenc;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for CipherDataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CipherDataType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="CipherValue" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
 *         &lt;element ref="{http://www.w3.org/2001/04/xmlenc#}CipherReference"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CipherDataType", propOrder = {
    "cipherValue",
    "cipherReference"
})
public class CipherDataType {
    @XmlJavaTypeAdapter(CVAdapter.class)
    @XmlElement(name = "CipherValue", namespace = "http://www.w3.org/2001/04/xmlenc#")
    protected byte[] cipherValue;
    @XmlElement(name = "CipherReference", namespace = "http://www.w3.org/2001/04/xmlenc#")
    protected CipherReferenceType cipherReference;

    public CipherDataType() {
    }

    /**
     * Gets the value of the cipherValue property.
     *
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getCipherValue() {
        return cipherValue;
    }

    /**
     * Sets the value of the cipherValue property.
     *
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setCipherValue(byte[] value) {
        this.cipherValue = value;
    }

    /**
     * Gets the value of the cipherReference property.
     *
     * @return
     *     possible object is
     *     {@link CipherReferenceType }
     *
     */
    public CipherReferenceType getCipherReference() {
        return cipherReference;
    }

    /**
     * Sets the value of the cipherReference property.
     *
     * @param value
     *     allowed object is
     *     {@link CipherReferenceType }
     *
     */
    public void setCipherReference(CipherReferenceType value) {
        this.cipherReference = value;
    }

}
