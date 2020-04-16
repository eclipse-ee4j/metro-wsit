/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * RetrievalMethod.java
 *
 * Created on January 26, 2006, 1:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.opt.crypto.dsig.keyinfo;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Abhijit Das
 */
@XmlRootElement(name="RetrievalMethod", namespace = "http://www.w3.org/2000/09/xmldsig#")
public class RetrievalMethod extends com.sun.xml.security.core.dsig.RetrievalMethodType {
    
    /** Creates a new instance of RetrievalMethod */
    public RetrievalMethod() {
    }
    
}
