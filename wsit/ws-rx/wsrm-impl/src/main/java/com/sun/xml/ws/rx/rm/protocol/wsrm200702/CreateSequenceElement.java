/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.protocol.wsrm200702;

import com.sun.xml.ws.rx.rm.protocol.CreateSequenceData;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.IncompleteSequenceBehavior;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;


import javax.xml.namespace.QName;
import jakarta.xml.ws.EndpointReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java class for CreateSequenceElement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreateSequenceElement">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/ws-rx/wsrm/200702}AcksTo"/>
 *         &lt;element ref="{http://docs.oasis-open.org/ws-rx/wsrm/200702}Expires" minOccurs="0"/>
 *         &lt;element name="Offer" type="{http://docs.oasis-open.org/ws-rx/wsrm/200702}OfferType" minOccurs="0"/>
 *         &lt;any/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateSequenceType", propOrder = {
"acksTo",
"any",
"expires",
"offer",
"securityTokenReference"
})
@XmlRootElement(name = "CreateSequence", namespace = "http://docs.oasis-open.org/ws-rx/wsrm/200702")
public class CreateSequenceElement {

    @XmlElement(name = "AcksTo", required = true, namespace = "http://docs.oasis-open.org/ws-rx/wsrm/200702")
    protected EndpointReference acksTo;
    @XmlElement(name = "Expires", namespace = "http://docs.oasis-open.org/ws-rx/wsrm/200702")
    protected Expires expires;
    @XmlElement(name = "Offer", namespace = "http://docs.oasis-open.org/ws-rx/wsrm/200702")
    protected OfferType offer;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlElement(name = "SecurityTokenReference", namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")
    private SecurityTokenReferenceType securityTokenReference;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public CreateSequenceElement() {
    }

    public CreateSequenceElement(CreateSequenceData data) {
        this();
        
        acksTo = data.getAcksToEpr();
        if (!data.doesNotExpire()) {
            expires = new Expires(data.getDuration());
        }

        if (data.getOfferedSequenceId() != null) {
            this.offer = new OfferType();
            offer.setId(data.getOfferedSequenceId());
            // Microsoft does not accept CreateSequence messages if AcksTo and Offer/Endpoint are not the same
            offer.setEndpoint(data.getAcksToEpr());
            
            if (!data.offeredSequenceDoesNotExpire()) {
                offer.setExpires(new Expires(data.getOfferedSequenceExpiry()));
            }

            if (data.getOfferedSequenceIncompleteBehavior() != IncompleteSequenceBehavior.getDefault()) {
                offer.setIncompleteSequenceBehavior(IncompleteSequenceBehaviorType.fromISB(data.getOfferedSequenceIncompleteBehavior()));
            }
        }
        if (data.getStrType() != null) {
            securityTokenReference = data.getStrType();
        }
    }

    public CreateSequenceData.Builder toDataBuilder() {
        final CreateSequenceData.Builder dataBuilder = CreateSequenceData.getBuilder(this.getAcksTo());
        dataBuilder.strType(securityTokenReference);

        if (expires != null) {
            dataBuilder.duration(expires.getDuration());
        }

        if (offer != null) {
            dataBuilder.offeredInboundSequenceId(offer.getId());
            if (offer.getExpires() != null) {
                dataBuilder.offeredSequenceExpiry(offer.getExpires().getDuration());
            }

            if (offer.getIncompleteSequenceBehavior() != null) {
                dataBuilder.offeredSequenceIncompleteBehavior(offer.getIncompleteSequenceBehavior().translate());
            }
        }

        return dataBuilder;
    }

    /**
     * Gets the value of the acksTo property.
     */
    public EndpointReference getAcksTo() {
        return acksTo;
    }

    /**
     * Sets the value of the acksTo property.
     */
    public void setAcksTo(EndpointReference value) {
        this.acksTo = value;
    }

    /**
     * Gets the value of the expires property.
     * 
     * @return
     *     possible object is
     *     {@link Expires }
     *     
     */
    public Expires getExpires() {
        return expires;
    }

    /**
     * Sets the value of the expires property.
     * 
     * @param value
     *     allowed object is
     *     {@link Expires }
     *     
     */
    public void setExpires(Expires value) {
        this.expires = value;
    }

    /**
     * Gets the value of the offer property.
     * 
     * @return
     *     possible object is
     *     {@link OfferType }
     *     
     */
    public OfferType getOffer() {
        return offer;
    }

    /**
     * Sets the value of the offer property.
     * 
     * @param value
     *     allowed object is
     *     {@link OfferType }
     *     
     */
    public void setOffer(OfferType value) {
        this.offer = value;
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

    public SecurityTokenReferenceType getSecurityTokenReference() {
        return securityTokenReference;
    }

    public void setSecurityTokenReference(SecurityTokenReferenceType securityTokenReference) {
        this.securityTokenReference = securityTokenReference;
    }
}
