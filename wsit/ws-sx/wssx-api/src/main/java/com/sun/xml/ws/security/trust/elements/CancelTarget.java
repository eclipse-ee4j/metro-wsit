/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: CancelTarget.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.Token;

/**
 * Defines Binding for requesting security tokens to be cancelled.
 *
 * @author WS-Trust Implementation Team
 */
public interface CancelTarget {
    
    /**
     *Constants denoting type of Cancel Target
     */
    String STR_TARGET_TYPE="SecurityTokenReference";
    String CUSTOM_TARGET_TYPE = "Custom";
    
    /**
     * Get the type of the Cancel Target information item
     */
    String getTargetType();
    
    /**
     * Gets the value of the any property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    Object getAny();

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    void setAny(Object value);
    
     /**
     * Set the STR for the Token to be Cancelled
     */
    void setSecurityTokenReference(SecurityTokenReference ref);
    
    /**
     * Get the STR for the Token to be Cancelled
     */
    SecurityTokenReference getSecurityTokenReference();
    
    /**
     * Set the token to be Cancelled
     */
    void setToken(Token token);
    
     /**
     * Get the token to be Cancelled
     */
    Token getToken();
    
}
