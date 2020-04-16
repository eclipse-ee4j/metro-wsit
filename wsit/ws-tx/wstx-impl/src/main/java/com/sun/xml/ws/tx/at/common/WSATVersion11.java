/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.common;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.api.tx.at.Transactional;
import com.sun.xml.ws.tx.at.common.client.CoordinatorProxyBuilder;
import com.sun.xml.ws.tx.at.common.client.ParticipantProxyBuilder;
import com.sun.xml.ws.tx.at.v11.client.CoordinatorProxyBuilderImpl;
import com.sun.xml.ws.tx.at.v11.NotificationBuilderImpl;
import com.sun.xml.ws.tx.at.v11.client.ParticipantProxyBuilderImpl;
import com.sun.xml.ws.tx.at.v11.types.Notification;
import com.sun.xml.ws.tx.coord.common.EndpointReferenceBuilder;

import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.soap.AddressingFeature;


class WSATVersion11 extends WSATVersion<Notification>  {

    WSATVersion11() {
        super(Transactional.Version.WSAT11);
        addressingVersion =  AddressingVersion.W3C;
        soapVersion = SOAPVersion.SOAP_11;
    }

    @Override
    public WSATHelper getWSATHelper() {
        return WSATHelper.V11;
    }

    @Override
    public CoordinatorProxyBuilder<Notification> newCoordinatorProxyBuilder() {
        return new CoordinatorProxyBuilderImpl();
    }

    @Override
    public ParticipantProxyBuilder<Notification> newParticipantProxyBuilder() {
        return new ParticipantProxyBuilderImpl();
    }

    @Override
    public NotificationBuilder<Notification> newNotificationBuilder() {
        return new NotificationBuilderImpl();
    }

    @Override
    public EndpointReferenceBuilder newEndpointReferenceBuilder() {
        return EndpointReferenceBuilder.W3C();
    }

    @Override
    public WebServiceFeature newAddressingFeature() {
        return new AddressingFeature();
    }
}
