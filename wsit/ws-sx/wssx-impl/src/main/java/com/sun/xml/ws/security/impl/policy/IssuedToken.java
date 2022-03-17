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
import com.sun.xml.ws.security.policy.Issuer;
import com.sun.xml.ws.security.policy.RequestSecurityTokenTemplate;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.namespace.QName;

import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
/**
 *
 * @author K.Venugopal@sun.com Abhijit.Das@Sun.com
 */


public class IssuedToken extends PolicyAssertion implements  com.sun.xml.ws.security.policy.IssuedToken, SecurityAssertionValidator{

    private boolean populated = false;
    private RequestSecurityTokenTemplate rstTemplate;
    private Issuer issuer = null;
    private IssuerName issuerName = null;
    private ArrayList<String> referenceType;
    private String id;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private boolean reqDK=false;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    private final QName itQname;
    private String includeToken;
    private Claims claims = null;

    /**
     * Creates a new instance of IssuedToken
     */
    public IssuedToken() {
        id= PolicyUtil.randomUUID();
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
    }

    public IssuedToken(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        id= PolicyUtil.randomUUID();
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
    }

    @Override
    public RequestSecurityTokenTemplate getRequestSecurityTokenTemplate() {
        populate();
        return rstTemplate;
    }

    @Override
    public Iterator getTokenRefernceType() {
        populate();
        return referenceType.iterator();
    }

    @Override
    public String getIncludeToken() {
        populate();
        return includeToken;
    }

    public void setIncludeToken(String type) {
        //includeToken = type;
        throw new UnsupportedOperationException();
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
    public boolean isRequireDerivedKeys() {
        populate();
        return reqDK;
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
            if ( this.hasParameters() ) {
                Iterator <PolicyAssertion> it = this.getParametersIterator();
                while ( it.hasNext() ) {
                    PolicyAssertion assertion = it.next();
                    if ( PolicyUtil.isIssuer(assertion, spVersion) ) {
                        this.issuer = (Issuer) assertion;
                    } else if ( PolicyUtil.isRequestSecurityTokenTemplate(assertion, spVersion)) {
                        this.rstTemplate = (RequestSecurityTokenTemplate) assertion;
                    } else if(PolicyUtil.isIssuerName(assertion, spVersion)){
                        issuerName = (IssuerName)assertion;
                    } else if(PolicyUtil.isClaimsElement(assertion) &&
                            SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri.equals(spVersion.namespaceUri) ){
                        claims = (Claims)assertion;
                    } else{
                        if(!assertion.isOptional()){
                            Constants.log_invalid_assertion(assertion, isServer, Constants.IssuedToken);
                            fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                        }
                    }
                }
            }
            if(issuer != null && issuerName != null){
                Constants.log_invalid_assertion(issuerName, isServer, Constants.SecureConversationToken);
                fitness = AssertionFitness.HAS_INVALID_VALUE;
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
            if(as == null){
                if(Constants.logger.getLevel() == Level.FINE){
                    Constants.logger.log(Level.FINE," Nested Policy is empty");
                }
                populated = true;
                return fitness;
            }
            Iterator<PolicyAssertion> ast = as.iterator();

            while(ast.hasNext()){
                PolicyAssertion assertion = ast.next();
                if(referenceType == null){
                    referenceType = new ArrayList<>();
                }
                if ( PolicyUtil.isRequireDerivedKeys(assertion, spVersion)) {
                    reqDK = true;
                } else if ( PolicyUtil.isRequireExternalReference(assertion, spVersion)) {
                    referenceType.add(assertion.getName().getLocalPart().intern());
                } else if ( PolicyUtil.isRequireInternalReference(assertion, spVersion)) {
                    referenceType.add(assertion.getName().getLocalPart().intern());
                } else{
                    if(!assertion.isOptional()){
                        Constants.log_invalid_assertion(assertion, isServer, Constants.IssuedToken);
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
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
