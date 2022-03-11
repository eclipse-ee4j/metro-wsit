/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
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
public interface SecurityContextToken extends Token{

    /**
     * returns a {@link java.util.Iterator } over the token reference types to be used.
     * @return either REQUIRE_EXTERNAL_URI_REFERENCE
     */
    Iterator getTokenRefernceType();

    /**
     * returns true if RequiredDerivedKey element is present underSecurityContextToken
     * @return true if RequireDerviedKeys element is present under SecurityContextToken or false.
     */
    boolean isRequireDerivedKeys();

    /**
     * returns the type of the token.
     * @return one of SC10_SECURITYCONTEXT_TOKEN
     */
    String getTokenType();
}
