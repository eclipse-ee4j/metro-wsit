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
    
    public static final String INCLUSIVE14N="InclusiveC14N";
    public static final String SOAP_NORMALIZATION10="SOAPNormalization10";
    public static final String STR_TRANSFORM10="STRTransform10";
    public static final String XPATH10="XPath10";
    public static final String XPATH_FILTER20="XPathFilter20";
    public static int MAX_SKL = 256;
    public static int MAX_AKL = 4096;
    public static int MIN_AKL = 1024;
    /**
     * returns the Algorithm suite to be used.
     * @return {@link AlgorithmSuiteValue}
     */
    public AlgorithmSuiteValue getType();
    
    /**
     * Property set containing INCLUSIVE14N,SOAP_NORMALIZATION10,STR_TRANSFORM10,XPATH10,XPATH_FILTER20
     * @return list identifying the properties
     */
    public Set getAdditionalProps();
    
    
    /**
     * Gets the Digest algorithm identified by this AlgorithmSuite.
     * @return String
     */
    public String getDigestAlgorithm();
    
    /**
     * Gets the Encryption algorithm
     * @return
     */
    public String getEncryptionAlgorithm();
    
    /**
     * Gets the Symmetric key signature algorithm
     * @return
     */
    public String getSymmetricKeySignatureAlgorithm();
    
    /**
     * Gets the Asymmetric key signature algorithm
     * @return
     */
    public String getAsymmetricKeySignatureAlgorithm();
    
    /**
     * Gets the Symmetric Key algorithm
     * @return
     */
    public String getSymmetricKeyAlgorithm();
    
    /**
     * Get the Assymetric key algorithm
     * @return
     */
    public String getAsymmetricKeyAlgorithm();
    
    /**
     * Gets the Signature key derivation algorithm
     * @return
     */
    public String getSignatureKDAlogrithm();
    
    /**
     * Gets the Encryprion key derivation algorithm
     * @return
     */
    public String getEncryptionKDAlogrithm();
    
    
    /**
     * Gets minimum key length  for symmetric key algorithm.
     * @return
     */
    public int getMinSKLAlgorithm();
    
    /*
     * Gets the computed key algorithm
     */
    public String getComputedKeyAlgorithm();
    /*
     *Gets the Maximum symmetric key length
     */
    public int getMaxSymmetricKeyLength();
    /*
     *Gets the minimum Asymmetric key length
     */
    public int getMinAsymmetricKeyLength();
    /*
     *Gets the maximum Asymmetric key length
     */
    public int getMaxAsymmetricKeyLength();

    public void setSignatureAlgorithm(String sigAlgo);

    public String getSignatureAlgorithm();
}
