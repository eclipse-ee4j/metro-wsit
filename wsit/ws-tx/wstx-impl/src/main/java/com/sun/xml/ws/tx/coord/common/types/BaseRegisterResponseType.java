/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common.types;

import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import jakarta.xml.ws.EndpointReference;

import org.w3c.dom.Element;


/**
 * <p>Java class for RegisterResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RegisterResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CoordinatorProtocolService" type="{http://www.w3.org/2005/08/addressing}EndpointReferenceType"/>
 *         &lt;any/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
public abstract class BaseRegisterResponseType<T extends EndpointReference,K> {

    protected K delegate;

    protected BaseRegisterResponseType(K delegate) {
        this.delegate = delegate;
    }

    /**
     * Gets the value of the coordinatorProtocolService property.
     * 
     * @return
     *     possible object is
     *     {@link EndpointReference }
     *     
     */
    public abstract T getCoordinatorProtocolService();

    /**
     * Sets the value of the coordinatorProtocolService property.
     * 
     * @param value
     *     allowed object is
     *     {@link EndpointReference }
     *     
     */
    public abstract void setCoordinatorProtocolService(T value);

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
     * {@link Element }
     * {@link Object }
     * 
     * 
     */
    public abstract List<Object> getAny();

    public abstract Map<QName, String> getOtherAttributes();

    public K getDelegate() {
       return delegate;
    }

}
