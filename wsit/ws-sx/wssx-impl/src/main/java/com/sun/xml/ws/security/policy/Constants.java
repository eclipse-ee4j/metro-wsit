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
public interface Constants {  
    
    public static final String HMAC_SHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
    public static final String RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    public static final String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
    public static final String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
    public static final String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
    public static final String AES128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
    public static final String AES192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
    public static final String AES256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
    public static final String TRIPLE_DES = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
    public static final String KW_AES128 = "http://www.w3.org/2001/04/xmlenc#kw-aes128";
    public static final String KW_AES192 = "http://www.w3.org/2001/04/xmlenc#kw-aes192";
    public static final String KW_AES256 = "http://www.w3.org/2001/04/xmlenc#kw-aes256";
    public static final String KW_TRIPLE_DES = "http://www.w3.org/2001/04/xmlenc#kw-tripledes";
    public static final String KW_RSA_OAEP = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
    public static final String KW_RSA15 = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
    public static final String PSHA1 = "http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1";
    public static final String PSHA1_L128 = "http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1";
    public static final String PSHA1_L192 = "http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1";
    public static final String PSHA1_L256 = "http://schemas.xmlsoap.org/ws/2005/02/sc/dk/p_sha1";
    public static final String XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
    public static final String XPATH20 = "http://www.w3.org/2002/06/xmldsig-filter2";
    public static final String C14N = "http://www.w3.org/2001/10/xml-c14n#";
    public static final String EXC14N = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String SNT = "http://www.w3.org/TR/soap12-n11n";
    public static final String STRT10 = "http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soapmessage- security-1.0#STR-Transform";
    
    //TODO:: Remove this constants from here.-Abhijit.
    
    public final static String MUSTSUPPORT_REF_THUMBPRINT = "MustSupportRefThumbprint";
    public final static String MUSTSUPPORT_REF_ENCRYPTED_KEY = "MustSupportRefEncryptedKey";
    public final static String REQUIRED_SIGNATURE_CONFIRMATION = "RequireSignatureConfirmation";
    
    public static final String MUST_SUPPORT_CLIENT_CHALLENGE = "MustSupportClientChallenge";
    public static final String MUST_SUPPORT_SERVER_CHALLENGE = "MustSupportServerChallenge";
    public static final String REQUIRE_CLIENT_ENTROPY = "RequireClientEntropy";
    public static final String REQUIRE_SERVER_ENTROPY= "RequireServerEntropy";
    public static final String MUST_SUPPORT_ISSUED_TOKENS = "MustSupportIssuedTokens";
    
    public static final String REQUIRE_REQUEST_SECURITY_TOKEN_COLLECTION = "RequireRequestSecurityTokenCollection";
    public static final String REQUIRE_APPLIES_TO = "RequireAppliesTo";
}
