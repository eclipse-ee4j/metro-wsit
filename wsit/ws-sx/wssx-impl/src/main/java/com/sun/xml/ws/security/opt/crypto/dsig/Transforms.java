/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * Transforms.java
 *
 * Created on January 25, 2006, 6:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.opt.crypto.dsig;

import java.util.List;
import jakarta.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author Abhijit Das
 * @author K.Venugopal@sun.com
 */
@XmlRootElement(name="Transforms",namespace = "http://www.w3.org/2000/09/xmldsig#")
public class Transforms extends com.sun.xml.security.core.dsig.TransformsType {
    
    /** Creates a new instance of Transforms */
    public Transforms() {
    }
    
    @Override
    public void setTransform(List<Transform> transform) {
        this.transform = transform;
    }

   
    
}
