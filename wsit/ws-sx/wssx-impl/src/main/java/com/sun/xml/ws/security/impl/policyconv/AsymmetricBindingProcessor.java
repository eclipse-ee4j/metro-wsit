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

import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.security.impl.policy.Constants;
import com.sun.xml.ws.security.policy.Binding;
import com.sun.xml.ws.security.policy.EncryptedElements;
import com.sun.xml.ws.security.policy.EncryptedParts;
import com.sun.xml.ws.security.policy.AsymmetricBinding;
import com.sun.xml.ws.security.policy.SignedElements;
import com.sun.xml.ws.security.policy.SignedParts;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.TimestampPolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import java.util.Vector;
import java.util.logging.Level;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class AsymmetricBindingProcessor extends BindingProcessor {
    private final AsymmetricBinding binding;
  
    
    /** Creates a new instance of AsymmetricBindingProcessor */
    public AsymmetricBindingProcessor(AsymmetricBinding asBinding,XWSSPolicyContainer container,
            boolean isServer,boolean isIncoming,Vector<SignedParts> signedParts,Vector<EncryptedParts> encryptedParts,
            Vector<SignedElements> signedElements,Vector<EncryptedElements> encryptedElements) {
        this.binding = asBinding;
        this.container = container;
        this.isServer = isServer;
        this.isIncoming = isIncoming;
        protectionOrder = binding.getProtectionOrder();
        tokenProcessor = new TokenProcessor(isServer,isIncoming,pid);
        iAP = new IntegrityAssertionProcessor(binding.getAlgorithmSuite(),binding.isSignContent());
        eAP = new EncryptionAssertionProcessor(binding.getAlgorithmSuite(),false);
        this.signedParts = signedParts;
        this.signedElements = signedElements;
        this.encryptedElements = encryptedElements;
        this.encryptedParts = encryptedParts;
        
    }
    
    
    public void process()throws PolicyException{
        Token st = getSignatureToken();
        Token et = getEncryptionToken();
        if(st != null){
            primarySP = new SignaturePolicy();
            primarySP.setUUID(pid.generateID());
            if(Constants.logger.isLoggable(Level.FINEST)){
                Constants.logger.log(Level.FINEST,"ID of Primary signature policy is "+primarySP.getUUID());
            }            
            tokenProcessor.addKeyBinding(binding,primarySP,st,true);
            SignaturePolicy.FeatureBinding spFB = (SignaturePolicy.FeatureBinding)primarySP.getFeatureBinding();
            //spFB.setCanonicalizationAlgorithm(CanonicalizationMethod.EXCLUSIVE);
            SecurityPolicyUtil.setCanonicalizationMethod(spFB, binding.getAlgorithmSuite());
            spFB.isPrimarySignature(true);
        }
        if(et != null){
            primaryEP = new EncryptionPolicy();
            primaryEP.setUUID(pid.generateID());            
            tokenProcessor.addKeyBinding(binding,primaryEP,et,false);
            if(Constants.logger.isLoggable(Level.FINEST)){
                Constants.logger.log(Level.FINEST,"ID of Encryption policy is "+primaryEP.getUUID());
            }
        }
        if(protectionOrder == Binding.SIGN_ENCRYPT){
            container.insert(primarySP);
        }else{
            container.insert(primaryEP);
            container.insert(primarySP);
            
        }
        addPrimaryTargets();
        if(foundEncryptTargets && binding.getSignatureProtection()){
            if(Constants.logger.isLoggable(Level.FINEST)){
                Constants.logger.log(Level.FINEST,"PrimarySignature will be Encrypted");
            }
            protectPrimarySignature();
        }
        if(binding.isIncludeTimeStamp()){
            if(Constants.logger.isLoggable(Level.FINEST)){
                Constants.logger.log(Level.FINEST,"Timestamp header will be added to the message and will be Integrity protected ");
            }
            TimestampPolicy tp = new TimestampPolicy();
            tp.setUUID(pid.generateID());
            container.insert(tp);
            if(!binding.isDisableTimestampSigning()){
                protectTimestamp(tp);
            }
        }
        if(binding.getTokenProtection()){
            if(Constants.logger.isLoggable(Level.FINEST)){
                Constants.logger.log(Level.FINEST,"Token reference by primary signature with ID "+primarySP.getUUID()+" will be Integrity protected");
            }
            if (primarySP != null) {
                protectToken((WSSPolicy) primarySP.getKeyBinding());
            }
        }
        
    }
    
    protected Token getEncryptionToken(){
        if(isServer^isIncoming){
              Token token = binding.getInitiatorToken();
             if (token == null){
                token = binding.getRecipientEncryptionToken();
            }
            return token;
        }else{
            Token token= binding.getRecipientToken();
            if (token == null){
                token = binding.getInitiatorEncryptionToken();
            }

            return token;
        }
    }
    
    protected Token getSignatureToken(){
        if(isServer^isIncoming){
            Token token = binding.getRecipientToken();
            if (token == null){
                 token = binding.getRecipientSignatureToken();
            }

            return token;
        }else{
            Token token= binding.getInitiatorToken();
            if (token == null){
                token = binding.getInitiatorSignatureToken();
            }

            return token;
        }
    }
    
    @Override
    protected Binding getBinding(){
        return binding;
    }
    
    @Override
    protected EncryptionPolicy getSecondaryEncryptionPolicy() throws PolicyException{
        if(sEncPolicy == null){
            sEncPolicy  = new EncryptionPolicy();
            sEncPolicy.setUUID(pid.generateID());
            Token token = getEncryptionToken();
            tokenProcessor.addKeyBinding(binding,sEncPolicy,token,false);
            container.insert(sEncPolicy);
        }
        return sEncPolicy;
    }
    
    @Override
    protected void close(){
        
        if(protectionOrder == Binding.SIGN_ENCRYPT){
            container.insert(primaryEP);
        }
    }
}
