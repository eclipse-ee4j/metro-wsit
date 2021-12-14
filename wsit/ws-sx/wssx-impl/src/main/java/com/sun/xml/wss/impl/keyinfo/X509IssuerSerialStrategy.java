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
 * $Id: X509IssuerSerialStrategy.java,v 1.2 2010-10-21 15:37:29 snajper Exp $
 */

package com.sun.xml.wss.impl.keyinfo;

import java.security.cert.X509Certificate;

import org.w3c.dom.Document;

import java.util.logging.Logger;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.XWSSecurityException;

import com.sun.xml.wss.core.KeyInfoHeaderBlock;
import com.sun.xml.wss.core.SecurityTokenReference;
import com.sun.xml.wss.core.reference.X509IssuerSerial;

/**
 * @author Vishal Mahajan
 */
public class X509IssuerSerialStrategy extends KeyInfoStrategy {

    protected static final Logger log =
        Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    X509Certificate cert = null;

    String alias = null;    

    public X509IssuerSerialStrategy(){
        
    }
    
    public X509IssuerSerialStrategy(String alias, boolean forSigning) {
        this.alias = alias;
        //this.forSigning = forSigning;
        this.cert = null;
    }

    @Override
    public void insertKey(
        SecurityTokenReference tokenRef, SecurableSoapMessage secureMsg) 
        throws XWSSecurityException {
        X509IssuerSerial x509IssuerSerial =
            new  X509IssuerSerial(secureMsg.getSOAPPart(), cert);
        tokenRef.setReference(x509IssuerSerial);
    }

    @Override
    public void insertKey(
        KeyInfoHeaderBlock keyInfo,
        SecurableSoapMessage secureMsg,
        String x509TokenId) // x509TokenId can be ignored
        throws XWSSecurityException {

        Document ownerDoc = keyInfo.getOwnerDocument();
        SecurityTokenReference tokenRef = 
            new SecurityTokenReference(ownerDoc);
        X509IssuerSerial x509IssuerSerial =
            new  X509IssuerSerial(ownerDoc, cert);
        tokenRef.setReference(x509IssuerSerial);
        keyInfo.addSecurityTokenReference(tokenRef);
    }

    @Override
    public void setCertificate(X509Certificate cert) {
        this.cert = cert;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
