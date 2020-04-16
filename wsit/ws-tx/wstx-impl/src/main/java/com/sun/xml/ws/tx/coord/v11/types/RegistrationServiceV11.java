/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v11.types;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.tx.at.localization.LocalizationMessages; 

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;

@WebServiceClient(name = "RegistrationService_V11", targetNamespace = "http://docs.oasis-open.org/ws-tx/wscoor/2006/06",
        wsdlLocation = "file:wsdls/wsc11/wstx-wscoor-1.1-wsdl-200702.wsdl")
public class RegistrationServiceV11
    extends Service
{

    private final static URL REGISTRATIONSERVICEV11_WSDL_LOCATION;
    private final static Logger LOGGER = Logger.getLogger(com.sun.xml.ws.tx.coord.v11.types.RegistrationServiceV11.class);

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = com.sun.xml.ws.tx.coord.v11.types.RegistrationServiceV11 .class.getResource(".");
            url = new URL(baseUrl, "wsdls/wsc11/wstx-wscoor-1.1-wsdl-200702.wsdl");
        } catch (MalformedURLException e) {
            LOGGER.warning(LocalizationMessages.WSAT_4623_FAILED_TO_CREATE_URL_FOR_WSDL());
            LOGGER.warning(e.getMessage());
        }
        REGISTRATIONSERVICEV11_WSDL_LOCATION = url;
    }

    public RegistrationServiceV11(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RegistrationServiceV11() {
        super(REGISTRATIONSERVICEV11_WSDL_LOCATION, new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "RegistrationService_V11"));
    }

    /**
     * 
     * @return
     *     returns RegistrationPortType
     */
    @WebEndpoint(name = "RegistrationPort")
    public RegistrationPortType getRegistrationPort() {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "RegistrationPort"), RegistrationPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RegistrationPortType
     */
    @WebEndpoint(name = "RegistrationPort")
    public RegistrationPortType getRegistrationPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "RegistrationPort"), RegistrationPortType.class, features);
    }

    /**
     * 
     * @return
     *     returns RegistrationCoordinatorPortType
     */
    @WebEndpoint(name = "RegistrationCoordinatorPort")
    public RegistrationCoordinatorPortType getRegistrationCoordinatorPort() {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "RegistrationCoordinatorPort"), RegistrationCoordinatorPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RegistrationCoordinatorPortType
     */
    @WebEndpoint(name = "RegistrationCoordinatorPort")
    public RegistrationCoordinatorPortType getRegistrationCoordinatorPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "RegistrationCoordinatorPort"), RegistrationCoordinatorPortType.class, features);
    }

    /**
     * 
     * @return
     *     returns RegistrationRequesterPortType
     */
    @WebEndpoint(name = "RegistrationRequesterPort")
    public RegistrationRequesterPortType getRegistrationRequesterPort() {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "RegistrationRequesterPort"), RegistrationRequesterPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RegistrationRequesterPortType
     */
    @WebEndpoint(name = "RegistrationRequesterPort")
    public RegistrationRequesterPortType getRegistrationRequesterPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "RegistrationRequesterPort"), RegistrationRequesterPortType.class, features);
    }

}
