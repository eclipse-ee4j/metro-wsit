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
 * SignatureMethod.java
 *
 * Created on January 24, 2006, 3:07 PM
 */

package com.sun.xml.ws.security.opt.crypto.dsig;

import java.security.spec.AlgorithmParameterSpec;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Abhijit Das
 */
@XmlRootElement(name="SignatureMethod",namespace = "http://www.w3.org/2000/09/xmldsig#")
public class SignatureMethod extends com.sun.xml.security.core.dsig.SignatureMethodType implements javax.xml.crypto.dsig.SignatureMethod {
    
    
    @XmlTransient private AlgorithmParameterSpec algSpec = null;
    /** Creates a new instance of SignatureMethod */
    public SignatureMethod() {
    }

    public AlgorithmParameterSpec getParameterSpec() {
        return algSpec;
    }
    
    public void setParameter(AlgorithmParameterSpec algSpec) {
        this.algSpec = algSpec;
    }

    public boolean isFeatureSupported(String string) {
        //TODO
        return false;
    }
    
    
}
