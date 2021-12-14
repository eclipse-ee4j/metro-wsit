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

import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.security.policy.Binding;
import com.sun.xml.ws.security.policy.EndorsingSupportingTokens;
import com.sun.xml.ws.security.policy.SignedElements;
import com.sun.xml.ws.security.policy.SignedEncryptedSupportingTokens;
import com.sun.xml.ws.security.policy.SignedEndorsingSupportingTokens;
import com.sun.xml.ws.security.policy.SignedParts;
import com.sun.xml.ws.security.policy.SignedSupportingTokens;
import com.sun.xml.ws.security.policy.SupportingTokens;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.ws.security.policy.TransportBinding;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.IssuedTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import com.sun.xml.wss.impl.policy.mls.TimestampPolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import java.util.Iterator;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class TransportBindingProcessor extends BindingProcessor {
    private TransportBinding binding = null;
    private TimestampPolicy tp = null;
    private boolean buildSP = false;
    private boolean buildEP = false;    
    
    /** Creates a new instance of TransportBindingProcessor */
    public TransportBindingProcessor(TransportBinding binding,boolean isServer,boolean isIncoming,XWSSPolicyContainer container){
        this.binding = binding;
        this.container = container;
        this.isIncoming = isIncoming;
        this.isServer = isServer;
        iAP = new IntegrityAssertionProcessor(binding.getAlgorithmSuite(),false);
        eAP = new EncryptionAssertionProcessor(binding.getAlgorithmSuite(),false);        
        this.tokenProcessor  = new TokenProcessor(isServer,isIncoming,pid);        
    }
    
    public void process() throws PolicyException{
        container.setPolicyContainerMode(binding.getLayout());
        if(binding.isIncludeTimeStamp()){
            tp = new TimestampPolicy();
            tp.setUUID(pid.generateID());
            container.insert(tp);
        }
    }
    
    @Override
    public void processSupportingTokens(SupportingTokens tokens) throws PolicyException{
        Iterator itr = tokens.getTokens();
        while(itr.hasNext()){
            Token token = (Token) itr.next();
            WSSPolicy policy = tokenProcessor.getWSSToken(token);
            if (policy instanceof IssuedTokenKeyBinding){
                ((IssuedTokenKeyBinding)policy).setSTRID(null);
            }else if (policy instanceof AuthenticationTokenPolicy.SAMLAssertionBinding){
                ((AuthenticationTokenPolicy.SAMLAssertionBinding)policy).setSTRID(null);
            }
            AuthenticationTokenPolicy atp = new AuthenticationTokenPolicy();
            atp.setFeatureBinding(policy);
            container.insert(atp);
        }
    }
    
    @Override
    public void processSupportingTokens(SignedSupportingTokens sst) throws PolicyException{
        Iterator itr = sst.getTokens();
        while(itr.hasNext()){
            Token token = (Token) itr.next();
            WSSPolicy policy = tokenProcessor.getWSSToken(token);
            if (policy instanceof IssuedTokenKeyBinding){
                ((IssuedTokenKeyBinding)policy).setSTRID(null);
            }else if (policy instanceof AuthenticationTokenPolicy.SAMLAssertionBinding){
                ((AuthenticationTokenPolicy.SAMLAssertionBinding)policy).setSTRID(null);
            }
            AuthenticationTokenPolicy atp = new AuthenticationTokenPolicy();
            atp.setFeatureBinding(policy);
            container.insert(atp);
        }
    }
    
    @Override
    public void processSupportingTokens(EndorsingSupportingTokens est) throws PolicyException{
        Iterator itr = est.getTokens();        
        if(est.getSignedElements().hasNext() || est.getSignedParts().hasNext()){
            buildSP = true;
        }
        while(itr.hasNext()){
            Token token = (Token) itr.next();
            SignaturePolicy sp = new SignaturePolicy();            
            SignaturePolicy.FeatureBinding spFB = (com.sun.xml.wss.impl.policy.mls.SignaturePolicy.FeatureBinding)sp.getFeatureBinding();
            //spFB.setCanonicalizationAlgorithm(CanonicalizationMethod.EXCLUSIVE);
            SecurityPolicyUtil.setCanonicalizationMethod(spFB, binding.getAlgorithmSuite());
            sp.setUUID(pid.generateID());
            tokenProcessor.addKeyBinding(binding,sp,token,false);
            // container.insert(sp.getKeyBinding());
            
            if(tp != null ){
                SignatureTarget target = iAP.getTargetCreator().newURISignatureTarget(tp.getUUID());
                iAP.getTargetCreator().addTransform(target);
                SecurityPolicyUtil.setName(target, tp);
                // there is no primary signature in Transport Binding
                //spFB.isEndorsingSignature(true);
                spFB.addTargetBinding(target);                                
            }
            /* Uncommenting this feature, as WCF fixed this on their side
             * Feature is : SignedParts/SignedElement support under EndorsingSuppotingToken for TransportBinding             
             */
            if(buildSP){
                Iterator<SignedParts>itr_sp = est.getSignedParts();
                while(itr_sp.hasNext()){
                    SignedParts target = itr_sp.next();
                    iAP.process(target,spFB);
                }
                Iterator<SignedElements> itr_se = est.getSignedElements();
                while(itr_se.hasNext()){
                    SignedElements target = itr_se.next();
                    iAP.process(target,spFB);
                }                
            }
            
            container.insert(sp);            
        }                
    }
    
    @Override
    public void processSupportingTokens(SignedEndorsingSupportingTokens set) throws PolicyException{
        Iterator itr = set.getTokens();
        while(itr.hasNext()){
            Token token = (Token) itr.next();
            SignaturePolicy sp = new SignaturePolicy();
            sp.setUUID(pid.generateID());
            SignaturePolicy.FeatureBinding spFB = (com.sun.xml.wss.impl.policy.mls.SignaturePolicy.FeatureBinding)sp.getFeatureBinding();
            //spFB.setCanonicalizationAlgorithm(CanonicalizationMethod.EXCLUSIVE);
            SecurityPolicyUtil.setCanonicalizationMethod(spFB, binding.getAlgorithmSuite());
            tokenProcessor.addKeyBinding( binding,sp,token,false);

            //protect primary signature
            
            if(tp != null){
                SignatureTarget target = iAP.getTargetCreator().newURISignatureTarget(tp.getUUID());
                iAP.getTargetCreator().addTransform(target);
                SecurityPolicyUtil.setName(target, tp);
                //SignaturePolicy.FeatureBinding spFB = (SignaturePolicy.FeatureBinding)sp.getFeatureBinding();
                //spFB.isEndorsingSignature(true);
                spFB.addTargetBinding(target);
                container.insert(sp);
            }           
        }
    }
    
    @Override
    public void processSupportingTokens(SignedEncryptedSupportingTokens sest) throws PolicyException{
        Iterator itr = sest.getTokens();
        while(itr.hasNext()){
            Token token = (Token) itr.next();
            WSSPolicy policy = tokenProcessor.getWSSToken(token);
            if (policy instanceof IssuedTokenKeyBinding){
                ((IssuedTokenKeyBinding)policy).setSTRID(null);
            }else if (policy instanceof AuthenticationTokenPolicy.SAMLAssertionBinding){
                ((AuthenticationTokenPolicy.SAMLAssertionBinding)policy).setSTRID(null);
            }
            AuthenticationTokenPolicy atp = new AuthenticationTokenPolicy();
            atp.setFeatureBinding(policy);
            container.insert(atp);
        }
    }        
    
    @Override
    protected EncryptionPolicy getSecondaryEncryptionPolicy() throws PolicyException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected Binding getBinding() {
        return binding;
    }        
    
    @Override
    protected void close(){
      
    }         
}
