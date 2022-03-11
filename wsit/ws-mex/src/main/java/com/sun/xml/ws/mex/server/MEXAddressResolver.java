/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.mex.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.PortAddressResolver;
import com.sun.xml.ws.mex.MessagesMessages;

/**
 * Implementation of the JAX-WS callback interface.
 *
 * Returns an address that JAX-WS uses to replace the current WSDL port address.
 *
 * @author Fabian Ritzmann
 */
class MEXAddressResolver extends PortAddressResolver {

    private static final Logger LOGGER = Logger.getLogger(MEXAddressResolver.class.getName());
    private final QName service;
    private final String port;
    private final String address;

    /**
     * Initialize the class.
     *
     * @param serviceName The name of the underlying service.
     * @param portName The name of the underlying port.
     * @param address The location address that MEX computed for this port.
     */
    MEXAddressResolver(@NotNull final QName serviceName, @NotNull final QName portName,
            @NotNull final String address) {
        this.service = serviceName;
        this.port = portName.getLocalPart();
        this.address = address;
    }

    @Override
    public String getAddressFor(@NotNull final QName serviceName, @NotNull final String portName) {
        return this.address;
    }

    /**
     * Return a new address that JAX-WS uses to overwrite the current WSDL port
     * address. This method implements the following algorithm:
     *
     * <ol>
     * <li>Only return a new address if the WSDL port and service names match.
     * (Returns null otherwise, which means that JAX-WS does not change the existing address.)
     * <li>If the original address has an "http" protocol prefix and the new address
     * has "https", return the original address.
     * <li>Otherwise return the new address.
     * </ol>
     *
     * @param serviceName The WSDL service name. May not be null.
     * @param portName The WSDL port name. May not be null.
     * @param currentAddress The current location address in the WSDL.
     * @return A new location address.
     */
    @Override
    public String getAddressFor(@NotNull final QName serviceName, @NotNull final String portName,
            final String currentAddress) {
        LOGGER.entering(MEXAddressResolver.class.getName(), "getAddressFor",
                new Object[] {serviceName, portName, currentAddress});
        String result = null;
        if (this.service.equals(serviceName) && this.port.equals(portName)) {
            result = getAddressFor(serviceName, portName);
            if (currentAddress != null) {
                try {
                    final URL addressUrl = new URL(this.address);
                    try {
                        final URL currentAddressUrl = new URL(currentAddress);
                        if (currentAddressUrl.getProtocol().toLowerCase().equals("http") &&
                                addressUrl.getProtocol().toLowerCase().equals("https")) {
                            result = currentAddress;
                            LOGGER.fine(MessagesMessages.MEX_0019_LEAVE_ADDRESS(currentAddress, portName));
                        }
                        else {
                            LOGGER.fine(MessagesMessages.MEX_0018_REPLACE_ADDRESS(currentAddress, portName, result));
                        }
                    } catch (MalformedURLException ex) {
                        LOGGER.fine(MessagesMessages.MEX_0020_CURRENT_ADDRESS_NO_URL(currentAddress, portName));
                    }
                } catch (MalformedURLException ex) {
                    LOGGER.fine(MessagesMessages.MEX_0021_NEW_ADDRESS_NO_URL(this.address, portName));
                }
            }
        }
        LOGGER.exiting(MEXAddressResolver.class.getName(), "getAddressFor", result);
        return result;
    }
}
