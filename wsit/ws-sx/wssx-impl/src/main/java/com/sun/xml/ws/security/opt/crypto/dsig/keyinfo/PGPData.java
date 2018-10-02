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
 * PGPData.java
 *
 * Created on January 24, 2006, 4:51 PM
 */

package com.sun.xml.ws.security.opt.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Abhijit Das
 */
@XmlRootElement(name="PGPData", namespace = "http://www.w3.org/2000/09/xmldsig#")
public class PGPData extends com.sun.xml.security.core.dsig.PGPDataType implements javax.xml.crypto.dsig.keyinfo.PGPData {
    
    /** Creates a new instance of PGPData */
    public PGPData() {
    }

    public byte[] getKeyId() {
        return null;
    }

    public byte[] getKeyPacket() {
        return null;
    }

    public List getExternalElements() {
        return null;
    }

    public boolean isFeatureSupported(String string) {
        return false;
    }
    
    public void setContent(List<Object> content) {
        this.content = content;
    }
    
}
