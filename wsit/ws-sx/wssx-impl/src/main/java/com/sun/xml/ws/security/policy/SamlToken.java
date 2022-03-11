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
 * This interface represents requirement for SAML Token
 * @author K.Venugopal@sun.com
 */
public interface SamlToken extends Token {


    /**
     * returns the type of the token.
     * @return one of WSS_SAML_V10_TOKEN10,WSS_SAML_V11_TOKEN10,WSS_SAML_V10_TOKEN11,WSS_SAML_V11_TOKEN11,WSS_SAML_V20_TOKEN11
     */
    String getTokenType();
    /**
     * returns a {@link java.util.Iterator } over the token reference types to be used.
     * @return either REQUIRE_KEY_IDENTIFIER_REFERENCE
     */
    Iterator getTokenRefernceType();

    /**
     * returns true if RequiredDerivedKey element is present under SAML Token.
     * @return true if RequireDerviedKeys element is present under SAML Token or false.
     */
    boolean isRequireDerivedKeys();

     /**
     * returns the issuer for the SAML token.
     * @return returns the issuer
     */
     Issuer getIssuer();

    /**
     *
     * @return the issuer name for SAML token
     */
    IssuerName getIssuerName();

    /**
     *
     * @return Claims
     */
    Claims getClaims();

}
