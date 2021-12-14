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
 * Represents BinarySecurityToken.
 * @author K.Venugopal@sun.com Abhijit.Das@Sun.Com
 */
public interface X509Token extends Token{
 
 
    /**
     * returns the type of the token.
     * @return one of WSSX509V1TOKEN10,WSSX509V3TOKEN10,WSSX509PKCS7TOKEN10,WSSX509PKIPATHV1TOKEN10,WSSX509V1TOKEN11,WSSX509V3TOKEN11,WSSX509PKCS7TOKEN11,WSSX509PKIPATHV1TOKEN11
     */
    String getTokenType();
    /**
     * returns a {@link java.util.Set } over the token reference types to be used.
     * @return either REQUIRE_KEY_IDENTIFIER_REFERENCE,REQUIRE_ISSUER_SERIAL_REFERENCE,REQUIRE_EMBEDDED_TOKEN_REFERENCE,REQUIRE_THUMBPRINT_REFERENCE
     */
    Set getTokenRefernceType();
    
     /**
     * returns true if RequiredDerivedKey element is present under X509 Token.
     * @return true if RequireDerviedKeys element is present under X509 Token or false.
     */
     boolean isRequireDerivedKeys();
    
    /**
     * returns the issuer for the X509 token.
     * @return returns the issuer
     */
    Issuer getIssuer();
    
    /**
     * 
     * @return the issuer name for X509 token
     */
    IssuerName getIssuerName();
    
    /**
     * 
     * @return Claims
     */
    Claims getClaims();
     
}
