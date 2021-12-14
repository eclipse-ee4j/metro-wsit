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
 * $Id: KeyNameStrategy.java,v 1.2 2010-10-21 15:37:29 snajper Exp $
 */

package com.sun.xml.wss.impl.keyinfo;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.security.cert.X509Certificate;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.XWSSecurityException;

import com.sun.xml.wss.core.KeyInfoHeaderBlock;
import com.sun.xml.wss.core.SecurityTokenReference;
import com.sun.xml.wss.logging.LogStringsMessages;

public class KeyNameStrategy extends KeyInfoStrategy {

    protected static final Logger log =
        Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    String keyName;

    public KeyNameStrategy() {
        this.keyName = null;
    }

    public KeyNameStrategy(String alias, boolean forSigning) {
        this.keyName = alias;
    }

    @Override
    public void insertKey(
        SecurityTokenReference tokenRef,
        SecurableSoapMessage secureMsg) {

        log.log(Level.SEVERE,
                LogStringsMessages.WSS_0703_UNSUPPORTED_OPERATION());
        throw new UnsupportedOperationException(
            "A ds:KeyName can't be put under a wsse:SecurityTokenReference");
    }


    @Override
    public void insertKey(
        KeyInfoHeaderBlock keyInfo,
        SecurableSoapMessage secureMsg,
        String x509TokenId) // x509TokenId can be ignored
    {

        keyInfo.addKeyName(keyName);
    }

    @Override
    public void setCertificate(X509Certificate cert) {
        log.log(Level.SEVERE,
                LogStringsMessages.WSS_0705_UNSUPPORTED_OPERATION());
        throw new UnsupportedOperationException(
            "Setting a certificate is not a supported operation for ds:KeyName strategy");
    }

    public String getKeyName() {
        return keyName;
    }

    @Override
    public String getAlias() {
        return keyName;
    }
    
    public void setKeyName(String name){
        keyName = name;
    }
}

