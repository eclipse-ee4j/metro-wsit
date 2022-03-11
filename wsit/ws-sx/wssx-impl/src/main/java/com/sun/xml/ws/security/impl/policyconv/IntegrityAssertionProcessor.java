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
import com.sun.xml.ws.security.policy.SignedParts;
import com.sun.xml.ws.security.policy.AlgorithmSuite;
import com.sun.xml.ws.security.policy.Header;
import com.sun.xml.ws.security.policy.SignedElements;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import com.sun.xml.wss.impl.policy.mls.Target;
import java.util.HashSet;
import java.util.Iterator;
import javax.xml.namespace.QName;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class IntegrityAssertionProcessor {

    private boolean contentOnly = false;
    private boolean seenBody = false;
    private boolean seenAttachments = false;
    private HashSet<Header> signParts  = new HashSet<>();
    private boolean allHeaders = false;
    private SignatureTargetCreator targetCreator = null;

    /** Creates a new instance of IntegrityAssertionProcessor */

    public IntegrityAssertionProcessor(AlgorithmSuite algorithmSuite, boolean contentOnly) {
        //this.algorithmSuite = algorithmSuite;
        this.contentOnly = contentOnly;
        targetCreator = new SignatureTargetCreator(false,algorithmSuite,contentOnly);
    }

    public SignatureTargetCreator getTargetCreator(){
        return targetCreator;
    }

    public void process(SignedParts signedParts,SignaturePolicy.FeatureBinding binding){
        Iterator tv = signedParts.getHeaders();
        if(SecurityPolicyUtil.isSignedPartsEmpty(signedParts)){
            if(!allHeaders){
                SignatureTarget target = targetCreator.newURISignatureTarget("");
                targetCreator.addTransform(target);
                target.setValue(SignatureTarget.ALL_MESSAGE_HEADERS);
                target.isSOAPHeadersOnly(true);
                binding.addTargetBinding(target);
                target.setContentOnly(contentOnly);
                allHeaders = true;
            }
            if(!seenBody){
                SignatureTarget target = targetCreator.newQNameSignatureTarget(Target.BODY_QNAME);
                targetCreator.addTransform(target);
                binding.addTargetBinding(target);
                target.setContentOnly(contentOnly);
                seenBody = true;
            }
        }else{
            while(tv.hasNext()){
                Header ht = (Header)tv.next();
                if(!allHeaders && !seenSignTarget(ht)){
                    SignatureTarget target = targetCreator.newQNameSignatureTarget(new QName(ht.getURI(),ht.getLocalName()));
                    targetCreator.addTransform(target);
                    target.isSOAPHeadersOnly(true);
                    target.setContentOnly(contentOnly);
                    binding.addTargetBinding(target);
                }
            }
            if(signedParts.hasBody()){
                if(!seenBody){
                    SignatureTarget target = targetCreator.newQNameSignatureTarget(Target.BODY_QNAME);
                    targetCreator.addTransform(target);
                    target.setContentOnly(contentOnly);
                    binding.addTargetBinding(target);
                    seenBody = true;
                }
            }
            if(signedParts.hasAttachments()){
                if(!seenAttachments){
                    SignatureTarget target = targetCreator.newURISignatureTarget("");
                    target.setValue(MessageConstants.PROCESS_ALL_ATTACHMENTS);
                    targetCreator.addAttachmentTransform(target, signedParts.attachmentProtectionType());
                    binding.addTargetBinding(target);
                    seenAttachments = true;
                }
            }
        }
        signParts.clear();
    }

    public void process(SignedElements signedElements,SignaturePolicy.FeatureBinding binding){
        Iterator<String> itr = signedElements.getTargets();
        while(itr.hasNext()){
            String xpathTarget = itr.next();
            SignatureTarget target = targetCreator.newXpathSignatureTarget(xpathTarget);
            targetCreator.addTransform(target);
            target.setContentOnly(contentOnly);
            //  target.setXPathVersion(signedElements.)
            binding.addTargetBinding(target);
        }
    }

    private boolean seenSignTarget(Header name ){
//        Iterator<Header> itr = signParts.iterator();
//        while(itr.hasNext()){
//            Header header = itr.next();
//            if(header.getLocalName().equals(name.getLocalName()) && header.getURI().equals(name.getURI())){
//                return true;
//            }
//        }
        if(signParts.contains(name)){
            return true;
        }
        signParts.add(name);
        return false;
    }

    public void process(QName targetName,SignaturePolicy.FeatureBinding binding){
        SignatureTarget target = targetCreator.newQNameSignatureTarget(targetName);
        targetCreator.addTransform(target);
        binding.addTargetBinding(target);
    }
}
