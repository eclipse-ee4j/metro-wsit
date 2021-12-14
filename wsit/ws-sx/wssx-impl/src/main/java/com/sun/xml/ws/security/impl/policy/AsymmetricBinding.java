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
import com.sun.xml.ws.security.policy.AlgorithmSuite;
import com.sun.xml.ws.security.policy.MessageLayout;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import static com.sun.xml.ws.security.impl.policy.Constants.*;


/**
 * 
 * @author K.Venugopal@sun.com 
 */

public class AsymmetricBinding extends com.sun.xml.ws.policy.PolicyAssertion implements com.sun.xml.ws.security.policy.AsymmetricBinding, SecurityAssertionValidator {   
    
    private AssertionFitness fitness = AssertionFitness.IS_VALID;   
    private Token initiatorToken;   
    private Token recipientToken;
    private Token initiatorSignatureToken;
    private Token recipientSignatureToken;
    private Token initiatorEncryptionToken;
    private Token recipientEncryptionToken;
    private AlgorithmSuite algSuite; 
    private boolean includeTimestamp = false;  
    private boolean disableTimestampSigning = false;
    private boolean contentOnly = true;   
    private  MessageLayout layout = MessageLayout.Lax; 
    private String protectionOrder = SIGN_ENCRYPT;   
    private boolean protectToken = false;  
    private boolean protectSignature = false;   
    private boolean populated = false;          
    private SecurityPolicyVersion spVersion;
    
    /**  
     * Creates a new instance of AsymmetricBinding     
     */ 
    public AsymmetricBinding() {
        spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    }    
    public AsymmetricBinding(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {      
        super(name,nestedAssertions,nestedAlternative);
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
    }    
    
    @Override
    public Token getRecipientToken() {
        populate();    
        return recipientToken; 
    }    
    
    @Override
    public Token getInitiatorToken() {
        populate();       
        return initiatorToken;   
    }

    @Override
    public Token getRecipientSignatureToken() {
        populate();
        return recipientSignatureToken;
    }

    @Override
    public Token getInitiatorSignatureToken() {
        populate();
        return initiatorSignatureToken;
    }
    @Override
    public Token getRecipientEncryptionToken() {
        populate();
        return recipientEncryptionToken;
    }

    @Override
    public Token getInitiatorEncryptionToken() {
        populate();
        return initiatorEncryptionToken;
    }
    
    public void setAlgorithmSuite(AlgorithmSuite algSuite) {   
        this.algSuite = algSuite;   
    }      
    
    @Override
    public AlgorithmSuite getAlgorithmSuite() {
        populate();      
        if(algSuite == null){    
            algSuite = new  com.sun.xml.ws.security.impl.policy.AlgorithmSuite();     
            logger.log(Level.FINE, "Using Default Algorithm Suite Basic128");    
        }       
        return algSuite;  
    }  
    
    public void includeTimeStamp(boolean value) {  
        populate();     
        this.includeTimestamp = value;   
    }    
    
    @Override
    public boolean isIncludeTimeStamp() {
        populate();      
        return includeTimestamp;   
    }  
    
    @Override
    public boolean isDisableTimestampSigning() {
        populate();
        return disableTimestampSigning;
    }
    
    public void setLayout(MessageLayout layout) {
        this.layout = layout;    
    }   
    
    @Override
    public MessageLayout getLayout() {
        populate();   
        return layout; 
    }      
    
    public void setInitiatorToken(Token token) {       
        this.initiatorToken = token; 
    } 
    
    public void setRecipientToken(Token token) {   
        this.recipientToken = token;   
    }

    public void setInitiatorSignatureToken(Token token) {
        this.initiatorSignatureToken = token;
    }

    public void setRecipientSignatureToken(Token token) {
        this.recipientSignatureToken = token;
    }

     public void setInitiatorEncryptionToken(Token token) {
        this.initiatorEncryptionToken = token;
    }

    public void setRecipientEncryptionToken(Token token) {
        this.recipientEncryptionToken = token;
    }
    
    @Override
    public boolean isSignContent() {
        populate();      
        return contentOnly;    
    }     
    
    public void setSignContent(boolean contentOnly) {  
        this.contentOnly = contentOnly;  
    }      
    
    public void setProtectionOrder(String order) {      
        this.protectionOrder = order;  
    }     
    
    @Override
    public String getProtectionOrder() {
        populate();    
        return protectionOrder;   
    }        
    
    public void setTokenProtection(boolean value) {    
        this.protectToken = value;   
    }  
    
    public void setSignatureProtection(boolean value) {
        this.protectSignature = value; 
    
    }   
    
    @Override
    public boolean getTokenProtection() {
        populate();       
        return protectToken;    
    }   
    
    @Override
    public boolean getSignatureProtection() {
        populate();       
        return protectSignature;  
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
            if(policy == null){           
                if(logger.isLoggable(Level.FINE)){       
                    logger.log(Level.FINE,"NestedPolicy is null");    
                }              
                populated = true;        
                return fitness;      
            }          
            AssertionSet as = policy.getAssertionSet();     
            Iterator<PolicyAssertion> ast = as.iterator();     
            while(ast.hasNext()){           
                PolicyAssertion assertion = ast.next(); 
                if(PolicyUtil.isInitiatorToken(assertion, spVersion)){    
                    this.initiatorToken = ((com.sun.xml.ws.security.impl.policy.Token)assertion).getToken();  
                }else if(PolicyUtil.isRecipientToken(assertion, spVersion)){     
                    this.recipientToken = ((com.sun.xml.ws.security.impl.policy.Token)assertion).getToken(); 
                }else if(PolicyUtil.isRecipientSignatureToken(assertion, spVersion)){
                    this.recipientSignatureToken = ((com.sun.xml.ws.security.impl.policy.Token)assertion).getToken();
                }else if(PolicyUtil.isRecipientEncryptionToken(assertion, spVersion)){
                    this.recipientEncryptionToken = ((com.sun.xml.ws.security.impl.policy.Token)assertion).getToken();
                }else if(PolicyUtil.isInitiatorSignatureToken(assertion, spVersion)){
                    this.initiatorSignatureToken = ((com.sun.xml.ws.security.impl.policy.Token)assertion).getToken();
                }else if(PolicyUtil.isInitiatorEncryptionToken(assertion, spVersion)){
                    this.initiatorEncryptionToken = ((com.sun.xml.ws.security.impl.policy.Token)assertion).getToken();
                }else if(PolicyUtil.isAlgorithmAssertion(assertion, spVersion)){
                    this.algSuite = (AlgorithmSuite) assertion;
                    String sigAlgo = assertion.getAttributeValue(new QName("signatureAlgorithm"));
                    this.algSuite.setSignatureAlgorithm(sigAlgo);
                }else if(PolicyUtil.isIncludeTimestamp(assertion, spVersion)){            
                    this.includeTimestamp = true;        
                }else if(PolicyUtil.isEncryptBeforeSign(assertion, spVersion)){    
                    this.protectionOrder = ENCRYPT_SIGN;             
                }else if (PolicyUtil.isSignBeforeEncrypt(assertion, spVersion)){
                    this.protectionOrder = SIGN_ENCRYPT;
                }else if(PolicyUtil.isContentOnlyAssertion(assertion, spVersion)){                      
                    this.contentOnly = false;          
                }else if(PolicyUtil.isMessageLayout(assertion, spVersion)){      
                    layout = ((Layout)assertion).getMessageLayout();   
                }else if(PolicyUtil.isProtectTokens(assertion, spVersion)){       
                    this.protectToken = true;         
                }else if(PolicyUtil.isEncryptSignature(assertion, spVersion)){    
                    this.protectSignature = true;        
                } else if(PolicyUtil.disableTimestampSigning(assertion)){
                    this.disableTimestampSigning = true;
                }else{      
                    if(!assertion.isOptional()){  
                        log_invalid_assertion(assertion, isServer,AsymmetricBinding); 
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
