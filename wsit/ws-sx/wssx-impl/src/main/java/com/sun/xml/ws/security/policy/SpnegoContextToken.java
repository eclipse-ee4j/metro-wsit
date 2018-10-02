/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;

/**
 * 
 * @author K.Venugopal@sun.com
 */
public interface SpnegoContextToken extends Token {
     
     /**
      * returns the issuer for the SpnegoContext token.
      * @return returns the issuer
      */
     public Issuer getIssuer();

     
     /**
     * returns true if RequiredDerivedKey element is present under SpnegoContextToken
     * @return true if RequireDerviedKeys element is present under SpnegoContextToken or false.
     */
    public boolean isRequireDerivedKeys();
        
}
