/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * STSConfiguration.java
 *
 * Created on March 24, 2006, 1:19 PM
 *
 */

package com.sun.xml.ws.security.trust.impl;

import com.sun.xml.ws.api.security.trust.config.STSConfiguration;
import com.sun.xml.ws.api.security.trust.config.TrustSPMetadata;

import java.util.HashMap;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;

/**
 *
 * @author Jiandong Guo
 */
public class DefaultSTSConfiguration implements STSConfiguration{
    private Map<String, TrustSPMetadata> spMap = new HashMap<String, TrustSPMetadata>();
    private String type;
    private String issuer;
    private boolean encryptIssuedToken = false;
    private boolean encryptIssuedKey = true;
    private long issuedTokenTimeout;
    
    private CallbackHandler callbackHandler;
    
    private Map<String, Object> otherOptions = new HashMap<String, Object>();
    
    
    public void addTrustSPMetadata(final TrustSPMetadata data, final String spEndpoint){
        spMap.put(spEndpoint, data);
    }
    
    public TrustSPMetadata getTrustSPMetadata(final String spEndpoint){
        return (TrustSPMetadata)spMap.get(spEndpoint);
    }
    
    public void setType(String type){
        this.type = type;
    } 
    
    public String getType(){
        return this.type;
    }
    
    public void setIssuer(String issuer){
        this.issuer = issuer;
    }
        
    public String getIssuer(){
        return this.issuer;
    }
      
    public void setEncryptIssuedToken(boolean encryptIssuedToken){
        this.encryptIssuedToken = encryptIssuedToken;
    }
    
    public boolean getEncryptIssuedToken(){
        return this.encryptIssuedToken;
    }
        
    public void setEncryptIssuedKey(boolean encryptIssuedKey){
        this.encryptIssuedKey = encryptIssuedKey;
    }
    
    public boolean getEncryptIssuedKey(){
        return this.encryptIssuedKey;
    }
        
    public void setIssuedTokenTimeout(long issuedTokenTimeout){
        this.issuedTokenTimeout = issuedTokenTimeout;
    }
    
    public long getIssuedTokenTimeout(){
        return this.issuedTokenTimeout;
    }
    
    public void setCallbackHandler(CallbackHandler callbackHandler){
        this.callbackHandler = callbackHandler;
    }
    
    public CallbackHandler getCallbackHandler(){
        return this.callbackHandler;
    }
    
    public Map<String, Object> getOtherOptions(){
        return this.otherOptions;
    }
}
