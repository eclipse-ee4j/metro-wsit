/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.config.management.server;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.config.management.EndpointCreationAttributes;
import com.sun.xml.ws.metro.api.config.management.ManagedEndpoint;
import com.sun.xml.ws.api.config.management.ManagedEndpointFactory;
import com.sun.xml.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.ws.config.management.ManagementMessages;

/**
 * Create a ManagedEndpoint if the policy of the endpoint requires it. Otherwise
 * returns the given endpoint.
 *
 * @author Fabian Ritzmann, Martin Grebac
 */
public class EndpointFactoryImpl implements ManagedEndpointFactory {

    private static final Logger LOGGER = Logger.getLogger(EndpointFactoryImpl.class);

    public <T> WSEndpoint<T> createEndpoint(WSEndpoint<T> endpoint, EndpointCreationAttributes attributes) {
        final ManagedServiceAssertion assertion = ManagedServiceAssertion.getAssertion(endpoint);
        if (assertion != null && !assertion.isManagementEnabled()) {
            LOGGER.config(ManagementMessages.WSM_5002_ENDPOINT_NOT_CREATED());
            return endpoint;
        } else {
            return new ManagedEndpoint<T>(endpoint, attributes);
        }
    }

}
