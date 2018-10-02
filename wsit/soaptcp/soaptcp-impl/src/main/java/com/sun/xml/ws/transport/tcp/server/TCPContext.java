/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * @author Alexey Stashok
 */
public interface TCPContext {
    public abstract URL getResource(String resource) throws MalformedURLException;
    public abstract InputStream getResourceAsStream(String resource) throws IOException;
    public abstract Set<String> getResourcePaths(String path);
    public abstract Object getAttribute(String name);
    public abstract void setAttribute(String name, Object value);    
}
