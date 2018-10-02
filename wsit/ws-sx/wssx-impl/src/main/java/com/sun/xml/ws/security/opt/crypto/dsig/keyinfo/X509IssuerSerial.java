/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * X509IssuerSerial.java
 *
 * Created on January 24, 2006, 4:54 PM
 */

package com.sun.xml.ws.security.opt.crypto.dsig.keyinfo;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Abhijit Das
 */
@XmlRootElement(name="X509IssuerSerial", namespace = "http://www.w3.org/2000/09/xmldsig#")
public class X509IssuerSerial extends com.sun.xml.security.core.dsig.X509IssuerSerialType implements javax.xml.crypto.dsig.keyinfo.X509IssuerSerial {
    
    /** Creates a new instance of X509IssuerSerial */
    public X509IssuerSerial() {
    }

    public String getIssuerName() {
        return x509IssuerName;
    }

    public BigInteger getSerialNumber() {
        return x509SerialNumber;
    }

    public boolean isFeatureSupported(String string) {
        return false;
    }
    
}
