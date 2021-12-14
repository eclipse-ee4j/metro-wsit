/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
    URL getResource(String resource) throws MalformedURLException;
    InputStream getResourceAsStream(String resource);
    Set<String> getResourcePaths(String path);
    Object getAttribute(String name);
    void setAttribute(String name, Object value);
}
