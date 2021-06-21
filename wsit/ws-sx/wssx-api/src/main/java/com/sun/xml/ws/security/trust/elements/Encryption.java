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
 * $Id: Encryption.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.Token;

/**
 *
 * @author WS-Trust Implementation Team.
 */
public interface Encryption {
    
    /**
     * Get the type of the Encryption information item
     */
    String getTargetType();

    /**
     * Gets the value of the any property.
     * 
     * 
     * @return possible object is
     *     {@link org.w3c.dom.Element }
     *     {@link Object }
     */
    Object getAny();

    /**
     * Sets the value of the any property.
     * 
     * 
     * @param value
     *     allowed object is
     *     {@link org.w3c.dom.Element }
     *     {@link Object }
     */
    void setAny(Object value);
    
    /**
     * Set the STR for the Token as the contents of Encryption
     */
    void setSecurityTokenReference(SecurityTokenReference ref);
    
    /**
     * Get the STR contained in this Encryption Type
     */
    SecurityTokenReference getSecurityTokenReference();

   /**
     * Set the Token as the contents of Encryption
     */
    void setToken(Token token);

    /**
     * Get the Token contained in this Encryption instance
     */
    Token getToken();

}
