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
 * $Id: RenewTarget.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.Token;

/**
 * Target specifying the Security token to be renewed.
 *
 * @author WS-Trust Implementation Team
 */
public interface RenewTarget {
   
    /**
     * Get the type of the renew Target information item
     */
    String getTargetType();

    /**
     * Set the type of the renew Target information item
     */
    void setTargetType(String renewTargetType);

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
     * Set the Token to be renewed
     */
    void setToken(Token token);
    
    /**
     * Get the Token to be renewed 
     */
    Token getToken();
    
     /**
     * Set the STR for the Token to be renewed
     */
    void setSecurityTokenReference(SecurityTokenReference ref);
    
    /**
     * Get the STR for the Token to be renewed 
     */
    SecurityTokenReference getSecurityTokenReference();
    
}
