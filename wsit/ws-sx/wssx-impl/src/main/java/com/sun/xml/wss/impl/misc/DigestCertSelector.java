/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.misc;

import java.security.cert.CertSelector;
import java.security.cert.Certificate;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.security.cert.X509Certificate;
import java.util.Arrays;


import com.sun.xml.wss.logging.LogStringsMessages;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;

/**
 *
 * @author Kumar Jayanti
 */
public class DigestCertSelector implements CertSelector {
    
    private final byte[] keyId;
    private final String algorithm;
     /** logger */
    protected static final Logger log =  Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    
    
    /** Creates a new instance of KeyIdentifierCertSelector */
    public DigestCertSelector(byte[] keyIdValue, String algo) {
        this.keyId = keyIdValue;
        this.algorithm = algo;
    }

    @Override
    public boolean match(Certificate cert) {
        if (cert instanceof X509Certificate) {
            byte[] thumbPrintIdentifier = null;
                                                                                                                      
            try {
                thumbPrintIdentifier = MessageDigest.getInstance(this.algorithm).digest(cert.getEncoded());
            } catch ( NoSuchAlgorithmException ex ) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0708_NO_DIGEST_ALGORITHM(),ex);
                throw new RuntimeException("Digest algorithm SHA-1 not found");
            } catch ( CertificateEncodingException ex) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0709_ERROR_GETTING_RAW_CONTENT(),ex);
                throw new RuntimeException("Error while getting certificate's raw content");
            }
        
            if (Arrays.equals(thumbPrintIdentifier, keyId)) {
                return true;
            }  
        }
        return false;
    }
    
    @Override
    public Object clone() {
        return new DigestCertSelector(this.keyId, this.algorithm);
    }
}
