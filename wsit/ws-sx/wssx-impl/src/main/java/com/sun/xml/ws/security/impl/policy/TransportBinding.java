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
import com.sun.xml.ws.security.policy.AlgorithmSuite;
import com.sun.xml.ws.security.policy.HttpsToken;
import com.sun.xml.ws.security.policy.MessageLayout;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import java.util.Collection;
import java.util.logging.Level;
import javax.xml.namespace.QName;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class TransportBinding extends PolicyAssertion implements com.sun.xml.ws.security.policy.TransportBinding, SecurityAssertionValidator{
    
    HttpsToken transportToken;
    private AlgorithmSuite algSuite;
    boolean includeTimeStamp=false;
    MessageLayout layout = MessageLayout.Lax;
    boolean populated = false;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private SecurityPolicyVersion spVersion;
    /**
     * Creates a new instance of TransportBinding
     */
    public TransportBinding() {
        spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    }
    
    public TransportBinding(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
    }
    
    public void addTransportToken(Token token) {
        transportToken = (HttpsToken) token;
    }
    
    @Override
    public Token getTransportToken() {
        populate();
        return transportToken;
    }
    
    public void setAlgorithmSuite(AlgorithmSuite algSuite) {
        this.algSuite = algSuite;
    }
    
    @Override
    public AlgorithmSuite getAlgorithmSuite() {
        populate();
        return algSuite;
    }
    
    public void includeTimeStamp(boolean value) {
        includeTimeStamp = value;
    }
    
    @Override
    public boolean isIncludeTimeStamp() {
        populate();
        return includeTimeStamp;
    }
    
    public void setLayout(MessageLayout layout) {
        this.layout = layout;
    }
    
    @Override
    public MessageLayout getLayout() {
        populate();
        return layout;
    }
    
    @Override
    public boolean isSignContent() {
        throw new UnsupportedOperationException("Not supported");
    }
    
    public void setSignContent(boolean contentOnly) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    public void setProtectionOrder(String order) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    @Override
    public String getProtectionOrder() {
        throw new UnsupportedOperationException("Not supported");
    }
    
    public void setTokenProtection(boolean token) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    public void setSignatureProtection(boolean token) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    @Override
    public boolean getTokenProtection() {
        throw new UnsupportedOperationException("Not supported");
    }
    
    @Override
    public boolean getSignatureProtection() {
        throw new UnsupportedOperationException("Not supported");
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
            AssertionSet assertions = policy.getAssertionSet();
            if(assertions == null){
                if(Constants.logger.getLevel() == Level.FINE){
                    Constants.logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            for(PolicyAssertion assertion : assertions){
                if(PolicyUtil.isAlgorithmAssertion(assertion, spVersion)){
                    this.algSuite = (AlgorithmSuite) assertion;
                    String sigAlgo = assertion.getAttributeValue(new QName("signatureAlgorithm"));
                    this.algSuite.setSignatureAlgorithm(sigAlgo);
                }else if(PolicyUtil.isToken(assertion, spVersion)){
                    transportToken = (HttpsToken)((com.sun.xml.ws.security.impl.policy.Token)assertion).getToken();
                }else if(PolicyUtil.isMessageLayout(assertion, spVersion)){
                    layout = ((Layout)assertion).getMessageLayout();
                }else if(PolicyUtil.isIncludeTimestamp(assertion, spVersion)){
                    includeTimeStamp=true;
                } else{
                    if(!assertion.isOptional()){
                        Constants.log_invalid_assertion(assertion, isServer, Constants.TransportBinding);
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }
            populated = true;
        }
        return fitness;
    }

    @Override
    public boolean isDisableTimestampSigning() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SecurityPolicyVersion getSecurityPolicyVersion() {
        return spVersion;
    }
}
