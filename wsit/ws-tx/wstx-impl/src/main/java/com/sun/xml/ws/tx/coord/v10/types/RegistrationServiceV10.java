/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v10.types;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.tx.at.localization.LocalizationMessages;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;

@WebServiceClient(name = "RegistrationService_V10", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", wsdlLocation = "file:wsdls/wsc10/wscoor.wsdl")
public class RegistrationServiceV10
    extends Service
{

    private final static URL REGISTRATIONSERVICEV10_WSDL_LOCATION;
    private final static Logger LOGGER = Logger.getLogger(com.sun.xml.ws.tx.coord.v10.types.RegistrationServiceV10 .class);

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = com.sun.xml.ws.tx.coord.v10.types.RegistrationServiceV10 .class.getResource(".");
            url = new URL(baseUrl, "file:wsdls/wsc10/wscoor.wsdl");
        } catch (MalformedURLException e) {
            LOGGER.warning(LocalizationMessages.WSAT_4622_FAILED_TO_CREATE_URL_FOR_WSDL());
            LOGGER.warning(e.getMessage());
        }
        REGISTRATIONSERVICEV10_WSDL_LOCATION = url;
    }

    public RegistrationServiceV10(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RegistrationServiceV10() {
        super(REGISTRATIONSERVICEV10_WSDL_LOCATION, new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "RegistrationService_V10"));
    }

    /**
     *
     * @return
     *     returns RegistrationRequesterPortType
     */
    @WebEndpoint(name = "RegistrationRequesterPortTypePort")
    public RegistrationRequesterPortType getRegistrationRequesterPortTypePort() {
        return super.getPort(new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "RegistrationRequesterPortTypePort"), RegistrationRequesterPortType.class);
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RegistrationRequesterPortType
     */
    @WebEndpoint(name = "RegistrationRequesterPortTypePort")
    public RegistrationRequesterPortType getRegistrationRequesterPortTypePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "RegistrationRequesterPortTypePort"), RegistrationRequesterPortType.class, features);
    }

    /**
     *
     * @return
     *     returns RegistrationPortTypeRPC
     */
    @WebEndpoint(name = "RegistrationPortTypeRPCPort")
    public RegistrationPortTypeRPC getRegistrationPortTypeRPCPort() {
        return super.getPort(new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "RegistrationPortTypeRPCPort"), RegistrationPortTypeRPC.class);
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RegistrationPortTypeRPC
     */
    @WebEndpoint(name = "RegistrationPortTypeRPCPort")
    public RegistrationPortTypeRPC getRegistrationPortTypeRPCPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "RegistrationPortTypeRPCPort"), RegistrationPortTypeRPC.class, features);
    }

    /**
     *
     * @return
     *     returns RegistrationCoordinatorPortType
     */
    @WebEndpoint(name = "RegistrationCoordinatorPortTypePort")
    public RegistrationCoordinatorPortType getRegistrationCoordinatorPortTypePort() {
        return super.getPort(new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "RegistrationCoordinatorPortTypePort"), RegistrationCoordinatorPortType.class);
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RegistrationCoordinatorPortType
     */
    @WebEndpoint(name = "RegistrationCoordinatorPortTypePort")
    public RegistrationCoordinatorPortType getRegistrationCoordinatorPortTypePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "RegistrationCoordinatorPortTypePort"), RegistrationCoordinatorPortType.class, features);
    }

}
