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

import com.sun.xml.ws.transport.http.ResourceLoader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * @author Alexey Stashok
 */
public final class TCPResourceLoader implements ResourceLoader {
    private final TCPContext context;

    public TCPResourceLoader(final TCPContext context) {
        this.context = context;
    }

    @Override
    public URL getResource(final String path) throws MalformedURLException {
        return context.getResource(path);
    }

    @Override
    public URL getCatalogFile() throws MalformedURLException {
        return getResource("/WEB-INF/jax-ws-catalog.xml");
    }

    @Override
    public Set<String> getResourcePaths(final String path) {
        return context.getResourcePaths(path);
    }
}
