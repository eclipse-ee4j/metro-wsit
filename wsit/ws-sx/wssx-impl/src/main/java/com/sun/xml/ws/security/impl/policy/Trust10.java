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
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.Constants;
import static com.sun.xml.ws.security.impl.policy.Constants.*;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class Trust10 extends PolicyAssertion implements com.sun.xml.ws.security.policy.TrustAssertion, SecurityAssertionValidator{
    Set<String> requiredProps;
    String version = "1.0";
    private boolean populated = false;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private SecurityPolicyVersion spVersion;
    
    /**
     * Creates a new instance of Trust10
     */
    public Trust10() {
        spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    }
    
    
    public Trust10(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
    }
    
    public void addRequiredProperty(String requirement) {
        if(requiredProps == null){
            requiredProps = new HashSet<String>();
        }
        requiredProps.add(requirement);
    }
    
    public Set getRequiredProperties() {
        populate();
        return requiredProps;
    }
    
    public String getType() {
        return version;
    }
    
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }
    private void populate(){
        populate(false);
    }
    
    private synchronized AssertionFitness populate(boolean isServer) {
        if(!populated){
            NestedPolicy policy = this.getNestedPolicy();
            if(policy == null){
                if(logger.getLevel() == Level.FINE){
                    logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            AssertionSet as = policy.getAssertionSet();
            for(PolicyAssertion assertion:as){
                if(PolicyUtil.isSupportClientChallenge(assertion, spVersion)){
                    addRequiredProperty(Constants.MUST_SUPPORT_CLIENT_CHALLENGE);
                }else if(PolicyUtil.isSupportServerChallenge(assertion, spVersion)){
                    addRequiredProperty(Constants.MUST_SUPPORT_SERVER_CHALLENGE);
                }else if(PolicyUtil.isRequireClientEntropy(assertion, spVersion)){
                    addRequiredProperty(Constants.REQUIRE_CLIENT_ENTROPY);
                }else if(PolicyUtil.isRequireServerEntropy(assertion, spVersion)){
                    addRequiredProperty(Constants.REQUIRE_SERVER_ENTROPY);
                }else if(PolicyUtil.isSupportIssuedTokens(assertion, spVersion)){
                    addRequiredProperty(Constants.MUST_SUPPORT_ISSUED_TOKENS);
                }else{
                    if(!assertion.isOptional()){
                        log_invalid_assertion(assertion, isServer,"Trust10");
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }
            
            populated = true;
        }
        return fitness;
    }
    
}
