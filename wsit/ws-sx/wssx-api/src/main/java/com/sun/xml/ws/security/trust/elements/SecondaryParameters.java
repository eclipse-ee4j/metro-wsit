/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.elements;

import java.util.List;

import com.sun.xml.ws.api.security.trust.Claims;

/**
 *
 * @author Jiandong Guo
 */
public interface SecondaryParameters extends WSTrustElementBase {
    
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
     */
    List<Object> getAny();
    
     /**
      * Set the desired claims settings for the requested token
      */
     void setClaims(Claims claims);

     /**
      * Get the desired claims settings for the token if specified, null otherwise
      */
     Claims getClaims();
}
