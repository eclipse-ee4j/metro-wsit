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
 * JAXBURIDereferencer.java
 *
 * Created on January 24, 2006, 11:47 AM
 */
package com.sun.xml.ws.security.opt.crypto.jaxb;



import jakarta.xml.bind.JAXBContext;

import jakarta.xml.bind.JAXBElement;

import javax.xml.crypto.Data;

import javax.xml.crypto.URIReference;

import javax.xml.crypto.URIReferenceException;

import javax.xml.crypto.XMLCryptoContext;



import com.sun.xml.ws.security.opt.impl.crypto.JAXBDataImpl;



/**

 *

 * @author Abhijit Das

 */

public class JAXBURIDereferencer  implements javax.xml.crypto.URIDereferencer {

    private JAXBElement jaxbElement = null;

    private JAXBContext jbContext = null;

    /**

     * Creates a new instance of JAXBURIDereferencer

     */

    public JAXBURIDereferencer() {

    }



    /**

     * Dereferences the specified URIReference and returns the dereferenced data.

     *

     * uriReference - the URIReference

     * xMLCryptoContext - an XMLCryptoContext that may contain additional useful 

     * information for dereferencing the URI. This implementation should 

     * dereference the specified URIReference against the context's baseURI 

     * parameter, if specified.

     *

     * 

     * @return Data - the dereferenced data

     */

    @Override
    public Data dereference(URIReference uRIReference, XMLCryptoContext xMLCryptoContext) throws URIReferenceException {

        JAXBDataImpl data = new JAXBDataImpl(getJaxbElement(), getJbContext(), new com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx(false));

        return data;

    }



    /**

     * Get the JAXBElement

     *

     * @return JAXBElement

     */

    public JAXBElement getJaxbElement() {

        return jaxbElement;

    }



    /*

     * Set JAXBElement

     * @param - jaxbElement 

     */

    public void setJaxbElement(JAXBElement jaxbElement) {

        this.jaxbElement = jaxbElement;

    }



    /**

     * Get JAXBContext

     *

     * @return JAXBContext

     */

    public JAXBContext getJbContext() {

        return jbContext;

    }



    /**

     * Set JAXBContext

     *

     * @param jbContext - JAXBContext

     */

    public void setJbContext(JAXBContext jbContext) {

        this.jbContext = jbContext;

    }

    

}

