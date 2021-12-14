/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;
import java.util.Iterator;


/**
 * This element represents a requirement for an issued token, that is one issued by some token
 * issuer using the mechanisms defined in WS-Trust.
 *
 * @author K.Venugopal@sun.com
 */
public interface IssuedToken extends Token{
    
    /**
     * returns {@link com.sun.xml.ws.security.policy.Issuer } which is the issuer for the issued token.
     * @return {@link com.sun.xml.ws.security.policy.Issuer} or null
     */
    Issuer getIssuer();
    
    /**
     * returns {@link com.sun.xml.ws.security.policy.IssuerName } which is the issuer for the issued token.
     * @return the issuer name for Issued token
     */
    IssuerName getIssuerName();
    
    /**
     * 
     * @return Claims
     */
    Claims getClaims();
   
    /**
     * returns {@link RequestSecurityTokenTemplate }
     * @return {@link RequestSecurityTokenTemplate}
     */
    RequestSecurityTokenTemplate getRequestSecurityTokenTemplate();
  
    
    /**
     * returns a {@link java.util.Iterator } over the token reference types to be used.
     * @return either REQUIRE_KEY_IDENTIFIER_REFERENCE,REQUIRE_ISSUER_SERIAL_REFERENCE,REQUIRE_EMBEDDED_TOKEN_REFERENCE,REQUIRE_THUMBPRINT_REFERENCE
     */
    Iterator getTokenRefernceType();
    
    boolean isRequireDerivedKeys();
    
}
