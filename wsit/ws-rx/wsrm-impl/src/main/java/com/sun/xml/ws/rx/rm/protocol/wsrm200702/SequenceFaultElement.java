/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.protocol.wsrm200702;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;

/**
 * <p>Java class for SequenceFaultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SequenceFaultType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FaultCode" type="{http://docs.oasis-open.org/ws-rx/wsrm/200702}FaultCodes"/&gt;
 *         &lt;element name="Detail" type="{http://docs.oasis-open.org/ws-rx/wsrm/200702}DetailType" minOccurs="0"/&gt;
 *         &lt;any/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SequenceFaultType", propOrder = {
"faultCode",
"detail",
"any"
})
@XmlRootElement(name = "SequenceFault", namespace = "http://docs.oasis-open.org/ws-rx/wsrm/200702")
public class SequenceFaultElement {

    @XmlElement(name = "FaultCode", required = true)
    protected QName faultCode;
    @XmlElement(name = "Detail")
    protected DetailType detail;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public SequenceFaultElement() {
        // empty
    }
    
    public SequenceFaultElement(QName faultCode) {
        this.faultCode = faultCode;
    }

    public SequenceFaultElement(QName faultCode, Detail soapFaultDetail) {
        this.faultCode = faultCode;

        if (soapFaultDetail != null && soapFaultDetail.hasChildNodes()) {
            this.detail = new DetailType();

            Iterator detailEntries = soapFaultDetail.getDetailEntries();
            while (detailEntries.hasNext()) {
                this.detail.getAny().add(detailEntries.next());
            }
        }
    }
  
    /**
     * Gets the value of the faultCode property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getFaultCode() {
        return faultCode;
    }

    /**
     * Sets the value of the faultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setFaultCode(QName value) {
        this.faultCode = value;
    }

    /**
     * Gets the value of the detail property.
     * 
     * @return
     *     possible object is
     *     {@link DetailType }
     *     
     */
    public DetailType getDetail() {
        return detail;
    }

    /**
     * Sets the value of the detail property.
     * 
     * @param value
     *     allowed object is
     *     {@link DetailType }
     *     
     */
    public void setDetail(DetailType value) {
        this.detail = value;
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
     * {@link Object }
     * {@link Element }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
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
