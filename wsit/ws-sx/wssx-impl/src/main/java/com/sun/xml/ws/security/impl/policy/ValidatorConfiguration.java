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

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.util.Iterator;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class ValidatorConfiguration extends PolicyAssertion implements com.sun.xml.ws.security.policy.ValidatorConfiguration, SecurityAssertionValidator{
    
    
    private boolean populated = false;
    private Iterator<PolicyAssertion> ast  = null;
    private static QName cmaxClockSkew =  new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"maxClockSkew");
    private static QName smaxClockSkew =  new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"maxClockSkew");
    private static QName ctimestampFreshnessLimit  =  new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"timestampFreshnessLimit");
    private static QName stimestampFreshnessLimit  =  new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"timestampFreshnessLimit"); 
    private static QName smaxNonceAge =  new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"maxNonceAge");
    private static QName crevocationEnabled =  new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"revocationEnabled");
    private static QName srevocationEnabled =  new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"revocationEnabled");
    private static QName cenforceKeyUsage=  new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"enforceKeyUsage");
    private static QName senforceKeyUsage =  new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"enforceKeyUsage");
    
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    /** Creates a new instance of ValidatorConfiguration */
    public ValidatorConfiguration() {
    }
    
    public ValidatorConfiguration(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }
    
    public Iterator<? extends PolicyAssertion> getValidators() {
        populate();
        return ast;
    }
    
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }
    
    private void populate(){
        populate(false);
    }
    
    private synchronized AssertionFitness populate(boolean isServer) {        
        if(!populated){
            this.ast  = this.getNestedAssertionsIterator();
            populated  = true;
        }
        return fitness;        
    }
    
    public String getMaxClockSkew() {       
        if(this.getAttributes().containsKey(cmaxClockSkew)){
            return this.getAttributeValue(cmaxClockSkew);
        }else if(this.getAttributes().containsKey(smaxClockSkew)){
            return this.getAttributeValue(smaxClockSkew);
        }
        return null;
    }
    
    public String getTimestampFreshnessLimit() {
         if(this.getAttributes().containsKey(ctimestampFreshnessLimit)){
            return this.getAttributeValue(ctimestampFreshnessLimit);
        }else if(this.getAttributes().containsKey(stimestampFreshnessLimit)){
            return this.getAttributeValue(stimestampFreshnessLimit);
        }
        return null;        
    }
    
    public String getMaxNonceAge() {
        if(this.getAttributes().containsKey(smaxNonceAge)){
            return this.getAttributeValue(smaxNonceAge);
        }
        return null;            
    }

    public String getRevocationEnabled() {
        if(this.getAttributes().containsKey(crevocationEnabled)){
            return this.getAttributeValue(crevocationEnabled);
        }else if(this.getAttributes().containsKey(srevocationEnabled)){
            return this.getAttributeValue(srevocationEnabled);
        }
        return null;
    }
    
    public String getEnforceKeyUsage() {
        if(this.getAttributes().containsKey(cenforceKeyUsage)){
            return this.getAttributeValue(cenforceKeyUsage);
        }else if(this.getAttributes().containsKey(senforceKeyUsage)){
            return this.getAttributeValue(senforceKeyUsage);
        }
        return null;
    }
}
