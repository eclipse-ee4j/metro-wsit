/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.mex.client;

import javax.xml.namespace.QName;

/**
 * Class to hold information about a port, such as
 * the port name, address, and name of the containing service.
 *
 * @see com.sun.xml.ws.mex.client.MetadataClient
 */
public class PortInfo {

    private final QName serviceName;
    private final QName portName;
    private final String address;

    PortInfo(QName serviceName, QName portName, String address) {
        this.serviceName = serviceName;
        this.portName = portName;
        this.address = address;
    }

    /**
     * Retrieve the qname for the service that
     * contains this port.
     */
    public QName getServiceName() {
        return serviceName;
    }

    /**
     * Retrieve the qname for this port.
     */
    public QName getPortName() {
        return portName;
    }

    /**
     * Retrieve the address for this port.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Utility method for obtaining port local name. This
     * method is equivalent to getPortName().getLocalPart().
     */
    public String getPortLocalPart() {
        return portName.getLocalPart();
    }

    /**
     * Utility method for obtaining port namespace. This
     * method is equivalent to getPortName().getNamespaceURI().
     */
    public String getPortNamespaceURI() {
        return portName.getNamespaceURI();
    }

    /**
     * Utility method for obtaining service local name. This
     * method is equivalent to getServiceName().getLocalPart().
     */
    public String getServiceLocalPart() {
        return serviceName.getLocalPart();
    }

}
