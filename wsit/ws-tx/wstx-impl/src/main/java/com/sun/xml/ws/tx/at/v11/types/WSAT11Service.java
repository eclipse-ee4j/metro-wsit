/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v11.types;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.tx.at.localization.LocalizationMessages;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;

@WebServiceClient(name = "WSAT11Service", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06",
        wsdlLocation = "file:wsdls/wsat11/wstx-wsat-1.1-wsdl-200702.wsdl")
public class WSAT11Service
    extends Service
{

    private final static URL WSAT11SERVICE_WSDL_LOCATION;
    private final static Logger LOGGER = Logger.getLogger(com.sun.xml.ws.tx.at.v11.types.WSAT11Service.class);

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = com.sun.xml.ws.tx.at.v11.types.WSAT11Service.class.getResource(".");
            url = new URL(baseUrl, "file:wsdls/wsat11/wstx-wsat-1.1-wsdl-200702.wsdl");
        } catch (MalformedURLException e) {
            LOGGER.warning(LocalizationMessages.WSAT_4619_FAILED_TO_CREATE_URL_FOR_WSDL());
            LOGGER.warning(e.getMessage());
        }
        WSAT11SERVICE_WSDL_LOCATION = url;
    }

    public WSAT11Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WSAT11Service() {
        super(WSAT11SERVICE_WSDL_LOCATION, new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "WSAT11Service"));
    }

    /**
     *
     * @return
     *     returns CoordinatorPortType
     */
    @WebEndpoint(name = "CoordinatorPort")
    public CoordinatorPortType getCoordinatorPort() {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "CoordinatorPort"), CoordinatorPortType.class);
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CoordinatorPortType
     */
    @WebEndpoint(name = "CoordinatorPort")
    public CoordinatorPortType getCoordinatorPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "CoordinatorPort"), CoordinatorPortType.class, features);
    }

    /**
     *
     * @return
     *     returns ParticipantPortType
     */
    @WebEndpoint(name = "ParticipantPort")
    public ParticipantPortType getParticipantPort() {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "ParticipantPort"), ParticipantPortType.class);
    }

    /**
     *
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ParticipantPortType
     */
    @WebEndpoint(name = "ParticipantPort")
    public ParticipantPortType getParticipantPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "ParticipantPort"), ParticipantPortType.class, features);
    }

}
