/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import static com.sun.xml.ws.security.impl.policy.Constants.*;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;

/**
 *
 * @author K.Venugopal@sun.com
 */

public class Token extends PolicyAssertion implements  com.sun.xml.ws.security.policy.Token, SecurityAssertionValidator{
    
    private String _id;
    private boolean populated= false;
    private com.sun.xml.ws.security.policy.Token _token;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    private final QName itQname;
    private String _includeToken;
    /**
     * Creates a new instance of Token
     */
    
    public Token(){
        _id= PolicyUtil.randomUUID();
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        _includeToken = spVersion.includeTokenAlways;
    }
    
    public Token(QName name) {
        _id= PolicyUtil.randomUUID();
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        _includeToken = spVersion.includeTokenAlways;
    }
    
    public Token(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        _includeToken = spVersion.includeTokenAlways;
        _id= PolicyUtil.randomUUID();
    }
    
    public com.sun.xml.ws.security.policy.Token getToken() {
        populate();
        return _token;
    }
    
    @Override
    public String getIncludeToken() {
        populate();
        return _includeToken;
    }
    
    public void setIncludeToken(String type) {
    }
    
    public void setToken(com.sun.xml.ws.security.policy.Token token) {
        //TODO
    }
    
    @Override
    public String getTokenId() {
        
        return _id;
    }
    
    @Override
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }
    private void populate(){
        populate(false);
    }
    
    private synchronized AssertionFitness populate(boolean isServer) {
        if(!populated){
            String tValue = getAttributeValue(itQname);
            if(tValue != null){
                _includeToken = tValue;
            }
            NestedPolicy policy = this.getNestedPolicy();
            if(policy == null){
                if(logger.getLevel() == Level.FINE){
                    logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            AssertionSet as = policy.getAssertionSet();
            Iterator<PolicyAssertion> ast = as.iterator();
            while(ast.hasNext()){
                PolicyAssertion assertion = ast.next();
                if(PolicyUtil.isToken(assertion, spVersion)){
                    _token = (com.sun.xml.ws.security.policy.Token)assertion;
                }else{
                    if(!assertion.isOptional()){
                        log_invalid_assertion(assertion, isServer,"Token");
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }
            
            populated = true;
        }
        return fitness;
    }

    @Override
    public SecurityPolicyVersion getSecurityPolicyVersion() {
        return spVersion;
    }
}
