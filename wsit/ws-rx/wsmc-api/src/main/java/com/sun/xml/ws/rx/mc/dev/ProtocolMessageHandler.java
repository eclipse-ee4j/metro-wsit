/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.dev;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import java.util.Collection;

/**
 * Implementations of this interface that are registered with 
 * {@link com.sun.xml.ws.rx.mc.runtime.WsMcResponseHandler#processResponse(Packet)}
 * are invoked to handle protocol response messages that don't correlate with any 
 * client request.
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public interface ProtocolMessageHandler {

    /**
     * Provides information about all WS-Addressing actions that this handler understands and can process.
     *
     * @return collection of all WS-Addressing actions that this handler understands and can process.
     *         Must not return {@code null}.
     */
    public @NotNull Collection<String> getSuportedWsaActions();

    /**
     * <p>
     * This method is invoked from {@link com.sun.xml.ws.rx.mc.runtime.WsMcResponseHandler#processResponse(Packet)}
     * in case it is not possible to resolve WS-A {@code RelatesTo} header from the response message to an existing
     * suspended fiber. In such case it is assumed that the response may contain some general WS-* protocol message
     * and collection of registered {@link ProtocolMessageHandler}s is consulted.
     * </p>
     *
     * <p>
     * In case the WS-Addressing {@code wsa:Action} header matches one of the supported WS-Addressing actions returned 
     * from {@link #getSuportedWsaActions()} method, the {@link #processProtocolMessage(com.sun.xml.ws.api.message.Packet)}
     * is invoked on {@link ProtocolMessageHandler} instance to process the protocol message.
     * </p>
     *
     * @param protocolMessage a protocol message to be handled
     */
    public void processProtocolMessage(Packet protocolMessage);
}
