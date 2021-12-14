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

import java.util.Set;

/**
 * This interface represents Kerberos Token
 * @author K.Venugopal@sun.com
 */
public interface KerberosToken extends Token{
    
 
    /**
     * returns the type of the token.
     * @return one of WSSKERBEROS_V5_AP_REQ_TOKEN11,WSSKERBEROS_GSS_V5_AP_REQ_TOKEN11
     */
    String getTokenType();
    /**
     * returns a {@link java.util.Set } over the token reference types to be used.
     * @return either REQUIRE_KEY_IDENTIFIER_REFERENCE
     */
    Set getTokenRefernceType();
    
    /**
     * returns true if RequiredDerivedKey element is present under Kerberos Token.
     * @return true if RequireDerviedKeys element is present under Kerbeors Token or false.
     */
    boolean isRequireDerivedKeys();
    
    /**
     * returns the issuer for the Kerberos token.
     * @return returns the issuer
     */
    Issuer getIssuer();
    
    /**
     * 
     * @return the issuer name for Kerberos token
     */
    IssuerName getIssuerName();
    
    /**
     * 
     * @return Claims
     */
    Claims getClaims();
  
}
