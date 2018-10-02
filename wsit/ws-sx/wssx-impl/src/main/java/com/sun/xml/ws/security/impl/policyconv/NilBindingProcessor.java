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
import com.sun.xml.ws.security.policy.SupportingTokens;
import com.sun.xml.ws.security.policy.Binding;
import com.sun.xml.ws.security.policy.EncryptedSupportingTokens;
import com.sun.xml.ws.security.policy.EndorsingSupportingTokens;
import com.sun.xml.ws.security.policy.MessageLayout;
import com.sun.xml.ws.security.policy.SignedEncryptedSupportingTokens;
import com.sun.xml.ws.security.policy.SignedEndorsingSupportingTokens;
import com.sun.xml.ws.security.policy.SignedSupportingTokens;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.TimestampPolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;

/**
 *
 * @author ashutoshshahi
 */
public class NilBindingProcessor extends BindingProcessor{
    
    public NilBindingProcessor(boolean isServer,boolean isIncoming,XWSSPolicyContainer container){
        this.container = container;
        this.isIncoming = isIncoming;
        this.isServer = isServer;
        this.tokenProcessor  = new TokenProcessor(isServer,isIncoming,pid);
    }
    
    public void process() throws PolicyException{
        container.setPolicyContainerMode(MessageLayout.Strict);
    }
    
    @Override
    protected void protectPrimarySignature()throws PolicyException{
        
    }
    
    @Override
    protected void protectTimestamp(TimestampPolicy tp){
        
    }
    
    @Override
    protected void protectToken(WSSPolicy token){
        
    }
    
    @Override
    protected void protectToken(WSSPolicy token,boolean ignoreSTR){
        
    }
    
    @Override
    protected void addPrimaryTargets()throws PolicyException{
        
    }
    
    @Override
    protected boolean requireSC(){
        return false;
    }

    @Override
    protected EncryptionPolicy getSecondaryEncryptionPolicy() throws PolicyException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void processSupportingTokens(SupportingTokens st) throws PolicyException{
        
        SupportingTokensProcessor stp =  new SupportingTokensProcessor(
                st, tokenProcessor,getBinding(),container,primarySP,primaryEP,pid);
        stp.process();
    }
    
    @Override
    public void processSupportingTokens(SignedSupportingTokens st) throws PolicyException{
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void processSupportingTokens(EndorsingSupportingTokens est) throws PolicyException{
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void processSupportingTokens(SignedEndorsingSupportingTokens est) throws PolicyException{
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void processSupportingTokens(SignedEncryptedSupportingTokens sest) throws PolicyException{
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void processSupportingTokens(EncryptedSupportingTokens est) throws PolicyException{
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected SignaturePolicy getSignaturePolicy(){
        return null;
    }

    @Override
    protected Binding getBinding() {
        return null;
    }

    @Override
    protected void close() {
        
    }

}
