/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.servicechannel;

import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.transport.tcp.server.WSTCPModule;

/**
 * @author Alexey Stashok
 */
public class ServiceChannelCreator {

    private static final WSEndpoint<ServiceChannelWSImpl> endpoint =
            WSTCPModule.getInstance().createServiceChannelEndpoint();

    public static WSEndpoint<ServiceChannelWSImpl> getServiceChannelEndpointInstance() {
        return endpoint;
    }
}
