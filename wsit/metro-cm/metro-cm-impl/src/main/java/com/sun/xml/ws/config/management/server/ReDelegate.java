/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.config.management.server;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.config.management.EndpointCreationAttributes;
import com.sun.xml.ws.metro.api.config.management.ManagedEndpoint;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.metro.api.config.management.ManagementMessages;
import com.sun.xml.ws.server.EndpointFactory;

import java.util.logging.Level;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceFeature;

/**
 * Create a new WSEndpoint instance and use it to replace the existing WSEndpoint
 * instance in a ManagedEndpoint.
 *
 * @author Fabian Ritzmann, Martin Grebac
 */
public class ReDelegate {

    private static final Logger LOGGER = Logger.getLogger(ReDelegate.class);

    /**
     * Replaces underlying endpoint in managedEndpoint instance with new instance
     * configured from a new set of features, using creation parameters of old endpoint
     * @param managedEndpoint - endpoint to be reconfigured
     * @param features - new set of features
     */
    public static <T> void recreate(ManagedEndpoint<T> managedEndpoint, WebServiceFeature... features) {
        try {
            WSEndpoint<T> delegate = recreateEndpoint(managedEndpoint, features);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine(ManagementMessages.WSM_5092_NEW_ENDPOINT_DELEGATE(delegate));
            }
            managedEndpoint.swapEndpointDelegate(delegate);

        } catch (Throwable e) {
            throw LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_5091_ENDPOINT_CREATION_FAILED(), e));
        }
    }

    /**
     * Replaces underlying endpoint in managedEndpoint instance with new instance
     * configured from a new set of features, using creation parameters of old endpoint
     * @param endpoint - endpoint to be reconfigured
     * @param features - new set of features
     * @return new endpoint instance, reconfigured with set of new features, or
     *         throws WebServiceException when recreation fails
     */
    private static <T> WSEndpoint<T> recreateEndpoint(ManagedEndpoint<T> endpoint, WebServiceFeature ... features) {

        // This allows the new endpoint to register with the same name for monitoring
        // as the old one.
        endpoint.closeManagedObjectManager();

        EndpointCreationAttributes creationAttributes = endpoint.getCreationAttributes();
        WSBinding recreatedBinding = BindingImpl.create(endpoint.getBinding().getBindingId(), features);

        final WSEndpoint<T> result = EndpointFactory.createEndpoint(endpoint.getImplementationClass(),
                creationAttributes.isProcessHandlerAnnotation(),
                creationAttributes.getInvoker(),
                endpoint.getServiceName(),
                endpoint.getPortName(),
                endpoint.getContainer(),
                recreatedBinding,
                null,
                null,
                creationAttributes.getEntityResolver(),
                creationAttributes.isTransportSynchronous());
        result.getComponents().addAll(endpoint.getComponents());

        return result;
    }

}
