/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v11.endpoint;

import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.api.tx.at.Transactional;
import com.sun.xml.ws.tx.coord.common.EndpointReferenceBuilder;
import com.sun.xml.ws.tx.coord.common.endpoint.BaseRegistration;
import com.sun.xml.ws.tx.coord.common.types.BaseRegisterResponseType;
import com.sun.xml.ws.tx.coord.v11.types.RegisterResponseType;
import com.sun.xml.ws.tx.coord.v11.types.RegisterType;
import com.sun.xml.ws.tx.coord.v11.XmlTypeAdapter;

import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;


public class RegistrationProxyImpl extends BaseRegistration<W3CEndpointReference, RegisterType, RegisterResponseType> {

    public RegistrationProxyImpl(WebServiceContext context) {
        super(context, Transactional.Version.WSAT11);
    }

    @Override
    protected EndpointReferenceBuilder<W3CEndpointReference> getEndpointReferenceBuilder() {
        return EndpointReferenceBuilder.W3C();
    }


    @Override
    protected BaseRegisterResponseType<W3CEndpointReference,RegisterResponseType> newRegisterResponseType() {
        return XmlTypeAdapter.adapt(new RegisterResponseType());
    }

    @Override
    protected String getCoordinatorAddress() {
        return WSATHelper.V11.getCoordinatorAddress();
    }


}
