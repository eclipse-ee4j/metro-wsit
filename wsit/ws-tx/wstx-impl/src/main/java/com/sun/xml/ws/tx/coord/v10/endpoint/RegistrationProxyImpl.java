/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v10.endpoint;

import com.sun.xml.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.api.tx.at.Transactional;
import com.sun.xml.ws.tx.coord.common.EndpointReferenceBuilder;
import com.sun.xml.ws.tx.coord.common.endpoint.BaseRegistration;
import com.sun.xml.ws.tx.coord.common.types.BaseRegisterResponseType;
import com.sun.xml.ws.tx.coord.v10.types.RegisterResponseType;
import com.sun.xml.ws.tx.coord.v10.types.RegisterType;
import com.sun.xml.ws.tx.coord.v10.XmlTypeAdapter;

import jakarta.xml.ws.WebServiceContext;


public class RegistrationProxyImpl extends BaseRegistration<MemberSubmissionEndpointReference, RegisterType, RegisterResponseType> {

    public RegistrationProxyImpl(WebServiceContext context) {
        super(context, Transactional.Version.WSAT10);
    }

    @Override
    protected EndpointReferenceBuilder<MemberSubmissionEndpointReference> getEndpointReferenceBuilder() {
        return EndpointReferenceBuilder.MemberSubmission();
    }

    @Override
    protected BaseRegisterResponseType<MemberSubmissionEndpointReference,RegisterResponseType> newRegisterResponseType() {
        return XmlTypeAdapter.adapt(new RegisterResponseType());
    }

    @Override
    protected String getCoordinatorAddress() {
        return WSATHelper.V10.getCoordinatorAddress();
    }


}
