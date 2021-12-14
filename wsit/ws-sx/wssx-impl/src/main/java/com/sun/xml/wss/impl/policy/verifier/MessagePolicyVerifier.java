/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * MessagePolicyVerifier.java
 *
 * Created on 26 October, 2006, 5:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl.policy.verifier;

import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.opt.impl.util.SOAPUtil;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.PolicyViolationException;
import com.sun.xml.wss.impl.WSSAssertion;
import com.sun.xml.wss.impl.WssSoapFaultException;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.impl.policy.MLSPolicy;
import com.sun.xml.wss.impl.policy.PolicyGenerationException;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy.SAMLAssertionBinding;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy.UsernameTokenBinding;
import com.sun.xml.wss.impl.policy.mls.DerivedTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.EncryptionTarget;
import com.sun.xml.wss.impl.policy.mls.IssuedTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.policy.mls.SecureConversationTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import com.sun.xml.wss.impl.policy.mls.SymmetricKeyBinding;
import com.sun.xml.wss.impl.policy.mls.Target;
import com.sun.xml.wss.impl.policy.mls.TimestampPolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.wss.impl.policy.spi.PolicyVerifier;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessagePolicyVerifier implements PolicyVerifier{
    private ProcessingContext ctx = null;
    private TargetResolver targetResolver;
    
    private static Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    /** Creates a new instance of MessagePolicyVerifier */
    public MessagePolicyVerifier(ProcessingContext ctx, TargetResolver targetResolver) {
        this.ctx = ctx;
        this.targetResolver = targetResolver;
    }
    /**
     * verifies whether inferred and actual security policies are same or not
     * @param ip SecurityPolicy
     * @param ap SecurityPolicy
     */
    @Override
    public void verifyPolicy(SecurityPolicy ip, SecurityPolicy ap) throws PolicyViolationException {
        
        MessagePolicy actualPolicy = (MessagePolicy)ap;
        MessagePolicy inferredSecurityPolicy = (MessagePolicy)ip;
        JAXBFilterProcessingContext context = null;
        if (ctx instanceof JAXBFilterProcessingContext) {
            context = (JAXBFilterProcessingContext)ctx;
        }
        //this code has been moved from SecurityRecipient.
        //because in the presence of alternatives this check has to be done
        //with a specific actualpolicy only.
        if (actualPolicy != null) {
            if (actualPolicy.isSSL() && context != null && !context.isSecure()) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_1601_SSL_NOT_ENABLED());
                throw new XWSSecurityRuntimeException(LogStringsMessages.WSS_1601_SSL_NOT_ENABLED());
            }
        }

        if(actualPolicy == null || actualPolicy.size() <= 0){
            if ((inferredSecurityPolicy != null) && (inferredSecurityPolicy.size() > 0)) {
                //this could be a plain SSL scenario
                if (!checkAllowExtraTimestamp(inferredSecurityPolicy)) {
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0805_POLICY_NULL());
                    throw new PolicyViolationException("ERROR: Policy for the service could not be obtained");
                }
            }
        } else if(inferredSecurityPolicy == null || inferredSecurityPolicy.size() <= 0){
            throw new PolicyViolationException("ERROR: No security header found in the message");
        } else{ // verify policy now
            try{
                for(int i = 0; i < actualPolicy.size(); i++) {
                    WSSPolicy actualPol = (WSSPolicy)actualPolicy.get(i);
                    if(PolicyTypeUtil.isSecondaryPolicy(actualPol)){
                        processSecondaryPolicy(actualPol, inferredSecurityPolicy);
                    } else if(PolicyTypeUtil.isPrimaryPolicy(actualPol)){
                        processPrimaryPolicy(actualPol, inferredSecurityPolicy);
                    }
                }
                
            } catch(Exception e){
                throw new PolicyViolationException(e);
            }
        }
    }

    private boolean isEncryptedSignature(WSSPolicy actualPol, WSSPolicy inferredPol) {
       if (PolicyTypeUtil.signaturePolicy(actualPol) && 
               PolicyTypeUtil.encryptionPolicy(inferredPol)) {
           EncryptionPolicy pol = (EncryptionPolicy)inferredPol;
           EncryptionPolicy.FeatureBinding fb = 
                   (EncryptionPolicy.FeatureBinding)pol.getFeatureBinding();
           if (fb.encryptsSignature()) {
               return true;
           }
       }
       return false;
    }
    
    /**
     * processes secondary policies
     * @param actualPol WSSPolicy
     * @param inferredSecurityPolicy MessagePolicy
     */
    private  void processSecondaryPolicy(WSSPolicy actualPol,
            MessagePolicy inferredSecurityPolicy) throws XWSSecurityException{
        try{
            if(PolicyTypeUtil.timestampPolicy(actualPol)){  
                boolean found = false;
                for(int j = 0; j < inferredSecurityPolicy.size(); j++) {
                    WSSPolicy pol = (WSSPolicy)inferredSecurityPolicy.get(j);
                    if(PolicyTypeUtil.timestampPolicy(pol)){
                        inferredSecurityPolicy.remove(pol);
                        found = true;
                        break;
                    }
                }
                if(!found){
                    if (MessageConstants.debug) {
                        log.log(Level.WARNING, LogStringsMessages.WSS_0276_INVALID_POLICY_NO_TIMESTAMP_SEC_HEADER());
                    }
                    // commenting for now, uncomment once this is corrected in SecurityPolicy
                    /*throw new XWSSecurityException("Policy Verification error:"
                            + "Timestamp not found in configured policy but occurs in message");*/
                }
            } else if(PolicyTypeUtil.usernameTokenPolicy(actualPol.getFeatureBinding())){
                boolean found = false;
                for(int j = 0; j < inferredSecurityPolicy.size(); j++) {
                    WSSPolicy pol = (WSSPolicy)inferredSecurityPolicy.get(j);
                    if(PolicyTypeUtil.usernameTokenPolicy(pol)){
                        AuthenticationTokenPolicy.UsernameTokenBinding actual =
                                (UsernameTokenBinding) actualPol.getFeatureBinding();
                        AuthenticationTokenPolicy.UsernameTokenBinding inferred =
                                (UsernameTokenBinding) pol;
                        if (inferred.hasNoPassword() && !actual.hasNoPassword()) {
                            throw SOAPUtil.newSOAPFaultException(
                                    MessageConstants.WSSE_FAILED_AUTHENTICATION,
                                    "Empty Password specified, Authentication of Username Password Token Failed",
                                    null, true);
                        }                        
                        //SP1.3
                        if(actual.getUseCreated() == true && inferred.getUseCreated() == false ){
                            throw SOAPUtil.newSOAPFaultException(
                                    MessageConstants.WSSE_INVALID_SECURITY_TOKEN,
                                    "Invalid Username Password Token. Missing Created ",
                                    null, true);
                        }
                        
                        if( actual.getUseNonce() == true && inferred.getUseNonce() == false){
                            throw SOAPUtil.newSOAPFaultException(
                                    MessageConstants.WSSE_INVALID_SECURITY_TOKEN,
                                    "Invalid Username Password Token. Missing Nonce ",
                                    null, true);
                        }
                        
                        inferredSecurityPolicy.remove(pol);
                        found = true;
                        break;
                    }
                }
                if(!found){
                    if(!((WSSPolicy)actualPol.getFeatureBinding()).isOptional()){
                       throw new XWSSecurityException("Policy Verification error:"
                            + "UsernameToken not found in message but occurs in configured policy");
                    }                    
                }
            } else if (PolicyTypeUtil.samlTokenPolicy(actualPol.getFeatureBinding())) {
                boolean found = false;
                for (int j = 0; j < inferredSecurityPolicy.size(); j++) {
                    WSSPolicy pol = (WSSPolicy) inferredSecurityPolicy.get(j);
                    if (PolicyTypeUtil.samlTokenPolicy(pol)) {
                        //AuthenticationTokenPolicy.SAMLAssertionBinding actual = (SAMLAssertionBinding) actualPol.getFeatureBinding();
                        //AuthenticationTokenPolicy.SAMLAssertionBinding inferred = (SAMLAssertionBinding) pol;
                        inferredSecurityPolicy.remove(pol);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    if(!((WSSPolicy)actualPol.getFeatureBinding()).isOptional()){
                       throw new XWSSecurityException("Policy Verification error:"
                            + "SAML Token not found in message but occurs in configured policy");
                    }
                }
            }else if (PolicyTypeUtil.issuedTokenKeyBinding(actualPol.getFeatureBinding())) {
                boolean found = false;
                for (int j = 0; j < inferredSecurityPolicy.size(); j++) {
                    WSSPolicy pol = (WSSPolicy) inferredSecurityPolicy.get(j);
                    if (PolicyTypeUtil.samlTokenPolicy(pol)) {
                        AuthenticationTokenPolicy.SAMLAssertionBinding actual =
                                (SAMLAssertionBinding) actualPol.getFeatureBinding().getKeyBinding();
                        AuthenticationTokenPolicy.SAMLAssertionBinding inferred =
                                (SAMLAssertionBinding) pol;
                        inferredSecurityPolicy.remove(pol);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new XWSSecurityException("Policy Verification error:" + "SAML token  not found in message but occurs in configured policy");
                }
            }
        } catch (WssSoapFaultException e) {
            throw e;
        } catch(Exception e){
            throw new XWSSecurityException(e);
        }
    }
    /**
     * processes primary policies
     * @param actualPol WSSPolicy
     * @param inferredSecurityPolicy MessagePolicy
     */
     @SuppressWarnings("unchecked")
    private  void processPrimaryPolicy(WSSPolicy actualPol,
            MessagePolicy inferredSecurityPolicy) throws XWSSecurityException{
        
        //WSSAssertion wssAssertion = ((ProcessingContextImpl)ctx).getWSSAssertion();
        if(PolicyTypeUtil.signaturePolicy(actualPol)){
            SignaturePolicy actualSignPolicy = (SignaturePolicy)actualPol;
            boolean isEndorsing = ((SignaturePolicy.FeatureBinding)actualSignPolicy.getFeatureBinding()).isEndorsingSignature();
            boolean isPrimary = ((SignaturePolicy.FeatureBinding)actualSignPolicy.getFeatureBinding()).isPrimarySignature();
            int nth = 0;
            WSSPolicy pol = getFirstPrimaryPolicy(inferredSecurityPolicy, isEndorsing, nth++);
            if(pol == null && isOptionalPolicy(actualSignPolicy) == true){
                    return;
            }
            if(pol == null){
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0268_ERROR_POLICY_VERIFICATION());
                throw new XWSSecurityException("Policy verification error:" +
                        "Missing Signature Element");
                }
            
            if(PolicyTypeUtil.signaturePolicy(pol)){
                SignaturePolicy inferredPol = (SignaturePolicy)pol;
                // verify key binding
                boolean isKBTrue = verifyKeyBinding(actualSignPolicy.getKeyBinding(), inferredPol.getKeyBinding(),
                        false);                
                while(!isKBTrue && !isPrimary){
                    pol = getFirstPrimaryPolicy(inferredSecurityPolicy, isEndorsing, nth++);                    
                    if (pol == null && isOptionalPolicy(actualSignPolicy) == true) {
                        return;
                    }
                    if(pol == null){
                        log.log(Level.SEVERE, LogStringsMessages.WSS_0268_ERROR_POLICY_VERIFICATION());
                        throw new XWSSecurityException("Policy verification error:" +
                                "Missing Signature Element - perhaps a second supporting signature or " +
                                "Incorrect Key types or references were used in Signature");
                    }
                    inferredPol = (SignaturePolicy)pol;
                    isKBTrue = verifyKeyBinding(actualSignPolicy.getKeyBinding(), inferredPol.getKeyBinding(),
                            false);
                    //nth++;
                }
                // verify target binding
                boolean isTBTrue = verifySignTargetBinding((SignaturePolicy.FeatureBinding)actualSignPolicy.getFeatureBinding(),
                        (SignaturePolicy.FeatureBinding)inferredPol.getFeatureBinding());
                
                inferredSecurityPolicy.remove(pol);
                if(!isKBTrue){
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0206_POLICY_VIOLATION_EXCEPTION());
                    throw new XWSSecurityException("Policy verification error: " +
                            "Incorrect Key types or references were used in Signature");
                }
                if(!isTBTrue){
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0206_POLICY_VIOLATION_EXCEPTION());
                    throw new XWSSecurityException("Policy verification error: " +
                            "One or more Signed Parts could not be validated");
                }
                checkTargets(actualPol, pol);
            } else{
                //It could be a case of Extra Security, an Encrypted Signature
                //when the policy just requires a Signature
                if (!isEncryptedSignature(actualPol, pol)) {
                    //check to see for the case when no Signature Target present in message
                    //The incoming message will not have Signature policy in that case.
                    if (checkTargetPresence(actualPol)) {
                        log.log(Level.SEVERE, LogStringsMessages.WSS_0206_POLICY_VIOLATION_EXCEPTION());
                        throw new XWSSecurityException("Signature Policy verification error: Looking for a Signature Element " + " in Security header, but found " + pol + ".");
                    }
                } else {
                    inferredSecurityPolicy.remove(pol);
                }
            }
        } else if(PolicyTypeUtil.encryptionPolicy(actualPol)){
            EncryptionPolicy actualEncryptionPolicy = (EncryptionPolicy)actualPol;
            WSSPolicy pol = getFirstPrimaryPolicy(inferredSecurityPolicy, false, 0);
            if(pol == null){
                log.log(Level.SEVERE, LogStringsMessages.WSS_0269_ERROR_ENCRYPTIONPOLICY_VERIFICATION());
                throw new XWSSecurityException("Encryption Policy verification error:" +
                        "Missing encryption element");
            }
            
            if(PolicyTypeUtil.encryptionPolicy(pol)){
                EncryptionPolicy inferredPol = (EncryptionPolicy)pol;
                //verify key binding
                boolean isKBTrue = verifyKeyBinding(actualEncryptionPolicy.getKeyBinding(),
                        inferredPol.getKeyBinding(), true);
                // verify target binding
                boolean isTBTrue = verifyEncTargetBinding((EncryptionPolicy.FeatureBinding)actualEncryptionPolicy.getFeatureBinding(),
                        (EncryptionPolicy.FeatureBinding)inferredPol.getFeatureBinding());
                
                inferredSecurityPolicy.remove(pol);
                if(!isKBTrue){
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0206_POLICY_VIOLATION_EXCEPTION());
                    throw new XWSSecurityException("Encryption Policy verification error: " +
                            "Incorrect Key types or references were used in encryption");
                }
                if(!isTBTrue){
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0206_POLICY_VIOLATION_EXCEPTION());
                    throw new XWSSecurityException("Policy verification error: " +
                            "One or more encrypted parts could not be validated");
                }
                List<Target> inferredList = ((EncryptionPolicy.FeatureBinding)pol.getFeatureBinding()).getTargetBindings();
                List<Target> actualList = ((EncryptionPolicy.FeatureBinding)actualPol.getFeatureBinding()).getTargetBindings();
                if(actualList.size() > inferredList.size()){
                    int nthEncrypt = 0;
                    EncryptionPolicy inferredPol2 = getNthEncryptionPolicy(inferredSecurityPolicy, nthEncrypt);
                    while(inferredPol2 != null){
                        boolean isKBTrue2 = verifyKeyBinding(actualEncryptionPolicy.getKeyBinding(),
                                inferredPol2.getKeyBinding(), true);
                        boolean isTBTrue2 = verifyEncTargetBinding((EncryptionPolicy.FeatureBinding)actualEncryptionPolicy.getFeatureBinding(),
                                (EncryptionPolicy.FeatureBinding)inferredPol2.getFeatureBinding());
                        if(!isKBTrue2 || !isTBTrue2){
                            nthEncrypt++;
                            inferredPol2 = getNthEncryptionPolicy(inferredSecurityPolicy, nthEncrypt);
                        } else{
                            List<Target> moreTargets = ((EncryptionPolicy.FeatureBinding)inferredPol2.getFeatureBinding()).getTargetBindings();
                            for(Target moreTarget : moreTargets){
                                ((EncryptionPolicy.FeatureBinding)inferredPol.getFeatureBinding()).addTargetBinding(moreTarget);
                            }
                            if(actualList.size() == inferredList.size()){
                                inferredSecurityPolicy.remove(inferredPol2);
                                break;
                            }
                            inferredSecurityPolicy.remove(inferredPol2);
                            nthEncrypt++;
                            inferredPol2 = getNthEncryptionPolicy(inferredSecurityPolicy, nthEncrypt);
                        }
                    }
                }
                checkTargets(actualPol, pol);
            } else{
                //check to see for the case when no Encryption Target present in message
                //The incoming message will not have Encryption policy in that case.
                if(checkTargetPresence(actualPol)){
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0206_POLICY_VIOLATION_EXCEPTION());
                    throw new XWSSecurityException("Encryption Policy verification error: Looking for an Encryption Element "
                            + " in Security header, but found " +  pol +  ".");
                }
            }
            
        }
        
    }
     @SuppressWarnings("unchecked")
    private void checkTargets(WSSPolicy actualPol, WSSPolicy inferredPol) throws XWSSecurityException{
        
        List<Target> inferredTargets = null;
        List<Target> actualTargets = null;
        
        if(PolicyTypeUtil.signaturePolicy(actualPol)){
            
            SignaturePolicy.FeatureBinding inferredFeatureBinding =
                    (SignaturePolicy.FeatureBinding)inferredPol.getFeatureBinding();
            SignaturePolicy.FeatureBinding actualFeatureBinding =
                    (SignaturePolicy.FeatureBinding)actualPol.getFeatureBinding();
            
            inferredTargets = (List<Target>)inferredFeatureBinding.getTargetBindings();
            actualTargets = (List<Target>)actualFeatureBinding.getTargetBindings();
            
        }else if(PolicyTypeUtil.encryptionPolicy(actualPol)){
            
            EncryptionPolicy.FeatureBinding inferredFeatureBinding =
                    (EncryptionPolicy.FeatureBinding)inferredPol.getFeatureBinding();
            EncryptionPolicy.FeatureBinding actualFeatureBinding =
                    (EncryptionPolicy.FeatureBinding)actualPol.getFeatureBinding();
            
            inferredTargets = (List<Target>)inferredFeatureBinding.getTargetBindings();
            actualTargets = (List<Target>)actualFeatureBinding.getTargetBindings();
            
        }
        targetResolver.resolveAndVerifyTargets(actualTargets, inferredTargets, actualPol);
    }
    /**
     * verifies whether actual and inferred key bindings are same or not
     * @param actualKeyBinding MLSPolicy
     * @param inferredKeyBinding MLSPolicy
     * @param isEncryptPolicy boolean
     * @return verified boolean
     */
    private boolean verifyKeyBinding(MLSPolicy actualKeyBinding,
            MLSPolicy inferredKeyBinding, boolean isEncryptPolicy)
            throws XWSSecurityException {
        boolean verified = false;
        if(actualKeyBinding != null && inferredKeyBinding != null){
            if (PolicyTypeUtil.usernameTokenBinding(actualKeyBinding) &&
                    PolicyTypeUtil.usernameTokenBinding(inferredKeyBinding)) {
                UsernameTokenBinding act = (UsernameTokenBinding) actualKeyBinding;
                UsernameTokenBinding inf = (UsernameTokenBinding) inferredKeyBinding;
                
                if (act.getUseCreated() == true && inf.getUseCreated() == false) { //SP13
                    throw new XWSSecurityException("Policy verification error: Invalid Usernametoken, Missing Created");
                } else if (act.getUseNonce() == true && inf.getUseNonce() == false) {
                    throw new XWSSecurityException("Policy verification error: Invalid Usernametoken, Missing Nonce");
                } else {
                    verified = true;
                }
            } else if (PolicyTypeUtil.x509CertificateBinding(actualKeyBinding) &&
                    PolicyTypeUtil.x509CertificateBinding(inferredKeyBinding)) {
                    /* TODO: cannot change actual policy, there seems to be a bug in
                     * security policy
                    AuthenticationTokenPolicy.X509CertificateBinding actualX509Bind =
                            (AuthenticationTokenPolicy.X509CertificateBinding)actualKeyBinding;
                    AuthenticationTokenPolicy.X509CertificateBinding inferredX509Bind =
                            (AuthenticationTokenPolicy.X509CertificateBinding)inferredKeyBinding;
                    // workaround - policy sets reference type as Thumprint
                    if(actualX509Bind.getReferenceType().equals(MessageConstants.THUMB_PRINT_TYPE))
                        actualX509Bind.setReferenceType(MessageConstants.KEY_INDETIFIER_TYPE);
                    correctIncludeTokenPolicy(actualX509Bind, wssAssertion);
                    if(actualX509Bind.getReferenceType().equals(inferredX509Bind.getReferenceType()))*/
                verified =  true;
            } else if(PolicyTypeUtil.kerberosTokenBinding(actualKeyBinding) && 
                    PolicyTypeUtil.kerberosTokenBinding(inferredKeyBinding)){ 
                verified = true;
            } else if(PolicyTypeUtil.symmetricKeyBinding(actualKeyBinding) &&
                    PolicyTypeUtil.symmetricKeyBinding(inferredKeyBinding)){
                verified = verifyKeyBinding(
                        actualKeyBinding.getKeyBinding(), inferredKeyBinding.getKeyBinding(),
                        isEncryptPolicy);
                if(((SymmetricKeyBinding)inferredKeyBinding).usesEKSHA1KeyBinding() && PolicyTypeUtil.usernameTokenBinding(actualKeyBinding.getKeyBinding())){
                    verified = true;
                }
            } else if(PolicyTypeUtil.issuedTokenKeyBinding(actualKeyBinding) &&
                    PolicyTypeUtil.issuedTokenKeyBinding(inferredKeyBinding)){
                
                verified = true;
            } else if(PolicyTypeUtil.secureConversationTokenKeyBinding(actualKeyBinding) &&
                    PolicyTypeUtil.secureConversationTokenKeyBinding(inferredKeyBinding)){
                
                verified = true;
            } else if(PolicyTypeUtil.derivedTokenKeyBinding(actualKeyBinding) &&
                    PolicyTypeUtil.derivedTokenKeyBinding(inferredKeyBinding)){
                
                verified = verifyKeyBinding(((DerivedTokenKeyBinding)actualKeyBinding).getOriginalKeyBinding(),
                        ((DerivedTokenKeyBinding)inferredKeyBinding).getOriginalKeyBinding(),
                        isEncryptPolicy);
            } else if (PolicyTypeUtil.usernameTokenBinding(actualKeyBinding) &&
                    PolicyTypeUtil.symmetricKeyBinding(inferredKeyBinding)){
                 MLSPolicy  ikbkb = inferredKeyBinding.getKeyBinding();
                 if (isEncryptPolicy && PolicyTypeUtil.usernameTokenBinding(ikbkb)) {
                    verified = true;
                }
            } else if (PolicyTypeUtil.x509CertificateBinding(actualKeyBinding) &&
                    PolicyTypeUtil.symmetricKeyBinding(inferredKeyBinding)){
                MLSPolicy ikbkb = inferredKeyBinding.getKeyBinding();
                if (isEncryptPolicy && PolicyTypeUtil.x509CertificateBinding(ikbkb)) {
                    verified = true;
                }
            } else if (PolicyTypeUtil.kerberosTokenBinding(actualKeyBinding) &&
                    PolicyTypeUtil.symmetricKeyBinding(inferredKeyBinding)){
                MLSPolicy ikbkb = inferredKeyBinding.getKeyBinding();
                if (isEncryptPolicy && PolicyTypeUtil.kerberosTokenBinding(ikbkb)) {
                    verified = true;
                }
            } else if (PolicyTypeUtil.samlTokenPolicy(actualKeyBinding) &&
                    PolicyTypeUtil.symmetricKeyBinding(inferredKeyBinding)){
                MLSPolicy ikbkb = inferredKeyBinding.getKeyBinding();
                if (isEncryptPolicy && PolicyTypeUtil.samlTokenPolicy(ikbkb)) {
                    verified = true;
                }
            } else if (PolicyTypeUtil.samlTokenPolicy(actualKeyBinding) &&
                    PolicyTypeUtil.samlTokenPolicy(inferredKeyBinding)){
                
                verified = true;
            } else if (PolicyTypeUtil.symmetricKeyBinding(actualKeyBinding) &&
                    PolicyTypeUtil.usernameTokenBinding(inferredKeyBinding)) {
                MLSPolicy akbkb = actualKeyBinding.getKeyBinding();
                if (isEncryptPolicy && PolicyTypeUtil.usernameTokenBinding(akbkb)) {
                    verified = true;
                }
            } else if (PolicyTypeUtil.symmetricKeyBinding(actualKeyBinding) &&
                    PolicyTypeUtil.x509CertificateBinding(inferredKeyBinding)) {
                MLSPolicy akbkb = actualKeyBinding.getKeyBinding();
                if (isEncryptPolicy && PolicyTypeUtil.x509CertificateBinding(akbkb)) {
                    verified = true;
                }
            } else if (PolicyTypeUtil.derivedTokenKeyBinding(actualKeyBinding)) {
                //workaround for IssuedToken under Endorsing, with PublicKey inside IssuedToken
                if (PolicyTypeUtil.issuedTokenKeyBinding(inferredKeyBinding) &&
                        PolicyTypeUtil.issuedTokenKeyBinding(
                        ((DerivedTokenKeyBinding)actualKeyBinding).getOriginalKeyBinding())) {
                    verified = true;
                }
            }else if (PolicyTypeUtil.keyValueTokenBinding(actualKeyBinding) &&
                      PolicyTypeUtil.keyValueTokenBinding(inferredKeyBinding)) {
                verified = true;
            }
        }
        
        return verified;
    }
    /**
     * verifies the signature target bindings
     * @param actualFeatureBinding SignaturePolicy.FeatureBinding
     * @param inferredFeatureBinding SignaturePolicy.FeatureBinding
     * @return boolean
     */
    private boolean verifySignTargetBinding(SignaturePolicy.FeatureBinding actualFeatureBinding,
            SignaturePolicy.FeatureBinding inferredFeatureBinding) throws XWSSecurityException {
        String actualCanonAlgo = actualFeatureBinding.getCanonicalizationAlgorithm();
        String inferredCanonAlgo = inferredFeatureBinding.getCanonicalizationAlgorithm();
        
        if(actualCanonAlgo == null || inferredCanonAlgo == null){
            throw new XWSSecurityException("ActualCanonicalizationAlgorithm or InferredCanonicalizationAlgorithm "
                    +" is null while verifying SignatureTargetBinding");
        }
        if(actualCanonAlgo.length() >0 && inferredCanonAlgo.length() >0 ){
            if(!inferredCanonAlgo.equals(actualCanonAlgo)){
                log.warning("Receiver side requirement verification failed,"+
                        " canonicalization algorithm received in the message is "+ inferredCanonAlgo
                        + " policy requires "+actualCanonAlgo);
                return false;
            }
        }
        
        return true;
    }
    /**
     * verifies the encryption target bindings
     * @param actualFeatureBinding EncryptionPolicy.FeatureBinding
     * @param inferredFeatureBinding EncryptionPolicy.FeatureBinding
     * @return boolean
     */
    private boolean verifyEncTargetBinding(EncryptionPolicy.FeatureBinding actualFeatureBinding,
            EncryptionPolicy.FeatureBinding inferredFeatureBinding){
        String rDA = inferredFeatureBinding.getDataEncryptionAlgorithm();
        String cDA = actualFeatureBinding.getDataEncryptionAlgorithm();
        if(cDA != null && cDA.length() > 0 ){
            if(!cDA.equals(rDA)){
                log.warning("Receiver side requirement verification failed, "+
                        "DataEncryptionAlgorithm specified in the receiver requirements did match with"
                        +" DataEncryptionAlgorithm used to encrypt the message."+
                        "Configured DataEncryptionAlgorithm is "+cDA+"  DataEncryptionAlgorithm used in the" +
                        "message is "+rDA);
                return false;
            }
        }
        return true;
    }
    
    private EncryptionPolicy getNthEncryptionPolicy(MessagePolicy securityPolicy, int nth) throws XWSSecurityException{
        try{
            int count = nth;
            for(int i = 0; i < securityPolicy.size(); i++){
                WSSPolicy pol = (WSSPolicy)securityPolicy.get(i);
                if(PolicyTypeUtil.isPrimaryPolicy(pol) && PolicyTypeUtil.encryptionPolicy(pol)){
                    if(((EncryptionPolicy.FeatureBinding)pol.getFeatureBinding()).encryptsIssuedToken()){
                        continue;
                    }
                    if(count > 0){
                        count --;
                    } else{
                        return (EncryptionPolicy)pol;
                    }
                }
            }
        } catch(Exception e){
            throw new XWSSecurityException(e);
        }
        return null;
    }
    
    private WSSPolicy getFirstPrimaryPolicy(MessagePolicy securityPolicy, boolean isEndorsingSign,
            int nth) throws XWSSecurityException{
        try{
            
            int count = nth;
            if(!isEndorsingSign){
                for(int i = 0; i < securityPolicy.size(); i++){
                    WSSPolicy pol = (WSSPolicy)securityPolicy.get(i);
                    if(PolicyTypeUtil.isPrimaryPolicy(pol)){
                        // accounts for encrypted SAML tokens issued by STS
                        if(PolicyTypeUtil.encryptionPolicy(pol) &&
                                ((EncryptionPolicy.FeatureBinding)pol.getFeatureBinding()).encryptsIssuedToken() ){
                        } else if(count > 0){
                            if(PolicyTypeUtil.signaturePolicy(pol))
                                count--;
                        } else if(nth != 0 && !PolicyTypeUtil.signaturePolicy(pol)){
                        } else{
                            return pol;
                        }
                    }
                }
            } else {
                // endorsingSign policy is not placed correctly in actual policy
                for(int i = count; i < securityPolicy.size(); i++){
                    WSSPolicy pol = (WSSPolicy)securityPolicy.get(i);
                    if(PolicyTypeUtil.isPrimaryPolicy(pol) && PolicyTypeUtil.signaturePolicy(pol)){
                        SignaturePolicy signPol = (SignaturePolicy)pol;
                        SignaturePolicy.FeatureBinding fb = (SignaturePolicy.FeatureBinding)signPol.getFeatureBinding();
                        for (int no_of_sig_targets=0; no_of_sig_targets < fb.getTargetBindings().size(); no_of_sig_targets++){
                            SignatureTarget target = (SignatureTarget)fb.getTargetBindings().get(no_of_sig_targets);
                            if("{http://www.w3.org/2000/09/xmldsig#}Signature".equals(target.getValue()))
                                return pol;
                        }
                    }
                }
            }
        } catch(Exception e){
            throw new XWSSecurityException(e);
        }
        return null;
    }
    
    @SuppressWarnings("static-access")
    private void correctIncludeTokenPolicy(AuthenticationTokenPolicy.X509CertificateBinding x509Bind,
            WSSAssertion wssAssertion){
        String iTokenType = x509Bind.getIncludeToken();
        if(x509Bind.INCLUDE_NEVER.equals(iTokenType) ||
                x509Bind.INCLUDE_NEVER_VER2.equals(iTokenType)){
            if(MessageConstants.DIRECT_REFERENCE_TYPE.equals(x509Bind.getReferenceType())){
                if(wssAssertion != null){
                    if(wssAssertion.getRequiredProperties().contains(WSSAssertion.MUST_SUPPORT_REF_KEYIDENTIFIER))
                        x509Bind.setReferenceType(MessageConstants.KEY_INDETIFIER_TYPE);
                    else if(wssAssertion.getRequiredProperties().contains(WSSAssertion.MUSTSUPPORT_REF_THUMBPRINT))
                        x509Bind.setReferenceType(MessageConstants.THUMB_PRINT_TYPE);
                } else {
                    // when wssAssertion is not set use KeyIdentifier
                    x509Bind.setReferenceType(MessageConstants.KEY_INDETIFIER_TYPE);
                }
            }
        } else if(x509Bind.INCLUDE_ALWAYS_TO_RECIPIENT.equals(iTokenType) ||
                  x509Bind.INCLUDE_ALWAYS.equals(iTokenType) ||
                  x509Bind.INCLUDE_ALWAYS_VER2.equals(iTokenType) ||
                  x509Bind.INCLUDE_ALWAYS_TO_RECIPIENT_VER2.equals(iTokenType)){
            x509Bind.setReferenceType(MessageConstants.DIRECT_REFERENCE_TYPE);
        }
    }
    
    public void printInferredSecurityPolicy(MessagePolicy inferredSecurityPolicy) throws Exception{
        StringBuffer buffer = new StringBuffer();
        if(inferredSecurityPolicy == null){
            buffer.append("Security Policy not set\n");
        } else{
            buffer.append("Size of Policy:: ").append(inferredSecurityPolicy.size()).append("\n");
            for(int i = 0; i < inferredSecurityPolicy.size(); i++){
                WSSPolicy pol = (WSSPolicy)inferredSecurityPolicy.get(i);
                if(PolicyTypeUtil.timestampPolicy(pol)){
                    buffer.append("Timestamp Policy\n");
                } else if(PolicyTypeUtil.usernameTokenPolicy(pol)){
                    buffer.append("UsernameToken Policy\n");
                } else if(PolicyTypeUtil.signaturePolicy(pol)){
                    buffer.append("Signature Policy\n");
                    SignaturePolicy sigPol = (SignaturePolicy)pol;
                    SignaturePolicy.FeatureBinding featureBinding =
                            (SignaturePolicy.FeatureBinding)sigPol.getFeatureBinding();
                    ArrayList targets = featureBinding.getTargetBindings();
                    buffer.append("\tCanonicalizationAlgorithm").append(featureBinding.getCanonicalizationAlgorithm()).append("\n");
                    buffer.append("\t Targets\n");
                    for(int j = 0; j < targets.size(); j++){
                        SignatureTarget target = (SignatureTarget)targets.get(j);
                        buffer.append("\t ").append(j).append(":Type:").append(target.getType()).append("\n");
                        buffer.append("\t  Value:").append(target.getValue()).append("\n");
                        buffer.append("\t  DigestAlgorithm:").append(target.getDigestAlgorithm()).append("\n");
                        ArrayList transforms = target.getTransforms();
                        
                        if(transforms != null){
                            buffer.append("\t  " + "Transforms::\n");
                            for(int k = 0; k < transforms.size(); k++){
                                buffer.append("\t " + "   ").append(((SignatureTarget.Transform) transforms.get(k)).getTransform()).append("\n");
                            }
                        }
                    }
                    MLSPolicy keyBinding = sigPol.getKeyBinding();
                    if(keyBinding != null){
                        buffer.append("\tKeyBinding\n");
                        printKeyBinding(keyBinding, buffer);
                    }
                } else if(PolicyTypeUtil.encryptionPolicy(pol)){
                    buffer.append("Encryption Policy\n");
                    EncryptionPolicy encPol = (EncryptionPolicy)pol;
                    EncryptionPolicy.FeatureBinding featureBinding =
                            (EncryptionPolicy.FeatureBinding)encPol.getFeatureBinding();
                    ArrayList targets = featureBinding.getTargetBindings();
                    buffer.append("\t Targets\n");
                    for(int j = 0; j < targets.size(); j++){
                        EncryptionTarget target = (EncryptionTarget)targets.get(j);
                        buffer.append("\t ").append(j).append(":").append("Type:").append(target.getType()).append("\n");
                        buffer.append("\t  Value:").append(target.getValue()).append("\n");
                        buffer.append("\t  ContentOnly:").append(target.getContentOnly()).append("\n");
                        buffer.append("\t  DataEncryptionAlgorithm:").append(target.getDataEncryptionAlgorithm()).append("\n");
                    }
                    MLSPolicy keyBinding = encPol.getKeyBinding();
                    if(keyBinding != null){
                        buffer.append("\tKeyBinding\n");
                        printKeyBinding(keyBinding, buffer);
                    }
                } else if(PolicyTypeUtil.signatureConfirmationPolicy(pol)){
                    buffer.append("SignatureConfirmation Policy\n");
                } else{
                    buffer.append(pol).append("\n");
                }
            }
        }
        if (MessageConstants.debug) {
            System.out.println(buffer);
        }
    }
    
    private void printKeyBinding(MLSPolicy keyBinding, StringBuffer buffer){
        if(keyBinding != null){
            if(keyBinding instanceof AuthenticationTokenPolicy.X509CertificateBinding){
                AuthenticationTokenPolicy.X509CertificateBinding x509Binding =
                        (AuthenticationTokenPolicy.X509CertificateBinding)keyBinding;
                buffer.append("\t  X509CertificateBinding\n");
                buffer.append("\t    ValueType:").append(x509Binding.getValueType()).append("\n");
                buffer.append("\t    ReferenceType:").append(x509Binding.getReferenceType()).append("\n");
            } else if(keyBinding instanceof AuthenticationTokenPolicy.SAMLAssertionBinding){
                AuthenticationTokenPolicy.SAMLAssertionBinding samlBinding =
                        (AuthenticationTokenPolicy.SAMLAssertionBinding)keyBinding;
                buffer.append("\t  SAMLAssertionBinding\n");
                //buffer.append("\t    ValueType:" + samlBinding.getValueType() + "\n");
                buffer.append("\t    ReferenceType:").append(samlBinding.getReferenceType()).append("\n");
            } else if(keyBinding instanceof SymmetricKeyBinding){
                SymmetricKeyBinding skBinding = (SymmetricKeyBinding)keyBinding;
                buffer.append("\t  SymmetricKeyBinding\n");
                AuthenticationTokenPolicy.X509CertificateBinding x509Binding =
                        (AuthenticationTokenPolicy.X509CertificateBinding)skBinding.getKeyBinding();
                if(x509Binding != null){
                    buffer.append("\t     X509CertificateBinding\n");
                    buffer.append("\t       ValueType:").append(x509Binding.getValueType()).append("\n");
                    buffer.append("\t       ReferenceType:").append(x509Binding.getReferenceType()).append("\n");
                }
            } else if(keyBinding instanceof IssuedTokenKeyBinding){
                buffer.append("\t  IssuedTokenKeyBinding\n");
                
            } else if(keyBinding instanceof SecureConversationTokenKeyBinding){
                buffer.append("\t  SecureConversationTokenKeyBinding\n");
                
            }else if(keyBinding instanceof DerivedTokenKeyBinding){
                buffer.append("\t  DerivedTokenKeyBinding\n");
                DerivedTokenKeyBinding dtkBinding = (DerivedTokenKeyBinding)keyBinding;
                buffer.append("\t  OriginalKeyBinding:\n");
                printKeyBinding(dtkBinding.getOriginalKeyBinding(), buffer);
            }
        }
    }

     @SuppressWarnings("unchecked")
    private boolean checkTargetPresence(WSSPolicy actualPol) throws XWSSecurityException{
        List<Target> actualTargets = null;
        if(PolicyTypeUtil.signaturePolicy(actualPol)){
            SignaturePolicy.FeatureBinding actualFeatureBinding =
                    (SignaturePolicy.FeatureBinding)actualPol.getFeatureBinding();
            actualTargets = (List<Target>)actualFeatureBinding.getTargetBindings();
        } else if(PolicyTypeUtil.encryptionPolicy(actualPol)){
            EncryptionPolicy.FeatureBinding actualFeatureBinding =
                    (EncryptionPolicy.FeatureBinding)actualPol.getFeatureBinding();
            actualTargets = (List<Target>)actualFeatureBinding.getTargetBindings();
        }
        
        return targetResolver.isTargetPresent(actualTargets);
    }

    private boolean checkAllowExtraTimestamp(MessagePolicy inferredSecurityPolicy) {
        //assumption : inferredSecurityPolicy != null and size > 0
        if (inferredSecurityPolicy.size() > 1) {
         return false;
        }
        SecurityPolicy pol = null;
        try {
            pol = inferredSecurityPolicy.get(0);
        } catch (Exception ex) {
            //ignore for now;
        }
        if (pol instanceof TimestampPolicy) {
            return true;
        }
        return false;
    }

    private boolean isOptionalPolicy(SignaturePolicy actualSignPolicy) throws PolicyGenerationException {
        if (((WSSPolicy) actualSignPolicy.getKeyBinding()).isOptional()) {
            return true;
        }
        if (actualSignPolicy.getKeyBinding().getKeyBinding() != null && ((WSSPolicy) actualSignPolicy.getKeyBinding().getKeyBinding()).isOptional()) {
            return true;
        }


        return false;
    }
}
