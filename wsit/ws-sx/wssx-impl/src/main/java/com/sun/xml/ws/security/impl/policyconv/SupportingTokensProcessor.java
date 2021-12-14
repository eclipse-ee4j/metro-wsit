/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.ws.security.impl.policy.X509Token;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.ws.security.policy.UserNameToken;
import com.sun.xml.ws.security.policy.AlgorithmSuite;
import com.sun.xml.ws.security.policy.Binding;
import com.sun.xml.ws.security.policy.EncryptedElements;
import com.sun.xml.ws.security.policy.EncryptedParts;
import com.sun.xml.ws.security.policy.SignedElements;
import com.sun.xml.ws.security.policy.SignedParts;
import com.sun.xml.ws.security.policy.SupportingTokens;
import com.sun.xml.ws.security.policy.Target;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.EncryptionTarget;
import com.sun.xml.wss.impl.policy.mls.IssuedTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.KeyBindingBase;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.QName;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.MessageConstants;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class SupportingTokensProcessor {
    protected TokenProcessor tokenProcessor = null;
    protected SignatureTargetCreator stc = null;
    protected EncryptionTargetCreator etc = null;
    protected Binding binding = null;
    protected XWSSPolicyContainer policyContainer = null;

    protected SignaturePolicy signaturePolicy = null;
    protected EncryptionPolicy encryptionPolicy = null;
    protected SupportingTokens st = null;
    protected IntegrityAssertionProcessor iAP = null;
    protected EncryptionAssertionProcessor eAP = null;

    protected ArrayList<SignaturePolicy> spList =null;
    protected ArrayList<EncryptionPolicy> epList =null;
    protected SignedParts emptySP  = null;
    protected boolean buildSP = false;
    protected boolean buildEP = false;
    protected PolicyID pid = null;

    protected SupportingTokensProcessor(){

    }
    /** Creates a new instance of SupportingTokensProcessor */
    public SupportingTokensProcessor(SupportingTokens st,TokenProcessor tokenProcessor,Binding binding,XWSSPolicyContainer container,SignaturePolicy sp,
            EncryptionPolicy ep,PolicyID pid) {
        this.st = st;
        this.tokenProcessor = tokenProcessor;
        this.binding = binding;
        this.pid =pid;
        this.policyContainer = container;
        this.encryptionPolicy  = ep;
        this.signaturePolicy = sp;
        AlgorithmSuite as = null;
        as = st.getAlgorithmSuite();
        if( as == null && binding != null){
            as = binding.getAlgorithmSuite();
        }
        boolean signContent = false;
        if(binding != null)
            signContent = binding.isSignContent();
        this.iAP = new IntegrityAssertionProcessor(as,signContent);
        this.eAP = new EncryptionAssertionProcessor(as,false);
        this.stc = iAP.getTargetCreator();
        this.etc = eAP.getTargetCreator();
        this.emptySP = getEmptySignedParts(st.getSignedParts());
    }

    public void process() throws PolicyException{
        Iterator tokens = st.getTokens();

        if(st.getEncryptedParts().hasNext() || st.getEncryptedElements().hasNext()){
            buildEP = true;
        }
        if(st.getSignedElements().hasNext() || st.getSignedParts().hasNext()){
            buildSP = true;
        }

        while(tokens.hasNext()){
            Token token = (Token) tokens.next();
            SecurityPolicyVersion spVersion = SecurityPolicyUtil.getSPVersion((PolicyAssertion)token);
            WSSPolicy policy = tokenProcessor.getWSSToken(token);
            if (this instanceof EndorsingSupportingTokensProcessor) {
                if (PolicyUtil.isUsernameToken((PolicyAssertion)token,spVersion)) {
                    AuthenticationTokenPolicy.UsernameTokenBinding utb =
                            (AuthenticationTokenPolicy.UsernameTokenBinding) policy;
                    utb.isEndorsing(true);                    
                }
            }
            if(PolicyUtil.isIssuedToken((PolicyAssertion) token, spVersion) &&
                    this instanceof EndorsingSupportingTokensProcessor){
                ((IssuedTokenKeyBinding)policy).setSTRID(null);
            }
            if ( policy.getUUID() != null ) {

                addToPrimarySignature(policy,token);

                encryptToken(token, spVersion);

                if(PolicyUtil.isSamlToken((PolicyAssertion)token, spVersion)){
                    correctSAMLBinding(policy);
                }

                collectSignaturePolicies(token);
                if(buildEP){
                    EncryptionPolicy ep = new EncryptionPolicy();
                    ep.setKeyBinding(policy);
                    getEPList().add(ep);
                }
            }

           //TODO:: Add token to MessagePolicy;
            if (!(this instanceof EndorsingSupportingTokensProcessor) ||
                    (this instanceof EndorsingSupportingTokensProcessor && token instanceof X509Token && token.getIncludeToken().endsWith("Never"))) {
                AuthenticationTokenPolicy atp = new AuthenticationTokenPolicy();
                atp.setFeatureBinding(policy);
                policyContainer.insert(atp);
            }
            //TODO: Take care of targets.
            addTargets();
        }
    }

    protected void collectSignaturePolicies(Token token) throws PolicyException{
        if(buildSP){
            createSupportingSignature(token);
        }
    }

    protected void createSupportingSignature(Token token) throws PolicyException{
        SignaturePolicy sp = new SignaturePolicy();
        sp.setUUID(pid.generateID());
        tokenProcessor.addKeyBinding(binding,sp,token,true);
        if(binding != null && binding.getTokenProtection()){
            protectToken((WSSPolicy) sp.getKeyBinding(), sp);
        }
        SignaturePolicy.FeatureBinding spFB = (com.sun.xml.wss.impl.policy.mls.SignaturePolicy.FeatureBinding)sp.getFeatureBinding();
        //spFB.setCanonicalizationAlgorithm(CanonicalizationMethod.EXCLUSIVE);
        AlgorithmSuite as = null;
        as = st.getAlgorithmSuite();
        if( as == null && binding != null){
            as = binding.getAlgorithmSuite();
        }
        SecurityPolicyUtil.setCanonicalizationMethod(spFB, as);
        //   sp.setKeyBinding(policy);
        getSPList().add(sp);
        endorseSignature(sp);
    }
    protected void addToPrimarySignature(WSSPolicy policy,Token token) throws PolicyException{
        //no-op
    }

    protected void endorseSignature(SignaturePolicy sp){
        //no-op
    }

    protected ArrayList<SignaturePolicy> getSPList(){
        if(spList == null){
            spList = new ArrayList<>();
        }
        return spList;
    }

    protected ArrayList<EncryptionPolicy> getEPList(){
        if(epList == null){
            epList = new ArrayList<>();
        }
        return epList;
    }

    protected void encryptToken(Token token, SecurityPolicyVersion spVersion)throws PolicyException{
        if(PolicyUtil.isUsernameToken((PolicyAssertion) token, spVersion) &&
                ((UserNameToken)token).hasPassword() &&
                !((UserNameToken)token).useHashPassword()){
            if ( binding != null && token.getTokenId()!= null ) {
                EncryptionPolicy.FeatureBinding fb =(EncryptionPolicy.FeatureBinding) encryptionPolicy.getFeatureBinding();
                EncryptionTarget et = etc.newURIEncryptionTarget(token.getTokenId());
                fb.addTargetBinding(et);
            }
        }
    }


    protected SignedParts getEmptySignedParts(Iterator itr){
        while(itr.hasNext()){
            Target target = (Target)itr.next();
            SecurityPolicyVersion spVersion = SecurityPolicyUtil.getSPVersion((PolicyAssertion)target);
            if(PolicyUtil.isSignedParts((PolicyAssertion)target, spVersion)){
                if(SecurityPolicyUtil.isSignedPartsEmpty((SignedParts) target)){
                    return (SignedParts) target;
                }
            }
        }
        return null;
    }

    protected void addTargets(){
        if(binding != null && Binding.SIGN_ENCRYPT.equals(binding.getProtectionOrder())){
            if(spList != null){
                populateSignaturePolicy();
            }
            if(epList != null){
                populateEncryptionPolicy();
            }
        }else{
            if(epList != null){
                populateEncryptionPolicy();
            }
            if(spList != null){
                populateSignaturePolicy();
            }
        }
    }

    protected void populateSignaturePolicy(){
        for(SignaturePolicy sp : spList){
            SignaturePolicy.FeatureBinding spFB = (SignaturePolicy.FeatureBinding)sp.getFeatureBinding();
            if(emptySP != null){
                iAP.process(emptySP,spFB);
            }else{
                Iterator<SignedParts>itr = st.getSignedParts();
                while(itr.hasNext()){
                    SignedParts target = itr.next();
                    iAP.process(target,spFB);
                }
            }
            Iterator<SignedElements> itr = st.getSignedElements();
            while(itr.hasNext()){
                SignedElements target = itr.next();
                iAP.process(target,spFB);
            }
            policyContainer.insert(sp);
        }
        spList.clear();
    }

    protected void populateEncryptionPolicy(){
        for(EncryptionPolicy ep :epList){
            EncryptionPolicy.FeatureBinding epFB = (EncryptionPolicy.FeatureBinding)ep.getFeatureBinding();
            Iterator<EncryptedElements> itr = st.getEncryptedElements();
            while(itr.hasNext()){
                EncryptedElements target = itr.next();
                eAP.process(target,epFB);
            }
            Iterator<EncryptedParts> epr = st.getEncryptedParts();
            while(epr.hasNext()){
                EncryptedParts target = epr.next();
                eAP.process(target,epFB);
            }
            policyContainer.insert(ep);
        }
    }

    protected void protectToken(WSSPolicy token,SignaturePolicy sp){
        String uid = token.getUUID();
        boolean strIgnore = false;
        String includeToken = ((KeyBindingBase) token).getIncludeToken();
        if(includeToken.endsWith("AlwaysToRecipient") ||includeToken.endsWith("Always")){
           strIgnore = true;
        }
        if ( uid != null ) {
            SignatureTargetCreator stcr = iAP.getTargetCreator();
            SignatureTarget stg = stcr.newURISignatureTarget(uid);
            SecurityPolicyUtil.setName(stg, token);
            if(!strIgnore){
                stcr.addSTRTransform(stg);
                stg.setPolicyQName(getQName(token));
            } else  {
               stcr.addTransform(stg);
            }            
            SignaturePolicy.FeatureBinding fb = (com.sun.xml.wss.impl.policy.mls.SignaturePolicy.FeatureBinding) sp.getFeatureBinding();
            fb.addTargetBinding(stg);
        }
    }

    protected void correctSAMLBinding(WSSPolicy policy) {
        //no-op
    }

    protected QName getQName(WSSPolicy token) {
        QName qName =null;
         if (PolicyTypeUtil.usernameTokenBinding(token)) {
            qName = new QName(MessageConstants.WSSE_NS, MessageConstants.USERNAME_TOKEN_LNAME);
        } else if (PolicyTypeUtil.x509CertificateBinding(token)) {
            qName = new QName(MessageConstants.WSSE_NS, MessageConstants.WSSE_BINARY_SECURITY_TOKEN_LNAME);
        } else if (PolicyTypeUtil.samlTokenPolicy(token)) {
            qName = new QName(MessageConstants.WSSE_NS, "SAMLToken");
        }
        return qName;
    }
}
