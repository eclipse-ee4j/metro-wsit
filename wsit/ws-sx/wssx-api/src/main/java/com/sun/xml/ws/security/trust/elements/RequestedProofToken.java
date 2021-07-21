/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: RequestedProofToken.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;
import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import java.net.URI;

/**
 * @author WS-Trust Implementation Team.
 */
public interface RequestedProofToken {
    
    /** constants indicating type of Proof Token 
     * @see getProofTokenType 
     */
    public static final String COMPUTED_KEY_TYPE = "ComputedKey";
    public static final String TOKEN_REF_TYPE = "SecurityTokenReference";
    public static final String ENCRYPTED_KEY_TYPE = "EncryptedKey";
    public static final String BINARY_SECRET_TYPE = "BinarySecret";
    public static final String CUSTOM_TYPE = "Custom";
    
    /**
     * Get the type of ProofToken present in this RequestedProofToken Instance
     */
    String getProofTokenType();

   /**
     * Set the type of ProofToken present in this RequestedProofToken Instance
     * @see getProofTokenType
     */
    void setProofTokenType(String proofTokenType);

    /**
     * Gets the value of the any property.
     * 
     * @return
     *     possible object is
     *     {@link org.w3c.dom.Element }
     *     {@link Object }
     *     
     */
    Object getAny();

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.w3c.dom.Element }
     *     {@link Object }
     *     
     */
    void setAny(Object value);
    
    /**
     * Set a SecurityTokenReference as the Proof Token 
     */
    void setSecurityTokenReference(SecurityTokenReference reference);
    
    /**
     * Gets the SecrityTokenReference if set 
     * @return SecurityTokenReference if set, null otherwise
     */
    SecurityTokenReference getSecurityTokenReference();
    
    /**
     *Sets the Computed Key URI (describing how to compute the Key)
     */
    void setComputedKey(URI computedKey);
    
    /**
     *Get the Computed Key URI (describing how to compute the Key)
     *@return computed key URI or null if none is set
     */
    URI getComputedKey();
    
    /**
     * Sets a wst:BinarySecret as the Proof Token
     */
     void setBinarySecret(BinarySecret secret);
     
     /**
      * Gets the BinarySecret proof Token if set
      * @return BinarySecret if set, null otherwise
      */
     BinarySecret getBinarySecret();
}
