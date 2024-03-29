/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2007.08.13 at 02:11:31 PM IST
//


package com.sun.xml.ws.security.trust.impl.wssx.bindings;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *         The RequestSecurityTokenCollection (RSTC) element is used to provide multiple RST requests.
 *         One or more RSTR elements in an RSTRC element are returned in the response to the RequestSecurityTokenCollection.
 *
 *
 * <p>Java class for RequestSecurityTokenCollectionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RequestSecurityTokenCollectionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RequestSecurityToken" type="{http://docs.oasis-open.org/ws-sx/ws-trust/200512}RequestSecurityTokenType" maxOccurs="unbounded" minOccurs="2"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestSecurityTokenCollectionType", propOrder = {
    "requestSecurityToken"
})
public class RequestSecurityTokenCollectionType {

    @XmlElement(name = "RequestSecurityToken", namespace = "http://docs.oasis-open.org/ws-sx/ws-trust/200512", required = true)
    protected List<RequestSecurityTokenType> requestSecurityToken;

    /**
     * Gets the value of the requestSecurityToken property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requestSecurityToken property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequestSecurityToken().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequestSecurityTokenType }
     *
     *
     */
    public List<RequestSecurityTokenType> getRequestSecurityToken() {
        if (requestSecurityToken == null) {
            requestSecurityToken = new ArrayList<>();
        }
        return this.requestSecurityToken;
    }

}
