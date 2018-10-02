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
 * KeyIdentifierCertSelector.java
 *
 * Created on February 26, 2007, 5:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl.misc;

import java.security.cert.CertSelector;
import java.security.cert.Certificate;

import com.sun.xml.wss.core.reference.X509SubjectKeyIdentifier;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import com.sun.xml.wss.XWSSecurityException;

/**
 *
 * @author kumar jayanti
 */
public class KeyIdentifierCertSelector implements CertSelector {
    
    private final byte[] keyId;
    /** Creates a new instance of KeyIdentifierCertSelector */
    public KeyIdentifierCertSelector(byte[] keyIdValue) {
        this.keyId = keyIdValue;
    }

    public boolean match(Certificate cert) {
        if (cert instanceof X509Certificate) {
            byte[] keyIdtoMatch = null;
            try {
                keyIdtoMatch =
                    X509SubjectKeyIdentifier.getSubjectKeyIdentifier((X509Certificate)cert);
            }catch (XWSSecurityException ex) {
                //ignore since not all certs in Certstore may have SKID
            }
            if (Arrays.equals(keyIdtoMatch, keyId)) {
                return true;
            }  
        }
        return false;
    }
    
    public Object clone() {
        return new KeyIdentifierCertSelector(this.keyId);
    }
    
}
