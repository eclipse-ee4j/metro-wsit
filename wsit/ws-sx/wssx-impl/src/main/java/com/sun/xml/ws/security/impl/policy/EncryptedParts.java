/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
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

import com.sun.xml.ws.security.policy.Header;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;

import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
/**
 *
 * @author K.Venugopal@sun.com Abhijit.Das@Sun.com
 */

public class EncryptedParts extends PolicyAssertion implements com.sun.xml.ws.security.policy.EncryptedParts, SecurityAssertionValidator {
    private boolean _body;
    private boolean _attachments;
    private List<Header> header;
    private boolean populated = false;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private SecurityPolicyVersion spVersion;
    
    /** Creates a new instance of EncryptedPartImpl */
    public EncryptedParts() {
        spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    }
    public EncryptedParts(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
    }
    
    public void addBody() {
        this._body = true;
    }
    
    @Override
    public boolean hasBody(){
        populate();
        return this._body;
    }
    
    @Override
    public boolean hasAttachments(){
        populate();
        return this._attachments;
    }
    
    public void addTarget(QName targetName) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Iterator getTargets() {
        populate();
        if(header == null){
            return Collections.emptyList().iterator();
        }
        return header.iterator();
    }
    
    //    public QName getName() {
    //        return Constants._EncryptedParts_QNAME;
    //    }
    
    @Override
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }
    private void populate(){
        populate(false);
    }
    
    private synchronized AssertionFitness populate(boolean isServer) {
        if(!populated){
            if ( this.hasNestedAssertions() ) {
                
                Iterator <PolicyAssertion> it = this.getNestedAssertionsIterator();
                while( it.hasNext() ) {
                    PolicyAssertion assertion = it.next();
                    if ( PolicyUtil.isBody(assertion, spVersion)) {
                        this._body = true;
                    } else if(PolicyUtil.isAttachments(assertion, spVersion)){
                        this._attachments = true;
                    } else {
                        if(header == null){
                            header = new ArrayList<>();
                        }
                        if(PolicyUtil.isHeader(assertion, spVersion)){
                            this.header.add((Header)assertion);
                        }else{
                            if(!assertion.isOptional()){
                                Constants.log_invalid_assertion(assertion, isServer, Constants.EncryptedParts);
                                fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                            }
                        }
                    }
                }
            }
            populated = true;
        }
        return fitness;
    }
    
    public void removeTarget(QName targetName) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void removeBody() {
        throw new UnsupportedOperationException();
    }
}
