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

import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.MessageLayout;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Collection;
import static com.sun.xml.ws.security.impl.policy.Constants.*;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;

/**
 *
 * @author K.Venugopal@sun.com
 */

public class Layout extends PolicyAssertion implements SecurityAssertionValidator {
    
    MessageLayout ml;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private boolean populated = false;
    private SecurityPolicyVersion spVersion;
    /**
     * Creates a new instance of Layout
     */
    public Layout() {
        spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    }
    
    public Layout(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
    }
    public MessageLayout getMessageLayout() {
        populate();
        return ml;
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
            AssertionSet assertionSet = policy.getAssertionSet();
            for(PolicyAssertion assertion : assertionSet){
                if(PolicyUtil.isLax(assertion, spVersion)){
                    ml =  MessageLayout.Lax;
                }else if(PolicyUtil.isLaxTsFirst(assertion, spVersion)){
                    ml = MessageLayout.LaxTsFirst;
                }else if(PolicyUtil.isLaxTsLast(assertion, spVersion)){
                    ml = MessageLayout.LaxTsLast;
                }else if(PolicyUtil.isStrict(assertion, spVersion)){
                    ml= MessageLayout.Strict;
                } else{
                    if(!assertion.isOptional()){
                        log_invalid_assertion(assertion, isServer,Layout);
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }
            populated = true;
        }
        return fitness;
    }
}
