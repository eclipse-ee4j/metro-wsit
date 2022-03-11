/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl;

/**
 *
 * @author sk112103
 */
public class AlgorithmSuite {
    
    private String digestAlgo;
    private String encryptionAlgo;
    private String symKeyAlgo;
    private String asymKeyAlgo;
    private String signatureAlgo;
    
    /** Creates a new instance of AlgorithmSuite 
     * TODO : Created a minimal CTOR for now. Add more info into this as needed.
     * Created to remove dependence of XWSS on WS-SecurityPolicy.
     */
    public AlgorithmSuite(String digAlgo, String encAlgo, String symkAlgo, String asymkAlgo ) {
        this.digestAlgo = digAlgo;
        this.encryptionAlgo = encAlgo;
        this.symKeyAlgo = symkAlgo;
        this.asymKeyAlgo = asymkAlgo;
    }
    
    public String getDigestAlgorithm() {
        return digestAlgo;
    }
    
    
    public String getEncryptionAlgorithm() {
        return encryptionAlgo;
    }
    
    
    public String getSymmetricKeyAlgorithm() {
        return symKeyAlgo;
    }
    
    public String getAsymmetricKeyAlgorithm() {
        return asymKeyAlgo;
    }
    
    public String getSignatureKDAlogrithm() {
        throw new UnsupportedOperationException("getSignatureKDAlogrithm not supported");
    }
    
    public String getEncryptionKDAlogrithm() {
        throw new UnsupportedOperationException("getEncryptionKDAlogrithm not supported");
    }
    
    public int getMinSKLAlgorithm() {
        throw new UnsupportedOperationException("getMinSKLAlgorithm not supported");
    }
    
    public String getSymmetricKeySignatureAlgorithm() {
        throw new UnsupportedOperationException("getSymmetricKeySignatureAlgorithm not supported");
    }
    
    public String getAsymmetricKeySignatureAlgorithm() {
      throw new UnsupportedOperationException(" getAsymmetricKeySignatureAlgorithm not supported");
    }

    public void setSignatureAlgorithm(String sigAlgo) {
        this.signatureAlgo = sigAlgo;
    }

    public String getSignatureAlgorithm() {
        return this.signatureAlgo;
    }
}
