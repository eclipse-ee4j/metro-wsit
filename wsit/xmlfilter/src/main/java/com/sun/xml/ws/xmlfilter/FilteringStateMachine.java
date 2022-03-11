/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.xmlfilter;

import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public interface FilteringStateMachine {

    /**
     * Based on the current invocation decides whether a processing state change
     * is required and returns the result of this decision.
     *
     * @param invocation current invocation executed on the XML stream writer
     * @param writer mirror writer that records all calls (even the ones filtered out)
     *        and thus represents the "unfiltered" status of the XML stream. The
     *        parameter may be used to query the status. Implementations of the
     *        {@link FilteringStateMachine} SHOULD NOT call any methods that may result
     *        in a modification of the XML stream represented by this {@code writer}
     *        parameter.
     * @return processing state change as required.
     */
    ProcessingStateChange getStateChange(final Invocation invocation, final XMLStreamWriter writer);
}
