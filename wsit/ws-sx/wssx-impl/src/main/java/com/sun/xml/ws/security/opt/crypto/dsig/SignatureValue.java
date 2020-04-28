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
 * SignatureValue.java
 *
 * Created on January 24, 2006, 3:48 PM
 */

package com.sun.xml.ws.security.opt.crypto.dsig;

import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;



/**
 *
 * @author Abhijit Das
 */
@XmlRootElement(name="SignatureValue", namespace = "http://www.w3.org/2000/09/xmldsig#")
public class SignatureValue extends com.sun.xml.security.core.dsig.SignatureValueType 
        implements javax.xml.crypto.dsig.XMLSignature.SignatureValue {
    
    /** Creates a new instance of SignatureValue */
    public SignatureValue() {
    }

    public boolean validate(XMLValidateContext xMLValidateContext) throws XMLSignatureException {
        return false;
    }

    public boolean isFeatureSupported(String string) {
        return false;
    }
    
}
