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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.namespace.QName;

import com.sun.xml.ws.security.policy.SecurityAssertionValidator.AssertionFitness;

/**
 *
 * @author mayank.mishra@Sun.com
 */
public class KerberosToken extends PolicyAssertion implements com.sun.xml.ws.security.policy.KerberosToken, SecurityAssertionValidator {

    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private boolean populated = false;
    private String tokenType = null;
    private String id = null;
    private boolean reqDK=false;
    private boolean isServer = false;
    private HashSet<String> referenceType = null;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    private final QName itQname;
    private String includeToken;
    private Issuer issuer = null;
    private IssuerName issuerName = null;
    private Claims claims = null;

    /** Creates a new instance of KerberosToken */
    public KerberosToken(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        id= PolicyUtil.randomUUID();
        referenceType = new HashSet<>();
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
    }


    @Override
    public String getTokenType() {
        populate();
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public Set getTokenRefernceType() {
        populate();
        return referenceType;
    }

    public void addTokenReferenceType(String tokenRefType) {
        referenceType.add(tokenRefType);
    }

    @Override
    public boolean isRequireDerivedKeys() {
        populate();
        return reqDK;
    }

    @Override
    public String getIncludeToken() {
        populate();
        return includeToken;
    }

    public void setIncludeToken(String type) {
        includeToken = type;
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
            if(this.getAttributeValue(itQname)!=null){
                this.includeToken = this.getAttributeValue(itQname);
            }
            NestedPolicy policy = this.getNestedPolicy();
            if(policy == null){
                if(Constants.logger.getLevel() == Level.FINE){
                    Constants.logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            AssertionSet as = policy.getAssertionSet();

            for(PolicyAssertion assertion: as){
                if(PolicyUtil.isTokenReferenceType(assertion, spVersion)){
                    referenceType.add(assertion.getName().getLocalPart().intern());
                }else if(PolicyUtil.isKerberosTokenType(assertion, spVersion)){
                    tokenType = assertion.getName().getLocalPart().intern();
                }else if (PolicyUtil.isRequireDerivedKeys(assertion, spVersion)) {
                    reqDK = true;
                } else{
                    if(!assertion.isOptional()){
                        Constants.log_invalid_assertion(assertion, isServer, Constants.KerberosToken);
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

}
