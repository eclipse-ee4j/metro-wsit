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
 * IssuerNameAndSerialCertSelector.java
 *
 * Created on February 28, 2007, 11:09 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl.misc;

import java.math.BigInteger;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import com.sun.xml.wss.XWSSecurityException;

import java.security.cert.CertificateEncodingException;
import javax.security.auth.x500.X500Principal;

/**
 *
 * @author kumar jayanti
 */
public class IssuerNameAndSerialCertSelector implements CertSelector {
    
    private final BigInteger serialNumber;
    private final String issuerName;
    
       /** logger */
    protected static final Logger log =  Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
  
    /** Creates a new instance of IssuerNameAndSerialCertSelector */
    public IssuerNameAndSerialCertSelector(BigInteger serialNum, String issuer) {
        this.serialNumber = serialNum;
        this.issuerName = issuer;
    }

    public boolean match(Certificate cert) {
        if (cert instanceof X509Certificate) {
           if (this.matchesIssuerSerialAndName(this.serialNumber, this.issuerName, (X509Certificate)cert)) {
               return true;
           }     
        }
        return false;
    }
    
    public Object clone() {
        return new IssuerNameAndSerialCertSelector(this.serialNumber, this.issuerName);
    }
    
    private boolean matchesIssuerSerialAndName(
        BigInteger serialNumberMatch,
        String issuerNameMatch,
        X509Certificate x509Cert) {
  
        
        X500Principal thisIssuerPrincipal = x509Cert.getIssuerX500Principal();
        X500Principal issuerPrincipal = new X500Principal(issuerName);

        BigInteger thisSerialNumber = x509Cert.getSerialNumber();


        if (serialNumber.equals(serialNumberMatch)
                && issuerPrincipal.equals(thisIssuerPrincipal)) {
            return true;
        }
        return false;
    }
}
