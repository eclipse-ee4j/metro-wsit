/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.connectioncache.spi.transport;

import java.io.IOException ;

/** The ContactInfo represents the information needed to establish a connection
 * to a (possibly different) process.  This is a subset of the PEPt 2.0 connection.
 * Any implemetnation of this interface must define hashCode and equals properly so that
 * it may be used in a Map.
 */
public interface ContactInfo<C extends Connection> {
    /** Create a new Connection from this ContactInfo.
     * Throws an IOException if Connection creation fails.
     */
    C createConnection() throws IOException ;
}
