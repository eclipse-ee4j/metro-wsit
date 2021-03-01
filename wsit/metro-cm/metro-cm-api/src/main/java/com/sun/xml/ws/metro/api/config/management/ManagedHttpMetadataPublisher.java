/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.metro.api.config.management;

import com.sun.istack.NotNull;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.server.ServiceDefinition;
import com.sun.xml.ws.config.management.ManagementMessages;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.HttpMetadataPublisher;
import com.sun.xml.ws.transport.http.WSHTTPConnection;

import java.io.IOException;

/**
 * Publish WSDL of a managed endpoint.
 * 
 * This implementation makes sure the WSDL policies are updated when the endpoint
 * was reconfigured.
 *
 * @author Fabian Ritzmann
 */
class ManagedHttpMetadataPublisher extends HttpMetadataPublisher implements Component {

    private static final Logger LOGGER = Logger.getLogger(ManagedHttpMetadataPublisher.class);

    @Override
    public <T> T getSPI(Class<T> spiType) {
        if (spiType.isAssignableFrom(this.getClass())) {
            return spiType.cast(this);
        }
        else {
            return null;
        }
    }

    @Override
    public boolean handleMetadataRequest(HttpAdapter adapter, WSHTTPConnection connection)
            throws IOException {
        final String query = connection.getQueryString();
        if (isWSDLQuery(query)) {
            publishWSDL(connection, adapter);
            return true;
        }
        else if (isInitQuery(query)) {
            LOGGER.info(ManagementMessages.WSM_5100_INIT_RECEIVED());
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns true if the given query string is for WSDL request.
     *
     * @param query
     *      String like "wsdl=2".
     *      Can be null.
     * @return true for WSDL requests
     *         false for web service requests
     */
    private boolean isWSDLQuery(String query) {
        return query != null && (query.equals("WSDL") || query.startsWith("wsdl"));
    }

    /**
     * Returns true if the given query string is init-cm. The case is ignored.
     *
     * @param query The query string. May be null.
     * @return True if the query string is init-cm. False otherwise.
     */
    private boolean isInitQuery(String query) {
        return query != null && query.toLowerCase().equals("init-cm");
    }

    /**
     * Sends out the WSDL (and other referenced documents)
     * in response to the GET requests to URLs like "?wsdl" or "?xsd=2".
     *
     * @param connection
     *      The connection to which the data will be sent.
     * @param adapter
     *      The HttpAdapter that handles the connection.
     *
     * @throws IOException when I/O errors happen
     */
    private void publishWSDL(@NotNull WSHTTPConnection connection, final @NotNull HttpAdapter adapter)
            throws IOException {
        // If the service definition has changed in the meantime, reprocess it
        final ServiceDefinition currentServiceDefinition = adapter.getEndpoint().getServiceDefinition();
        if (adapter.getServiceDefinition() != currentServiceDefinition) {
            adapter.initWSDLMap(currentServiceDefinition);
        }
        adapter.publishWSDL(connection);
    }

}
