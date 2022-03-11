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
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.namespace.QName;

/**
 * @author K.Venugopal@sun.com, Mayank.Mishra@Sun.com
 */

public class SpnegoContextToken extends PolicyAssertion implements com.sun.xml.ws.security.policy.SpnegoContextToken, SecurityAssertionValidator{

    private boolean populated = false;
    private PolicyAssertion rdKey = null;
    //Boolean requiredDerivedKeys = false;
    private String id;
    private Issuer issuer = null;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    private final QName itQname;
    private String includeToken;

    /**
     * Creates a new instance of SpnegoContextToken
     */
    public SpnegoContextToken() {
        id= PolicyUtil.randomUUID();
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
    }

    public SpnegoContextToken(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        id= PolicyUtil.randomUUID();
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
    }


    @Override
    public Issuer getIssuer() {
        populate();
        return this.issuer;
    }

    @Override
    public boolean isRequireDerivedKeys() {
        populate();
        return rdKey != null;
    }


    @Override
    public String getIncludeToken() {
        populate();
        return includeToken;
    }


    @Override
    public String getTokenId() {
        return id;
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
            NestedPolicy policy = this.getNestedPolicy();
            if(this.getAttributeValue(itQname) != null){
                includeToken = this.getAttributeValue(itQname);
            }
            if(policy == null){
                if(Constants.logger.getLevel() == Level.FINE){
                    Constants.logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            AssertionSet as = policy.getAssertionSet();
            Iterator<PolicyAssertion> paItr = as.iterator();

            while(paItr.hasNext()){
                PolicyAssertion assertion  = paItr.next();
                if(PolicyUtil.isRequireDerivedKeys(assertion, spVersion)){
                    rdKey = assertion;
                } else{
                    if(!assertion.isOptional()){
                        Constants.log_invalid_assertion(assertion, isServer,"SpnegoContextToken");
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }

            if ( this.hasNestedAssertions() ) {
                Iterator <PolicyAssertion> it = this.getNestedAssertionsIterator();
                while(it.hasNext()){
                    PolicyAssertion assertion = it.next();
                    if(PolicyUtil.isIssuer(assertion, spVersion)){
                        issuer = (Issuer)assertion;
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
