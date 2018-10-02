/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.impl.client;

import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.api.security.trust.client.SecondaryIssuedTokenParameters;

/**
 *
 * @author Jiandong Guo
 */
public class SecondaryIssuedTokenParametersImpl implements SecondaryIssuedTokenParameters{

    private String tokenType = null;
    
    private String keyType = null;
    
    private long keySize = -1;
    
    private String signatureAlg = null;
    
    private String encAlg = null;
    
    private String canAlg = null;
    
    private String keyWrapAlg = null;
    
    private String signWith = null;
    
    private String encryptWith = null;
    
    private Claims claims = null;
    
    public void setTokenType(String tokenType){
        this.tokenType = tokenType;
    }
    
    public void setKeyType(String keyType){
        this.keyType = keyType;
    }
    
    public void setKeySize(long keySize){
        this.keySize = keySize;
    }
    
    public void setSignWith(String signWithAlg){
        this.signWith = signWithAlg;
    }
    
    public void setEncryptWith(String encWithAlg){
        this.encryptWith = encWithAlg;
    }
    
    public void setSignatureAlgorithm(String sigAlg){
        this.signatureAlg = sigAlg;
    }
    
    public void setEncryptionAlgorithm(String encAlg){
        this.encAlg = encAlg;
    }
    
    public void setCanonicalizationAlgorithm(String canAlg){
        this.canAlg = canAlg;
    }
    
    public void setKeyWrapAlgorithm(String keyWrapAlg){
        this.keyWrapAlg = keyWrapAlg;
    }
    
    public void setClaims(Claims claims){
        this.claims = claims;
    }

    public String getTokenType(){
        return this.tokenType;
    }
    
    public String getKeyType(){
        return this.keyType;
    }
    
    public long getKeySize(){
        return this.keySize;
    }
    
    public String getSignatureAlgorithm(){
        return this.signatureAlg;
    }
    
    public String getEncryptionAlgorithm(){
        return this.encAlg;
    }
    
    public String getCanonicalizationAlgorithm(){
        return this.canAlg;
    }
    
    public String getKeyWrapAlgorithm(){
        return this.keyWrapAlg;
    }
    
    public String getSignWith(){
        return signWith;
    }
    
    public String getEncryptWith(){
        return encryptWith;
    }
    
    public Claims getClaims(){
        return this.claims;
    }
}
