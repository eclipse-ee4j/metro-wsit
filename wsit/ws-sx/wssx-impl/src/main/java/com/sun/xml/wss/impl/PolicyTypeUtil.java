/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: PolicyTypeUtil.java,v 1.2 2010-10-21 15:37:15 snajper Exp $
 */

package com.sun.xml.wss.impl;

import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.wss.impl.policy.SecurityPolicy;

/*
 * A type utility class for policies (useful for avoiding instanceof checks).
 */
public final class PolicyTypeUtil {

    private PolicyTypeUtil() {}

    public static final String SEC_POLICY_CONTAINER_TYPE = "SecurityPolicyContainer";
    public static final String DYN_SEC_POLICY_TYPE= "DynamicSecurityPolicy";
    public static final String SEC_POLICY_ALTERNATIVES_TYPE= "SecurityPolicyAlternatives";
    public static final String BOOLEAN_COMPOSER_TYPE = "BooleanComposer";
    public static final String APP_SEC_CONFIG_TYPE = "ApplicationSecurityConfiguration";
    public static final String DECL_SEC_CONFIG_TYPE = "DeclarativeSecurityConfiguration";
    public static final String MESSAGEPOLICY_CONFIG_TYPE = "MessagePolicy";

    public static final String AUTH_POLICY_TYPE = "AuthenticationTokenPolicy";
    public static final String SIGNATURE_POLICY_TYPE = "SignaturePolicy";
    public static final String ENCRYPTION_POLICY_TYPE = "EncryptionPolicy";
    public static final String TIMESTAMP_POLICY_TYPE = "TimestampPolicy";
    public static final String SIGNATURE_CONFIRMATION_POLICY_TYPE = "SignatureConfirmationPolicy";

    public static final String USERNAMETOKEN_TYPE = "UsernameTokenBinding";
    public static final String X509CERTIFICATE_TYPE = "X509CertificateBinding";
    public static final String SAMLASSERTION_TYPE = "SAMLAssertionBinding";
    public static final String SYMMETRIC_KEY_TYPE = "SymmetricKeyBinding";
    public static final String KERBEROS_BST_TYPE =  "KerberosTokenBinding";
    public static final String RSATOKEN_TYPE = "RsaTokenBinding";

    public static final String PRIVATEKEY_BINDING_TYPE = "PrivateKeyBinding";
    public static final String ENCRYPTION_POLICY_FEATUREBINDING_TYPE = "EncryptionPolicy.FeatureBinding";
    public static final String SIGNATURE_POLICY_FEATUREBINDING_TYPE = "SignaturePolicy.FeatureBinding";

    public static final String DERIVED_TOKEN_KEY_BINDING = "DerivedTokenKeyBinding";
    public static final String ISSUED_TOKEN_KEY_BINDING = "IssuedTokenKeyBinding";
    public static final String SECURE_CONVERSATION_TOKEN_KEY_BINDING = "SecureConversationTokenKeyBinding";

    public static final String MANDATORY_TARGET_POLICY_TYPE = "MandatoryTargetPolicy";
    public static final String MANDATORY_TARGET_FEATUREBINDING_TYPE = "MandatoryTargetPolicy.FeatureBinding";

    public static boolean isPrimaryPolicy(WSSPolicy policy) {
        if (policy == null) return false;

        return signaturePolicy(policy) || encryptionPolicy(policy);
    }

    public static boolean isSecondaryPolicy(WSSPolicy policy) {
        if (policy == null) return false;
        return authenticationTokenPolicy(policy) || timestampPolicy(policy);
    }

    public static boolean signaturePolicyFeatureBinding(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == SIGNATURE_POLICY_FEATUREBINDING_TYPE);
    }

    public static boolean encryptionPolicyFeatureBinding(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == ENCRYPTION_POLICY_FEATUREBINDING_TYPE);
    }

    public static boolean privateKeyBinding(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() ==  PRIVATEKEY_BINDING_TYPE);
    }

    public static boolean encryptionPolicy(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == ENCRYPTION_POLICY_TYPE);
    }

    public static boolean signaturePolicy(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == SIGNATURE_POLICY_TYPE);
    }

    public static boolean timestampPolicy(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == TIMESTAMP_POLICY_TYPE);
    }

    public static boolean signatureConfirmationPolicy(SecurityPolicy policy){
        if(policy == null) return false;
        return (policy.getType() == SIGNATURE_CONFIRMATION_POLICY_TYPE);
    }

    public static boolean authenticationTokenPolicy(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == AUTH_POLICY_TYPE);
    }

    public static boolean usernameTokenPolicy(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == USERNAMETOKEN_TYPE);
    }

   public static boolean usernameTokenBinding(SecurityPolicy policy){
       if(policy == null) return false;
       return (policy.getType() == USERNAMETOKEN_TYPE );
   }

    public static boolean x509CertificateBinding(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == X509CERTIFICATE_TYPE);
    }

    public static boolean keyValueTokenBinding(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == RSATOKEN_TYPE);
    }

    public static boolean kerberosTokenBinding(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == KERBEROS_BST_TYPE);
    }

    public static boolean samlTokenPolicy(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == SAMLASSERTION_TYPE);
    }

    public static boolean symmetricKeyBinding(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == SYMMETRIC_KEY_TYPE);
    }

    public static boolean booleanComposerPolicy(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == BOOLEAN_COMPOSER_TYPE);
    }

    public static boolean dynamicSecurityPolicy(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == DYN_SEC_POLICY_TYPE);
    }

    public static boolean messagePolicy(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == MESSAGEPOLICY_CONFIG_TYPE);
    }

    public static boolean applicationSecurityConfiguration(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == APP_SEC_CONFIG_TYPE);
    }

    public static boolean declarativeSecurityConfiguration(SecurityPolicy policy) {
        if (policy == null) return false;
        return (policy.getType() == DECL_SEC_CONFIG_TYPE);
    }

    public static boolean derivedTokenKeyBinding(SecurityPolicy policy) {
        if ( policy == null ) return false;
        return ( policy.getType() == DERIVED_TOKEN_KEY_BINDING);
    }

    public static boolean issuedTokenKeyBinding(SecurityPolicy policy) {
        if ( policy == null) return false;
        return ( policy.getType() == ISSUED_TOKEN_KEY_BINDING);
    }

    public static boolean secureConversationTokenKeyBinding(
              SecurityPolicy policy) {
        if ( policy == null) return false;
        return ( policy.getType() == SECURE_CONVERSATION_TOKEN_KEY_BINDING);
    }


    public static boolean isMandatoryTargetPolicy(SecurityPolicy policy){
        if ( policy == null) return false;
        return ( policy.getType() == MANDATORY_TARGET_POLICY_TYPE);
    }
}
