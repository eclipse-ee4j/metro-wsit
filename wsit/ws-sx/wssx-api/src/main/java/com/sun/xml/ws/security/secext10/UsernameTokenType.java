/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This type represents a username token per Section 4.1
 * 
 * <p>Java class for UsernameTokenType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="UsernameTokenType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Username" type="{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}AttributedString"/>
 *         <any/>
 *       </sequence>
 *       <attribute ref="{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Id"/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UsernameTokenType", propOrder = {
    "username",
    "password",
    "nonce",
    "created",
    "salt",
    "iteration",
    "any"
})
public class UsernameTokenType {

    @XmlElement(name = "Username", required = true)
    protected AttributedString username;
    
    @XmlElement(name = "Password")
    protected AttributedString password = null;
    
    @XmlElement(name = "Nonce")
    protected AttributedString nonce = null;
    
    @XmlElement(name = "Created", namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd")
    protected AttributedString created = null;
    
    @XmlElement(name = "Salt", namespace = "http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd")
    protected  AttributedString  salt = null;
    
    @XmlElement(name ="Iteration",namespace = "http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd")
    protected AttributedString iteration;
    
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "Id", namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link AttributedString }
     *     
     */
    public AttributedString getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributedString }
     *     
     */
    public void setUsername(AttributedString value) {
        this.username = value;
    }
    
    public AttributedString getPassword() {
        return password;
    }
    
    public void setPassword(AttributedString value) {
        this.password = value;
    }
    
    public AttributedString getNonce() {
        return nonce;
    }
    
    public void setNonce(AttributedString value) {
        this.nonce = value;
    }
    
    public AttributedString getCreated() {
        return created;
    }
    
    public void setCreated(AttributedString value) {
        this.created = value;
    }
    public AttributedString getIteration() {
        return iteration;
    }
    public void setIteration(AttributedString value) {
       this.iteration = value;
    }
    public AttributedString getSalt() {
       return salt;
    }
    public void setSalt(AttributedString salt) {
        this.salt = salt ;
    }        
    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.w3c.dom.Element }
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
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

}
