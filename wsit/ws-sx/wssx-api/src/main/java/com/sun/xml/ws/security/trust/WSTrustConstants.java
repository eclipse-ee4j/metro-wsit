/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust;

/**
 * Common Constants pertaining to WS-Trust
 * @author WS-Trust Implementation Team
 */
public class WSTrustConstants {

    public static final String SAML_CONFIRMATION_METHOD = "Saml-Confirmation-Method";

    public static final String USE_KEY_RSA_KEY_PAIR = "UseKey-RSAKeyPair";

    public static final String USE_KEY_SIGNATURE_ID = "UseKey-SignatureID";

    public static final String STS_CALL_BACK_HANDLER = "stsCallbackHandler";

    public static final String SAML_ASSERTION_ELEMENT_IN_RST = "SamlAssertionElementInRST";

    public static final String WST_VERSION = "WSTrustVersion";

    public static final String AUTHN_CONTEXT_CLASS = "AuthnContextClass";

    public static final String SECURITY_ENVIRONMENT = "SecurityEnvironment";

    public static final String SAML10_ASSERTION_TOKEN_TYPE = "urn:oasis:names:tc:SAML:1.0:assertion";

    public static final String SAML11_ASSERTION_TOKEN_TYPE = "http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV1.1";

    public static final String SAML20_ASSERTION_TOKEN_TYPE = "urn:oasis:names:tc:SAML:2.0:assertion";

    public static final String SAML20_WSS_TOKEN_TYPE = "http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0";

    public static final String OPAQUE_TYPE = "opaque";

    public static final String SAML11_TYPE = "urn:oasis:names:tc:SAML:1.1:assertion";

    /** the Trust namespace URI */
    public static final String WST_NAMESPACE = "http://schemas.xmlsoap.org/ws/2005/02/trust";

    /** the prefix to use for Trust */
    public static final String WST_PREFIX = "wst";

    /** URI for different request types */
    public static final String ISSUE_REQUEST = WST_NAMESPACE + "/Issue";
    public static final String RENEW_REQUEST = WST_NAMESPACE + "/Renew";
    public static final String CANCEL_REQUEST = WST_NAMESPACE + "/Cancel";
    public static final String VALIDATE_REQUEST = WST_NAMESPACE + "/Validate";
    public static final String KEY_EXCHANGE_REQUEST = WST_NAMESPACE + "/KET";

    /**
     * URI for KeyType
     */
    public static final String PUBLIC_KEY = WST_NAMESPACE+ "/PublicKey";
    public static final String SYMMETRIC_KEY = WST_NAMESPACE + "/SymmetricKey";
    public static final String NO_PROOF_KEY = "http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey";

   /**
     * Constants denoting type of Elements
     */
    public static final String STR_TYPE = "SecurityTokenReference";
    public static final String TOKEN_TYPE = "Token";

    /** Action URIs */
    public static final String REQUEST_SECURITY_TOKEN_ISSUE_ACTION = "http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue";
    public static final String REQUEST_SECURITY_TOKEN_RESPONSE_ISSUE_ACTION = "http://schemas.xmlsoap.org/ws/2005/02/trust/RSTR/Issue";


    /** computed key PSHA1 */
    public static final String CK_PSHA1= "http://schemas.xmlsoap.org/ws/2005/02/trust/CK/PSHA1";

    /** computed key HASH */
    public static final String CK_HASH= "http://schemas.xmlsoap.org/ws/2005/02/trust/CK/HASH";

    /**
     * The default value for AppliesTo if appliesTo is not specified.
     */
    public static final String DEFAULT_APPLIESTO = "default";

    /**
     * Property name for the STS WSDL location URL to be set on the client side
     */
    public static final String PROPERTY_URL= "WSTRUST_PROPERTY_URL";
    /**
     * Property name for the STS port name to be set on the client side
     */
    public static final String PROPERTY_PORT_NAME= "WSTRUST_PROPERTY_PORT_NAME";
    /**
     * Property name for the STS service name to be set on the client side
     */
    public static final String PROPERTY_SERVICE_NAME= "WSTRUST_PROPERTY_SERVICE_NAME";

    /**
     * Property name for the STS end point URL to be set on the client side
     */
    public static final String PROPERTY_SERVICE_END_POINT = "STS_END_POINT";

    /**
     * List of STS Properties
     */
    public enum STS_PROPERTIES  { PROPERTY_URL, PROPERTY_PORT_NAME, PROPERTY_SERVICE_NAME, PROPERTY_SERVICE_END_POINT }

    public static final String IS_TRUST_MESSAGE = "isTrustMessage";

    public static final String TRUST_ACTION = "trustAction";
}
