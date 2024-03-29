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

import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;


/**
 * <p>Java class for BinaryExchangeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BinaryExchangeType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *       &lt;attribute name="EncodingType" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *       &lt;attribute name="ValueType" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryExchangeType", propOrder = {
    "value"
})
public class BinaryExchangeType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "EncodingType", required = true)
    protected String encodingType;
    @XmlAttribute(name = "ValueType", required = true)
    protected String valueType;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the encodingType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEncodingType() {
        return encodingType;
    }

    /**
     * Sets the value of the encodingType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEncodingType(String value) {
        this.encodingType = value;
    }

    /**
     * Gets the value of the valueType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValueType() {
        return valueType;
    }

    /**
     * Sets the value of the valueType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValueType(String value) {
        this.valueType = value;
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
