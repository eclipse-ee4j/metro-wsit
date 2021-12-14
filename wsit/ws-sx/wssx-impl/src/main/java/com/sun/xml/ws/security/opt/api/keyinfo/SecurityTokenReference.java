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
 * SecurityTokenReference.java
 *
 * Created on August 2, 2006, 3:50 PM
 */

package com.sun.xml.ws.security.opt.api.keyinfo;

import com.sun.xml.ws.security.opt.api.reference.Reference;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public interface SecurityTokenReference extends Token{
    
    String KEYIDENTIFIER = "Identifier";
    String REFERENCE = "Direct";//TODO: This looks incorrect
    String X509DATA_ISSUERSERIAL = "X509Data";
    String DIRECT_REFERENCE = "Reference";
    
    /**
     * Sets the appropriate reference type for STR - like EkyIndentifier, Direct reference etc
     * @param ref The reference type used in STR
     */
    void setReference(Reference ref);
    
    /**
     * 
     * @return The Reference used inside STR
     */
    Reference getReference();
    
    /**
     * set the WSS 1.1 Token type for SecurityTokenRerference
     * @param tokenType the value of TokenType attribute used in WSS 1.1
     */
    void setTokenType(String tokenType);
    
    /**
     * get the WSS 1.1 Token type for SecurityTokenRerference
     * @return the value of TokenType attribute used in WSS 1.1
     */
    String getTokenType();
    
}
