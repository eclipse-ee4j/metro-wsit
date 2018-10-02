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
 * KeyInfo.java
 *
 * Created on January 24, 2006, 4:07 PM
 */

package com.sun.xml.ws.security.opt.crypto.dsig.keyinfo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;

/**
 *
 * @author Abhijit Das
 */
@XmlRootElement(name="KeyInfo", namespace = "http://www.w3.org/2000/09/xmldsig#")
@XmlType(name = "KeyInfoType")
public class KeyInfo extends com.sun.xml.security.core.dsig.KeyInfoType implements javax.xml.crypto.dsig.keyinfo.KeyInfo {
    
    /** Creates a new instance of KeyInfo */
    public KeyInfo() {
    }

    @Override
    public void marshal(XMLStructure xMLStructure, XMLCryptoContext xMLCryptoContext) throws MarshalException {
        
    }

    @Override
    public boolean isFeatureSupported(String string) {
        return false;
    }
    
    public void setContent(List<XMLStructure> content ) {
        this.content = content;
    }

    @Override
    public List<XMLStructure> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return content;
    }
}
