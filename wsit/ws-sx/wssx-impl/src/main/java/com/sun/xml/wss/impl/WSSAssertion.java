/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * WSSAssertion.java
 *
 * Created on September 5, 2006, 12:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author sk112103
 */
public class WSSAssertion {
    
    Set<String> requiredPropSet;
    String version = "1.0";
    
    /** Creates a new instance of WSSAssertion */
    public WSSAssertion(Set<String> props, String version) {
        requiredPropSet = props;
        //fix for wsit issue# 510
        if (requiredPropSet == null) {
            requiredPropSet = new HashSet<String>();
        }
        this.version = version;
    }
    
    public final static String MUSTSUPPORT_REF_THUMBPRINT = "MustSupportRefThumbprint";
    public final static String MUSTSUPPORT_REF_ENCRYPTED_KEY = "MustSupportRefEncryptedKey";
    public final static String REQUIRE_SIGNATURE_CONFIRMATION = "RequireSignatureConfirmation";    
    public static final String MUST_SUPPORT_CLIENT_CHALLENGE = "MustSupportClientChallenge";
    public static final String MUST_SUPPORT_SERVER_CHALLENGE = "MustSupportServerChallenge";
    public static final String REQUIRE_CLIENT_ENTROPY = "RequireClientEntropy";
    public static final String REQUIRE_SERVER_ENTROPY= "RequireServerEntropy";
    public static final String MUST_SUPPORT_ISSUED_TOKENS = "MustSupportIssuedTokens";
    public static final String MUSTSUPPORT_REF_ISSUER_SERIAL= "MustSupportRefIssuerSerial";
    public static final String REQUIRE_EXTERNAL_URI_REFERENCE = "RequireExternalUriReference";
    public static final String REQUIRE_EMBEDDED_TOKEN_REF = "RequireEmbeddedTokenReference";
    public static final String MUST_SUPPORT_REF_KEYIDENTIFIER = "MustSupportRefKeyIdentifier";
   
    /**
     * List of WSS properties
     * @return {@link java.util.Set}
     */
    public Set getRequiredProperties() {
        return requiredPropSet;
    }
    /**
     * WSS version
     * @return 1.0
     */
    public String getType() {
        return version;    
    }

}
