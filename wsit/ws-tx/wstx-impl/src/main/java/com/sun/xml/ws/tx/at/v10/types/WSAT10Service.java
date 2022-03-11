/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v10.types;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.tx.at.localization.LocalizationMessages;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;

@WebServiceClient(name = "WSAT10Service", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat",
        wsdlLocation = "file:wsdls/wsat10/wsat.wsdl")
public class WSAT10Service
    extends Service
{

    private final static URL WSAT10SERVICE_WSDL_LOCATION;
    private final static Logger LOGGER = Logger.getLogger(com.sun.xml.ws.tx.at.v10.types.WSAT10Service.class);

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = com.sun.xml.ws.tx.at.v10.types.WSAT10Service.class.getResource(".");
            url = new URL(baseUrl, "file:wsdls/wsat10/wsat.wsdl");
        } catch (MalformedURLException e) {
            LOGGER.warning(LocalizationMessages.WSAT_4618_FAILED_TO_CREATE_URL_FOR_WSDL());
            LOGGER.warning(e.getMessage());
        }
        WSAT10SERVICE_WSDL_LOCATION = url;
    }

    public WSAT10Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WSAT10Service() {
        super(WSAT10SERVICE_WSDL_LOCATION, new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "WSAT10Service"));
    }

    /**
     *
     * @return
     *     returns CoordinatorPortType
     */
    @WebEndpoint(name = "CoordinatorPortTypePort")
    public CoordinatorPortType getCoordinatorPortTypePort() {
        return super.getPort(new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "CoordinatorPortTypePort"), CoordinatorPortType.class);
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CoordinatorPortType
     */
    @WebEndpoint(name = "CoordinatorPortTypePort")
    public CoordinatorPortType getCoordinatorPortTypePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "CoordinatorPortTypePort"), CoordinatorPortType.class, features);
    }

    /**
     *
     * @return
     *     returns ParticipantPortType
     */
    @WebEndpoint(name = "ParticipantPortTypePort")
    public ParticipantPortType getParticipantPortTypePort() {
        return super.getPort(new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "ParticipantPortTypePort"), ParticipantPortType.class);
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ParticipantPortType
     */
    @WebEndpoint(name = "ParticipantPortTypePort")
    public ParticipantPortType getParticipantPortTypePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "ParticipantPortTypePort"), ParticipantPortType.class, features);
    }

}
