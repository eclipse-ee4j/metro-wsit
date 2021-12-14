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
 * Represents UsernameToken Assertion
 * @author K.Venugopal@sun.com
 */
public interface UserNameToken extends Token{
    
  
    /**
     * UsernameToken version
     * @return 1.0 or 1.1
     */
    String getType();
    
    /**
     * returns true is Nonce needs to be used in the UsernameToken Header sent in the message.
     */
    boolean useNonce();
    
    /**
     * returns true is Created needs to be used in the UsernameToken Header sent in the message.
     */
    boolean useCreated();
    
    /**
     * returns true is password needs to be used in the UsernameToken Header sent in the message.
     */
    boolean hasPassword();
    
    /**
     * @return true if password hash should be used instead of plaintext password
     */
    boolean useHashPassword();
    
    /**
     * returns the issuer for the Username token.
     * @return returns the issuer
     */
    Issuer getIssuer();
    
    /**
     * 
     * @return the issuer name for Username token
     */
    IssuerName getIssuerName();
    
    /**
     * 
     * @return Claims
     */
    Claims getClaims();
    
    boolean isRequireDerivedKeys();

    Set getTokenRefernceType() ;
}
