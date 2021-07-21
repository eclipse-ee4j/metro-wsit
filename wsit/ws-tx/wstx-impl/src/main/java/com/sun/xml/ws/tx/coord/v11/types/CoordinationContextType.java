/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v11.types;

import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;


/**
 * <p>Java class for CoordinationContextType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="CoordinationContextType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Identifier">
 *           <complexType>
 *             <simpleContent>
 *               <extension base="<http://www.w3.org/2001/XMLSchema>anyURI">
 *                 <anyAttribute namespace='##other'/>
 *               </extension>
 *             </simpleContent>
 *           </complexType>
 *         </element>
 *         <element ref="{http://docs.oasis-open.org/ws-tx/wscoor/2006/06}Expires" minOccurs="0"/>
 *         <element name="CoordinationType" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         <element name="RegistrationService" type="{http://www.w3.org/2005/08/addressing}EndpointReferenceType"/>
 *       </sequence>
 *       <anyAttribute processContents='lax' namespace='##other'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoordinationContextType", propOrder = {
    "identifier",
    "expires",
    "coordinationType",
    "registrationService"
})
@XmlSeeAlso({
    com.sun.xml.ws.tx.coord.v11.types.CreateCoordinationContextType.CurrentContext.class,
    CoordinationContext.class
})
public class CoordinationContextType {

    @XmlElement(name = "Identifier", required = true)
    protected CoordinationContextType.Identifier identifier;
    @XmlElement(name = "Expires")
    protected Expires expires;
    @XmlElement(name = "CoordinationType", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String coordinationType;
    @XmlElement(name = "RegistrationService", required = true)
    protected W3CEndpointReference registrationService;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the identifier property.
     * 
     * @return
     *     possible object is
     *     {@link CoordinationContextType.Identifier }
     *     
     */
    public CoordinationContextType.Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoordinationContextType.Identifier }
     *     
     */
    public void setIdentifier(CoordinationContextType.Identifier value) {
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
     * Gets the value of the coordinationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoordinationType() {
        return coordinationType;
    }

    /**
     * Sets the value of the coordinationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoordinationType(String value) {
        this.coordinationType = value;
    }

    /**
     * Gets the value of the registrationService property.
     * 
     * @return
     *     possible object is
     *     {@link W3CEndpointReference }
     *     
     */
    public W3CEndpointReference getRegistrationService() {
        return registrationService;
    }

    /**
     * Sets the value of the registrationService property.
     * 
     * @param value
     *     allowed object is
     *     {@link W3CEndpointReference }
     *     
     */
    public void setRegistrationService(W3CEndpointReference value) {
        this.registrationService = value;
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
     *   <simpleContent>
     *     <extension base="<http://www.w3.org/2001/XMLSchema>anyURI">
     *       <anyAttribute namespace='##other'/>
     *     </extension>
     *   </simpleContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Identifier {

        @XmlValue
        @XmlSchemaType(name = "anyURI")
        protected String value;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

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

}
