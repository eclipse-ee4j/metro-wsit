/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v11.client;

import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.at.v11.types.CoordinatorPortType;
import com.sun.xml.ws.tx.at.v11.types.ParticipantPortType;

import javax.xml.namespace.QName;
import javax.xml.ws.*;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This is the service client for WSAT11 endpoitns.
 * 
 */
@WebServiceClient(name = "WSAT11Service", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", wsdlLocation = "wstx-wsat-1.1-wsdl-200702.wsdl")
public class WSAT11Service
    extends Service
{
    private static URL WSAT11SERVICE_WSDL_LOCATION;
    static {
        try {
            WSAT11SERVICE_WSDL_LOCATION = new URL(WSATHelper.V11.getCoordinatorAddress() + "?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
    public WSAT11Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WSAT11Service() {
        super(WSAT11SERVICE_WSDL_LOCATION, new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "WSAT11Service"));
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CoordinatorPortType
     */
    @WebEndpoint(name = "CoordinatorPort")
    public CoordinatorPortType getCoordinatorPort(EndpointReference epr, WebServiceFeature... features) {
        return super.getPort(epr, CoordinatorPortType.class, features);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ParticipantPortType
     */
    @WebEndpoint(name = "ParticipantPort")
    public ParticipantPortType getParticipantPort(EndpointReference epr,WebServiceFeature... features) {
        return super.getPort(epr, ParticipantPortType.class, features);
    }

}
