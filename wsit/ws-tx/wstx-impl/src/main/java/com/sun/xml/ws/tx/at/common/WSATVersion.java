/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.ws.tx.coord.common.EndpointReferenceBuilder;

import javax.xml.ws.WebServiceFeature;

/**
 *
 * A class absorbs differences of WSAT versions.
 */
public abstract class WSATVersion<T>{
    public final static WSATVersion<com.sun.xml.ws.tx.at.v10.types.Notification> v10 = new WSATVersion10();
    public final static WSATVersion<com.sun.xml.ws.tx.at.v11.types.Notification> v11 = new WSATVersion11();

    private Transactional.Version version;
    protected AddressingVersion addressingVersion;
    protected SOAPVersion soapVersion;

    public static WSATVersion getInstance(Transactional.Version version){
    if (Transactional.Version.WSAT10 == version||Transactional.Version.DEFAULT == version) {
            return v10;
        }else if (Transactional.Version.WSAT11 == version || Transactional.Version.WSAT12 == version) {
            return v11;
        } else {
            throw new IllegalArgumentException(version + "is not a supported ws-at version");
        }
    }

     WSATVersion(Transactional.Version version) {
        this.version = version;
    }

     public abstract WSATHelper getWSATHelper();

    public AddressingVersion getAddressingVersion() {
        return addressingVersion;
    }

    public SOAPVersion getSOPAVersion() {
        return soapVersion;
    }

    public Transactional.Version getVersion() {
        return version;
    }

    public abstract CoordinatorProxyBuilder<T> newCoordinatorProxyBuilder();
    public abstract ParticipantProxyBuilder<T> newParticipantProxyBuilder();
    public abstract NotificationBuilder<T> newNotificationBuilder();
    public abstract EndpointReferenceBuilder newEndpointReferenceBuilder();
    public abstract WebServiceFeature newAddressingFeature();
}
