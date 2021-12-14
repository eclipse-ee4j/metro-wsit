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
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import java.util.Collection;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class IssuerName extends PolicyAssertion implements com.sun.xml.ws.security.policy.IssuerName, SecurityAssertionValidator{

    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private boolean populated = false;
    String issuerName = null;
    
    /**
     * Creates a new instance of Issuer
     */
    public IssuerName() {
    }
    
    public IssuerName(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }
    
    @Override
    public String getIssuerName() {
        populate();
        return issuerName;
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
            issuerName = this.getValue();
            populated = true;
        }
        return fitness;
    }

}
