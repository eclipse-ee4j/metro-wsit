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

import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

/**
 *
 * @author Kumar Jayanti
 */
public class PublicKeyCertSelector implements CertSelector {

    PublicKey key = null;
    /** Creates a new instance of PublicKeyCertSelector */
    public PublicKeyCertSelector(PublicKey pk) {
        this.key = pk;
    }

    @Override
    public boolean match(Certificate cert) {
        if (cert == null) {
            return false;
        }
        if (key == null) {
            //todo: log here
            throw new XWSSecurityRuntimeException("PublicKeyCertSelector instantiated with Null Key");
        }
        if (cert instanceof X509Certificate) {
            X509Certificate x509Cert = (X509Certificate)cert;
            return key.equals(x509Cert.getPublicKey());
        }
        return false;
    }

     @Override
     public Object clone() {
        return new PublicKeyCertSelector(key);
    }
}
