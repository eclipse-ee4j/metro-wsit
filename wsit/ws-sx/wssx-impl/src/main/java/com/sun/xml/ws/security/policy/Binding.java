/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;


/**
 * Base Interface for Security Policy Binding assertions, identifies Algorithms that are supported,describes the layout of
 * the security header.
 * @author K.Venugopal@sun.com
 */
public interface Binding{
    
    public static final String ENCRYPT_SIGN = "EncryptBeforeSigning";
    public static final String SIGN_ENCRYPT = "SignBeforeEncrypting";
  
    /**
     * returns the {@link AlgorithmSuite} assertions defined in the policy.
     * @return {@link AlgorithmSuite}
     */
    public AlgorithmSuite getAlgorithmSuite();
  
    /**
     * returns true if TimeStamp property is enabled in this binding
     * @return true or false
     */
    public boolean isIncludeTimeStamp();
    
    
    public boolean isDisableTimestampSigning();
 
    /**
     * returns the Layout {@link MessageLayout }of  the SecurityHeader.
     * @return one of {@link MessageLayout }
     */
    public MessageLayout getLayout();
    /**
     * returns true if body and header content only has to be signed, false if entire body and header has to be signed.
     * @return true if body and header content only has to be signed, false if entire body and header has to be signed.
     */
    public boolean isSignContent();    
    
        
    /**
     * gets data protection order should be one one of Binding.SIGN_ENCRYPT or Binding.ENCRYPT_SIGN
     * @return one of Binding.SIGN_ENCRYPT or Binding.ENCRYPT_SIGN
     */
    public String getProtectionOrder();    
       
        
    /**
     * 
     * @return true if token has to be protected else false.
     */
    public boolean getTokenProtection();
    
    /**
     *
     * @return true if signature has to be encrypted else false.
     */
    public boolean getSignatureProtection();
    
    /**
     *
     * @return the version of Security Policy
     */
    public SecurityPolicyVersion getSecurityPolicyVersion();
}
