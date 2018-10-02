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
 * $Id: UseKey.java,v 1.2 2010-10-21 15:35:41 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import java.net.URI;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.Token;

/**
 *
 * @author WS-Trust Implementation Team
 */
public interface UseKey {
    
    /**
     * Set the Token as the contents of UseKey
     */
    void setToken(Token token);
    
    /**
     * Get the Token contained in the element, null otherwise.
     */
    Token getToken();    
    
    /**
     * Set the option Sig attribute of UseKey
     */
    void setSignatureID(URI sigID);
    
    /**
     * get the Sig attribute value if set, null otherwise
     */
    URI getSignatureID();
}
