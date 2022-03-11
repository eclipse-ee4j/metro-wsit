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
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;

import java.util.Collection;

import java.util.Map;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator.AssertionFitness;
import java.util.Iterator;


/**
 *
 * @author K.Venugopal@sun.com
 */
public class HttpsToken extends PolicyAssertion implements com.sun.xml.ws.security.policy.HttpsToken, SecurityAssertionValidator{
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private boolean populated = false;
    private boolean requireCC = false;
    private boolean httpBasicAuthentication = false;
    private boolean httpDigestAuthentication = false;
    private String id = "";
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    private final QName rccQname;
    private Issuer issuer = null;
    private IssuerName issuerName = null;
    private Claims claims = null;
    /**
     * Creates a new instance of HttpsToken
     */
    public HttpsToken() {
        id= PolicyUtil.randomUUID();
        rccQname = new QName(spVersion.namespaceUri, Constants.RequireClientCertificate);
    }
    
    public HttpsToken(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        id= PolicyUtil.randomUUID();
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
        rccQname = new QName(spVersion.namespaceUri, Constants.RequireClientCertificate);
    }
    
    public void setRequireClientCertificate(boolean value) {
        Map<QName, String> attrs = this.getAttributes();
        QName rccQname = new QName(spVersion.namespaceUri, Constants.RequireClientCertificate);
        attrs.put(rccQname,Boolean.toString(value));
        requireCC = value;
    }
    
    @Override
    public boolean isRequireClientCertificate() {
        populate();
        return this.requireCC;
    }
    
    @Override
    public String getIncludeToken() {
        throw new UnsupportedOperationException("This method is not supported for HttpsToken");
    }
    
    public void setIncludeToken(String type) {
        throw new UnsupportedOperationException("This method is not supported for HttpsToken");
    }
    
    @Override
    public String getTokenId() {
        return id;
    }
    
    @Override
    public Issuer getIssuer() {
        populate();
        return issuer;
    }
    
    @Override
    public IssuerName getIssuerName() {
        populate();
        return issuerName;
    }
    
    @Override
    public Claims getClaims(){
        populate();
        return claims;
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
            if(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri.equals(
                    spVersion.namespaceUri)){
                String value = this.getAttributeValue(rccQname);
                requireCC = Boolean.valueOf(value);
            }
            NestedPolicy policy = this.getNestedPolicy();
            if(policy == null){
                if(Constants.logger.getLevel() == Level.FINE){
                    Constants.logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            AssertionSet assertionSet = policy.getAssertionSet();
            for(PolicyAssertion assertion: assertionSet){
               if(PolicyUtil.isRequireClientCertificate(assertion, spVersion)){
                   requireCC = true;
               } else if(PolicyUtil.isHttpBasicAuthentication(assertion, spVersion)){
                   httpBasicAuthentication = true;
               } else if(PolicyUtil.isHttpDigestAuthentication(assertion, spVersion)){
                   httpDigestAuthentication = true;
               }else{
                    if(!assertion.isOptional()){
                        Constants.log_invalid_assertion(assertion, isServer,"HttpsToken");
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }
            if ( this.hasParameters() ) {
                Iterator <PolicyAssertion> it = this.getParametersIterator();
                while(it.hasNext()){
                    PolicyAssertion assertion = it.next();
                    if(PolicyUtil.isIssuer(assertion, spVersion)){
                        issuer = (Issuer)assertion;
                    } else if(PolicyUtil.isIssuerName(assertion, spVersion)){
                        issuerName = (IssuerName)assertion;
                    } else if(PolicyUtil.isClaimsElement(assertion) && 
                            SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri.equals(spVersion.namespaceUri) ){
                        claims = (Claims)assertion;
                    }
                }
            }
            if(issuer != null && issuerName != null){
                Constants.log_invalid_assertion(issuerName, isServer, Constants.SecureConversationToken);
                fitness = AssertionFitness.HAS_INVALID_VALUE;
            }
            populated = true;
        }
        return fitness;
        
    }
    
    @Override
    public SecurityPolicyVersion getSecurityPolicyVersion() {
        return spVersion;
    }

    @Override
    public boolean isHttpBasicAuthentication() {
        populate();
        if(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri.equals(
                spVersion.namespaceUri)){
            throw new UnsupportedOperationException("HttpBasicAuthentication is only supported for" +
                    "SecurityPolicy 1.2 and later");
        }
        return httpBasicAuthentication;
    }

    @Override
    public boolean isHttpDigestAuthentication() {
        populate();
        if(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri.equals(
                spVersion.namespaceUri)){
            throw new UnsupportedOperationException("HttpDigestAuthentication is only supported for" +
                    "SecurityPolicy 1.2 and later");
        }
        return httpDigestAuthentication;
    }
}
