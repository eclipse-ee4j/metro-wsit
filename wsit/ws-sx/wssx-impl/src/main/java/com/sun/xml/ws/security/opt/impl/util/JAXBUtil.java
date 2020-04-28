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
 * JAXBUtil.java
 *
 * Created on July 20, 2006, 3:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.opt.impl.util;

import com.sun.xml.ws.api.SOAPVersion;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.ws.WebServiceException;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class JAXBUtil {
    public static final WSSNamespacePrefixMapper prefixMapper11 = new WSSNamespacePrefixMapper();
    public static final WSSNamespacePrefixMapper prefixMapper12 = new WSSNamespacePrefixMapper(true);
    private static ThreadLocal<WeakReference<JAXBContext>> jc = new ThreadLocal<WeakReference<JAXBContext>>();

    private static  JAXBContext jaxbContext;
    private static JAXBContext customjaxbContext;

    static {
        initJAXBContext();
    }

    public static JAXBContext getCustomIdentityJAXBContext() {
        initCustomJAXBContext();
        return customjaxbContext;
    }

    private static void initCustomJAXBContext() {
        try {
            //JAXB might access private class members by reflection so
            //make it JAXBContext privileged
            AccessController.doPrivileged(new PrivilegedExceptionAction() {

                public Object run() throws Exception {
                    customjaxbContext = JAXBContext.newInstance(
                            "com.sun.xml.ws.security.opt.crypto.dsig:com.sun.xml.ws.security.opt.crypto.dsig.keyinfo:com.sun.xml.security.core.dsig:com.sun.xml.security.core.xenc:" +
                            "com.sun.xml.ws.security.opt.impl.keyinfo:com.sun.xml.ws.security.opt.impl.reference:" +
                            "com.sun.xml.ws.security.secext10:com.sun.xml.ws.security.wsu10:com.sun.xml.ws.security.secext11:" +
                            "com.sun.xml.ws.security.secconv.impl.bindings:" +
                            "com.sun.xml.ws.security.secconv.impl.wssx.bindings:com.sun.xml.security.core.ai:");
                    return null;
                }
            });
        }catch (Exception je) {
            throw new WebServiceException(je);
        }
    }

    @SuppressWarnings("unchecked")
    private static void initJAXBContext() {
        try {
            //JAXB might access private class members by reflection so
            //make it JAXBContext privileged
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws Exception {
                    jaxbContext = JAXBContext.newInstance(
                            "com.sun.xml.ws.security.opt.crypto.dsig:com.sun.xml.ws.security.opt.crypto.dsig.keyinfo:com.sun.xml.security.core.dsig:com.sun.xml.security.core.xenc:" +
                            "com.sun.xml.ws.security.opt.impl.keyinfo:com.sun.xml.ws.security.opt.impl.reference:" +
                            "com.sun.xml.ws.security.secext10:com.sun.xml.ws.security.wsu10:com.sun.xml.ws.security.secext11:" +
                            "com.sun.xml.ws.security.secconv.impl.bindings:" +
                            "com.sun.xml.ws.security.secconv.impl.wssx.bindings:");
                    return null;
                }
            });
        }catch (Exception je) {
            throw new WebServiceException(je);
        }
    }

    public static JAXBContext getJAXBContext(){
        return jaxbContext;
    }


    public static Marshaller createMarshaller(SOAPVersion soapVersion)throws JAXBException {
        try{
            Marshaller marshaller = jaxbContext.createMarshaller();
            if(SOAPVersion.SOAP_11 == soapVersion){
                marshaller.setProperty("org.glassfish.jaxb.runtime.marshaller.namespacePrefixMapper", prefixMapper11);
            }else{
                marshaller.setProperty("org.glassfish.jaxb.runtime.marshaller.namespacePrefixMapper", prefixMapper12);
            }
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT,true);
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", false);
            return marshaller;
        }catch(jakarta.xml.bind.PropertyException pe){
            throw new JAXBException("Error occurred while setting security marshaller properties",pe);
        }

    }

    public static void setSEIJAXBContext(JAXBContext context){
        jc.set(new WeakReference<JAXBContext>(context));
    }

    public static JAXBContext getSEIJAXBContext(){
        return jc.get().get();
    }

}
