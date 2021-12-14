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
 * AlgorithmMethod.java
 *
 * Created on January 24, 2006, 11:43 AM
 */

package com.sun.xml.ws.security.opt.crypto;

import java.security.spec.AlgorithmParameterSpec;

/**
 *
 * @author Abhijit Das
 */
public class AlgorithmMethod implements javax.xml.crypto.AlgorithmMethod {
    
    private AlgorithmParameterSpec algSpec = null;
    private String alg = null;
    
    /** Creates a new instance of AlgorithmMethod */
    public AlgorithmMethod() {
    }

    public void setAlgorithm(String alg) {
        this.alg = alg;
    }
    
    @Override
    public String getAlgorithm() {
        return alg;
    }

    public void setParameterSpec(AlgorithmParameterSpec algSpec) {
        this.algSpec = algSpec;
    }
    
    @Override
    public AlgorithmParameterSpec getParameterSpec() {
        return algSpec;
    }
    
}
