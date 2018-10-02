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

import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.security.policy.Binding;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.ws.security.policy.SupportingTokens;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class EndorsingSupportingTokensProcessor extends SupportingTokensProcessor {
    
    protected SignaturePolicy primarySP= null;
    /** Creates a new instance of EndorsingSupportingTokensProcessor */
    public EndorsingSupportingTokensProcessor(SupportingTokens st,TokenProcessor tokenProcessor,Binding binding,
            XWSSPolicyContainer container,SignaturePolicy sp,EncryptionPolicy ep,PolicyID pid) {
        super(st,tokenProcessor,binding,container,sp,ep,pid);
    }
    
    protected void addToPrimarySignature(WSSPolicy policy,Token token) throws PolicyException{
    }
    
    
    protected void collectSignaturePolicies(Token token) throws PolicyException{
        createSupportingSignature(token);
    }
    
    protected void endorseSignature(SignaturePolicy sp){
        SignaturePolicy.FeatureBinding spFB = (SignaturePolicy.FeatureBinding)sp.getFeatureBinding();
        SignatureTarget sigTarget = stc.newURISignatureTarget(signaturePolicy.getUUID());
        stc.addTransform(sigTarget);
        SecurityPolicyUtil.setName(sigTarget, signaturePolicy);
        spFB.addTargetBinding(sigTarget);
        spFB.isEndorsingSignature(true);
    }
    
    protected void correctSAMLBinding(WSSPolicy policy) {
        ((AuthenticationTokenPolicy.SAMLAssertionBinding)policy).setAssertionType(AuthenticationTokenPolicy.SAMLAssertionBinding.HOK_ASSERTION);
    }
    
    @Override
    protected void encryptToken(Token token, SecurityPolicyVersion spVersion)throws PolicyException{
    }
}
