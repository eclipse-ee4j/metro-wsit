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
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import static com.sun.xml.ws.security.impl.policy.Constants.*;
/**
 *
 * @author K.Venugopal@sun.com
 */

public class SecureConversationToken extends PolicyAssertion implements com.sun.xml.ws.security.policy.SecureConversationToken, SecurityAssertionValidator{
    private NestedPolicy bootstrapPolicy = null;
    private String id = null;
    private boolean populated = false;
    private PolicyAssertion rdKey = null;
    private Set<String> referenceType = null;
    private Issuer issuer = null;
    private IssuerName issuerName = null;
    private String tokenType = null;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    private final QName itQname;
    private String includeToken;
    private PolicyAssertion mustNotSendCancel = null;
    private PolicyAssertion mustNotSendRenew = null;
    private Claims claims = null;

    /**
     * Creates a new instance of SecureConversationToken
     */
    public SecureConversationToken() {
         id= PolicyUtil.randomUUID();
         itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
         includeToken = spVersion.includeTokenAlways;
    }
    
    public SecureConversationToken(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        id= PolicyUtil.randomUUID();
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
    }
    
    
    @Override
    public Set getTokenRefernceTypes() {
        populate();
        if(referenceType == null ){
            return Collections.emptySet();
        }
        return referenceType;
    }
    
    @Override
    public boolean isRequireDerivedKeys() {
        populate();
        if( rdKey != null){
            return true;
        }
        return false;
    }
    
    
        @Override
        public boolean isMustNotSendCancel() {
        populate();
        if( mustNotSendCancel != null){
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isMustNotSendRenew() {
        populate();
        if( mustNotSendRenew != null){
            return true;
        }
        return false;
    }
    
    @Override
    public String getTokenType() {
        populate();
        return this.tokenType;
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
    public String getIncludeToken() {
        populate();
        return includeToken;
    }
    
    public void setIncludeToken(String type) {
        Map<QName, String> attrs = this.getAttributes();
        QName tokenName = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        attrs.put(tokenName,type);
    }
    
    
    
    @Override
    public NestedPolicy getBootstrapPolicy() {
        populate();
        return bootstrapPolicy;
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
            String tmp = getAttributeValue(itQname);
            if(tmp != null)
                includeToken = tmp;
            NestedPolicy policy = this.getNestedPolicy();
            if(policy == null){
                if(logger.getLevel() == Level.FINE){
                    logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            AssertionSet as = policy.getAssertionSet();
            Iterator<PolicyAssertion> paItr = as.iterator();
            while(paItr.hasNext()){
                PolicyAssertion assertion = paItr.next();
                if(PolicyUtil.isBootstrapPolicy(assertion, spVersion)){
                    bootstrapPolicy = assertion.getNestedPolicy();
                }else if(PolicyUtil.isRequireDerivedKeys(assertion, spVersion)){
                    rdKey =  assertion;
                }else if(PolicyUtil.isRequireExternalUriReference(assertion, spVersion)){
                    if(referenceType == null){
                        referenceType = new HashSet<>();
                    }
                    referenceType.add(assertion.getName().getLocalPart().intern());
                }else if(PolicyUtil.isSC10SecurityContextToken(assertion, spVersion)){
                    tokenType = assertion.getName().getLocalPart();
                }else if(PolicyUtil.isMustNotSendCancel(assertion, spVersion)){
                    mustNotSendCancel = assertion;
                }else if(PolicyUtil.isMustNotSendRenew(assertion, spVersion)){
                    mustNotSendRenew = assertion;
                }else{
                    if(!assertion.isOptional()){
                        log_invalid_assertion(assertion, isServer,SecureConversationToken);
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
    public SecurityPolicyVersion getSecurityPolicyVersion() {
        return spVersion;
    }
}

