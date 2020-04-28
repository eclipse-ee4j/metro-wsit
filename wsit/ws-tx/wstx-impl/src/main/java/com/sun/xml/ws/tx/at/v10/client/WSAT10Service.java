/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v10.client;

import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.at.v10.types.CoordinatorPortType;
import com.sun.xml.ws.tx.at.v10.types.ParticipantPortType;

import javax.xml.namespace.QName;
import jakarta.xml.ws.*;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This is the service client for WSAT10 endpoitns.
 *
 */
@WebServiceClient(name = "WSAT10Service", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat",
        wsdlLocation = "wsat.wsdl")
public class WSAT10Service
    extends Service
{
    private static URL WSAT10SERVICE_WSDL_LOCATION;
    static {
        try {
            WSAT10SERVICE_WSDL_LOCATION = new URL(WSATHelper.V10.getCoordinatorAddress() + "?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public WSAT10Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WSAT10Service() {
        super(WSAT10SERVICE_WSDL_LOCATION, new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "WSATCoordinator"));
    }


    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CoordinatorPortType
     */
    @WebEndpoint(name = "CoordinatorPortTypePort")
    public CoordinatorPortType getCoordinatorPortTypePort(EndpointReference epr, WebServiceFeature... features) {
        return super.getPort(epr, CoordinatorPortType.class, features);
    }


    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ParticipantPortType
     */
    @WebEndpoint(name = "ParticipantPortTypePort")
    public ParticipantPortType getParticipantPortTypePort(EndpointReference epr, WebServiceFeature... features) {
        return super.getPort(epr,ParticipantPortType.class, features);
    }

}
