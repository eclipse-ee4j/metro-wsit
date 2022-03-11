/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.server;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.ServletContext;

/**
 * @author Alexey Stashok
 */
@SuppressWarnings({"unchecked"})
public final class TCPServletContext implements TCPContext {

    private final ServletContext servletContext;
    private final Map<String, Object> attributes = new HashMap<>();

    public TCPServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public InputStream getResourceAsStream(final String resource) {
        return servletContext.getResourceAsStream(resource);
    }

    @Override
    public Set<String> getResourcePaths(final String path) {
        return servletContext.getResourcePaths(path);
    }

    @Override
    public URL getResource(final String resource) throws MalformedURLException {
        return servletContext.getResource(resource);
    }


    @Override
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }
}
