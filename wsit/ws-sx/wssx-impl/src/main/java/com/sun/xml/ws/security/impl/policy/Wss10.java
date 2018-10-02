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
import java.util.HashSet;
import java.util.Set;
import static com.sun.xml.ws.security.impl.policy.Constants.*;
import java.util.logging.Level;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class Wss10 extends PolicyAssertion implements com.sun.xml.ws.security.policy.WSSAssertion, SecurityAssertionValidator {
    
    Set<String> requiredPropSet;
    String version = "1.0";   
    boolean populated = false;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private SecurityPolicyVersion spVersion;
    
    /**
     * Creates a new instance of WSSAssertion
     */
    public Wss10() {
        spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    }
    
    public Wss10(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
    }
    
    
    
    public void addRequiredProperty(String requirement) {
        if(requiredPropSet == null){
            requiredPropSet = new HashSet<String>();
        }
        requiredPropSet.add(requirement);
    }
    
    public Set<String> getRequiredProperties() {
        populate();
        return requiredPropSet;
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
            for(PolicyAssertion pa:as){
                if(PolicyUtil.isWSS10PolicyContent(pa, spVersion)){
                    addRequiredProperty(pa.getName().getLocalPart().intern());
                }else{
                    if(!pa.isOptional()){
                        log_invalid_assertion(pa, isServer,"Wss10");
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }
            populated = true;
        }
        return fitness;
    }
}
