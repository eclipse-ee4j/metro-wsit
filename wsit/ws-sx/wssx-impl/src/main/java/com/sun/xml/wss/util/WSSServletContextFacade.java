/*
 * Copyright (c) 2022 Eclipse Foundation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
     * @param attributeName
     * @param attributeValue
     */
    public void setStringAttribute(String attributeName, String attributeValue) {
        context.setAttribute(attributeName, attributeValue);
    }

    /**
     * Don't use this method for non-string attributes.
     *
     * @param attributeName
     * @return the servlet context attribute.
     */
    public String getStringAttribute(String attributeName) {
        return (String) context.getAttribute(attributeName);
    }
}
