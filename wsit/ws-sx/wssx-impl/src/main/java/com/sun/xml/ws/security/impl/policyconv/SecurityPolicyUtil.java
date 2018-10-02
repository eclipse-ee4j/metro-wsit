/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policyconv;

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.Target;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.namespace.QName;
import com.sun.xml.ws.security.policy.EncryptedParts;
import com.sun.xml.ws.security.policy.SignedParts;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.ws.security.impl.policy.Constants;
import com.sun.xml.ws.security.impl.policy.PolicyUtil;
import com.sun.xml.ws.security.policy.AlgorithmSuite;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class SecurityPolicyUtil {
    
    private static final QName signaturePolicy = new QName(MessageConstants.DSIG_NS, MessageConstants.SIGNATURE_LNAME);
    private static final QName usernameTokenPolicy = new QName(MessageConstants.WSSE_NS, MessageConstants.USERNAME_TOKEN_LNAME);
    private static final QName x509TokenPolicy = new QName(MessageConstants.WSSE_NS, "BinarySecurityToken");
    private static final QName timestampPolicy = new QName(MessageConstants.WSU_NS, MessageConstants.TIMESTAMP_LNAME);
    
    /** Creates a new instance of SecurityPolicyUtil */
    public SecurityPolicyUtil() {
    }
    
    public static boolean isSignedPartsEmpty(SignedParts sp){
        if(!(sp.hasBody() || sp.hasAttachments())){
            if(!sp.getHeaders().hasNext()){
                return true;
            }
        }
        return false;
    }
    
    public static boolean isEncryptedPartsEmpty(EncryptedParts ep){
        if(!(ep.hasBody() || ep.hasAttachments())){
            if(!ep.getTargets().hasNext()){
                return true;
            }
        }
        return false;
    }
    
    public static String convertToXWSSConstants(String type){
        if(type.contains(Token.REQUIRE_THUMBPRINT_REFERENCE)){
            return MessageConstants.THUMB_PRINT_TYPE;
        }else if(type.contains(Token.REQUIRE_KEY_IDENTIFIER_REFERENCE)){
            return MessageConstants.KEY_INDETIFIER_TYPE;
        }else if(type.contains(Token.REQUIRE_ISSUER_SERIAL_REFERENCE)){
            return MessageConstants.X509_ISSUER_TYPE ;
        }
        throw new UnsupportedOperationException(type+"  is not supported");
    }
    
    public static void setName(Target target, WSSPolicy policy){
        if(target.getType() == Target.TARGET_TYPE_VALUE_URI){
            target.setPolicyQName(getQNameFromPolicy(policy));
        }
    }
    
    private static QName getQNameFromPolicy(WSSPolicy policy){
        if(PolicyTypeUtil.signaturePolicy(policy)){
            return signaturePolicy;
        } else if(PolicyTypeUtil.timestampPolicy(policy)){
            return timestampPolicy;
        } else if(PolicyTypeUtil.x509CertificateBinding(policy)){
            return x509TokenPolicy;
        } else if(PolicyTypeUtil.usernameTokenPolicy(policy)){
            return usernameTokenPolicy;
        } else if(PolicyTypeUtil.secureConversationTokenKeyBinding(policy)){
            return MessageConstants.SCT_NAME;
        } else if (PolicyTypeUtil.samlTokenPolicy(policy)) {
            return new QName(MessageConstants.WSSE_NS, "SAMLToken");
        }
        return null;
    }
    
    public static void setCanonicalizationMethod(SignaturePolicy.FeatureBinding spFB, AlgorithmSuite algorithmSuite){
        if(algorithmSuite != null && algorithmSuite.getAdditionalProps().contains(Constants.InclusiveC14N)){
            spFB.setCanonicalizationAlgorithm(CanonicalizationMethod.INCLUSIVE);
        } else{
            spFB.setCanonicalizationAlgorithm(CanonicalizationMethod.EXCLUSIVE);
        }
        
        if(algorithmSuite != null && algorithmSuite.getAdditionalProps().contains(Constants.InclusiveC14NWithCommentsForCm)){
            spFB.setCanonicalizationAlgorithm(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS);
        } else if(algorithmSuite != null && algorithmSuite.getAdditionalProps().contains(Constants.ExclusiveC14NWithCommentsForCm)){
            spFB.setCanonicalizationAlgorithm(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS);
        }
    }
    
    public static SecurityPolicyVersion getSPVersion(PolicyAssertion pa){
        String nsUri = pa.getName().getNamespaceURI();
        SecurityPolicyVersion spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
        return spVersion;
    }
}
