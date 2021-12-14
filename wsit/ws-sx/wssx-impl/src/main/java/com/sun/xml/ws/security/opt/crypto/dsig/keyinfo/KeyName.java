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
 * KeyName.java
 *
 * Created on January 24, 2006, 4:45 PM
 */

package com.sun.xml.ws.security.opt.crypto.dsig.keyinfo;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Abhijit Das
 */
@XmlRootElement(name="KeyName", namespace = "http://www.w3.org/2000/09/xmldsig#")
public class KeyName implements javax.xml.crypto.dsig.keyinfo.KeyName {
    
    private String keyName = null;
    
    /** Creates a new instance of KeyName */
    public KeyName() {
    }

    @Override
    public String getName() {
        return keyName;
    }
    
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    @Override
    public boolean isFeatureSupported(String string) {
        return false;
    }
    
}
