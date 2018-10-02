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
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Collection;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import static com.sun.xml.ws.security.impl.policy.Constants.*;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class KeyValueToken extends PolicyAssertion implements com.sun.xml.ws.security.policy.KeyValueToken,Cloneable, SecurityAssertionValidator{
    
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private boolean populated = false;
    private String tokenType = null;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY12NS;
    private final QName itQname;
    private String includeToken;
    private String id = null;
    private boolean isServer = false;
    
    /** Creates a new instance of KeyValueToken */
    public KeyValueToken() {
        id= PolicyUtil.randomUUID();
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
    }
    
    public KeyValueToken(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        
        id= PolicyUtil.randomUUID();
        
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public String getTokenType() {
        populate();
        return tokenType;
    }
    
    public String getIncludeToken() {
        populate();
        return includeToken;
    }
    
    public void setIncludeToken(String type) {
        includeToken = type;
    }
    
    public String getTokenId() {
        return id;
    }
    
    public SecurityPolicyVersion getSecurityPolicyVersion() {
        return spVersion;
    }
    
    public SecurityAssertionValidator.AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }

    private void populate() {
        populate(false);
    }

    private SecurityAssertionValidator.AssertionFitness populate(boolean isServer) {
        if(!populated){
            if(this.getAttributeValue(itQname)!=null){
                this.includeToken = this.getAttributeValue(itQname);
            }
            NestedPolicy policy = this.getNestedPolicy();
            if(policy == null){
                if(logger.getLevel() == Level.FINE){
                    logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            AssertionSet assertionSet = policy.getAssertionSet();
            for(PolicyAssertion assertion: assertionSet){
                if(PolicyUtil.isKeyValueTokenType(assertion, spVersion)){
                    tokenType = assertion.getName().getLocalPart();
                }else{
                    if(!assertion.isOptional()){
                        log_invalid_assertion(assertion, isServer,"KeyValueToken");
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }
            populated = true;
        }
        return fitness;
    }
    
}
