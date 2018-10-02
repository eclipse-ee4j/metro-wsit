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

/**
 * Invocation processor implements processing of {@code XMLStreamWriter} method invocations.
 * This allows to implement and plug in additional features or enhancements to the standard
 * {@code XMLStreamWriter} implementations.
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public interface InvocationProcessor {
    
    /**
     * Processes the {@code XMLStreamWriter} invocation.
     *
     * @param invocation description of the {@code XMLStreamWriter} invocation to be processed
     *
     * @return {@code XMLStreamWriter} invocation result.
     */
    public Object process(Invocation invocation) throws InvocationProcessingException;
}
