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

import com.sun.xml.ws.api.addressing.WSEndpointReference;

/**
 * This internal API interface provides access to the basic WS-MC runtime features.
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public interface WsmcRuntimeProvider {

    /**
     * Provides the current endpoint's WS-MC annonymous URI
     */
    WSEndpointReference getWsmcAnonymousEndpointReference();

    /**
     * This method may be used by other WS-* client-side implementations to register handlers for the
     * one-way protocol messages that may be received from the server side.
     */
    void registerProtocolMessageHandler(ProtocolMessageHandler handler);
}
