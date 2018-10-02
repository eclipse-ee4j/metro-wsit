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
import static com.sun.xml.ws.security.impl.policy.Constants.*;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;
import javax.xml.namespace.QName;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class TransportToken extends Token implements com.sun.xml.ws.security.policy.TransportToken, SecurityAssertionValidator  {
    private String id;
    private HttpsToken token = null;
    private boolean populated;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private QName itQname;
    private String includeToken;
    /**
     * Creates a new instance of TransportToken
     */
    public TransportToken() {
         id= PolicyUtil.randomUUID();
         itQname = new QName(getSecurityPolicyVersion().namespaceUri, Constants.IncludeToken);
         includeToken = "";
    }
    
    public TransportToken(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        id= PolicyUtil.randomUUID();
        itQname = new QName(getSecurityPolicyVersion().namespaceUri, Constants.IncludeToken);
    }
    
    @Override
    public String getTokenId() {
        return id;
    }
    @Override
    public String getIncludeToken() {
        throw new UnsupportedOperationException("This method is not supported for TransportToken");
    }
    @Override
    public void setIncludeToken(String type) {
        throw new UnsupportedOperationException("This method is not supported for TransportToken");
    }
    
    public com.sun.xml.ws.security.policy.HttpsToken getHttpsToken() {
        populate();
        return token;
    }
    public void setHttpsToken(com.sun.xml.ws.security.policy.HttpsToken token){
        //TODO::
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
            this.includeToken = this.getAttributeValue(itQname);
            NestedPolicy policy = this.getNestedPolicy();
            AssertionSet assertionSet = policy.getAssertionSet();
            for(PolicyAssertion assertion: assertionSet){
                if(PolicyUtil.isHttpsToken(assertion, getSecurityPolicyVersion())){
                    token = (HttpsToken) assertion;
                }else{
                    if(!assertion.isOptional()){
                        log_invalid_assertion(assertion, isServer,"TransportToken");
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }
            this.populated  = true;
        }
        return fitness;
    }
}
