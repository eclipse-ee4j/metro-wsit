/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.crypto.jaxb;

import javax.xml.crypto.KeySelector;
import java.security.Key;

/**
 * Contains common context information for XML signature operation
 *
 */
public class JAXBSignContext extends com.sun.xml.ws.security.opt.crypto.jaxb.JAXBCryptoContext implements javax.xml.crypto.dsig.XMLSignContext {

    /**
     * Default constructor
     */
    public JAXBSignContext(){

    }

    public JAXBSignContext(Key signingKey){
        if (signingKey == null) {
            throw new NullPointerException("signingKey cannot be null");
        }
        setKeySelector(KeySelector.singletonKeySelector(signingKey));
    }

    public JAXBSignContext(KeySelector ks){
        if (ks == null) {
            throw new NullPointerException("key selector cannot be null");
        }
        setKeySelector(ks);
    }
}
