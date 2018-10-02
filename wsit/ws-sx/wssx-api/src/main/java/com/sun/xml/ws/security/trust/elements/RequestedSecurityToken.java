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
 * $Id: RequestedSecurityToken.java,v 1.2 2010-10-21 15:35:41 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import com.sun.xml.ws.security.Token;

/**
 * @author WS-Trust Implementation Team.
 */
public interface RequestedSecurityToken {
    /**
     * Gets the value of the any property.
     * 
     * @return
     *     possible object is
     *     {@link Element }
     *     {@link Object }    
     */
    Object getAny();

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *     allowed object is
     *     {@link Element }
     *     {@link Object }
     */
    void setAny(Object value);
    
    /**
     * Returns the Security Token contained in the RequestedSecurityToken element.
     * @return {@link Token}
     */
    Token getToken();

    /**
     * Sets the value of the Security Token in the RequestedSecurityToken element.
     * @param token {@link Token}
     */
    void setToken(Token token);

}
