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

import com.sun.xml.ws.security.policy.Lifetime;

/**
 * Contains information to be sent in message to the token issuer when requesting for IssuedTokens
 * @author K.Venugopal@sun.com
 */
public interface RequestSecurityTokenTemplate {
    
  
    public String getTrustVersion();
    
    /**
     * Get the type of security token, specified as a String.
     * @return {@link String}
     */
    String getTokenType();
    
  
    
    /**
     * Get the type of request, specified as a String.
     * The String indicates the class of function that is requested.
     * @return {@link String}
     */
    String getRequestType();
    
  
    
    /**
     * Get the desired LifeTime settings for the token if specified, null otherwise
     */
    Lifetime getLifetime();
    
   
    
    /**
     * Set the desired policy settings for the requested token
     * @param appliesTo {@link AppliesTo}
     */
//    void setAppliesTo(AppliesTo appliesTo);
    
    /**
     * Get the desired AppliesTo policy settings for the token if specified, null otherwise
     * @return {@link AppliesTo}
     */
//    AppliesTo getAppliesTo();
    
    
    /**
     * get Authentication Type parameter if set, null otherwise
     */
    String getAuthenticationType();
    
   
    /**
     * get KeyType Parameter if set, null otherwise
     */
    String getKeyType();
    
    
    /**
     * get the KeySize parameter if specified, 0 otherwise
     */
    int getKeySize();
    
   
    /**
     * get SignatureAlgorithm value if set, return default otherwise
     */
    String getSignatureAlgorithm();
    
        
    /**
     * get EncryptionAlgorithm value if set, return default otherwise
     */
    String getEncryptionAlgorithm();
    
   
    /**
     * get CanonicalizationAlgorithm value if set, return default otherwise
     */
    String getCanonicalizationAlgorithm();
    
   

  
    /**
     * Get the desired proofEncryption settings for the token if specified, false otherwise
     */
    boolean getProofEncryptionRequired();
    
  
    /**
     * get CanonicalizationAlgorithm value if set, return default otherwise
     */
    String getComputedKeyAlgorithm();
    
  
    
    /**
     * get Encryption value if set, return false otherwise
     */
    boolean getEncryptionRequired();
    
     
    /**
     * Get the Signature Algorithm to be used with the token if set, null otherwise
     */
    String getSignWith();
   
    
    /**
     * Get the Encryption Algorithm to be used with the token if set, null otherwise
     */
    String getEncryptWith();
    
    /**
     * Get the KeyWrap Algorithm used for key wrapping when STS encrypts the issued token 
     * for the relying party using an asymmetric key.     
     */
    String getKeyWrapAlgorithm();
      
    /**
     * 
     * @return Claims
     */ 
    Claims getClaims();
    
}

