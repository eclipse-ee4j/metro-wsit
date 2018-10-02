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
 * AlgorithmSuiteValue.java
 *
 * Created on February 14, 2006, 2:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.policy;

/**
 * AlgorithmSuiteValue identifies the algorithm to be used to protect the message.
 * @author Abhijit Das
 */
public enum AlgorithmSuiteValue {
    Basic256(
            Constants.SHA1,
            Constants.AES256,
            Constants.KW_AES256,
            Constants.KW_RSA_OAEP,
            Constants.PSHA1_L256,
            Constants.PSHA1_L192,
            256),
    Basic192(
            Constants.SHA1,
            Constants.AES192,
            Constants.KW_AES192,
            Constants.KW_RSA_OAEP,
            Constants.PSHA1_L192,
            Constants.PSHA1_L192,
            192),
    Basic128(
            Constants.SHA1,
            Constants.AES128,
            Constants.KW_AES128,
            Constants.KW_RSA_OAEP,
            Constants.PSHA1_L128,
            Constants.PSHA1_L128,
            128),
    TripleDes(
            Constants.SHA1,
            Constants.TRIPLE_DES,
            Constants.KW_TRIPLE_DES,
            Constants.KW_RSA_OAEP,
            Constants.PSHA1_L192,
            Constants.PSHA1_L192,
            192),
    Basic256Rsa15(
            Constants.SHA1,
            Constants.AES256,
            Constants.KW_AES256,
            Constants.KW_RSA15,
            Constants.PSHA1_L256,
            Constants.PSHA1_L192,
            256),
    Basic192Rsa15(
            Constants.SHA1,
            Constants.AES192,
            Constants.KW_AES192,
            Constants.KW_RSA15,
            Constants.PSHA1_L192,
            Constants.PSHA1_L192,
            192),
    Basic128Rsa15(
            Constants.SHA1,
            Constants.AES128,
            Constants.KW_AES128,
            Constants.KW_RSA15,
            Constants.PSHA1_L128,
            Constants.PSHA1_L128,
            128),
    TripleDesRsa15(
            Constants.SHA1,
            Constants.TRIPLE_DES,
            Constants.KW_TRIPLE_DES,
            Constants.KW_RSA15,
            Constants.PSHA1_L192,
            Constants.PSHA1_L192,
            192),
    Basic256Sha256(
            Constants.SHA256,
            Constants.AES256,
            Constants.KW_AES256,
            Constants.KW_RSA_OAEP,
            Constants.PSHA1_L256,
            Constants.PSHA1_L192,
            256),
    Basic192Sha256(
            Constants.SHA256,
            Constants.AES192,
            Constants.KW_AES192,
            Constants.KW_RSA_OAEP,
            Constants.PSHA1_L192,
            Constants.PSHA1_L192,
            192),
    Basic128Sha256(
            Constants.SHA256,
            Constants.AES128,
            Constants.KW_AES128,
            Constants.KW_RSA_OAEP,
            Constants.PSHA1_L128,
            Constants.PSHA1_L128,
            128),
    TripleDesSha256(
            Constants.SHA256,
            Constants.TRIPLE_DES,
            Constants.KW_TRIPLE_DES,
            Constants.KW_RSA_OAEP,
            Constants.PSHA1_L192,
            Constants.PSHA1_L192,
            192),
    Basic256Sha256Rsa15(
            Constants.SHA256,
            Constants.AES256,
            Constants.KW_AES256,
            Constants.KW_RSA15,
            Constants.PSHA1_L256,
            Constants.PSHA1_L192,
            256),
    Basic192Sha256Rsa15(
            Constants.SHA256,
            Constants.AES192,
            Constants.KW_AES192,
            Constants.KW_RSA15,
            Constants.PSHA1_L192,
            Constants.PSHA1_L192,
            192),
    Basic128Sha256Rsa15(
            Constants.SHA256,
            Constants.AES128,
            Constants.KW_AES128,
            Constants.KW_RSA15,
            Constants.PSHA1_L128,
            Constants.PSHA1_L128,
            128),
    TripleDesSha256Rsa15(
            Constants.SHA256,
            Constants.TRIPLE_DES,
            Constants.KW_TRIPLE_DES,
            Constants.KW_RSA15,
            Constants.PSHA1_L192,
            Constants.PSHA1_L192,
            192);
            
    
    private final String dsigAlgorithm;
    private final String encAlgorithm;
    private final String symKWAlgorithm;
    private final String asymKWAlgorithm;
    private final String encKDAlgorithm;
    private final String sigKDAlgorithm;
    private final int minSKLAlgorithm;
    
    
    AlgorithmSuiteValue(
            String dsigAlgorithm,
            String encAlgorithm,
            String symKWAlgorithm,
            String asymKWAlgorithm,
            String encKDAlgorithm,
            String sigKDAlgorithm,
            int minSKLAlgorithm) {
        
        this.dsigAlgorithm = dsigAlgorithm;
        this.encAlgorithm = encAlgorithm;
        this.symKWAlgorithm = symKWAlgorithm;
        this.asymKWAlgorithm = asymKWAlgorithm;
        this.encKDAlgorithm = encKDAlgorithm;
        this.sigKDAlgorithm = sigKDAlgorithm;
        this.minSKLAlgorithm = minSKLAlgorithm;
    }

    public String getDigAlgorithm() {
        return dsigAlgorithm;
    }

    public String getEncAlgorithm() {
        return encAlgorithm;
    }

    public String getSymKWAlgorithm() {
        return symKWAlgorithm;
    }

    public String getAsymKWAlgorithm() {
        return asymKWAlgorithm;
    }

    public String getEncKDAlgorithm() {
        return encKDAlgorithm;
    }

    public String getSigKDAlgorithm() {
        return sigKDAlgorithm;
    }

    public int getMinSKLAlgorithm() {
        return minSKLAlgorithm;
    }
}
