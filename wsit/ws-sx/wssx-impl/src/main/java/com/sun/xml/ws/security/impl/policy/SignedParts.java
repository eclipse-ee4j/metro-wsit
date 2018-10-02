/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.Header;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.wss.impl.MessageConstants;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 *
 * @author K.Venugopal@sun.com
 */


public class SignedParts extends PolicyAssertion implements com.sun.xml.ws.security.policy.SignedParts, SecurityAssertionValidator {
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private boolean body;
    private boolean attachments;
    private String attachmentProtectionType = MessageConstants.SWA11_ATTACHMENT_CONTENT_SIGNATURE_TRANSFORM;
    private boolean populated = false;
    private Set<PolicyAssertion> targets = new HashSet<PolicyAssertion>();
    private SecurityPolicyVersion spVersion;
    
    /**
     * Creates a new instance of SignedParts
     */
    public SignedParts() {
        spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    }
    
    public SignedParts(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
    }
    
    public void addBody() {
        
    }
    
    public boolean hasBody() {
        populate();
        return body;
    }
    
    public boolean hasAttachments() {
        populate();
        return attachments;
    }
    
    public String attachmentProtectionType(){
        populate();
        return attachmentProtectionType;
    }
    
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }
    private void populate(){
        populate(false);
    }
    
    private synchronized AssertionFitness populate(boolean isServer) {
        if(!populated){
            if(this.hasNestedAssertions()){
                Iterator <PolicyAssertion> it = this.getNestedAssertionsIterator();
                while( it.hasNext() ) {
                    PolicyAssertion as = (PolicyAssertion) it.next();
                    if(PolicyUtil.isBody(as, spVersion)){
                        // assertions.remove(as);
                        body = true;
                        // break;
                    } else if(PolicyUtil.isAttachments(as, spVersion)){
                        attachments = true;
                        if(as.hasParameters()){
                            Iterator <PolicyAssertion> attachIter = as.getParametersIterator();
                            while(attachIter.hasNext()){
                                PolicyAssertion attachType = attachIter.next();
                                if(PolicyUtil.isAttachmentCompleteTransform(attachType, spVersion)){
                                    attachmentProtectionType = MessageConstants.SWA11_ATTACHMENT_COMPLETE_SIGNATURE_TRANSFORM;
                                } else if(PolicyUtil.isAttachmentContentTransform(attachType, spVersion)){
                                    attachmentProtectionType = MessageConstants.SWA11_ATTACHMENT_CONTENT_SIGNATURE_TRANSFORM;
                                }
                            }
                        }
                    } else{
                        targets.add(as);
                    }
                }
                //targets = assertions;
            }
            populated = true;
        }
        return fitness;
    }
    
    public void addHeader(Header header) {
        
    }
    
    public Iterator getHeaders() {
        populate();
        if(targets == null){
            return Collections.emptyList().iterator();
        }
        return targets.iterator();
    }
}
