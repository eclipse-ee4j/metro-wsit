/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: Lifetime.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;
import com.sun.xml.ws.security.wsu10.AttributedDateTime;

/**
 *
 * @author WS-Trust Implementation Team
 */
public interface Lifetime {
    /**
     * Gets the value of the created property.
     * 
     * @return
     *     possible object is
     *     {@link AttributedDateTime }
     *     
     */
    AttributedDateTime getCreated();

    /**
     * Gets the value of the expires property.
     * 
     * @return
     *     possible object is
     *     {@link AttributedDateTime }
     *     
     */
    AttributedDateTime getExpires();

    /**
     * Sets the value of the created property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributedDateTime }
     *     
     */
    void setCreated(AttributedDateTime value);

    /**
     * Sets the value of the expires property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributedDateTime }
     *     
     */
    void setExpires(AttributedDateTime value);
    
}
