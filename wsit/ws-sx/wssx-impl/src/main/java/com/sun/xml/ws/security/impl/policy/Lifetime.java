/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * LifeTimeImpl.java
 *
 * Created on February 23, 2006, 12:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;
import static com.sun.xml.ws.security.impl.policy.Constants.*;
import java.util.logging.Level;
/**
 *
 * @author Abhijit Das
 */
public class Lifetime extends PolicyAssertion implements com.sun.xml.ws.security.policy.Lifetime, SecurityAssertionValidator {
    
    private String created;
    private String expires;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private boolean populated = false;
    
    /** Creates a new instance of LifeTimeImpl */
    public Lifetime(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }
    
    public String getCreated() {
        populate();
        return created;
    }
    
    public void setCreated(String created) {
        this.created = created;
    }
    
    public String getExpires() {
        populate();
        return expires;
    }
    
    public void setExpires(String expires) {
        this.expires = expires;
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
            for(PolicyAssertion pa : as){
                if ( PolicyUtil.isCreated(pa) ) {
                    this.created = pa.getValue();
                } else if ( PolicyUtil.isExpires(pa) ) {
                    this.expires = pa.getValue();
                }
            }
            populated = true;
        }
        return fitness;
    }
}
