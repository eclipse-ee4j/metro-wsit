/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * SecurityElement.java
 *
 * Created on August 1, 2006, 2:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.opt.api;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author K.Venugopal@sun.com
 */
public interface SecurityElement {
    /**
     * 
     * @return id
     */
    String getId();
    /**
     *
     */
    void setId(final String id);
    /**
     * 
     * @return namespace uri of the security header element.
     */
    String getNamespaceURI();
    
    /**
     * Gets the local name of this header element.
     *
     * @return
     *      this string must be interned.
     */
    String getLocalPart();
    
   
    /**
     * Reads the header as a {@link XMLStreamReader}.
     * 
     * <p>
     * The returned parser points at the start element of this header.
     * (IOW, {@link XMLStreamReader#getEventType()} would return
     * {@link XMLStreamReader#START_ELEMENT}.
     * 
     * <p><strong>Performance Expectation</strong>
     * <p>
     * For some Header implementations, this operation
     * is a non-trivial operation. Therefore, use of this method
     * is discouraged unless the caller is interested in reading
     * the whole header.
     * 
     * <p>
     * Similarly, if the caller wants to use this method only to do
     * the API conversion (such as simply firing SAX events from
     * {@link XMLStreamReader}), then the JAX-WS team requests
     * that you talk to us.
     * 
     * <p>
     * Messages that come from tranport usually provides
     * a reasonably efficient implementation of this method.
     * 
     * @return must not null.
     */
    XMLStreamReader readHeader() throws XMLStreamException;  
    
}
