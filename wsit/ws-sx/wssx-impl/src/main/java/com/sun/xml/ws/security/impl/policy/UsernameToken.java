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
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import static com.sun.xml.ws.security.impl.policy.Constants.*;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class UsernameToken extends PolicyAssertion implements com.sun.xml.ws.security.policy.UserNameToken, java.lang.Cloneable, SecurityAssertionValidator {
    
    private String tokenType;
    private String id;
    private boolean populated;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private boolean hasPassword = true;
    private boolean useHashPassword = false;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    private final QName itQname;
    private String includeToken;
    private Issuer issuer = null;
    private IssuerName issuerName = null;
    private Claims claims = null;
    private boolean reqDK=false;
    private boolean useNonce = false;
    private boolean useCreated = false;
    
    /**
     * Creates a new instance of UsernameToken
     */
    public UsernameToken() {
         id= PolicyUtil.randomUUID();
         itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
         includeToken = spVersion.includeTokenAlways;        
    }
    
    public UsernameToken(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        id= PolicyUtil.randomUUID();
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;        
    }    
    
    public void setType(String type) {
        this.tokenType = type;
    }
    
    @Override
    public String getType() {
        populate();
        return tokenType;
    }
    
    
    @Override
    public String getTokenId() {
        return id;
    }
    
    public void setTokenId(String _id) {
        this.id = _id;
    }
    
    @Override
    public String getIncludeToken() {
        populate();
        return  includeToken;
    }
    
    public void setIncludeToken(String type) {
        Map<QName, String> attrs = this.getAttributes();
        QName itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        attrs.put(itQname,type);
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
    @Override
    public boolean hasPassword(){
        return hasPassword;
    }
    
    @Override
    public boolean useHashPassword(){
        return useHashPassword;
    }
    
    @Override
    public boolean isRequireDerivedKeys() {
        populate();
        return reqDK;
    }
    
    private synchronized AssertionFitness populate(boolean isServer) {
        if(!populated){
            if(this.getAttributeValue(itQname) != null){
                this.includeToken = this.getAttributeValue(itQname);
            }
            NestedPolicy policy = this.getNestedPolicy();
            if(policy == null){
                if(logger.getLevel() == Level.FINE){
                    logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            AssertionSet assertionSet = policy.getAssertionSet();
            for(PolicyAssertion assertion: assertionSet){
                if (PolicyUtil.isUsernameTokenType(assertion, spVersion)) {
                    tokenType = assertion.getName().getLocalPart();
                } else if (PolicyUtil.hasPassword(assertion, spVersion)) {
                    hasPassword = false;
                } else if(PolicyUtil.isHashPassword(assertion, spVersion)){
                    useHashPassword = true;
                } else if (PolicyUtil.isRequireDerivedKeys(assertion, spVersion)){
                       reqDK = true;
                }else if (PolicyUtil.useCreated(assertion, spVersion)){
                       useCreated = true;
                }else if (PolicyUtil.useNonce(assertion, spVersion)){
                       useNonce = true;
                       useCreated = true;
                }else{
                    if(!assertion.isOptional()){
                        log_invalid_assertion(assertion, isServer,"UsernameToken");
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
                log_invalid_assertion(issuerName, isServer,SecureConversationToken);
                fitness = AssertionFitness.HAS_INVALID_VALUE;
            }
            populated = true;
        }
        return fitness;
    }
    
    @Override
    public Object clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SecurityPolicyVersion getSecurityPolicyVersion() {
        return spVersion;
    }

    @Override
    public Set getTokenRefernceType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean useNonce() {
        return useNonce;
    }

    @Override
    public boolean useCreated() {
        return useCreated;
    }
}
