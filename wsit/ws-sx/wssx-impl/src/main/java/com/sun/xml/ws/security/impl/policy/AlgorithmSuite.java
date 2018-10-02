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
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.AlgorithmSuiteValue;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import static com.sun.xml.ws.security.impl.policy.Constants.*;


/**
 *
 * @author K.Venugopal@sun.com Abhijit.das@Sun.com
 */

public class AlgorithmSuite extends com.sun.xml.ws.policy.PolicyAssertion implements com.sun.xml.ws.security.policy.AlgorithmSuite,SecurityAssertionValidator{
    
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private AlgorithmSuiteValue value;
    private HashSet<String> props = new HashSet<String>();
    private boolean populated = false;    
    private SecurityPolicyVersion spVersion;
    private String signatureAlgo = null;
    /**
     * Creates a new instance of AlgorithmSuite
     */
    public AlgorithmSuite() {
        spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    }
    
    public AlgorithmSuite(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
    }
    
    public Set getAdditionalProps() {
        return props;
    }
    
    public void setAdditionalProps(Set properties) {
    }
    
    public void setType(AlgorithmSuiteValue value) {
        this.value = value;
        populated = true;
    }
    
    public AlgorithmSuiteValue getType() {
        populate();
        return value;
    }
    
    public String getDigestAlgorithm() {
        populate();
        return value.getDigAlgorithm();
    }
    
    
    public String getEncryptionAlgorithm() {
        populate();
        return value.getEncAlgorithm();
    }
    
    
    public String getSymmetricKeyAlgorithm() {
        populate();
        return value.getSymKWAlgorithm();
    }
    
    public String getAsymmetricKeyAlgorithm() {
        populate();
        return value.getAsymKWAlgorithm();
    }
    
    public String getSignatureKDAlogrithm() {
        populate();
        return value.getSigKDAlgorithm();
    }
    
    public String getEncryptionKDAlogrithm() {
        populate();
        return value.getEncKDAlgorithm();
    }
    
    public int getMinSKLAlgorithm() {
        populate();
        return value.getMinSKLAlgorithm();
    }
    
    public String getSymmetricKeySignatureAlgorithm() {
        return com.sun.xml.ws.security.policy.Constants.HMAC_SHA1;
    }
    
    public String getAsymmetricKeySignatureAlgorithm() {
        return com.sun.xml.ws.security.policy.Constants.RSA_SHA1;
    }
    
    private void populate(){
        populate(false);
    }
    
    private synchronized AssertionFitness populate(boolean isServer) {
        
        if(!populated){
            NestedPolicy policy = this.getNestedPolicy();
            if(policy == null){
                if(logger.isLoggable(Level.FINE)){
                    logger.log(Level.FINE,"NestedPolicy is null");
                }
                if(this.value == null){
                    this.value = AlgorithmSuiteValue.Basic128;
                }
                return fitness;
            }
            AssertionSet as = policy.getAssertionSet();
            
            Iterator<PolicyAssertion> ast = as.iterator();
            while(ast.hasNext()){
                PolicyAssertion assertion = ast.next();
                if(this.value == null){
                    AlgorithmSuiteValue av = PolicyUtil.isValidAlgorithmSuiteValue(assertion, spVersion);
                    if(av != null){
                        this.value = av;
                        continue;
                    }
                }
                if(PolicyUtil.isInclusiveC14N(assertion, spVersion)){
                    this.props.add(Constants.InclusiveC14N);
                }else if(PolicyUtil.isXPath(assertion, spVersion)){
                    this.props.add(Constants.XPath);
                }else if(PolicyUtil.isXPathFilter20(assertion)){
                    this.props.add(Constants.XPathFilter20);
                }else if(PolicyUtil.isSTRTransform10(assertion, spVersion)){
                    this.props.add(Constants.STRTransform10);
                }else if(PolicyUtil.isInclusiveC14NWithComments(assertion)){
                    if(PolicyUtil.isInclusiveC14NWithCommentsForTransforms(assertion)){
                        this.props.add(Constants.InclusiveC14NWithCommentsForTransforms);
                    }
                    if(PolicyUtil.isInclusiveC14NWithCommentsForCm(assertion)){
                        this.props.add(Constants.InclusiveC14NWithCommentsForCm);
                    }
                }else if(PolicyUtil.isExclusiveC14NWithComments(assertion)){
                    if(PolicyUtil.isExclusiveC14NWithCommentsForTransforms(assertion)){
                        this.props.add(Constants.ExclusiveC14NWithCommentsForTransforms);
                    } 
                    if(PolicyUtil.isExclusiveC14NWithCommentsForCm(assertion)){
                        this.props.add(Constants.ExclusiveC14NWithCommentsForCm);
                    }
                }else{
                    if(!assertion.isOptional()){
                        log_invalid_assertion(assertion, isServer,AlgorithmSuite);
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }
            if(this.value == null){
                this.value = AlgorithmSuiteValue.Basic128;
            }
            populated = true;
        }
        return fitness;
    }
    
    
    public String getComputedKeyAlgorithm() {
        return com.sun.xml.ws.security.policy.Constants.PSHA1;
    }
    
    public int getMaxSymmetricKeyLength() {
        return MAX_SKL;
    }
    
    public int getMinAsymmetricKeyLength() {
        return MIN_AKL;
    }
    
    public int getMaxAsymmetricKeyLength() {
        return MAX_AKL;
    }
    
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }

    public void setSignatureAlgorithm(String sigAlgo) {
       this.signatureAlgo = sigAlgo;
    }
    public String getSignatureAlgorithm() {
       return this.signatureAlgo ;
    }
}
