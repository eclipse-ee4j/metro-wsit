/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.protocol.wsrm200702;

import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;

import javax.xml.namespace.QName;
import java.math.BigInteger;
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
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element ref="{http://docs.oasis-open.org/ws-rx/wsrm/200702}Identifier"/>
 *         <choice>
 *           <sequence>
 *             <choice>
 *               <element name="AcknowledgementRange" maxOccurs="unbounded">
 *                 <complexType>
 *                   <complexContent>
 *                     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                       <sequence>
 *                       </sequence>
 *                       <attribute name="Upper" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *                       <attribute name="Lower" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *                     </restriction>
 *                   </complexContent>
 *                 </complexType>
 *               </element>
 *               <element name="None">
 *                 <complexType>
 *                   <complexContent>
 *                     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                       <sequence>
 *                       </sequence>
 *                     </restriction>
 *                   </complexContent>
 *                 </complexType>
 *               </element>
 *             </choice>
 *             <element name="Final" minOccurs="0">
 *               <complexType>
 *                 <complexContent>
 *                   <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                     <sequence>
 *                     </sequence>
 *                   </restriction>
 *                 </complexContent>
 *               </complexType>
 *             </element>
 *           </sequence>
 *           <element name="Nack" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" maxOccurs="unbounded"/>
 *         </choice>
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
@XmlType(name = "", propOrder = {
"identifier",
"acknowledgementRange",
"none",
"_final",
"bufferRemaining",
"nack",
"any"
})
@XmlRootElement(name = "SequenceAcknowledgement", namespace = "http://docs.oasis-open.org/ws-rx/wsrm/200702")
public class SequenceAcknowledgementElement {

    @XmlElement(name = "Identifier", required = true, namespace = "http://docs.oasis-open.org/ws-rx/wsrm/200702")
    protected Identifier identifier;
    @XmlElement(name = "AcknowledgementRange", namespace = "http://docs.oasis-open.org/ws-rx/wsrm/200702")
    protected List<SequenceAcknowledgementElement.AcknowledgementRange> acknowledgementRange;
    @XmlElement(name = "None", namespace = "http://docs.oasis-open.org/ws-rx/wsrm/200702")
    protected SequenceAcknowledgementElement.None none;
    @XmlElement(name = "Final", namespace = "http://docs.oasis-open.org/ws-rx/wsrm/200702")
    protected SequenceAcknowledgementElement.Final _final;
    @XmlElement(name = "BufferRemaining", namespace = "http://schemas.microsoft.com/ws/2006/05/rm")
    protected Integer bufferRemaining;
    @XmlElement(name = "Nack", namespace = "http://docs.oasis-open.org/ws-rx/wsrm/200702")
    @XmlSchemaType(name = "unsignedLong")
    protected List<BigInteger> nack;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

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
     * Gets the value of the acknowledgementRange property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the acknowledgementRange property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAcknowledgementRange().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SequenceAcknowledgementElement#acknowledgementRange}
     *
     *
     */
    public List<SequenceAcknowledgementElement.AcknowledgementRange> getAcknowledgementRange() {
        if (acknowledgementRange == null) {
            acknowledgementRange = new ArrayList<>();
        }
        return this.acknowledgementRange;
    }

    /**
     * Gets the value of the none property.
     *
     * @return
     *     possible object is
     *     {@link SequenceAcknowledgementElement.None }
     *
     */
    public SequenceAcknowledgementElement.None getNone() {
        return none;
    }

    /**
     * Sets the value of the none property.
     *
     * @param value
     *     allowed object is
     *     {@link SequenceAcknowledgementElement.None }
     *
     */
    public void setNone(SequenceAcknowledgementElement.None value) {
        this.none = value;
    }

    /**
     * Gets the value of the final property.
     *
     * @return
     *     possible object is
     *     {@code SequenceAcknowledgementElement#Final }
     *
     */
    public SequenceAcknowledgementElement.Final getFinal() {
        return _final;
    }

    /**
     * Sets the value of the final property.
     *
     * @param value
     *     allowed object is
     *     {@link SequenceAcknowledgementElement.Final }
     *
     */
    public void setFinal(SequenceAcknowledgementElement.Final value) {
        this._final = value;
    }

    /**
     * Gets the value of the nack property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nack property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNack().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigInteger }
     *
     *
     */
    public List<BigInteger> getNack() {
        if (nack == null) {
            nack = new ArrayList<>();
        }
        return this.nack;
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
            any = new ArrayList<>();
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

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *       </sequence>
     *       <attribute name="Upper" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
     *       <attribute name="Lower" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class AcknowledgementRange {

        @XmlAttribute(name = "Upper", required = true)
        @XmlSchemaType(name = "unsignedLong")
        protected BigInteger upper;
        @XmlAttribute(name = "Lower", required = true)
        @XmlSchemaType(name = "unsignedLong")
        protected BigInteger lower;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<>();

        /**
         * Gets the value of the upper property.
         *
         * @return
         *     possible object is
         *     {@link BigInteger }
         *
         */
        public BigInteger getUpper() {
            return upper;
        }

        /**
         * Sets the value of the upper property.
         *
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *
         */
        public void setUpper(BigInteger value) {
            this.upper = value;
        }

        /**
         * Gets the value of the lower property.
         *
         * @return
         *     possible object is
         *     {@link BigInteger }
         *
         */
        public BigInteger getLower() {
            return lower;
        }

        /**
         * Sets the value of the lower property.
         *
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *
         */
        public void setLower(BigInteger value) {
            this.lower = value;
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

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Final {
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class None {
    }

    public void setId(String idString) {
        com.sun.xml.ws.rx.rm.protocol.wsrm200702.Identifier newId = new Identifier();
        newId.setValue(idString);
        setIdentifier(newId);
    }

    public String getId() {
        return getIdentifier().getValue();
    }

    public int getBufferRemaining() {
        if (bufferRemaining == null) {
            return -1;
        }
        return bufferRemaining;
    }

    public void setBufferRemaining(int value) {
        bufferRemaining = value;
    }

    public void addAckRange(long lower, long upper) {
        if (nack != null) {
            throw new IllegalArgumentException(LocalizationMessages.WSRM_4002_BOTH_ACKS_AND_NACKS_MESSAGE());
        }
        //check validity of indices
        if (lower > upper) {
            throw new IllegalArgumentException(LocalizationMessages.WSRM_4003_UPPERBOUND_LESSTHAN_LOWERBOUND_MESSAGE());
        }

        //TODO Further validity checking
        SequenceAcknowledgementElement.AcknowledgementRange range = new SequenceAcknowledgementElement.AcknowledgementRange();
        range.setLower(BigInteger.valueOf(lower));
        range.setUpper(BigInteger.valueOf(upper));
        getAcknowledgementRange().add(range);
    }

    public void addNack(long index) {
        if (acknowledgementRange != null) {
            throw new IllegalArgumentException(LocalizationMessages.WSRM_4002_BOTH_ACKS_AND_NACKS_MESSAGE());
        }

        getNack().add(BigInteger.valueOf(index));
    }
}
