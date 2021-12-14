/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * SecurityContextToken.java
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import static com.sun.xml.ws.security.impl.policy.Constants.*;
import javax.xml.namespace.QName;

/**
 *
 * @author Mayank.Mishra@Sun.com
 */
public class SecurityContextToken extends PolicyAssertion implements com.sun.xml.ws.security.policy.SecurityContextToken, SecurityAssertionValidator{
    private String id;
    //private List<String> tokenRefType;
    private boolean populated = false;
    private String tokenType;
    private PolicyAssertion rdKey = null;
    private Set<String> referenceType = null;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    private final QName itQname;
    private String includeToken;
    
    /** Creates a new instance of SecurityContextToken */
    public SecurityContextToken(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        id= PolicyUtil.randomUUID();
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
    
    @Override
    public Iterator getTokenRefernceType() {
        //this check is not necessary as tokenRefType is always null
        /*if ( tokenRefType != null ) {
            return tokenRefType.iterator();
        } else {
            return Collections.emptyList().iterator();
        }*/
        return Collections.emptyList().iterator();
    }
    
    @Override
    public boolean isRequireDerivedKeys() {
        populate();
        if (rdKey != null ) {
            return true;
        }
        return false;
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
                if(logger.getLevel() == Level.FINE){
                    logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            AssertionSet as = policy.getAssertionSet();
            Iterator<PolicyAssertion> paItr = as.iterator();
            
            while(paItr.hasNext()){
                PolicyAssertion assertion  = paItr.next();
                if(PolicyUtil.isSecurityContextTokenType(assertion, spVersion)){
                    tokenType = assertion.getName().getLocalPart().intern();
                }else if(PolicyUtil.isRequireDerivedKeys(assertion, spVersion)){
                    rdKey = assertion;
                }else if(PolicyUtil.isRequireExternalUriReference(assertion, spVersion)){
                    if(referenceType == null){
                        referenceType = new HashSet<>();
                    }
                    referenceType.add(assertion.getName().getLocalPart().intern());
                } else{
                    if(!assertion.isOptional()){
                        if(logger.getLevel() == Level.SEVERE){
                            logger.log(Level.SEVERE,LogStringsMessages.SP_0100_INVALID_SECURITY_ASSERTION(assertion, "SecurityContextToken"));
                        }
                        if(isServer){
                            throw new UnsupportedPolicyAssertion("Policy assertion "+
                                    assertion+" is not supported under SecurityContextToken assertion");
                        }
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
