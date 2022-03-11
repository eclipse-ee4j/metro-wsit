/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v11.client;

import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.coord.v11.types.RegistrationPortType;
import com.sun.xml.ws.tx.coord.v11.types.RegistrationRequesterPortType;
import com.sun.xml.ws.tx.coord.v11.types.RegistrationCoordinatorPortType;

import javax.xml.namespace.QName;

import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;

import java.net.MalformedURLException;
import java.net.URL;

@WebServiceClient(name = "RegistrationService_V11", targetNamespace = "http://docs.oasis-open.org/ws-tx/wscoor/2006/06", wsdlLocation = "wstx-wscoor-1.1-wsdl-200702.wsdl")
public class RegistrationServiceV11
    extends Service
{
    
    private static URL REGISTRATIONSERVICEV11_WSDL_LOCATION;
    static {
        try {
            REGISTRATIONSERVICEV11_WSDL_LOCATION = new URL(WSATHelper.V11.getRegistrationCoordinatorAddress() + "?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public RegistrationServiceV11(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RegistrationServiceV11() {
        super(REGISTRATIONSERVICEV11_WSDL_LOCATION, new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "RegistrationService_V11"));
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RegistrationPortType
     */
    @WebEndpoint(name = "RegistrationPort")
    public RegistrationPortType getRegistrationPort(EndpointReference epr, WebServiceFeature... features) {
        return super.getPort(epr, RegistrationPortType.class, features);
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RegistrationRequesterPortType
     */
    @WebEndpoint(name = "RegistrationRequesterPort")
    public RegistrationRequesterPortType getRegistrationRequesterPort(EndpointReference epr, WebServiceFeature... features) {
        return super.getPort(epr, RegistrationRequesterPortType.class, features);
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RegistrationCoordinatorPortType
     */
    @WebEndpoint(name = "RegistrationCoordinatorPort")
    public RegistrationCoordinatorPortType getRegistrationCoordinatorPort(EndpointReference epr, WebServiceFeature... features) {
        return super.getPort(epr, RegistrationCoordinatorPortType.class, features);
    }

}
