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
 * $Id: WSTrustElementBase.java,v 1.2 2010-10-21 15:35:41 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import java.net.URI;

import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.impl.bindings.PolicyReference;

import com.sun.xml.ws.api.security.trust.WSTrustException;

/**
 * @author WS-Trust Implementation Team.
 */
public interface WSTrustElementBase {

  /**
    * Get the type of security token, specified as a URI.
    * @return {@link URI}
    */
    URI getTokenType();

   /**
     * Set the type of security token, specified as a URI.
     * @param tokenType {@link URI}
     */
    void setTokenType(URI tokenType);

   /**
     * Get the desired LifeTime settings for the token if specified, null otherwise
     */
    Lifetime getLifetime();

   /**
     * Set the desired lifetime settings for the requested token
     */
    void setLifetime(Lifetime lifetime);

    /**
     * Get the entropy for the requested token
     * @return {@link Entropy}
     */
    Entropy getEntropy();

    /**
     * Set the entropy for the requested token
     * @param entropy {@link Entropy}
     */
    void setEntropy(Entropy entropy);

    /**
      * Set the desired policy settings for the requested token
      * @param appliesTo {@link AppliesTo}
      */
      void setAppliesTo(AppliesTo appliesTo);

     /**
      * Get the desired AppliesTo policy settings for the token if specified, null otherwise
      * @return {@link AppliesTo}
      */
      AppliesTo getAppliesTo();

    /**
      * Set the value of OnBehalfOf for the requested token
      * @param onBehalfOf {@link OnBehalfOf}
      */
      void setOnBehalfOf(OnBehalfOf onBehalfOf);

    /**
      * Get the value of OnBehalfOf for the token if specified, null otherwise
      * @return {@link OnBehalfOf}
      */
      OnBehalfOf getOnBehalfOf();

    /**
     * set Issuer of the SecurityToken Presented in the message
     */
    void setIssuer(Issuer issuer);

    /**
     * get Issuer of the SecurityToken Presented in the Message, null otherwise
     */
    Issuer getIssuer();

    /**
      * set a {@code<wst:Renewing/>} element to make a renewable Issue request
      */
    void setRenewable(Renewing renew);

   /**
     * get the {@code<wst:Renewing/>} element if present, null otherwise
     */
    Renewing getRenewable();


    /**
     * Set a SignChallenge
     */
    void setSignChallenge(SignChallenge challenge);

    /**
     * get SignChallenge element if any, null otherwise
     */
    SignChallenge getSignChallenge();

    /**
     * set a BinaryExchange
     */
    void setBinaryExchange(BinaryExchange challenge);

    /**
     * get BinaryExchange element if any, null otherwise
     */
    BinaryExchange getBinaryExchange();


     /**
     * set AuthenticationType
     */
    void setAuthenticationType(URI uri);

    /**
     * get Authentication Type parameter if set, null otherwise
     */
    URI getAuthenticationType();

    /**
     * set KeyType parameter
     */
    void setKeyType(URI keytype) throws WSTrustException;

    /**
     * get KeyType Parameter if set, null otherwise
     */
    URI getKeyType();

    /**
     * set the KeySize parameter
     */
    void setKeySize(long size);

    /**
     * get the KeySize parameter if specified, 0 otherwise
     */
    long getKeySize();

    /**
     * set SignatureAlgorithm
     */
    void setSignatureAlgorithm(URI algorithm);

    /**
     * get SignatureAlgorithm value if set, return default otherwise
     */
    URI getSignatureAlgorithm();

    /**
     * set EncryptionAlgorithm
     */
    void setEncryptionAlgorithm(URI algorithm);

    /**
     * get EncryptionAlgorithm value if set, return default otherwise
     */
    URI getEncryptionAlgorithm();

    /**
     * set CanonicalizationAlgorithm
     */
    void setCanonicalizationAlgorithm(URI algorithm);

    /**
     * get CanonicalizationAlgorithm value if set, return default otherwise
     */
    URI getCanonicalizationAlgorithm();

    /**
     * Set the desired useKey settings for the requested token
     */
    void setUseKey(UseKey useKey);

    /**
     * Get the desired useKey settings for the token if specified, null otherwise
     */
    UseKey getUseKey();

    /**
      * Set the desired proofEncryption settings for the requested token
      */
     void setProofEncryption(ProofEncryption proofEncryption);

     /**
      * Get the desired proofEncryption settings for the token if specified, null otherwise
      */
     ProofEncryption getProofEncryption();

    /**
     * set ComputedKeyAlgorithm
     */
    void setComputedKeyAlgorithm(URI algorithm);

    /**
     * get CanonicalizationAlgorithm value if set, return default otherwise
     */
    URI getComputedKeyAlgorithm();

    /**
     * set Encryption
     */
    void setEncryption(Encryption enc);

    /**
     * get Encryption value if set, return null otherwise
     */
    Encryption getEncryption();

    /**
     * Set the Signature Algorithm to be used with the issued token
     */
    void setSignWith(URI algorithm);

    /**
     * Get the Signature Algorithm to be used with the token if set, null otherwise
     */
    URI getSignWith();

    /**
     * Set the Encryption Algorithm to be used with the issued token
     */
    void setEncryptWith(URI algorithm);

    /**
     * Get the Encryption Algorithm to be used with the token if set, null otherwise
     */
    URI getEncryptWith();

    /**
     * set KeyWrapAlgorithm
     */
    void setKeyWrapAlgorithm(URI algorithm);

    /**
     * get KeyWrapAlgorithm value if set, return default otherwise
     */
    URI getKeyWrapAlgorithm();

    /**
     * set the Delegate to which the issued token be delegated
     */
    void setDelegateTo(DelegateTo to);

    /**
     * get the DelegateTo value if set, null otherwise
     */
    DelegateTo getDelegateTo();

    /**
     * Set if the requested token be forwardable
     */
    void setForwardable(boolean flag);

    /**
     * Get the value of the Forwardable flag
     * NOTE: default value of this flag is true
     */
     boolean getForwardable();

    /**
     * Set if the requested token be delegatable
     */
    void setDelegatable(boolean flag);

    /**
     * Get the value of the Delegatable flag
     * NOTE: default value of this flag is false
     */
     boolean getDelegatable();

    /**
     * Set the desired policy settings for the requested token
     */
    void setPolicy(Policy policy);

    /**
     * Get the desired policy settings for the token if specified, null otherwise
     */
    Policy getPolicy();

   /**
    * Set the desired policyReference settings for the requested token
    */
   void setPolicyReference(PolicyReference policyRef);

   /**
    * Get the desired policyReference settings for the token if specified, null otherwise
    */
   PolicyReference getPolicyReference();

    /**
      * Get the AllowPostdating element for the token if specified, null otherwise
      * NOTE: Although this is an issue with the WSTrust spec, leaving it here for now.
      * This can be removed or modified later depending on the outcome of the issue.
      */
    AllowPostdating getAllowPostdating();

    /**
      * Set the desired policyReference settings for the requested token
      */
    void setAllowPostdating(AllowPostdating allowPostDating);

}
