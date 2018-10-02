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

import java.util.Iterator;

/**
 *
 * @author K.Venugopal@sun.com
 */
public interface RelToken extends Token{
   
    /**
     * returns the type of the token.
     * @return one of WSS_REL_V10_TOKEN10,WSS_REL_V20_TOKEN10,WSS_REL_V10_TOKEN11,WSS_REL_V20_TOKEN11
     */
    public String getTokenType();
    /**
     * returns a {@link java.util.Iterator } over the token reference types to be used.
     * @return either REQUIRE_KEY_IDENTIFIER_REFERENCE
     */
    public Iterator getTokenRefernceType();
    
    /**
     * returns true if RequiredDerivedKey element is present under REL Token.
     * @return true if RequireDerviedKeys element is present under REL Token or false.
     */
    public boolean isRequireDerivedKeys();
   
}
