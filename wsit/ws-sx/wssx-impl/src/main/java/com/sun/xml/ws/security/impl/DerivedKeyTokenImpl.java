/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * DerivedKeyTokenImpl.java
 *
 * Created on December 23, 2005, 7:11 PM
 */

package com.sun.xml.ws.security.impl;

import com.sun.xml.ws.security.DerivedKeyToken;
import com.sun.xml.wss.impl.misc.SecurityUtil;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Ashutosh Shahi
 */
public class DerivedKeyTokenImpl implements DerivedKeyToken {
    
    private long length = 32; // Default length 
    private long offset = 0; // Default offset
    private long generation = 0;
    private String label = this.DEFAULT_DERIVEDKEYTOKEN_LABEL;
    private byte[] secret, nonce;
    
    /** Creates a new instance of DerivedKeyTokenImpl */
    public DerivedKeyTokenImpl(long offset, long length, byte[] secret){
        this.offset = offset;
        this.length = length;
        this.secret = secret;
        try {
            nonce = new byte[18];
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.nextBytes(nonce);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                    "No such algorithm found" + e.getMessage());
        }
    }

    public DerivedKeyTokenImpl(long offset, long length, byte[] secret, byte[] nonce){
        this.offset = offset;
        this.length = length;
        this.secret = secret;
        this.nonce = nonce;
    }
    
    public DerivedKeyTokenImpl(long offset, long length, byte[] secret, byte[] nonce, String label){
        this.offset = offset;
        this.length = length;
        this.secret = secret;
        this.nonce = nonce;
        if(label != null){
            this.label = label;
        }
    }
    
    public DerivedKeyTokenImpl(long generation, byte[] secret){
        this.generation = generation;
        this.secret = secret;
        try {
            nonce = new byte[18];
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.nextBytes(nonce);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                    "No such algorithm found" + e.getMessage());
        }
    }
    
    public URI getAlgorithm() {
        try {
            return new URI(this.DEFAULT_DERIVED_KEY_TOKEN_ALGORITHM);
        } catch (URISyntaxException ex) {
            //ignore
        }
        return null;
    }
    
    public long getLength() {
        return length;
    }
    
    public long getOffset() {
        return offset;
    }
    
    public String getType() {
        return this.DERIVED_KEY_TOKEN_TYPE;
    }
    
    public Object getTokenValue() {
        //TODO: implement this method
        return null;
    }
    
    public long getGeneration() {
        return generation;
    }
    
    public String getLabel(){
        return label;
    }
    
    public byte[] getNonce() {
        return nonce;
    }
    
    
    public SecretKey generateSymmetricKey(String algorithm) 
        throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        
           byte[] temp = label.getBytes("UTF-8");
           byte[] seed = new byte[temp.length + nonce.length];
           System.arraycopy(temp, 0, seed, 0, temp.length);
           System.arraycopy(nonce, 0, seed, temp.length, nonce.length);
           
           byte[] tempBytes = SecurityUtil.P_SHA1(secret, seed, (int)(offset + length));
           byte[] key = new byte[(int)length];
           
           for(int i = 0; i < key.length; i++)
               key[i] = tempBytes[i+(int)offset];
           
           SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
           return (SecretKey)keySpec;
       
    }

}
