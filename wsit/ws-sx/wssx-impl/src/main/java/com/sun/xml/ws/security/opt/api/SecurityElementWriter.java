/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.api;

import java.io.OutputStream;
import java.util.HashMap;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public interface SecurityElementWriter {
    
    /**
     * Writes out the header.
     *
     * @throws XMLStreamException
     *      if the operation fails for some reason. This leaves the
     *      writer to an undefined state.
     */
    void writeTo(XMLStreamWriter streamWriter) throws XMLStreamException;
    
    /**
     *
     */
    void writeTo(XMLStreamWriter streamWriter, HashMap props) throws XMLStreamException;
    
    /**
     *
     */
    void writeTo(OutputStream os);
}
