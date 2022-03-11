/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policyconv;

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.security.impl.policy.PolicyUtil;
import com.sun.xml.ws.security.policy.Binding;
import com.sun.xml.ws.security.policy.SupportingTokens;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class SignedEndorsingSupportingTokensProcessor extends EndorsingSupportingTokensProcessor {


    /** Creates a new instance of EndorsingSupportingTokensProcessor */
    public SignedEndorsingSupportingTokensProcessor(SupportingTokens st,TokenProcessor tokenProcessor,Binding binding,
            XWSSPolicyContainer container,SignaturePolicy sp,EncryptionPolicy ep,PolicyID pid) {
        super(st,tokenProcessor,binding,container,sp,ep,pid);
    }


    @Override
    protected void addToPrimarySignature(WSSPolicy policy, Token token) throws PolicyException{
        String includeToken = token.getIncludeToken();
        SecurityPolicyVersion spVersion = SecurityPolicyUtil.getSPVersion((PolicyAssertion) token);
        SignatureTarget target = null;
        if (includeToken.endsWith("Never") && PolicyUtil.isX509Token((PolicyAssertion) token, spVersion)) {
            String uid = pid.generateID();
            ((AuthenticationTokenPolicy.X509CertificateBinding) policy).setSTRID(uid);
            target = stc.newURISignatureTargetForSSToken(uid);
           //this flag will be used for computing securitytokenreference when the includetoken type is Never !!
            target.isITNever(true);
        } else {
            target = stc.newURISignatureTargetForSSToken(policy.getUUID());
        }
        SecurityPolicyUtil.setName(target, policy);

        if((!PolicyUtil.isUsernameToken((PolicyAssertion) token, spVersion) &&
           !spVersion.includeTokenAlways.equals(includeToken) &&
           !spVersion.includeTokenAlwaysToRecipient.equals(includeToken)) || PolicyUtil.isSamlToken((PolicyAssertion)token,spVersion)
           || PolicyUtil.isIssuedToken((PolicyAssertion)token,spVersion)){
            stc.addSTRTransform(target);
            target.setPolicyQName(getQName(policy));
        } else {
             stc.addTransform(target);
        }
        SignaturePolicy.FeatureBinding spFB = (SignaturePolicy.FeatureBinding)signaturePolicy.getFeatureBinding();
        spFB.addTargetBinding(target);
    }
}
