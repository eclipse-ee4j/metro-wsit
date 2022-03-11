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
 * DigestMethod.java
 *
 * Created on January 24, 2006, 2:40 PM
 */

package com.sun.xml.ws.security.opt.crypto.dsig;

import java.security.spec.AlgorithmParameterSpec;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Abhijit Das
 */
@XmlRootElement(name="DigestMethod",namespace = "http://www.w3.org/2000/09/xmldsig#")
public class DigestMethod extends com.sun.xml.security.core.dsig.DigestMethodType implements javax.xml.crypto.dsig.DigestMethod {

    @XmlTransient private AlgorithmParameterSpec algSpec = null;

    /** Creates a new instance of DigestMethod */
    public DigestMethod() {
    }

    public void setParameterSpec(AlgorithmParameterSpec algSpec) {
        this.algSpec = algSpec;
    }

    @Override
    public AlgorithmParameterSpec getParameterSpec() {
        return algSpec;
    }

    @Override
    public boolean isFeatureSupported(String string) {
        //TODO
        return false;
    }

}
