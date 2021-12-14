/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policyconv;

import com.sun.xml.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.security.policy.AlgorithmSuite;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Cache XWSS Policy i,e MessagePolicy for each message and cache all the
 * Issued and SecureConversation Tokens for quick lookup.
 *
 * @author K.Venugopal@sun.com
 */
public class SecurityPolicyHolder {
    
    private MessagePolicy mp = null;
    private List<PolicyAssertion> scList = null;
    private List<PolicyAssertion> issuedTokenList = null;
    private List<PolicyAssertion> kerberosTokenList = null;
    private static final List<PolicyAssertion> EMPTY_LIST = Collections.emptyList();
    private AlgorithmSuite suite  = null;
    private HashMap<WSDLFault,SecurityPolicyHolder> faultFPMap = null;
    private HashMap<String,Set<PolicyAssertion>> configAssertions;
    private boolean isIssuedTokenAsEncryptedSupportingToken = false;
    
    /**
     * Creates a new instance of SecurityPolicyHolder
     */
    public SecurityPolicyHolder() {
    }
    
    public void setMessagePolicy(MessagePolicy mp){
        this.mp= mp;
    }
    
    public MessagePolicy getMessagePolicy(){
        return this.mp;
    }
    
    public void  addSecureConversationToken(PolicyAssertion pa){
        if(scList == null){
            scList = new ArrayList<>();
        }
        scList.add(pa);
    }
    
    public List<PolicyAssertion> getSecureConversationTokens(){
        return ((scList==null)?EMPTY_LIST:scList);
    }
    
    public void  addKerberosToken(PolicyAssertion pa){
        if(kerberosTokenList == null){
            kerberosTokenList = new ArrayList<>();
        }
        kerberosTokenList.add(pa);
    }
    
    public List<PolicyAssertion> getKerberosTokens(){
        return ((kerberosTokenList==null)?EMPTY_LIST:kerberosTokenList);
    }
    
    public void addIssuedToken(PolicyAssertion pa){
        if(issuedTokenList == null){
            issuedTokenList = new ArrayList<>();
        }
        issuedTokenList.add(pa);
    }
    
    public void addIssuedTokens(List<PolicyAssertion> list ){
        if(issuedTokenList == null){
            issuedTokenList =  list;
        }else{
            issuedTokenList.addAll(list);
        }
    }
    
    public List<PolicyAssertion> getIssuedTokens(){
        return ((issuedTokenList==null)?EMPTY_LIST:issuedTokenList);
    }
    
    public AlgorithmSuite getBindingLevelAlgSuite(){
        return suite;
    }
    
    public void setBindingLevelAlgSuite(AlgorithmSuite suite){
        this.suite = suite;
    }
    
    public boolean isIssuedTokenAsEncryptedSupportingToken(){
        return this.isIssuedTokenAsEncryptedSupportingToken;
    }
    
    public void isIssuedTokenAsEncryptedSupportingToken(boolean isIssuedTokenAsEncryptedSupportingToken){
        this.isIssuedTokenAsEncryptedSupportingToken = isIssuedTokenAsEncryptedSupportingToken;
    }
    
    public void addFaultPolicy(WSDLFault fault , SecurityPolicyHolder policy){
        if(faultFPMap == null){
            faultFPMap = new HashMap<>();
        }
        faultFPMap.put(fault,policy);
    }
    
    public SecurityPolicyHolder getFaultPolicy(WSDLFault fault){
        if(faultFPMap == null){
            return null;
        }
        return faultFPMap.get(fault);
    }
    
    public void addConfigAssertions(PolicyAssertion assertion){
        if(configAssertions == null){
            configAssertions = new HashMap<>();
        }
        Set<PolicyAssertion> assertions = configAssertions.get(assertion.getName().getNamespaceURI());
        if(assertions == null){
            assertions = new HashSet<>();
            configAssertions.put(assertion.getName().getNamespaceURI(),assertions);
        }
        assertions.add(assertion);
    }
    
    public Set<PolicyAssertion> getConfigAssertions(String namespaceuri){
        if(configAssertions == null){
            return null;
        }
        return configAssertions.get(namespaceuri);        
    }
}
