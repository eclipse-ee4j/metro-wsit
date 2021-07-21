/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common.types;

import java.util.Map;
import javax.xml.namespace.QName;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import jakarta.xml.ws.EndpointReference;


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
 *               </extension>
 *             </simpleContent>
 *           </complexType>
 *         </element>
 *         <element ref="{http://docs.oasis-open.org/ws-tx/wscoor/2006/06}Expires" minOccurs="0"/>
 *         <element name="CoordinationType" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         <element name="RegistrationService" type="{http://www.w3.org/2005/08/addressing}EndpointReferenceType"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
public interface CoordinationContextTypeIF<T extends EndpointReference,E,I,C> {

    /**
     * Gets the value of the identifier property.
     * 
     * @return
     *     possible object is
     *     {@link BaseIdentifier }
     *     
     */
    public abstract BaseIdentifier<I> getIdentifier();

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link BaseIdentifier }
     *     
     */
    public abstract void setIdentifier(BaseIdentifier<I> value);

    /**
     * Gets the value of the expires property.
     * 
     * @return
     *     possible object is
     *     {@link BaseExpires }
     *     
     */
    public abstract BaseExpires<E> getExpires();

    /**
     * Sets the value of the expires property.
     * 
     * @param value
     *     allowed object is
     *     {@link BaseExpires }
     *     
     */
    public abstract void setExpires(BaseExpires<E> value);

    /**
     * Gets the value of the coordinationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public abstract String getCoordinationType();

    /**
     * Sets the value of the coordinationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public abstract void setCoordinationType(String value);

    /**
     * Gets the value of the registrationService property.
     * 
     * @return
     *     possible object is
     *     {@link W3CEndpointReference }
     *     
     */
    public abstract T  getRegistrationService();

    /**
     * Sets the value of the registrationService property.
     * 
     * @param value
     *     allowed object is
     *     {@link W3CEndpointReference }
     *     
     */
    public abstract void setRegistrationService(T value);

    public abstract Map<QName, String> getOtherAttributes();

    public abstract C getDelegate();


}
