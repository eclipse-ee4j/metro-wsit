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
import javax.security.auth.callback.Callback;

/**
 * Callback class for obtaining the keystore
 * Used only for Metro(WSIT) Security Scenarios
 * It assumes that load method on the returned KeyStore was called
 * by the user to initialized the KeyStore.
 */
public class KeyStoreCallback extends XWSSCallback implements Callback {

    private KeyStore keystore;

    public KeyStore getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStore keystore) {
        this.keystore = keystore;
    }
}
