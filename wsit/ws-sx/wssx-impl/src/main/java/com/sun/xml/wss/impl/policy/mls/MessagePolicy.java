/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: MessagePolicy.java,v 1.2 2010-10-21 15:37:34 snajper Exp $
 */

package com.sun.xml.wss.impl.policy.mls;

import com.sun.xml.wss.impl.MessageConstants;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import com.sun.xml.wss.XWSSecurityException;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.wss.logging.LogDomainConstants;

import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.policy.PolicyGenerationException;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import java.util.HashMap;

import com.sun.xml.wss.impl.misc.SecurityUtil;

import com.sun.xml.wss.impl.AlgorithmSuite;
import com.sun.xml.wss.impl.WSSAssertion;

import com.sun.xml.wss.impl.MessageLayout;
/**
 * Represents an ordered collection of Security Policies
 */
public class MessagePolicy implements SecurityPolicy {

    protected static final Logger log =  Logger.getLogger(
            LogDomainConstants.IMPL_CONFIG_DOMAIN,
            LogDomainConstants.IMPL_CONFIG_DOMAIN_BUNDLE);

    private ArrayList info;
    private ArrayList optionals;

    private boolean dumpMessages = false;
    private boolean enableDynamicPolicyFlag = false;
    private boolean bsp = false;
    private boolean enableWSS11PolicyFlag = false;
    private boolean enableSignatureConfirmation = false;
    private WSSAssertion wssAssertion;
    private MessageLayout layout = MessageLayout.Lax;
    private boolean onSSL = false;
    private int optimizedType = -1;

    //TODO: temporary workaround for obtain the algosuite
    // need to remove this once we have the SC Layer taking care of it
    private AlgorithmSuite algoSuite;
    //ID of the policy Alternative
    private String polAltId;


    /**
     * Construct an Empty MessagePolicy
     */
    public MessagePolicy() {
        info = new ArrayList();
        optionals = new ArrayList();
    }

    @SuppressWarnings("unchecked")
     public int getOptimizedType() throws XWSSecurityException {

        if ( optimizedType != -1 )
            return optimizedType;

        if ( enableDynamicPolicy() ) {
            optimizedType = MessageConstants.NOT_OPTIMIZED;
            return optimizedType;
        }

        StringBuilder securityOperation = new StringBuilder();
        securityOperation.append("_BODY");

        StringBuilder tmpBuffer = new StringBuilder();

        SignatureTarget sigTarget = null;
        EncryptionTarget encTarget = null;

        WSSPolicy policy = null;
        String targetValue = null;
        int secureHeaders = -1;
        int secureAttachments = -1;

        HashMap map = new HashMap();

        ArrayList primaryPolicies = getPrimaryPolicies() ;
        ArrayList secondaryPolicies = getSecondaryPolicies();

        int size = primaryPolicies.size();
        int secondaryPoliciesSize = secondaryPolicies.size();


        if ( size == 0 && secondaryPoliciesSize > 0 ) {
            optimizedType = MessageConstants.SECURITY_HEADERS;
            return optimizedType;
        }



        int iterator = 0;

        for ( iterator =0 ; iterator < secondaryPoliciesSize; iterator++) {
            policy = (WSSPolicy)secondaryPolicies.get(iterator);
            if ( policy.getType().intern() == "uri" ) {
                if ( PolicyTypeUtil.usernameTokenPolicy(policy)) {
                    map.put("UsernameToken", policy.getUUID() );
                } else if ( PolicyTypeUtil.timestampPolicy(policy)) {
                    map.put("Timestamp", policy.getUUID());
                } else if ( PolicyTypeUtil.samlTokenPolicy(policy)) {
                    map.put("Assertion", policy.getUUID());
                }
            }
        }



        for ( iterator=0; iterator<size; iterator++ ) {
            policy = (WSSPolicy)primaryPolicies.get(iterator);

            if ( PolicyTypeUtil.signaturePolicy(policy) ) {
                tmpBuffer.delete(0, tmpBuffer.length());
                SignaturePolicy.FeatureBinding featureBinding =
                        (SignaturePolicy.FeatureBinding)policy.getFeatureBinding();

                int targetBindingSize = featureBinding.getTargetBindings().size();
                for ( int targetIterator = 0; targetIterator<targetBindingSize; targetIterator++) {
                    sigTarget = (SignatureTarget)featureBinding.getTargetBindings().get(targetIterator);

                    if (sigTarget == null){
                        throw new XWSSecurityException("Signature Target is null.");
                    }

                    if ( sigTarget != null &&
                            sigTarget.getTransforms().size() > 1 ) {
                        optimizedType = MessageConstants.NOT_OPTIMIZED;
                        return optimizedType;
                    }

                    if ( sigTarget.getTransforms().size() == 1) {
                        SignatureTarget.Transform transform = (SignatureTarget.Transform)sigTarget.getTransforms().get(0);
                        if ( transform != null ) {
                            if ( transform.getTransform().intern() !=
                                    MessageConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS) {
                                optimizedType = MessageConstants.NOT_OPTIMIZED;
                                return optimizedType;
                            }
                        }
                    }


                    if ( sigTarget.getType().intern() == "qname") {
                        targetValue = sigTarget.getQName().getLocalPart().intern();
                    } else if ( sigTarget.getType().intern() == "uri") {
                        if ( map.containsKey(sigTarget.getValue() )) {
                            targetValue = map.get(sigTarget.getValue()).toString();
                        } else if ( sigTarget.getValue().intern() == "attachmentRef:attachment" ||
                                    sigTarget.getValue().startsWith("cid:")) {
                            targetValue = "Attachment";
                        }
                    } else if ( sigTarget.getType().intern() == "xpath") {
                        optimizedType = MessageConstants.NOT_OPTIMIZED;
                        return optimizedType;
                    }

                    if ( targetValue == "Body" ) {
                        if ( tmpBuffer.indexOf("_SIGN") == -1) {
                            tmpBuffer.append("_SIGN");
                            if ( secureHeaders == 1 || secureHeaders == -1)
                                secureHeaders = 0;
                            if ( secureAttachments == 1 || secureAttachments == -1)
                                secureAttachments = 0;
                        }
                    } else if ( targetValue == "Timestamp" ||
                                targetValue == "UsernameToken" ||
                                targetValue == "Assertion" ) {
                        if ( secureHeaders == -1)
                            secureHeaders = 1;
                    } else if ( targetValue == "Attachment") {
                        if ( secureAttachments == -1 )
                            secureAttachments = 1;
                    } else {
                        return MessageConstants.NOT_OPTIMIZED;
                    }
                }
                securityOperation.insert(securityOperation.indexOf("_BODY"), tmpBuffer);
            } else if ( PolicyTypeUtil.encryptionPolicy(policy) ) {
                tmpBuffer.delete(0, tmpBuffer.length());
                EncryptionPolicy.FeatureBinding featureBinding =
                        (EncryptionPolicy.FeatureBinding)policy.getFeatureBinding();

                int targetBindingSize = featureBinding.getTargetBindings().size();
                for ( int targetIterator = 0; targetIterator<targetBindingSize; targetIterator++) {
                    encTarget = (EncryptionTarget)featureBinding.getTargetBindings().get(targetIterator);



                    if ( encTarget.getType().intern() == "qname") {
                        targetValue = encTarget.getQName().getLocalPart().intern();
                    } else if ( encTarget.getType().intern() == "uri") {
                        if ( map.containsKey(encTarget.getValue() )) {
                            targetValue = map.get(encTarget.getValue()).toString();
                        } else if ( encTarget.getValue().intern() == "attachmentRef:attachment" ||
                                    encTarget.getValue().startsWith("cid:")) {
                            targetValue = "Attachment";
                        }
                    } else if ( encTarget.getType().intern() == "xpath") {
                        optimizedType = MessageConstants.NOT_OPTIMIZED;
                        return optimizedType;
                    }

                    if ( targetValue == "Body" ) {
                        if ( tmpBuffer.indexOf("_ENCRYPT") == -1) {
                            tmpBuffer.append("_ENCRYPT");
                            if ( secureHeaders == 1 || secureHeaders == -1)
                                secureHeaders = 0;
                            if ( secureAttachments == 1 || secureAttachments == -1)
                                secureAttachments = 0;
                        }
                    } else if ( targetValue == "Timestamp" ||
                                targetValue == "UsernameToken" ||
                                targetValue == "Assertion" ) {
                        if ( secureHeaders == -1)
                            secureHeaders = 1;
                    } else if ( targetValue == "Attachment") {
                        if ( secureAttachments == -1 )
                            secureAttachments = 1;
                    } else {
                        return MessageConstants.NOT_OPTIMIZED;
                    }
                }
                securityOperation.insert(securityOperation.indexOf("_BODY"), tmpBuffer);
            }
        }



        if ( secureHeaders == 1 && secureAttachments != 1) {
            optimizedType = MessageConstants.SECURITY_HEADERS;
            return optimizedType;
        } else if ( secureAttachments == 1 && secureAttachments != 1) {
            optimizedType = MessageConstants.SECURE_ATTACHMENTS;
            return optimizedType;
        } else if ( secureHeaders == 1 && secureAttachments == 1) {
            optimizedType = MessageConstants.SECURITY_HEADERS_AND_ATTACHMENTS;
            return optimizedType;
        }

        String type = securityOperation.toString().intern();

        switch (type) {
            case "_SIGN_BODY":
                optimizedType = MessageConstants.SIGN_BODY;
                break;
            case "_SIGN_ENCRYPT_BODY":
                optimizedType = MessageConstants.SIGN_ENCRYPT_BODY;
                break;
            case "_ENCRYPT_SIGN_BODY":
                optimizedType = MessageConstants.NOT_OPTIMIZED;//MessageConstants.ENCRYPT_SIGN_BODY;
                break;
            case "_ENCRYPT_BODY":
                optimizedType = MessageConstants.NOT_OPTIMIZED;// MessageConstants.ENCRYPT_BODY;
                break;
        }

        return optimizedType;
    }


    /**
     * Append a SecurityPolicy
     * @param item SecurityPolicy instance to be appended
     */
    @SuppressWarnings("unchecked")
    public void append(SecurityPolicy item) {
        //BooleanComposer.checkType(item);
        info.add(item);
    }

    /**
     * Prepend a SecurityPolicy
     * @param item SecurityPolicy instance to be prepended
     */
    @SuppressWarnings("unchecked")
    public void prepend(SecurityPolicy item) {
        //BooleanComposer.checkType(item);
        int i = 0;
        for(i = 0; i < info.size(); i++ ){
            SecurityPolicy sp = (SecurityPolicy)info.get(i);
            if(!sp.getType().equals(PolicyTypeUtil.SIGNATURE_CONFIRMATION_POLICY_TYPE)){
                break;
            }
        }
        info.add(i, item);
    }

    /**
     * Append a policy collection
     * @param items Collection of SecurityPolicy instances to be appended
     */
    @SuppressWarnings("unchecked")
    public void appendAll(Collection items) {
        Iterator i = items.iterator();
        while (i.hasNext()) {
            SecurityPolicy item = (SecurityPolicy) i.next();
            //BooleanComposer.checkType(item);
        }
        info.addAll(items);
    }

    /**
     * clear this policy collection
     */
    public void removeAll() {
        info.clear();
    }

    /**
     * @return size of policy collection
     */
    public int size() {
        return info.size();
    }

    /**
     * Get the Security policy at the specified index
     * @param index index to the policy collection
     * @return SecurityPolicy instance at the specified index
     */
    public SecurityPolicy get(int index) {

        if (!optionals.isEmpty()) addOptionals();

        return (SecurityPolicy) info.get(index);
    }

    /**
     * @return <code>Iterator</code> iterator on policy collection
     */
    public Iterator iterator() {

        if (!optionals.isEmpty()) addOptionals();

        return info.iterator();
    }

    /**
     * @return true if collection is empty
     */
    public boolean isEmpty() {
        return info.isEmpty();
    }

    /**
     * remove the specified SecurityPolicy
     * @param item the SecurityPolicy instance to be removed
     */
    public void remove(SecurityPolicy item) {
        int i = info.indexOf(item);
        if (i == -1) {
            return;
        }
        info.remove(i);
    }

    /**
     * Insert the additional policy before the existing policy
     * @param existing SecurityPolicy instance before which the additional policy needs to be inserted
     * @param additional SecurityPolicy instance to be inserted
     */
    @SuppressWarnings("unchecked")
    public void insertBefore(SecurityPolicy existing, SecurityPolicy additional) {
        //BooleanComposer.checkType(existing);
        //BooleanComposer.checkType(additional);

        int i = info.indexOf(existing);
        if (i == -1) {
            return;
        }
        info.add(i, additional);
    }

    /**
     * @param dump set it to true if messages should be Logged
     */
    public void dumpMessages(boolean dump) {
        dumpMessages = dump;
    }

    /**
     * @return true if logging of messages is enabled
     */
    public boolean dumpMessages() {
        return dumpMessages;
    }

    /*
     * @param flag boolean that indicates if dynamic policy is enabled
     */
    public void enableDynamicPolicy(boolean flag) {
        enableDynamicPolicyFlag = flag;
    }

    /*
     * @return true if dynamic policy is enabled
     */
    public boolean enableDynamicPolicy() {
        return enableDynamicPolicyFlag;
    }

    public void setWSSAssertion(WSSAssertion wssAssertion)
        throws PolicyGenerationException{
        this.wssAssertion = wssAssertion;
        if("1.1".equals(this.wssAssertion.getType())){
            enableWSS11PolicyFlag = true;
        }
        if(this.wssAssertion.getRequiredProperties().contains("RequireSignatureConfirmation")){
            enableSignatureConfirmation = true;
        }
        if(enableSignatureConfirmation){
            SignatureConfirmationPolicy signConfirmPolicy = new SignatureConfirmationPolicy();
            String id = SecurityUtil.generateUUID();
            signConfirmPolicy.setUUID(id);
            prepend(signConfirmPolicy);
        }
    }

    public WSSAssertion getWSSAssertion() {
        return this.wssAssertion;
    }

    public void enableSignatureConfirmation(boolean flag) throws PolicyGenerationException{
        enableSignatureConfirmation = flag;
        if(enableSignatureConfirmation){
            SignatureConfirmationPolicy signConfirmPolicy = new SignatureConfirmationPolicy();
            String id = SecurityUtil.generateUUID();
            signConfirmPolicy.setUUID(id);
            append(signConfirmPolicy);
        }
    }

   public boolean enableSignatureConfirmation() {
       return enableSignatureConfirmation;
   }

   public void enableWSS11Policy(boolean flag){
       enableWSS11PolicyFlag = flag;
   }

    public boolean enableWSS11Policy() {
        return enableWSS11PolicyFlag;
    }

    /*
     */
    public void isBSP(boolean flag) {
        bsp = flag;
    }

    /*
     */
    public boolean isBSP() {
        return bsp;
    }

    /*
     */
    public void removeOptionalTargets() {
        optionals.clear();
    }

    /*
     * @param optionals specify optional targets that can be signed/encrypted
     */
    @SuppressWarnings("unchecked")
    public void addOptionalTargets(ArrayList optionls) throws XWSSecurityException {
        Iterator i = optionls.iterator();

        while (i.hasNext()) {
            try {
                Target target = (Target) i.next();
                target.setEnforce(false);
            } catch (ClassCastException cce) {
                String message = "Target should be of types: " +
                        "com.sun.xml.wss.impl.policy.mls.SignatureTarget OR " +
                        "com.sun.xml.wss.impl.policy.mls.EncryptionTarget";
                log.log(Level.SEVERE, "WSS1100.classcast.target",
                        new Object[] {message});
                        throw new XWSSecurityException(message, cce);
            }
        }

        optionals.addAll(optionls);
    }

    /*
     * @param target add an optional target for signature/encryption
     */
    @SuppressWarnings("unchecked")
    public void addOptionalTarget(Target target) {
        target.setEnforce(false);
        optionals.add(target);
    }

    /**
     * Equals operator
     * @param policy <code>MessagePolicy</code> to be compared for equality
     * @return true if the policy is equal to this policy
     */
    public boolean equals(MessagePolicy policy) {

        boolean assrt = policy.dumpMessages() && policy.enableDynamicPolicy();

        if (assrt) {
            ArrayList primary0 = getPrimaryPolicies();
            ArrayList secdary0 = getSecondaryPolicies();

            ArrayList primary1 = policy.getPrimaryPolicies();
            ArrayList secdary1 = policy.getSecondaryPolicies();

            if (primary0.equals(primary1) && secdary0.equals(secdary1)) assrt = true;
        }

        return assrt;
    }

    /*
     * @return primary policy list
     */
    @SuppressWarnings("unchecked")
    public ArrayList getPrimaryPolicies() {
        ArrayList list = new ArrayList();

        Iterator i = iterator();
        while (i.hasNext()) {
            SecurityPolicy policy = (SecurityPolicy) i.next();
            if(PolicyTypeUtil.encryptionPolicy(policy) || PolicyTypeUtil.signaturePolicy(policy)){
                list.add(policy);
            }
        }

        return list;
    }

    /*
     * @return secondary policy list
     */
    @SuppressWarnings("unchecked")
    public ArrayList getSecondaryPolicies() {
        ArrayList list = new ArrayList();

        Iterator i = iterator();
        while (i.hasNext()) {
            SecurityPolicy policy = (SecurityPolicy) i.next();

            if(PolicyTypeUtil.authenticationTokenPolicy(policy) || PolicyTypeUtil.timestampPolicy(policy)){
                list.add(policy);
            }
        }

        return list;
    }

    private void addOptionals() {

        Iterator j = info.iterator();

        while (j.hasNext()) {

            SecurityPolicy policy = (SecurityPolicy) j.next();

            if (policy instanceof WSSPolicy) {
                processWSSPolicy((WSSPolicy) policy);
            } /*else
                if (PolicyTypeUtil.booleanComposerPolicy(policy)) {
                processBooleanComposer((BooleanComposer)policy);
                }*/

        }

        optionals.clear();
    }

    /*
     * @param policy WSSPolicy
     */
    private void processWSSPolicy(WSSPolicy policy) {
        if (PolicyTypeUtil.signaturePolicy(policy)) {
            SignaturePolicy sPolicy = (SignaturePolicy) policy;
            SignaturePolicy.FeatureBinding fBinding =
                    (SignaturePolicy.FeatureBinding) sPolicy.getFeatureBinding();

            Iterator it = optionals.iterator();
            for (; it.hasNext(); ) {
                fBinding.addTargetBinding((Target)it.next());
            }
        } else
            if (PolicyTypeUtil.encryptionPolicy(policy)) {
            EncryptionPolicy ePolicy = (EncryptionPolicy) policy;
            EncryptionPolicy.FeatureBinding fBinding =
                    (EncryptionPolicy.FeatureBinding) ePolicy.getFeatureBinding();

            Iterator it = optionals.iterator();
            for (; it.hasNext(); ) {
                fBinding.addTargetBinding((Target)it.next());
            }
            }
    }

    /*
     * @param composer BooleanComposer
     */
    /*private void processBooleanComposer(BooleanComposer composer) {
        if (PolicyTypeUtil.booleanComposerPolicy(composer.getPolicyA())) {
               processBooleanComposer((BooleanComposer) composer.getPolicyA());
        } else {
            processWSSPolicy((WSSPolicy) composer.getPolicyA());
        }

        if (PolicyTypeUtil.booleanComposerPolicy(composer.getPolicyB())) {
            processBooleanComposer((BooleanComposer) composer.getPolicyB());
        } else {
            processWSSPolicy((WSSPolicy) composer.getPolicyB());
        }
    }*/

    /**
     * @return the type of the policy
     */
    @Override
    public String getType() {
        return PolicyTypeUtil.MESSAGEPOLICY_CONFIG_TYPE;
    }

    public void setAlgorithmSuite(AlgorithmSuite algSuite){
        this.algoSuite = algSuite;
    }

    public AlgorithmSuite getAlgorithmSuite(){
        return this.algoSuite;
    }

    public MessageLayout getLayout(){
        return layout;
    }

    public void setLayout(MessageLayout layout){
        this.layout = layout;
    }

    public void setSSL(boolean value){
        this.onSSL = value;
    }
    public boolean isSSL(){
        return onSSL;
    }

    public String getPolicyAlternativeId() {
        return polAltId;
    }
    public void setPolicyAlternativeId(String polId) {
        this.polAltId = polId;
    }

}
