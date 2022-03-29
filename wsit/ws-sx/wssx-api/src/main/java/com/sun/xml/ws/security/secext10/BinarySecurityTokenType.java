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
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.2-b01-fcs
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2006.08.10 at 02:34:36 PM IST
//


package com.sun.xml.ws.security.secext10;

import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * A security token that is encoded in binary
 *
 * <p>Java class for BinarySecurityTokenType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="BinarySecurityTokenType">
 *   <simpleContent>
 *     <extension base="<http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd>EncodedString">
 *       <attribute name="ValueType" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     </extension>
 *   </simpleContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinarySecurityTokenType")
public class BinarySecurityTokenType

{

    @XmlAttribute(name = "ValueType")
    protected String valueType;

    @XmlAttribute(name = "EncodingType")
    protected String encodingType;

    @XmlValue
    protected byte[] value;
    @XmlAttribute(name = "Id", namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    public BinarySecurityTokenType() {
    }


    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
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


    public void setValue(byte[] value){
        this.value = value;
    }

    public byte[] getValue(){
        return this.value;
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


}
