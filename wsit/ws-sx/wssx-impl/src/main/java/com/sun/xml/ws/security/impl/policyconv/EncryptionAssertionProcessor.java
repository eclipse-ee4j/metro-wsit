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

import com.sun.xml.ws.security.policy.AlgorithmSuite;
import com.sun.xml.ws.security.policy.EncryptedElements;
import com.sun.xml.ws.security.policy.EncryptedParts;
import com.sun.xml.ws.security.policy.Header;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.EncryptionTarget;
import java.util.HashSet;
import java.util.Iterator;
import javax.xml.namespace.QName;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class EncryptionAssertionProcessor {    
    private boolean bodyEncrypted = false;
    private boolean encryptAttachments = false;    
    private HashSet<Header> encryptedParts = new HashSet<>();
    //  private EncryptionTargetCreator etc =null;
    private EncryptionTargetCreator etCreator = null;
    /** Creates a new instance of EncryptionAssertionProcessor */
    public EncryptionAssertionProcessor(AlgorithmSuite algorithmSuite,boolean enforce) {
        //this.algorithmSuite = algorithmSuite;
        //this.enforce = enforce;
        this.etCreator = new EncryptionTargetCreator(algorithmSuite,enforce);
    }
    
    public EncryptionTargetCreator getTargetCreator(){
        return etCreator;
    }
    
    public void process(EncryptedParts encryptParts,EncryptionPolicy.FeatureBinding binding){
        if(SecurityPolicyUtil.isEncryptedPartsEmpty(encryptParts)){
            if(!bodyEncrypted){
                EncryptionTarget target = etCreator.newQNameEncryptionTarget(EncryptionTarget.BODY_QNAME);
                target.setContentOnly(true);
                binding.addTargetBinding(target);              
                bodyEncrypted = true;
            }
        }
        Iterator tv = encryptParts.getTargets();
        while(tv.hasNext()){
            Header ht = (Header)tv.next();
            if(!seenEncryptedParts(ht)){
                EncryptionTarget target = etCreator.newQNameEncryptionTarget(new QName(ht.getURI(),ht.getLocalName()));
                target.isSOAPHeadersOnly(true);
                binding.addTargetBinding(target);               
            }
        }
        
        if(encryptParts.hasBody() && !bodyEncrypted){
            EncryptionTarget target = etCreator.newQNameEncryptionTarget(EncryptionTarget.BODY_QNAME);
            target.setContentOnly(true);
            binding.addTargetBinding(target);         
            bodyEncrypted = true;
        }
        
        if(encryptParts.hasAttachments() && !encryptAttachments){
            EncryptionTarget target = etCreator.newURIEncryptionTarget(MessageConstants.PROCESS_ALL_ATTACHMENTS);
            target.setContentOnly(true);
            etCreator.addAttachmentTransform(target, MessageConstants.SWA11_ATTACHMENT_CIPHERTEXT_TRANSFORM);
            binding.addTargetBinding(target);
            encryptAttachments = true;
        }
    }
    
    //TODO:merge multiple EncryptedElements
    public void process(EncryptedElements encryptedElements , EncryptionPolicy.FeatureBinding binding){
        Iterator<String> eeItr = encryptedElements.getTargets();
        while(eeItr.hasNext()){
            String xpathTarget = eeItr.next();
            EncryptionTarget target = etCreator.newXpathEncryptionTarget(xpathTarget);
            binding.addTargetBinding(target);          
        }
    }
    
    private boolean seenEncryptedParts(Header header){
        if(encryptedParts.contains(header)){
            return true;
        }
        encryptedParts.add(header);
        return false;
    }
    
    public void process(QName targetName,EncryptionPolicy.FeatureBinding binding){
        EncryptionTarget target = etCreator.newQNameEncryptionTarget(targetName);
        binding.addTargetBinding(target);      
    }
}
