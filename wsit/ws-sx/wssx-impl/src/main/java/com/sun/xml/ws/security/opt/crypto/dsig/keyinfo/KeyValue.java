/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * KeyValue.java
 *
 * Created on January 24, 2006, 4:49 PM
 */

package com.sun.xml.ws.security.opt.crypto.dsig.keyinfo;

import java.math.BigInteger;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Abhijit Das
 */
@XmlRootElement(name="KeyValue", namespace = "http://www.w3.org/2000/09/xmldsig#")
public class KeyValue extends com.sun.xml.security.core.dsig.KeyValueType implements javax.xml.crypto.dsig.keyinfo.KeyValue {
    
    /** Creates a new instance of KeyValue */
    public KeyValue() {
    }
    
    public PublicKey getPublicKey() throws KeyException {
        PublicKey publicKey = null;
        for ( Object o : content) {
            if ( o instanceof DSAKeyValue) {
                DSAKeyValue dsaKeyValue = (DSAKeyValue) o;
                
                DSAPublicKeySpec spec = new DSAPublicKeySpec(
                        new BigInteger(dsaKeyValue.getY()),
                        new BigInteger(dsaKeyValue.getP()),
                        new BigInteger(dsaKeyValue.getQ()),
                        new BigInteger(dsaKeyValue.getG()) );
                try {
                    KeyFactory fac = KeyFactory.getInstance("DSA");
                    return fac.generatePublic(spec);
                } catch (Exception ex) {
                    throw new KeyException(ex);
                }
            } else if ( o instanceof RSAKeyValue) {
                RSAKeyValue rsaKayValue = (RSAKeyValue) o;
                
                RSAPublicKeySpec spec = new RSAPublicKeySpec(
                        new BigInteger(rsaKayValue.getModulus()),
                        new BigInteger(rsaKayValue.getExponent()));
                
                try {
                    KeyFactory fac = KeyFactory.getInstance("RSA");
                    return fac.generatePublic(spec);
                } catch (Exception ex) {
                    throw new KeyException(ex);
                }
            }
        }
        return null;
    }
    
    public boolean isFeatureSupported(String string) {
        return false;
    }
    
    public void setContent(List<Object> content ) {
        this.content = content;
    }
    
}
