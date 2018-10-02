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

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.Lifetime;
import java.util.Collection;
import java.util.Iterator;
import static com.sun.xml.ws.security.impl.policy.Constants.*;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
/**
 *
 * @author K.Venugopal@sun.com Abhijit.Das@Sun.COM
 */
public class RequestSecurityTokenTemplate extends PolicyAssertion implements com.sun.xml.ws.security.policy.RequestSecurityTokenTemplate, SecurityAssertionValidator {
    
    private boolean populated = false;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    String tokenType;
    String requestType;
    Lifetime lifeTime;
    String authenticationType;
    private String keyType;
    private int keySize;
    private String sigAlgo;
    private String encAlgo;
    private String canonAlgo;
    private boolean isProofEncRequired = false;
    private String computedKeyAlgo;
    private boolean isEncRequired = false;
    private String signWith;
    private String encryptWith;
    private String keyWrapAlgo;
    private String wstVer;
    private Claims claims = null;
    
    /**
     * Creates a new instance of RequestSecurityTokenTemplate
     */
    public RequestSecurityTokenTemplate() {
    }
    
    public RequestSecurityTokenTemplate(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }
    
    public String getTokenType() {
        populate();
        return tokenType;
    }
    
    public String getRequestType() {
        populate();
        return this.requestType;
    }
    
    public Lifetime getLifetime() {
        populate();
        return lifeTime;
    }
    
    
    public String getAuthenticationType() {
        populate();
        return authenticationType;
    }
    
    
    public String getKeyType() {
        populate();
        return keyType;
    }
    
    public int getKeySize() {
        populate();
        return keySize;
    }
    
    
    
    public String getSignatureAlgorithm() {
        populate();
        return sigAlgo;
    }
    
    
    public String getEncryptionAlgorithm() {
        populate();
        return encAlgo;
    }
    
    
    public String getCanonicalizationAlgorithm() {
        populate();
        return canonAlgo;
    }
    
    
    public boolean getProofEncryptionRequired() {
        populate();
        return isProofEncRequired;
    }
    
    
    
    public String getComputedKeyAlgorithm() {
        populate();
        return computedKeyAlgo;
    }
    
    public String getKeyWrapAlgorithm() {
        populate();
        return keyWrapAlgo;
    }    
    
    public boolean getEncryptionRequired() {
        populate();
        return isEncRequired;
    }
    
    
    
    public String getSignWith() {
        populate();
        return signWith;
    }
    
    
    public String getEncryptWith() {
        populate();
        return encryptWith;
    }
    
    public Claims getClaims(){
        populate();
        return claims;
    }
    
    public String getTrustVersion() {
        populate();
        return wstVer;
    }
    
    
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }
    private void populate(){
        populate(false);
    }
    
    private synchronized AssertionFitness populate(boolean isServer) {
        if(!populated){
            if ( this.hasNestedAssertions() ) {
                
                Iterator <PolicyAssertion> it =this.getNestedAssertionsIterator();
                while( it.hasNext() ) {
                    PolicyAssertion assertion = (PolicyAssertion) it.next();
                    if (this.wstVer == null){
                        this.wstVer = assertion.getName().getNamespaceURI();
                    }
                    //TODO: Support all RequestSecurityTokenTemplate elements
                    if ( PolicyUtil.isKeyType(assertion) ) {
                        this.keyType = assertion.getValue();
                    } else if ( PolicyUtil.isKeySize(assertion) ) {
                        this.keySize = Integer.valueOf(assertion.getValue());
                    }  else if ( PolicyUtil.isEncryption(assertion) ) {
                        this.isEncRequired = true;
                    } else if ( PolicyUtil.isProofEncryption(assertion) ) {
                        this.isProofEncRequired = true;
                    } else if ( PolicyUtil.isLifeTime(assertion) ) {
                        this.lifeTime = (Lifetime) assertion;
                    }else if(PolicyUtil.isSignWith(assertion)){
                        this.signWith = assertion.getValue();
                    }else if(PolicyUtil.isEncryptWith(assertion)){
                        this.encryptWith = assertion.getValue();
                    }else if(PolicyUtil.isTrustTokenType(assertion)){
                        this.tokenType = assertion.getValue();
                    }else if(PolicyUtil.isRequestType(assertion)){
                        this.requestType = assertion.getValue();
                    }else if(PolicyUtil.isAuthenticationType(assertion)){
                        this.authenticationType = assertion.getValue();
                    }else if(PolicyUtil.isSignatureAlgorithm(assertion)){
                        this.sigAlgo = assertion.getValue();
                    }else if(PolicyUtil.isEncryptionAlgorithm(assertion)){
                        this.encAlgo = assertion.getValue();
                    }else if(PolicyUtil.isCanonicalizationAlgorithm(assertion)){
                        this.canonAlgo = assertion.getValue();
                    }else if(PolicyUtil.isComputedKeyAlgorithm(assertion)){
                        this.computedKeyAlgo = assertion.getValue();
                    }else if(PolicyUtil.isKeyWrapAlgorithm(assertion)){
                        this.keyWrapAlgo = assertion.getValue();
                    }else if(PolicyUtil.isEncryption(assertion)){
                        isEncRequired = true;
                    }else if(PolicyUtil.isClaimsElement(assertion)) {
                        claims = (Claims)assertion;
                    }else if(PolicyUtil.isEntropyElement(assertion)){
                        // Valid assertion.
                    }else {
                        if(!assertion.isOptional()){
                            log_invalid_assertion(assertion, isServer,RequestSecurityTokenTemplate);
                            fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                        }
                    }
                    
                }
            }
            populated = true;
        }
        return fitness;
    }
}
