/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */


package com.sun.xml.wss.saml.util;

import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.ws.WebServiceException;

/**
 *
 * @author root
 */
public class SAML20JAXBUtil {

    /** Creates a new instance of SAML20JAXBUtil */
    static JAXBContext jaxbContext;
    public static final WSSNamespacePrefixMapper prefixMapper11 = new WSSNamespacePrefixMapper();
    public static final WSSNamespacePrefixMapper prefixMapper12 = new WSSNamespacePrefixMapper(true);

    static {
        try {
            //JAXB might access private class members by reflection so 
            //make it JAXBContext privileged
            AccessController.doPrivileged(new PrivilegedExceptionAction<>() {
                @Override
                public Object run() throws Exception {
                    jaxbContext = JAXBContext.newInstance("com.sun.xml.wss.saml.internal.saml20.jaxb20");
                    return null;
                }
            });
        } catch (Exception je) {
            throw new WebServiceException(je);
        }
    }

    public static JAXBContext getJAXBContext() {
        return jaxbContext;
    }
    /**
     * 
     * @param namespaces list of ":" separated namespaces to be used to create the JAXBContext
     * @return JAXBContext initialized with the namespaces
     */
    public static JAXBContext getJAXBContext(String namespaces) throws JAXBException{
        jaxbContext = JAXBContext.newInstance("com.sun.xml.wss.saml.internal.saml20.jaxb20"
                                               +":"+namespaces);
        return jaxbContext;
    }       
}
