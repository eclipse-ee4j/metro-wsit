/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;

/**
 * DerivedKeyToken Interface
 * TODO: This defintion is incomplete. Currently it has only those members which are required
 * for the Trust Interop Scenarios
 */
public interface DerivedKeyToken extends Token {

    String DERIVED_KEY_TOKEN_TYPE="http://schemas.xmlsoap.org/ws/2005/02/sc/dk";

    String DEFAULT_DERIVED_KEY_TOKEN_ALGORITHM="http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1";
    
    String DEFAULT_DERIVEDKEYTOKEN_LABEL = "WS-SecureConversationWS-SecureConversation";

    URI getAlgorithm();

    byte[] getNonce();

    long  getLength();

    long  getOffset();
    
    long getGeneration();
    
    String getLabel();
    
    SecretKey generateSymmetricKey(String algorithm) throws InvalidKeyException, NoSuchAlgorithmException;
}
