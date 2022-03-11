/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.util;

import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;

import java.util.logging.Level;
import java.util.logging.Logger;

 /**
  * As the ServletContext is not a mandatory dependency, we have to expect it is not present.
  * To avoid direct dependency on the ServletContext class we created this class.
  * If the ServletContext class is not available or given object is not it's instance,
  * static methods return null.
  *
  * @author David Matejcek
  */
public class ServletContextUtil {
    private static final String CLASS_NAME = "jakarta.servlet.ServletContext";

    private static final Logger log = Logger.getLogger(
        LogDomainConstants.WSS_API_DOMAIN,
        LogDomainConstants.WSS_API_DOMAIN_BUNDLE
    );


    private ServletContextUtil() {
        // static tool
    }


    /**
     * Wraps the context or returns null if it is not possible.
     *
     * @return {@link WSSServletContextFacade} or null
     */
    public static WSSServletContextFacade wrap(Object context) {
        if (context == null) {
            return null;
        }
        Class<?> servletContextClass = findServletContextClass();
        if (servletContextClass == null) {
            return null;
        }
        if (servletContextClass.isInstance(servletContextClass)) {
            return new WSSServletContextFacade(context);
        }
        return null;
    }


    /**
     * @return null or the {@link WSSServletContextFacade} wrapping a Servletcontext instance bound
     *         to this endpoint
     */
    public static WSSServletContextFacade getServletContextFacade(final WSEndpoint<?> endpoint) {
        Container container = endpoint.getContainer();
        if (container == null) {
            return null;
        }
        final Class<?> contextClass = findServletContextClass();
        if (contextClass == null) {
            log.log(Level.WARNING, LogStringsMessages.WSS_1542_SERVLET_CONTEXT_NOTFOUND());
            return null;
        }
        return wrap(container.getSPI(contextClass));
    }


    /**
     * Tries to load the ServletContext class by the thread's context loader
     * or by the loader which was used to load this class.
     *
     * @return jakarta.servlet.ServletContext class or null
     */
    private static Class<?> findServletContextClass() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            try {
                return loader.loadClass(CLASS_NAME);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        try {
            return ServletContextUtil.class.getClassLoader().loadClass(CLASS_NAME);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
