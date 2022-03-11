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

/**
 *
 * @author K.Venugopal@sun.com
 */
public interface HttpsToken extends Token {

    /**
     * returns value of RequireClientCertificate attribute for 2005/07 SP version
     * or true if RequireClientCertificate assertion is present in SP 1.2 version
     * @return true or false
     */
    boolean isRequireClientCertificate();

    /**
     * valid for SecurityPolicy 1.2 only
     * returns true if HttpBasicAuthentication nested policy assertion is present
     * @return true or false
     */
    boolean isHttpBasicAuthentication();

    /**
     * valid for SecurityPolicy 1.2 only
     * returns true if HttpDigestAuthentication nested policy assertion is present
     * @return true or false
     */
    boolean isHttpDigestAuthentication();

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
