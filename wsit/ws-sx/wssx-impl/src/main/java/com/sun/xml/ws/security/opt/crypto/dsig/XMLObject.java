/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.crypto.dsig;

import jakarta.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 *
 * @author Abhijit Das
 */
public class XMLObject extends com.sun.xml.security.core.dsig.ObjectType implements javax.xml.crypto.dsig.XMLObject {
    
    @XmlTransient private List content1 = null;
    
    /** Creates a new instance of XMLObject */
    public XMLObject() {
    }

    @Override
    public boolean isFeatureSupported(String string) {
        //TODO:
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List getContent() {
        return content1;
    }

    public void setContent(List content) {
        this.content1 = content;
    }
    
}
