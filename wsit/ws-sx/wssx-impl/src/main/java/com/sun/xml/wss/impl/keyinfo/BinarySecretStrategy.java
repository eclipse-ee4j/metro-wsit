/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * BinarySecretStrategy.java
 *
 * Created on January 4, 2006, 3:22 PM
 */

package com.sun.xml.wss.impl.keyinfo;

import com.sun.xml.wss.impl.misc.Base64;
import com.sun.xml.wss.core.KeyInfoHeaderBlock;
import com.sun.xml.wss.core.SecurityTokenReference;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Abhijit Das
 */
public class BinarySecretStrategy extends KeyInfoStrategy {

    private byte[] secret = null;

    protected static final Logger log =
        Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /**
     * Creates a new instance of BinarySecretStrategy
     */
    public BinarySecretStrategy() {
    }

    public BinarySecretStrategy(byte[] secret) {
        this.secret = secret;
    }

    @Override
    public void insertKey(KeyInfoHeaderBlock keyInfo, SecurableSoapMessage secureMsg, String x509TokenId) {
       //TODO: need to rework this
       // keyInfo.addBinarySecret(secret);
    }

    @Override
    public void insertKey(SecurityTokenReference tokenRef, SecurableSoapMessage secureMsg) {
        log.log(Level.SEVERE,
                LogStringsMessages.WSS_0703_UNSUPPORTED_OPERATION());
        throw new UnsupportedOperationException(
            "A ds:BinarySecret can't be put under a wsse:SecurityTokenReference");
    }

    @Override
    public void setCertificate(X509Certificate cert) {
        log.log(Level.SEVERE,
                LogStringsMessages.WSS_0705_UNSUPPORTED_OPERATION());
        throw new UnsupportedOperationException(
            "Setting a certificate is not a supported operation for ds:BinarySecret strategy");
    }

    @Override
    public String getAlias() {
        return Base64.encode(secret);
    }

    public void setSecret(byte[] secret) {
        this.secret = secret;
    }

}
