/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.testing.filters;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.rx.testing.PacketFilter;

/**
 * Filter instance of this class checks if the RM is enabled on the current WS port.
 * <p>
 * If RM is not enabled, invocation of this filter results in a {@link IllegalStateException}
 * being thrown.
 *
 */
public final class RmEnabledCheckFilter extends PacketFilter {

    @Override
    public Packet filterClientRequest(Packet request) throws Exception {
        checkRmVersion();

        return request;
    }

    @Override
    public Packet filterServerResponse(Packet response) throws Exception {
        checkRmVersion();

        return response;
    }

    private void checkRmVersion() throws IllegalStateException {
        if (getRmVersion() == null) {
            throw new IllegalStateException("Reliable Messaging is not enabled on this port!");
        }
    }
}
