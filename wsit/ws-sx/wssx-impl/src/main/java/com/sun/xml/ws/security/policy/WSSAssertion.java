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
 * Represents WSS Properties
 * @author K.Venugopal@sun.com
 */
public interface WSSAssertion {
    String MUSTSUPPORT_REF_THUMBPRINT = "MustSupportRefThumbprint";
    String MUSTSUPPORT_REF_ENCRYPTED_KEY = "MustSupportRefEncryptedKey";
    String REQUIRE_SIGNATURE_CONFIRMATION = "RequireSignatureConfirmation";
    String MUST_SUPPORT_CLIENT_CHALLENGE = "MustSupportClientChallenge";
    String MUST_SUPPORT_SERVER_CHALLENGE = "MustSupportServerChallenge";
    String REQUIRE_CLIENT_ENTROPY = "RequireClientEntropy";
    String REQUIRE_SERVER_ENTROPY= "RequireServerEntropy";
    String MUST_SUPPORT_ISSUED_TOKENS = "MustSupportIssuedTokens";
    String MUSTSUPPORT_REF_ISSUER_SERIAL= "MustSupportRefIssuerSerial";
    String REQUIRE_EXTERNAL_URI_REFERENCE = "RequireExternalUriReference";
    String REQUIRE_EMBEDDED_TOKEN_REF = "RequireEmbeddedTokenReference";
    String MUST_SUPPORT_REF_KEYIDENTIFIER = "MustSupportRefKeyIdentifier";
   
    /**
     * List of WSS properties
     * @return {@link java.util.Set}
     */
    Set<String> getRequiredProperties();
    /**
     * WSS version
     * @return 1.0
     */
    String getType();
}
