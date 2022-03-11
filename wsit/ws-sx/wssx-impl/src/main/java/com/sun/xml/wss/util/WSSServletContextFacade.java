/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 Eclipse Foundation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Facade for the {@link jakarta.servlet.ServletContext} class.
 */
public class WSSServletContextFacade {
    private final jakarta.servlet.ServletContext context;

    WSSServletContextFacade(Object context) {
        this.context = (jakarta.servlet.ServletContext) context;
    }

    /**
     * Looks for a file as a resource from a ServletContext.
     *
     * @param path the path of the file resource
     * @return URL pointing to the given config file or null
     */
    public URL getResource(String path) {
        try {
            return this.context.getResource(path);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * @return the configuration name of the logical host on which the ServletContext is deployed.
     */
    public String getVirtualServerName() {
        return context.getVirtualServerName();
    }

    /**
     * @return the context path of the web application.
     */
    public String getContextPath() {
        return context.getContextPath();
    }

    /**
     * Sets the servlet context attribute.
     *
     */
    public void setStringAttribute(String attributeName, String attributeValue) {
        context.setAttribute(attributeName, attributeValue);
    }

    /**
     * Don't use this method for non-string attributes.
     *
     * @return the servlet context attribute.
     */
    public String getStringAttribute(String attributeName) {
        return (String) context.getAttribute(attributeName);
    }
}
