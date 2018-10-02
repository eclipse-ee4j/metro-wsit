/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.xmlfilter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * The interface provides API contract for {@link InvocationProcessor} factory 
 * implementations. Implementations of this interface may be passed into {@link EnhancedXmlStreamWriterProxy}
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public interface InvocationProcessorFactory {
    
    /**
     * Factory method creates {@link InvocationProcessor} instance that implements
     * additional {@link XMLStreamWriter} feature or enhancement.
     *
     * @param writer underlying {@link XMLStreamWriter} instance that should be enhanced 
     * with the new feature(s).
     *
     * @return newly created {@link InvocationProcessor} instance.
     * 
     * @throws XMLStreamException in case of any problems with creation of
     *         new {@link InvocationProcessor} instance.
     */
    InvocationProcessor createInvocationProcessor(XMLStreamWriter writer) throws XMLStreamException;
}
