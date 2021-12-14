/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: ConfigurationConstants.java,v 1.2 2010-10-21 15:37:25 snajper Exp $
 */

package com.sun.xml.wss.impl.config;

import com.sun.xml.wss.impl.MessageConstants;
import javax.xml.namespace.QName;

/**
 * @author XWS-Security Development Team
 */
public interface ConfigurationConstants {

    String CONFIGURATION_URL = "http://java.sun.com/xml/ns/xwss/config";
    String DEFAULT_CONFIGURATION_PREFIX = "xwss";


    // --- JAXRPC Security Configuration -- //
    String JAXRPC_SECURITY_ELEMENT_NAME = "JAXRPCSecurity";
    String SECURITY_ENVIRONMENT_HANDLER_ELEMENT_NAME = "SecurityEnvironmentHandler";
    String SERVICE_ELEMENT_NAME = "Service";
    String PORT_ELEMENT_NAME = "Port";
    String OPERATION_ELEMENT_NAME = "Operation";
    String NAME_ATTRIBUTE_NAME = "name";

    // 2.0 additions
    String OPTIMIZE_ATTRIBUTE_NAME = "optimize";
    String ID_ATTRIBUTE_NAME = "id"; // this one is to be used everywhere including UUID
    String CONFORMANCE_ATTRIBUTE_NAME = "conformance";
    String USECACHE_ATTRIBUTE_NAME = "useCache";

    String BSP_CONFORMANCE = "bsp";

    String RETAIN_SEC_HEADER = "retainSecurityHeader";
    String RESET_MUST_UNDERSTAND = "resetMustUnderstand";
    
    // --- Declarative Configuration --
    String DECLARATIVE_CONFIGURATION_ELEMENT_NAME = "SecurityConfiguration";

    String DUMP_MESSAGES_ATTRIBUTE_NAME = "dumpMessages";

    //2.0 addition
    String ENABLE_DYNAMIC_POLICY_ATTRIBUTE_NAME = "enableDynamicPolicy";
    
    // WSS 1.1 Policy
    String ENABLE_WSS11_POLICY_ATTRIBUTE_NAME = "enableWSS11Policy";

    //TODO: something used by config tool check and remove
    String SIGNED_TOKEN_REQUIRED_ATTRIBUTE_NAME = "signedTokenRequired";

    // OptionalTargets
    String OPTIONAL_TARGETS_ELEMENT_NAME = "OptionalTargets";


    // requireSignature 
    String SIGNATURE_REQUIREMENT_ELEMENT_NAME = "RequireSignature";
    String TIMESTAMP_REQUIRED_ATTRIBUTE_NAME = "requireTimestamp";

    // requireEncryption
    String ENCRYPTION_REQUIREMENT_ELEMENT_NAME = "RequireEncryption";

    // requireUsernameToken
    String USERNAMETOKEN_REQUIREMENT_ELEMENT_NAME = "RequireUsernameToken";
    String NONCE_REQUIRED_ATTRIBUTE_NAME = "nonceRequired";
    String PASSWORD_DIGEST_REQUIRED_ATTRIBUTE_NAME = "passwordDigestRequired";

    // requireTimestamp
    String TIMESTAMP_REQUIREMENT_ELEMENT_NAME = "RequireTimestamp";

    // Timestamp
    String TIMESTAMP_ELEMENT_NAME = "Timestamp";
    String TIMEOUT_ATTRIBUTE_NAME = "timeout";

    // Sign     
    String SIGN_OPERATION_ELEMENT_NAME = "Sign";
    String INCLUDE_TIMESTAMP_ATTRIBUTE_NAME = "includeTimestamp";

    // Encrypt
    String ENCRYPT_OPERATION_ELEMENT_NAME = "Encrypt";

    //2.0 addition
    //SAML Assertion
    String SAML_ASSERTION_ELEMENT_NAME = "SAMLAssertion";
    String SAML_ASSERTION_TYPE_ATTRIBUTE_NAME = "type";
    String SAML_AUTHORITY_ID_ATTRIBUTE_NAME = "authorityId";
    String SAML_KEYIDENTIFIER_ATTRIBUTE_NAME = "keyIdentifier";

    String SV_SAML_TYPE = "SV";
    String HOK_SAML_TYPE = "HOK";
   

    String REQUIRE_SAML_ASSERTION_ELEMENT_NAME = "RequireSAMLAssertion";


    // X509Token
    String X509TOKEN_ELEMENT_NAME = "X509Token";
    String KEY_REFERENCE_TYPE_ATTRIBUTE_NAME = "keyReferenceType";
    String CERTIFICATE_ALIAS_ATTRIBUTE_NAME = "certificateAlias";
    //2.0 addition
    String ENCODING_TYPE_ATTRIBUTE_NAME = "EncodingType";
    String VALUE_TYPE_ATTRIBUTE_NAME = "ValueType";

    // SymmetricKey
    String SYMMETRIC_KEY_ELEMENT_NAME = "SymmetricKey";
    String SYMMETRIC_KEY_ALIAS_ATTRIBUTE_NAME = "keyAlias";

    // Target
    String TARGET_ELEMENT_NAME = "Target";
    String TARGET_TYPE_ATTRIBUTE_NAME = "type";
    String CONTENT_ONLY_ATTRIBUTE_NAME = "contentOnly";
    String ENFORCE_ATTRIBUTE_NAME = "enforce";
    String TARGET_VALUE_SOAP_BODY = "SOAP-BODY";

    //2.0 addition
    String URI_TARGET = "uri";
    String QNAME_TARGET = "qname";
    String XPATH_TARGET = "xpath";
    
    String ENCRYPTION_TARGET_ELEMENT_NAME = "EncryptionTarget";
    String SIGNATURE_TARGET_ELEMENT_NAME = "SignatureTarget";

    //2.0 addition
    String DIGEST_METHOD_ELEMENT_NAME = "DigestMethod";
    String CANONICALIZATION_METHOD_ELEMENT_NAME = "CanonicalizationMethod";
    String SIGNATURE_METHOD_ELEMENT_NAME = "SignatureMethod";
    String KEY_ENCRYPTION_METHOD_ELEMENT_NAME = "KeyEncryptionMethod";
    String DATA_ENCRYPTION_METHOD_ELEMENT_NAME = "DataEncryptionMethod";

    //2.0 addition
    String TRANSFORM_ELEMENT_NAME = "Transform";
    String ALGORITHM_PARAMETER_ELEMENT_NAME = "AlgorithmParameter";

    //2.0 addition
    String ALGORITHM_ATTRIBUTE_NAME = "algorithm";
    String VALUE_ATTRIBUTE_NAME = "value";
    String DISABLE_INCLUSIVE_PREFIX = "disableInclusivePrefix";

    
    // keyReferenceType
    String DIRECT_KEY_REFERENCE_TYPE = MessageConstants.DIRECT_REFERENCE_TYPE;
    String IDENTIFIER_KEY_REFERENCE_TYPE = MessageConstants.KEY_INDETIFIER_TYPE;
    String SERIAL_KEY_REFERENCE_TYPE = MessageConstants.X509_ISSUER_TYPE;
    //2.0 addition
    String EMBEDDED_KEY_REFERENCE_TYPE = MessageConstants.EMBEDDED_REFERENCE_TYPE;

    // UsernamePassword    
    String USERNAME_PASSWORD_AUTHENTICATION_ELEMENT_NAME = "UsernameToken";
    String USERNAME_ATTRIBUTE_NAME = "name";
    String PASSWORD_ATTRIBUTE_NAME = "password";
    String USE_NONCE_ATTRIBUTE_NAME = "useNonce";
    String DIGEST_PASSWORD_ATTRIBUTE_NAME = "digestPassword";


    QName DECLARATIVE_CONFIGURATION_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            DECLARATIVE_CONFIGURATION_ELEMENT_NAME);
    QName SIGN_OPERATION_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            SIGN_OPERATION_ELEMENT_NAME);
    QName ENCRYPT_OPERATION_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            ENCRYPT_OPERATION_ELEMENT_NAME);
    QName TARGET_QNAME = new QName(
            CONFIGURATION_URL,
            TARGET_ELEMENT_NAME);
    QName TIMESTAMP_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            TIMESTAMP_ELEMENT_NAME);
    QName X509TOKEN_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            X509TOKEN_ELEMENT_NAME);
    QName SYMMETRIC_KEY_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            SYMMETRIC_KEY_ELEMENT_NAME);
    QName USERNAME_PASSWORD_AUTHENTICATION_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            USERNAME_PASSWORD_AUTHENTICATION_ELEMENT_NAME);
    QName TIMESTAMP_REQUIREMENT_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            TIMESTAMP_REQUIREMENT_ELEMENT_NAME);
    QName SIGNATURE_REQUIREMENT_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            SIGNATURE_REQUIREMENT_ELEMENT_NAME);
    QName ENCRYPTION_REQUIREMENT_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            ENCRYPTION_REQUIREMENT_ELEMENT_NAME);
    QName USERNAMETOKEN_REQUIREMENT_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            USERNAMETOKEN_REQUIREMENT_ELEMENT_NAME);
    QName OPTIONAL_TARGETS_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            OPTIONAL_TARGETS_ELEMENT_NAME);
    QName JAXRPC_SECURITY_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            JAXRPC_SECURITY_ELEMENT_NAME);
    QName SERVICE_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            SERVICE_ELEMENT_NAME);
    QName SECURITY_ENVIRONMENT_HANDLER_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            SECURITY_ENVIRONMENT_HANDLER_ELEMENT_NAME);
    QName PORT_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            PORT_ELEMENT_NAME);
    QName OPERATION_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            OPERATION_ELEMENT_NAME);

    //2.0 additions
    QName SAML_ELEMENT_QNAME = new QName(
             CONFIGURATION_URL,
             SAML_ASSERTION_ELEMENT_NAME);
    QName SAML_REQUIREMENT_ELEMENT_QNAME = new QName(
             CONFIGURATION_URL,
             REQUIRE_SAML_ASSERTION_ELEMENT_NAME);

    //2.0 addition
    QName ENCRYPTION_TARGET_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            ENCRYPTION_TARGET_ELEMENT_NAME);

    QName SIGNATURE_TARGET_ELEMENT_QNAME = new QName(
             CONFIGURATION_URL,
             SIGNATURE_TARGET_ELEMENT_NAME);

    //2.0 addition
    QName DIGEST_METHOD_ELEMENT_QNAME =  new QName(
            CONFIGURATION_URL,
            DIGEST_METHOD_ELEMENT_NAME);

    QName CANONICALIZATION_METHOD_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL,
            CANONICALIZATION_METHOD_ELEMENT_NAME);

    QName SIGNATURE_METHOD_ELEMENT_QNAME =  new QName(
            CONFIGURATION_URL,
            SIGNATURE_METHOD_ELEMENT_NAME);

    QName KEY_ENCRYPTION_METHOD_ELEMENT_QNAME =  new QName(
            CONFIGURATION_URL,
            KEY_ENCRYPTION_METHOD_ELEMENT_NAME);

    QName DATA_ENCRYPTION_METHOD_ELEMENT_QNAME =  new QName(
            CONFIGURATION_URL,
            DATA_ENCRYPTION_METHOD_ELEMENT_NAME); 

    //2.0 addition
    QName TRANSFORM_ELEMENT_QNAME = new QName(
            CONFIGURATION_URL, 
            TRANSFORM_ELEMENT_NAME);

    QName ALGORITHM_PARAMETER_ELEMENT_QNAME =  new QName(
            CONFIGURATION_URL,
            ALGORITHM_PARAMETER_ELEMENT_NAME);

    //2.0 addition
    String DEFAULT_DATA_ENC_ALGO = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
    String DEFAULT_KEY_ENC_ALGO = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";

    // 2.0 addition
    String MAX_NONCE_AGE = "maxNonceAge";
    String MAX_CLOCK_SKEW = "maxClockSkew" ;
    String TIMESTAMP_FRESHNESS_LIMIT = "timestampFreshnessLimit";
    String STRID =  "strId";
}
