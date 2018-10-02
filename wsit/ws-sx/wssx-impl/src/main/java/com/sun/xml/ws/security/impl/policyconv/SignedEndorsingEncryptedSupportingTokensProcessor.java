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
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.security.impl.policy.PolicyUtil;
import com.sun.xml.ws.security.policy.Binding;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.ws.security.policy.SignedEndorsingEncryptedSupportingTokens;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.EncryptionTarget;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class SignedEndorsingEncryptedSupportingTokensProcessor extends SignedEndorsingSupportingTokensProcessor{
    private boolean isIssuedTokenAsEncryptedSupportingToken = false;
    
    /** Creates a new instance of SignedEndorsingEncryptedSupportingTokensProcessor */
    public SignedEndorsingEncryptedSupportingTokensProcessor(SignedEndorsingEncryptedSupportingTokens st,TokenProcessor tokenProcessor,Binding binding,
            XWSSPolicyContainer container,SignaturePolicy sp,EncryptionPolicy ep,PolicyID pid) {
        super(st,tokenProcessor,binding,container,sp,ep,pid);
    }
    
    protected void encryptToken(Token token, SecurityPolicyVersion spVersion)throws PolicyException{    
        if ( token.getTokenId()!= null ) {
            EncryptionPolicy.FeatureBinding fb =(EncryptionPolicy.FeatureBinding) encryptionPolicy.getFeatureBinding();
            EncryptionTarget et = etc.newURIEncryptionTarget(token.getTokenId());
            fb.addTargetBinding(et);
            
            if(PolicyUtil.isIssuedToken((PolicyAssertion) token, spVersion)){
                isIssuedTokenAsEncryptedSupportingToken = true;
            }
        }   
    }
    
    protected boolean isIssuedTokenAsEncryptedSupportingToken(){
        return isIssuedTokenAsEncryptedSupportingToken;
    }
    
}
