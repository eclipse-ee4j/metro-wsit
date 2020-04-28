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
import com.sun.xml.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.api.tx.at.Transactional;
import com.sun.xml.ws.tx.at.common.client.CoordinatorProxyBuilder;
import com.sun.xml.ws.tx.at.common.client.ParticipantProxyBuilder;
import com.sun.xml.ws.tx.at.v10.client.CoordinatorProxyBuilderImpl;
import com.sun.xml.ws.tx.at.v10.NotificationBuilderImpl;
import com.sun.xml.ws.tx.at.v10.client.ParticipantProxyBuilderImpl;
import com.sun.xml.ws.tx.at.v10.types.Notification;
import com.sun.xml.ws.tx.coord.common.EndpointReferenceBuilder;

import jakarta.xml.ws.WebServiceFeature;


class WSATVersion10 extends WSATVersion<Notification>  {
    WSATVersion10() {
        super(Transactional.Version.WSAT10);
        addressingVersion =  AddressingVersion.MEMBER;
        soapVersion = SOAPVersion.SOAP_11;
    }

    @Override
    public WSATHelper getWSATHelper() {
        return WSATHelper.V10;
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
    public EndpointReferenceBuilder newEndpointReferenceBuilder() {
        return EndpointReferenceBuilder.MemberSubmission();
    }

    @Override
    public WebServiceFeature newAddressingFeature() {
        return new MemberSubmissionAddressingFeature();
    }

    @Override
    public NotificationBuilder newNotificationBuilder() {
        return new NotificationBuilderImpl();
    }

}
