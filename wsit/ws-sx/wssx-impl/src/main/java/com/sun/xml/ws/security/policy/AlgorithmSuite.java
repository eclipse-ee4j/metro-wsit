/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;

import java.util.Set;

/**
 * Represents the AlgorithmSuite assertion.
 * <p>
 * Syntax :
 *
 * <pre>{@code
 *  <xmp>
 *  <sp:AlgorithmSuite ... >
 *      <wsp:Policy>
 *          (
 *          <sp:Basic256 ... /> |
 *          <sp:Basic192 ... /> |
 *          <sp:Basic128 ... /> |
 *          <sp:TripleDes ... /> |
 *          <sp:Basic256Rsa15 ... /> |
 *          <sp:Basic192Rsa15 ... /> |
 *          <sp:Basic128Rsa15 ... /> |
 *          <sp:TripleDesRsa15 ... /> |
 *          <sp:Basic256Sha256 ... /> |
 *          <sp:Basic192Sha256 ... /> |
 *          <sp:Basic128Sha256 ... /> |
 *          <sp:TripleDesSha256 ... /> |
 *          <sp:Basic256Sha256Rsa15 ... /> |
 *          <sp:Basic192Sha256Rsa15 ... /> |
 *          <sp:Basic128Sha256Rsa15 ... /> |
 *          <sp:TripleDesSha256Rsa15 ... /> |
 *
 *           ...)
 *          <sp:InclusiveC14N ... /> ?
 *          <sp:SOAPNormalization10 ... /> ?
 *          <sp:STRTransform10 ... /> ?
 *          <sp:XPath10 ... /> ?
 *          <sp:XPathFilter20 ... /> ?
 *          ...
 *      </wsp:Policy>
 *    ...
 *   </sp:AlgorithmSuite>
 * </xmp>
 *}</pre>
 *
 * @author K.Venugopal@sun.com
 */
public interface AlgorithmSuite {
    
    String INCLUSIVE14N="InclusiveC14N";
    String SOAP_NORMALIZATION10="SOAPNormalization10";
    String STR_TRANSFORM10="STRTransform10";
    String XPATH10="XPath10";
    String XPATH_FILTER20="XPathFilter20";
    int MAX_SKL = 256;
    int MAX_AKL = 4096;
    int MIN_AKL = 1024;
    /**
     * returns the Algorithm suite to be used.
     * @return {@link AlgorithmSuiteValue}
     */
    AlgorithmSuiteValue getType();
    
    /**
     * Property set containing INCLUSIVE14N,SOAP_NORMALIZATION10,STR_TRANSFORM10,XPATH10,XPATH_FILTER20
     * @return list identifying the properties
     */
    Set getAdditionalProps();
    
    
    /**
     * Gets the Digest algorithm identified by this AlgorithmSuite.
     * @return String
     */
    String getDigestAlgorithm();
    
    /**
     * Gets the Encryption algorithm
     */
    String getEncryptionAlgorithm();
    
    /**
     * Gets the Symmetric key signature algorithm
     */
    String getSymmetricKeySignatureAlgorithm();
    
    /**
     * Gets the Asymmetric key signature algorithm
     */
    String getAsymmetricKeySignatureAlgorithm();
    
    /**
     * Gets the Symmetric Key algorithm
     */
    String getSymmetricKeyAlgorithm();
    
    /**
     * Get the Assymetric key algorithm
     */
    String getAsymmetricKeyAlgorithm();
    
    /**
     * Gets the Signature key derivation algorithm
     */
    String getSignatureKDAlogrithm();
    
    /**
     * Gets the Encryprion key derivation algorithm
     */
    String getEncryptionKDAlogrithm();
    
    
    /**
     * Gets minimum key length  for symmetric key algorithm.
     */
    int getMinSKLAlgorithm();
    
    /*
     * Gets the computed key algorithm
     */
    String getComputedKeyAlgorithm();
    /*
     *Gets the Maximum symmetric key length
     */
    int getMaxSymmetricKeyLength();
    /*
     *Gets the minimum Asymmetric key length
     */
    int getMinAsymmetricKeyLength();
    /*
     *Gets the maximum Asymmetric key length
     */
    int getMaxAsymmetricKeyLength();

    void setSignatureAlgorithm(String sigAlgo);

    String getSignatureAlgorithm();
}
