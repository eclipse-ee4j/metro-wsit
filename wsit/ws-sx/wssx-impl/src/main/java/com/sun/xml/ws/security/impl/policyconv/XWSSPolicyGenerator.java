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

import com.sun.xml.ws.security.impl.policy.Constants;
import com.sun.xml.ws.security.impl.policy.PolicyUtil;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.security.impl.policy.LogStringsMessages;
import com.sun.xml.ws.security.impl.policy.Trust10;
import com.sun.xml.ws.security.impl.policy.Trust13;
import com.sun.xml.ws.security.policy.AsymmetricBinding;
import com.sun.xml.ws.security.policy.AlgorithmSuite;
import com.sun.xml.ws.security.policy.Binding;
import com.sun.xml.ws.security.policy.EncryptedElements;
import com.sun.xml.ws.security.policy.EncryptedParts;
import com.sun.xml.ws.security.policy.EncryptedSupportingTokens;
import com.sun.xml.ws.security.policy.EndorsingEncryptedSupportingTokens;
import com.sun.xml.ws.security.policy.EndorsingSupportingTokens;
import com.sun.xml.ws.security.policy.RequiredElements;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.ws.security.policy.SignedElements;
import com.sun.xml.ws.security.policy.SignedEncryptedSupportingTokens;
import com.sun.xml.ws.security.policy.SignedEndorsingEncryptedSupportingTokens;
import com.sun.xml.ws.security.policy.SignedEndorsingSupportingTokens;
import com.sun.xml.ws.security.policy.SignedParts;
import com.sun.xml.ws.security.policy.SignedSupportingTokens;
import com.sun.xml.ws.security.policy.SupportingTokens;
import com.sun.xml.ws.security.policy.SymmetricBinding;
import com.sun.xml.ws.security.policy.TransportBinding;
import com.sun.xml.wss.impl.policy.PolicyGenerationException;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import com.sun.xml.ws.security.policy.WSSAssertion;
import java.util.logging.Level;


/**
 * Will convert WS Security Policy to XWSS policy.
 * WS Security Policies are  digested and cached by the
 * first pipe. The same objects are shared across instances
 * of the pipe(refer to Pipe javadoc for more information)
 *
 * @author K.Venugopal@sun.com
 */

public class XWSSPolicyGenerator {

    String _protectionOrder = "";

    SignaturePolicy _primarySP  = null;
    EncryptionPolicy _primaryEP = null;
    //current secondary encryption policy
    EncryptionPolicy _sEncPolicy = null;
    SignaturePolicy _csSP = null;
    XWSSPolicyContainer _policyContainer = null;
    Binding _binding;
    Policy effectivePolicy = null;
    boolean isServer = false;
    boolean isIncoming = false;
    private PolicyAssertion wssAssertion = null;
    private WSSAssertion wss11 = null;
    private Trust10 trust10 = null;
    private Trust13 trust13 = null;
    //true if signed by primary signature
    //private boolean signBody = false;

    //true if encrypted by primary encryption policy
    private boolean encryptBody = false;
    //private HashSet<Header> signParts  = new HashSet<Header>();

    private Vector<SignedParts> signedParts = new Vector<>();
    private Vector<EncryptedParts> encryptedParts = new Vector<>();
    private Vector<SignedElements> signedElements = new Vector<>();
    private Vector<EncryptedElements> encryptedElements = new Vector<>();
    private boolean ignoreST = false;
    private boolean transportBinding = false;
    private IntegrityAssertionProcessor iAP = null;
    private EncryptionAssertionProcessor eAP = null;
    private Binding policyBinding = null;
    private List<RequiredElements> reqElements = new ArrayList<>();
    private SecurityPolicyVersion spVersion;
    private boolean isIssuedTokenAsEncryptedSupportingToken = false;
    /** Creates a new instance of WSPolicyProcessorImpl
     */
    //public XWSSPolicyGenerator(AssertionSet assertionSet,boolean isServer,boolean isIncoming){
    public XWSSPolicyGenerator(Policy effectivePolicy,boolean isServer,boolean isIncoming,
            SecurityPolicyVersion spVersion){
        this.effectivePolicy = effectivePolicy;
        this._policyContainer = new XWSSPolicyContainer(isServer,isIncoming);
        this.isServer = isServer;
        this.isIncoming = isIncoming;
        this.spVersion = spVersion;
    }

    public XWSSPolicyGenerator(Policy effectivePolicy,boolean isServer,boolean isIncoming){
        this.effectivePolicy = effectivePolicy;
        this._policyContainer = new XWSSPolicyContainer(isServer,isIncoming);
        this.isServer = isServer;
        this.isIncoming = isIncoming;
        this.spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    }

    public AlgorithmSuite getBindingLevelAlgSuite(){
        if(_binding != null) {
            return _binding.getAlgorithmSuite();
        }
        else {
            return new com.sun.xml.ws.security.impl.policy.AlgorithmSuite();
        }
    }

    public void process(boolean ignoreST) throws PolicyException {
        this.ignoreST = ignoreST;
        process();
    }


    public void process() throws PolicyException {
        collectPolicies();
        PolicyAssertion binding = (PolicyAssertion)getBinding();
        policyBinding =(Binding) binding;
        if(binding == null){
            //log error.
            //logger.log(Level.SEVERE,LogStringsMessages.SP_0105_ERROR_BINDING_ASSR_NOT_PRESENT());
            //throw new PolicyException(LogStringsMessages.SP_0105_ERROR_BINDING_ASSR_NOT_PRESENT());
            // We handle this now
            NilBindingProcessor nbp = new NilBindingProcessor(isServer, isIncoming,_policyContainer);
            nbp.process();
            processNonBindingAssertions(nbp);
            return;
        }
        if(PolicyUtil.isTransportBinding(binding, spVersion)){
            if(Constants.logger.isLoggable(Level.FINE)){
                Constants.logger.log(Level.FINE, "TransportBinding was configured in the policy");
            }
            TransportBindingProcessor tbp= new TransportBindingProcessor((TransportBinding)binding,isServer, isIncoming,_policyContainer);
            tbp.process();
            processNonBindingAssertions(tbp);
            transportBinding = true;
        }else{

            iAP = new IntegrityAssertionProcessor(_binding.getAlgorithmSuite(),_binding.isSignContent());
            eAP = new EncryptionAssertionProcessor(_binding.getAlgorithmSuite(),false);

            _policyContainer.setPolicyContainerMode(_binding.getLayout());
            if(PolicyUtil.isSymmetricBinding(binding.getName(), spVersion)) {

                if(Constants.logger.isLoggable(Level.FINE)){
                    Constants.logger.log(Level.FINE, "SymmetricBinding was configured in the policy");
                }
                SymmetricBindingProcessor sbp =  new SymmetricBindingProcessor((SymmetricBinding) _binding, _policyContainer,
                        isServer, isIncoming,signedParts,encryptedParts,
                        signedElements,encryptedElements);
                if(wssAssertion != null && PolicyUtil.isWSS11(wssAssertion, spVersion)){
                    sbp.setWSS11((WSSAssertion)wssAssertion);
                }
                sbp.process();
                processNonBindingAssertions(sbp);
                sbp.close();

            }else if(PolicyUtil.isAsymmetricBinding(binding.getName(), spVersion) ){

                if(Constants.logger.isLoggable(Level.FINE)){
                    Constants.logger.log(Level.FINE, "AsymmetricBinding was configured in the policy");
                }
                AsymmetricBindingProcessor abp = new AsymmetricBindingProcessor((AsymmetricBinding) _binding, _policyContainer,
                        isServer, isIncoming,signedParts,encryptedParts,
                        signedElements,encryptedElements);
                if( wssAssertion != null && PolicyUtil.isWSS11(wssAssertion, spVersion)){
                    abp.setWSS11((WSSAssertion)wssAssertion);
                }
                abp.process();
                processNonBindingAssertions(abp);
                abp.close();
            }
        }
    }

    public MessagePolicy getXWSSPolicy()throws PolicyException{
        MessagePolicy mp = null;
        try{
            if (wssAssertion != null) {
                mp = _policyContainer.getMessagePolicy(PolicyUtil.isWSS11(wssAssertion, spVersion));
            } else {
                mp = _policyContainer.getMessagePolicy(false);
            }
        }catch(PolicyGenerationException ex){
            Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0113_UNABLE_TO_DIGEST_POLICY(effectivePolicy),ex);
            throw new PolicyException("Unable to digest SecurityPolicy ");
        }
        //try{
        if(wssAssertion != null){
            try{
                mp.setWSSAssertion(getWssAssertion((com.sun.xml.ws.security.policy.WSSAssertion) wssAssertion));
            } catch (PolicyGenerationException ex) {
                Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0104_ERROR_SIGNATURE_CONFIRMATION_ELEMENT(ex.getMessage()),ex);
                throw new PolicyException(LogStringsMessages.SP_0104_ERROR_SIGNATURE_CONFIRMATION_ELEMENT(ex.getMessage()));
            }
        }
        if(policyBinding!= null && policyBinding.getAlgorithmSuite() != null){
            mp.setAlgorithmSuite(getAlgoSuite(policyBinding.getAlgorithmSuite()));
        }
        if(policyBinding!= null && policyBinding.getLayout()!= null){
            mp.setLayout(getLayout(policyBinding.getLayout()));
        }
        if(isIncoming && reqElements.size() > 0){
            try {
                com.sun.xml.ws.security.impl.policyconv.RequiredElementsProcessor rep =
                        new com.sun.xml.ws.security.impl.policyconv.RequiredElementsProcessor(reqElements, mp);
                rep.process();
            } catch (PolicyGenerationException ex) {
                Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0103_ERROR_REQUIRED_ELEMENTS(ex.getMessage()),ex);
                throw new PolicyException(LogStringsMessages.SP_0103_ERROR_REQUIRED_ELEMENTS(ex.getMessage()));
            }
        }
        if(transportBinding){
            mp.setSSL(transportBinding);
        }
        return mp;
    }

    private void processNonBindingAssertions(BindingProcessor bindingProcessor) throws PolicyException{
        for(AssertionSet assertionSet: effectivePolicy){
            for(PolicyAssertion assertion:assertionSet){
                if(PolicyUtil.isBinding(assertion, spVersion)){
                }else if(!ignoreST && shouldAddST() && PolicyUtil.isSupportingToken(assertion, spVersion)){
                    bindingProcessor.processSupportingTokens((SupportingTokens)assertion);
                } else if(!ignoreST && shouldAddST() && PolicyUtil.isSignedSupportingToken(assertion, spVersion)){
                    bindingProcessor.processSupportingTokens((SignedSupportingTokens)assertion);
                }else if(!ignoreST && shouldAddST() && PolicyUtil.isEndorsedSupportingToken(assertion, spVersion)){
                    bindingProcessor.processSupportingTokens((EndorsingSupportingTokens)assertion);
                }else if(!ignoreST && shouldAddST() && PolicyUtil.isSignedEndorsingSupportingToken(assertion, spVersion)){
                    bindingProcessor.processSupportingTokens((SignedEndorsingSupportingTokens)assertion);
                }else if(!ignoreST && shouldAddST() && PolicyUtil.isSignedEncryptedSupportingToken(assertion, spVersion)){
                    bindingProcessor.processSupportingTokens((SignedEncryptedSupportingTokens)assertion);
                    isIssuedTokenAsEncryptedSupportingToken(bindingProcessor.isIssuedTokenAsEncryptedSupportingToken());
                }else if(!ignoreST && shouldAddST() && PolicyUtil.isEncryptedSupportingToken(assertion, spVersion)){
                    bindingProcessor.processSupportingTokens((EncryptedSupportingTokens)assertion);
                    isIssuedTokenAsEncryptedSupportingToken(bindingProcessor.isIssuedTokenAsEncryptedSupportingToken());
                }else if(!ignoreST && shouldAddST() && PolicyUtil.isEndorsingEncryptedSupportingToken(assertion, spVersion)){
                    bindingProcessor.processSupportingTokens((EndorsingEncryptedSupportingTokens)assertion);
                    isIssuedTokenAsEncryptedSupportingToken(bindingProcessor.isIssuedTokenAsEncryptedSupportingToken());
                }else if(!ignoreST && shouldAddST() && PolicyUtil.isSignedEndorsingEncryptedSupportingToken(assertion, spVersion)){
                    bindingProcessor.processSupportingTokens((SignedEndorsingEncryptedSupportingTokens)assertion);
                    isIssuedTokenAsEncryptedSupportingToken(bindingProcessor.isIssuedTokenAsEncryptedSupportingToken());
                }else if(PolicyUtil.isWSS10(assertion, spVersion)){
                    wssAssertion = assertion;
                }else if(PolicyUtil.isWSS11(assertion, spVersion)){
                    wssAssertion = assertion;
                }else if(PolicyUtil.isTrust10(assertion, spVersion)){
                    trust10 = (Trust10)assertion;
                }else if(PolicyUtil.isTrust13(assertion, spVersion)){
                    trust13 = (Trust13)assertion;
                }
            }
        }
    }

    private Binding getBinding(){
        return _binding;
    }

    private void collectPolicies(){
        for(AssertionSet assertionSet: effectivePolicy){
            for(PolicyAssertion assertion:assertionSet){
                if(PolicyUtil.isSignedParts(assertion, spVersion)){
                    signedParts.add((SignedParts)assertion);
                }else if(PolicyUtil.isEncryptParts(assertion, spVersion)){
                    encryptedParts.add((EncryptedParts)assertion);
                }else if(PolicyUtil.isSignedElements(assertion, spVersion)){
                    signedElements.add((SignedElements)assertion);
                }else if(PolicyUtil.isEncryptedElements(assertion, spVersion)){
                    encryptedElements.add((EncryptedElements)assertion);
                }else if(PolicyUtil.isWSS10(assertion, spVersion)){
                    wssAssertion = assertion;
                }else if(PolicyUtil.isWSS11(assertion, spVersion)){
                    wssAssertion = assertion;
                }else if(PolicyUtil.isTrust10(assertion, spVersion)){
                    trust10 = (Trust10)assertion;
                }else if(PolicyUtil.isTrust13(assertion, spVersion)){
                    trust13 = (Trust13)assertion;
                }else if(PolicyUtil.isBinding(assertion, spVersion)){
                    _binding =(Binding) assertion;
                }else if(PolicyUtil.isRequiredElements(assertion, spVersion)){
                    reqElements.add((RequiredElements)assertion);
                }
            }
        }
    }

    private boolean shouldAddST(){
        if(isServer && !isIncoming){
            return false;
        }

        if(!isServer && isIncoming){
            return false;
        }
        return true;
    }

    protected com.sun.xml.wss.impl.AlgorithmSuite getAlgoSuite(AlgorithmSuite suite) {
        com.sun.xml.wss.impl.AlgorithmSuite als = new com.sun.xml.wss.impl.AlgorithmSuite(
                suite.getDigestAlgorithm(),
                suite.getEncryptionAlgorithm(),
                suite.getSymmetricKeyAlgorithm(),
                suite.getAsymmetricKeyAlgorithm());
        als.setSignatureAlgorithm(suite.getSignatureAlgorithm());
        return als;
    }

    protected com.sun.xml.wss.impl.WSSAssertion getWssAssertion(WSSAssertion asser) {
        com.sun.xml.wss.impl.WSSAssertion assertion = new com.sun.xml.wss.impl.WSSAssertion(
                asser.getRequiredProperties(),
                asser.getType());
        return assertion;
    }

    protected com.sun.xml.wss.impl.MessageLayout getLayout(
            com.sun.xml.ws.security.policy.MessageLayout layout) {

        switch(layout) {
            case Strict :{
                if(Constants.logger.isLoggable(Level.FINE)){
                    Constants.logger.log(Level.FINE,"MessageLayout has been configured to be  STRICT ");
                }
                return com.sun.xml.wss.impl.MessageLayout.Strict;
            }
            case Lax :{
                if(Constants.logger.isLoggable(Level.FINE)){
                    Constants.logger.log(Level.FINE,"MessageLayout has been configured to be LAX ");
                }
                return com.sun.xml.wss.impl.MessageLayout.Lax;
            }
            case LaxTsFirst :{
                if(Constants.logger.isLoggable(Level.FINE)){
                    Constants.logger.log(Level.FINE,"MessageLayout has been configured to be LaxTimestampFirst ");
                }
                return com.sun.xml.wss.impl.MessageLayout.LaxTsFirst;
            }
            case LaxTsLast :{
                if(Constants.logger.isLoggable(Level.FINE)){
                    Constants.logger.log(Level.FINE,"MessageLayout has been configured tp be LaxTimestampLast ");
                }
                return com.sun.xml.wss.impl.MessageLayout.LaxTsLast;
            }default :{
                if(Constants.logger.isLoggable(Level.SEVERE)){
                    Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0106_UNKNOWN_MESSAGE_LAYOUT(layout));
                }
                throw new RuntimeException(LogStringsMessages.SP_0106_UNKNOWN_MESSAGE_LAYOUT(layout));
            }
        }
    }

    public boolean isIssuedTokenAsEncryptedSupportingToken(){
        return this.isIssuedTokenAsEncryptedSupportingToken;
    }

    private void isIssuedTokenAsEncryptedSupportingToken(boolean value){
        this.isIssuedTokenAsEncryptedSupportingToken = value;
    }

}
