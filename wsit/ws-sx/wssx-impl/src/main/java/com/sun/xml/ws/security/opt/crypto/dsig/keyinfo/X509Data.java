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
 * X509Data.java
 *
 * Created on January 24, 2006, 4:52 PM
 */

package com.sun.xml.ws.security.opt.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Abhijit Das
 */
@XmlRootElement(name="X509Data", namespace = "http://www.w3.org/2000/09/xmldsig#")
public class X509Data extends com.sun.xml.security.core.dsig.X509DataType implements javax.xml.crypto.dsig.keyinfo.X509Data {
    
    /** Creates a new instance of X509Data */
    public X509Data() {
    }

    public List getContent() {
        return x509IssuerSerialOrX509SKIOrX509SubjectName;
    }
    
    
    public boolean isFeatureSupported(String string) {
        return false;
    }
    
    public void setX509IssuerSerialOrX509SKIOrX509SubjectName(List<Object> x509IssuerSerialOrX509SKIOrX509SubjectName){
        this.x509IssuerSerialOrX509SKIOrX509SubjectName = x509IssuerSerialOrX509SKIOrX509SubjectName;
    }
    
}
