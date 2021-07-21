/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.protocol.wsrm200702;

import com.sun.xml.ws.rx.rm.protocol.CreateSequenceResponseData;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.IncompleteSequenceBehavior;
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
import javax.xml.namespace.QName;

/**
 * <p>Java class for CreateSequenceResponseElement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="CreateSequenceResponseType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element ref="{http://docs.oasis-open.org/ws-rx/wsrm/200702}Identifier"/>
 *         <element ref="{http://docs.oasis-open.org/ws-rx/wsrm/200702}Expires" minOccurs="0"/>
 *         <element name="IncompleteSequenceBehavior" type="{http://docs.oasis-open.org/ws-rx/wsrm/200702}IncompleteSequenceBehaviorType" minOccurs="0"/>
 *         <element name="Accept" type="{http://docs.oasis-open.org/ws-rx/wsrm/200702}AcceptType" minOccurs="0"/>
 *         <any/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateSequenceResponseType", propOrder = {
"identifier",
"expires",
"incompleteSequenceBehavior",
"accept",
"any"
})
@XmlRootElement(name = "CreateSequenceResponse", namespace = "http://docs.oasis-open.org/ws-rx/wsrm/200702")
public class CreateSequenceResponseElement {

    @XmlElement(name = "Identifier", required = true)
    protected Identifier identifier;
    @XmlElement(name = "Expires")
    protected Expires expires;
    @XmlElement(name = "IncompleteSequenceBehavior")
    protected IncompleteSequenceBehaviorType incompleteSequenceBehavior;
    @XmlElement(name = "Accept")
    protected AcceptType accept;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public CreateSequenceResponseElement() {
    }

    public CreateSequenceResponseElement(CreateSequenceResponseData data) {
        this();

        identifier = new Identifier(data.getSequenceId());

        if (data.getIncompleteSequenceBehavior() != IncompleteSequenceBehavior.getDefault()) {
            incompleteSequenceBehavior = IncompleteSequenceBehaviorType.fromISB(data.getIncompleteSequenceBehavior());
        }

        if (!data.doesNotExpire()) {
            expires = new Expires(data.getDuration());
        }
        if (data.getAcceptedSequenceAcksTo() != null) {
            accept = new AcceptType();
            accept.setAcksTo(data.getAcceptedSequenceAcksTo());
        }
    }

    public CreateSequenceResponseData.Builder toDataBuilder() {
        CreateSequenceResponseData.Builder dataBuilder = CreateSequenceResponseData.getBuilder(identifier.getValue());

        if (expires != null && expires.getDuration() != Sequence.NO_EXPIRY) {
            dataBuilder.duration(expires.getDuration());
        }

        if (accept != null) {
            dataBuilder.acceptedSequenceAcksTo(accept.getAcksTo());
        }

        if (incompleteSequenceBehavior != null) {
            dataBuilder.incompleteSequenceBehavior(incompleteSequenceBehavior.translate());
        }

        return dataBuilder;        
    }

    /**
     * Gets the value of the identifier property.
     * 
     * @return
     *     possible object is
     *     {@link Identifier }
     *     
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link Identifier }
     *     
     */
    public void setIdentifier(Identifier value) {
        this.identifier = value;
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
     * Gets the value of the incompleteSequenceBehavior property.
     * 
     * @return
     *     possible object is
     *     {@link IncompleteSequenceBehaviorType }
     *     
     */
    public IncompleteSequenceBehaviorType getIncompleteSequenceBehavior() {
        return incompleteSequenceBehavior;
    }

    /**
     * Sets the value of the incompleteSequenceBehavior property.
     * 
     * @param value
     *     allowed object is
     *     {@link IncompleteSequenceBehaviorType }
     *     
     */
    public void setIncompleteSequenceBehavior(IncompleteSequenceBehaviorType value) {
        this.incompleteSequenceBehavior = value;
    }

    /**
     * Gets the value of the accept property.
     * 
     * @return
     *     possible object is
     *     {@link AcceptType }
     *     
     */
    public AcceptType getAccept() {
        return accept;
    }

    /**
     * Sets the value of the accept property.
     * 
     * @param value
     *     allowed object is
     *     {@link AcceptType }
     *     
     */
    public void setAccept(AcceptType value) {
        this.accept = value;
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
     * {@link org.w3c.dom.Element }
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
