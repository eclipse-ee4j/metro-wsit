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
 * 
 * 
 * @author K.Venugopal@sun.com
 *  
 */

public class SymmetricBinding extends PolicyAssertion implements com.sun.xml.ws.security.policy.SymmetricBinding, SecurityAssertionValidator{    
    
    private AssertionFitness fitness = AssertionFitness.IS_VALID;   
    boolean populated = false;     
    Token protectionToken ;
    Token signatureToken ;   
    Token encryptionToken ; 
    MessageLayout layout = MessageLayout.Lax;   
    AlgorithmSuite algSuite;      
    boolean includeTimestamp=false;   
    boolean disableTimestampSigning=false;
    boolean contentOnly = true;     
    String protectionOrder = SIGN_ENCRYPT;     
    boolean protectToken = false;      
    boolean protectSignature = false;
    private SecurityPolicyVersion spVersion;
    
    /** 
     * 
     * Creates a new instance of SymmetricBinding
     *     
     */     
    
    public SymmetricBinding() {  
        spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    }     
    
    public SymmetricBinding(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {            
        
        super(name,nestedAssertions,nestedAlternative); 
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
    }            
    
    public Token getEncryptionToken() {    
        populate();      
        return encryptionToken;    
    }          
    
    public Token getSignatureToken() {  
        populate();       
        return signatureToken;      
    }          
    
    public Token getProtectionToken() { 
        populate();  
        return protectionToken; 
    }          
    
    public void setAlgorithmSuite(AlgorithmSuite algSuite) {    
        this.algSuite = algSuite;         
    }      
    
    public AlgorithmSuite getAlgorithmSuite() { 
        populate();      
        if(algSuite == null){         
            algSuite = new  com.sun.xml.ws.security.impl.policy.AlgorithmSuite();          
            logger.log(Level.FINE, "Using Default Algorithm Suite Basic128");    
        
        }    
        return algSuite;    
    }           
    
    public void includeTimeStamp(boolean value) {   
        includeTimestamp = value;            
    }                
    
    public boolean isIncludeTimeStamp() {      
        populate();           
        return includeTimestamp;      
    }   
    
    public boolean isDisableTimestampSigning(){
        populate();
        return disableTimestampSigning;
    }
    
    public void setLayout(MessageLayout layout) {    
        this.layout = layout;          
    }            
    
    public MessageLayout getLayout() {
        populate();         
        return layout;         
    }              
    
    public void setEncryptionToken(Token token) {  
        encryptionToken = token ;          
    }              
    
    public void setSignatureToken(Token token) {  
        signatureToken = token;  
    }             
    
    public void setProtectionToken(Token token) {  
        protectionToken = token;    
    }               
    
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
    
    public boolean getTokenProtection() {    
        populate();         
        return protectToken;    
    }              
    
    public boolean getSignatureProtection() {    
        populate();         
        return protectSignature;       
    }             
    
    private void populate(){       
        populate(false);      
    }             
    
    private synchronized AssertionFitness populate(boolean isServer) {     
        if(!populated){                      
            NestedPolicy policy = this.getNestedPolicy();
            if(policy == null){                            
                if(logger.getLevel() == Level.FINE){        
                    logger.log(Level.FINE,"NestedPolicy is null");      
                }                           
                populated = true;        
                return fitness;          
            }               
            AssertionSet as = policy.getAssertionSet();    
            Iterator<PolicyAssertion> ast = as.iterator();       
            while(ast.hasNext()){                           
                PolicyAssertion assertion = ast.next();     
                if(PolicyUtil.isSignatureToken(assertion, spVersion)){  
                    this.signatureToken = ((com.sun.xml.ws.security.impl.policy.Token)assertion).getToken();      
                }else if(PolicyUtil.isEncryptionToken(assertion, spVersion)){ 
                    this.encryptionToken =((com.sun.xml.ws.security.impl.policy.Token)assertion).getToken();   
                }else if(PolicyUtil.isProtectionToken(assertion, spVersion)){                   
                    this.protectionToken = ((com.sun.xml.ws.security.impl.policy.Token)assertion).getToken(); 
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
                } else{                                    
                    if(!assertion.isOptional()){         
                        log_invalid_assertion(assertion, isServer,SymmetricBinding);   
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;    
                    }                              
                }             
            }                   
            populated = true;     
        }             
        return fitness;
    }           
    
    public AssertionFitness validate(boolean isServer) {    
        return populate(isServer);       
    } 

    public SecurityPolicyVersion getSecurityPolicyVersion() {
        return spVersion;
    }
}
