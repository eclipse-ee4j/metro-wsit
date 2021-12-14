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
 * JAXBStructure.java
 *
 * Created on February 6, 2006, 5:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.opt.crypto.jaxb;

import jakarta.xml.bind.JAXBElement;

/**
 *
 * @author Abhijit Das
 */
public class JAXBStructure implements javax.xml.crypto.XMLStructure {
    
    private final JAXBElement jbElement;
    /** Creates a new instance of JAXBStructure */
    public JAXBStructure(JAXBElement jbElement) {
        if(jbElement == null)
            throw new NullPointerException("JAXBElement cannot be null");
        this.jbElement = jbElement;
    }
    
    /**
     * Returns the JAXBElement contained in this <code>JAXBStructure</code>.
     *
     * @return the JAXBElement
     */
    public JAXBElement getJAXBElement(){
        return jbElement;
    }

    /**
     */
    @Override
    public boolean isFeatureSupported(String feature) {
        if (feature == null) {
            throw new NullPointerException();
        } else {
            return false;
        }
    }
    
}
