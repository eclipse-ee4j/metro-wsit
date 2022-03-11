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
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.ws.security.policy.Token;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.xml.namespace.QName;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class SupportingTokens extends PolicyAssertion implements com.sun.xml.ws.security.policy.SupportingTokens{

    private AlgorithmSuite algSuite;
    private List<com.sun.xml.ws.security.policy.SignedParts> spList = new ArrayList<>(1);
    private List<com.sun.xml.ws.security.policy.EncryptedParts> epList = new ArrayList<>(1);
    private List<com.sun.xml.ws.security.policy.SignedElements> seList = new ArrayList<>(1);
    private List<com.sun.xml.ws.security.policy.EncryptedElements> eeList = new ArrayList<>(1);;
    private boolean isServer = false;
    private List<Token> _tokenList;
    private boolean populated;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;

    /**
     * Creates a new instance of SupportingTokens
     */
    public SupportingTokens() {
    }

    public SupportingTokens(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
    }

    public void setAlgorithmSuite(AlgorithmSuite algSuite) {
        this.algSuite =algSuite;
    }

    @Override
    public AlgorithmSuite getAlgorithmSuite() {
        populate();
        return algSuite;
    }


    public void addToken(Token token) {
        if(_tokenList == null){
            _tokenList = new ArrayList<>();
            //Workaround - workaround to remove duplicate UsernameToken : uncomment this
            //_tokenList.add(token);
        }
        //Workaround - comment
        _tokenList.add(token);
    }

    @Override
    public Iterator getTokens() {
        populate();
        if ( _tokenList != null ) {
            return _tokenList.iterator();
        }
        return Collections.emptyList().iterator();
    }

    private synchronized void populate(){

        if(!populated){
            NestedPolicy policy = this.getNestedPolicy();
            if(policy == null){
                if(Constants.logger.getLevel() == Level.FINE){
                    Constants.logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return;
            }
            AssertionSet as = policy.getAssertionSet();
            Iterator<PolicyAssertion> ast = as.iterator();
            while(ast.hasNext()){
                PolicyAssertion assertion = ast.next();
                if(PolicyUtil.isAlgorithmAssertion(assertion, spVersion)){
                    this.algSuite = (AlgorithmSuite) assertion;
                    String sigAlgo = assertion.getAttributeValue(new QName("signatureAlgorithm"));
                    this.algSuite.setSignatureAlgorithm(sigAlgo);
                }else if(PolicyUtil.isToken(assertion, spVersion)){
                    addToken((Token)assertion);
                    //this._tokenList.add((Token)assertion);
                }else if(PolicyUtil.isSignedParts(assertion, spVersion)){
                    spList.add((SignedParts) assertion);
                }else if(PolicyUtil.isSignedElements(assertion, spVersion)){
                    seList.add((SignedElements)assertion);
                }else if(PolicyUtil.isEncryptParts(assertion, spVersion)){
                    epList.add((EncryptedParts)assertion);
                }else if(PolicyUtil.isEncryptedElements(assertion, spVersion)){
                    eeList.add((EncryptedElements)assertion);
                }else{
                    if(!assertion.isOptional()){
                        if(Constants.logger.getLevel() == Level.SEVERE){
                            Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0100_INVALID_SECURITY_ASSERTION(assertion, "SecurityContextToken"));
                        }
                        if(isServer){
                            throw new UnsupportedPolicyAssertion("Policy assertion "+
                                    assertion+" is not supported under SupportingTokens assertion");
                        }
                    }
                }
            }
            Iterator<PolicyAssertion> parameterAssertion = this.getParametersIterator();
            while(parameterAssertion.hasNext()){
                PolicyAssertion assertion = parameterAssertion.next();
                if(PolicyUtil.isSignedParts(assertion, spVersion)){
                    spList.add((SignedParts) assertion);
                }else if(PolicyUtil.isSignedElements(assertion, spVersion)){
                    seList.add((SignedElements)assertion);
                }else if(PolicyUtil.isEncryptParts(assertion, spVersion)){
                    epList.add((EncryptedParts)assertion);
                }else if(PolicyUtil.isEncryptedElements(assertion, spVersion)){
                    eeList.add((EncryptedElements)assertion);
                }else{
                    if(!assertion.isOptional()){
                        if(Constants.logger.getLevel() == Level.SEVERE){
                            Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0100_INVALID_SECURITY_ASSERTION(assertion, "SecurityContextToken"));
                        }
                        if(isServer){
                            throw new UnsupportedPolicyAssertion("Policy assertion "+
                                    assertion+" is not supported under SupportingTokens assertion");
                        }
                    }
                }
            }
            populated = true;
        }
    }

    @Override
    public String getIncludeToken() {
        return "";
    }

    public void setIncludeToken(String type) {
    }

    @Override
    public String getTokenId() {
        return "";
    }

    @Override
    public Iterator<com.sun.xml.ws.security.policy.SignedParts> getSignedParts() {
        populate();
        return spList.iterator();
    }

    @Override
    public Iterator<com.sun.xml.ws.security.policy.SignedElements> getSignedElements() {
        populate();
        return seList.iterator();
    }

    @Override
    public Iterator<com.sun.xml.ws.security.policy.EncryptedParts> getEncryptedParts() {
        populate();
        return epList.iterator();
    }

    @Override
    public Iterator<com.sun.xml.ws.security.policy.EncryptedElements> getEncryptedElements() {
        populate();
        return eeList.iterator();
    }

    @Override
    public SecurityPolicyVersion getSecurityPolicyVersion() {
        return spVersion;
    }

}
