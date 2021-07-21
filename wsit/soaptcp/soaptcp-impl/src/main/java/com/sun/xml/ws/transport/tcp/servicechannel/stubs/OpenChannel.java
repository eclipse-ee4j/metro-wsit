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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for openChannel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="openChannel">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="targetWSURI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="negotiatedMimeTypes" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         <element name="negotiatedParams" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "openChannel", propOrder = {
    "targetWSURI",
    "negotiatedMimeTypes",
    "negotiatedParams"
})
public class OpenChannel {

    @XmlElement(required = true)
    protected String targetWSURI;
    @XmlElement(required = true)
    protected List<String> negotiatedMimeTypes;
    protected List<String> negotiatedParams;

    /**
     * Gets the value of the targetWSURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetWSURI() {
        return targetWSURI;
    }

    /**
     * Sets the value of the targetWSURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetWSURI(String value) {
        this.targetWSURI = value;
    }

    /**
     * Gets the value of the negotiatedMimeTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the negotiatedMimeTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNegotiatedMimeTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNegotiatedMimeTypes() {
        if (negotiatedMimeTypes == null) {
            negotiatedMimeTypes = new ArrayList<String>();
        }
        return this.negotiatedMimeTypes;
    }

    /**
     * Gets the value of the negotiatedParams property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the negotiatedParams property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNegotiatedParams().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNegotiatedParams() {
        if (negotiatedParams == null) {
            negotiatedParams = new ArrayList<String>();
        }
        return this.negotiatedParams;
    }

}
