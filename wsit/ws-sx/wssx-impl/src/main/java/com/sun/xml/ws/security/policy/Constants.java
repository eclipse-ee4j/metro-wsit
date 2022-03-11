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
public interface Constants {

    String HMAC_SHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
    String RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
    String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
    String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
    String AES128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
    String AES192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
    String AES256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
    String TRIPLE_DES = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
    String KW_AES128 = "http://www.w3.org/2001/04/xmlenc#kw-aes128";
    String KW_AES192 = "http://www.w3.org/2001/04/xmlenc#kw-aes192";
    String KW_AES256 = "http://www.w3.org/2001/04/xmlenc#kw-aes256";
    String KW_TRIPLE_DES = "http://www.w3.org/2001/04/xmlenc#kw-tripledes";
    String KW_RSA_OAEP = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
    String KW_RSA15 = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
    String PSHA1 = "http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1";
    String PSHA1_L128 = "http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1";
    String PSHA1_L192 = "http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1";
    String PSHA1_L256 = "http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1";
    String XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
    String XPATH20 = "http://www.w3.org/2002/06/xmldsig-filter2";
    String C14N = "http://www.w3.org/2001/10/xml-c14n#";
    String EXC14N = "http://www.w3.org/2001/10/xml-exc-c14n#";
    String SNT = "http://www.w3.org/TR/soap12-n11n";
    String STRT10 = "http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soapmessage- security-1.0#STR-Transform";

    //TODO:: Remove this constants from here.-Abhijit.

    String MUSTSUPPORT_REF_THUMBPRINT = "MustSupportRefThumbprint";
    String MUSTSUPPORT_REF_ENCRYPTED_KEY = "MustSupportRefEncryptedKey";
    String REQUIRED_SIGNATURE_CONFIRMATION = "RequireSignatureConfirmation";

    String MUST_SUPPORT_CLIENT_CHALLENGE = "MustSupportClientChallenge";
    String MUST_SUPPORT_SERVER_CHALLENGE = "MustSupportServerChallenge";
    String REQUIRE_CLIENT_ENTROPY = "RequireClientEntropy";
    String REQUIRE_SERVER_ENTROPY= "RequireServerEntropy";
    String MUST_SUPPORT_ISSUED_TOKENS = "MustSupportIssuedTokens";

    String REQUIRE_REQUEST_SECURITY_TOKEN_COLLECTION = "RequireRequestSecurityTokenCollection";
    String REQUIRE_APPLIES_TO = "RequireAppliesTo";
}
