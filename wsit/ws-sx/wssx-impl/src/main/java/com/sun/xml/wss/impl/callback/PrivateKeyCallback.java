/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.callback;

import java.security.KeyStore;
import java.security.PrivateKey;
import javax.security.auth.callback.Callback;

/**
 * Callback class for obtaining the private key from KeyStore
 * Used only for Metro(WSIT) Security Scenarios
 */
public class PrivateKeyCallback extends XWSSCallback implements Callback {

    /**
     * The Private Key to be set by the CallbackHandler.
     */
    private PrivateKey key;
    /**
     * CallbackHandler Implementations may choose to ignore the keystore
     */
    private KeyStore keystore;
    /** 
     * The alias (identifier) for the required PrivateKey
     */
    private String   alias;

    public PrivateKey getKey() {
        return key;
    }

    public void setKey(PrivateKey key) {
        this.key = key;
    }

    public KeyStore getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStore keystore) {
        this.keystore = keystore;
    }
    
    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    public String getAlias() {
        return alias;
    }
    
}
