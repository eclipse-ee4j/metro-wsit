/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v10.client;

import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.coord.v10.types.RegistrationCoordinatorPortType;
import com.sun.xml.ws.tx.coord.v10.types.RegistrationPortTypeRPC;
import com.sun.xml.ws.tx.coord.v10.types.RegistrationRequesterPortType;

import javax.xml.namespace.QName;

import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;

import java.net.MalformedURLException;
import java.net.URL;




@WebServiceClient(name = "RegistrationService_V10", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor",
        wsdlLocation = "wscoor.wsdl")
public class RegistrationServiceV10
    extends Service
{

    private static URL REGISTRATIONSERVICEV10_WSDL_LOCATION;
    static {
        try {
            REGISTRATIONSERVICEV10_WSDL_LOCATION = new URL(WSATHelper.V10.getRegistrationCoordinatorAddress() + "?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public RegistrationServiceV10(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RegistrationServiceV10() {
        super(REGISTRATIONSERVICEV10_WSDL_LOCATION, new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "Coordinator"));
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RegistrationRequesterPortType
     */
    @WebEndpoint(name = "RegistrationRequesterPortTypePort")
    public RegistrationRequesterPortType getRegistrationRequesterPortTypePort(EndpointReference epr,WebServiceFeature... features) {
        return super.getPort(epr, RegistrationRequesterPortType.class, features);
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RegistrationPortTypeRPC
     */
    @WebEndpoint(name = "RegistrationPortTypeRPCPort")
    public RegistrationPortTypeRPC getRegistrationPortTypeRPCPort(EndpointReference epr, WebServiceFeature... features) {
        return super.getPort(epr, RegistrationPortTypeRPC.class, features);
    }


    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RegistrationCoordinatorPortType
     */
    @WebEndpoint(name = "RegistrationCoordinatorPortTypePort")
    public RegistrationCoordinatorPortType getRegistrationCoordinatorPortTypePort(EndpointReference epr, WebServiceFeature... features) {
        return super.getPort(epr, RegistrationCoordinatorPortType.class, features);
    }

}
