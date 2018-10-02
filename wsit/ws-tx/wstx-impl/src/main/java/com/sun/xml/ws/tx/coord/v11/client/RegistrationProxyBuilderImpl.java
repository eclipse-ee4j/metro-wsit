/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v11.client;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.coord.common.EndpointReferenceBuilder;
import com.sun.xml.ws.tx.coord.common.RegistrationIF;
import com.sun.xml.ws.tx.coord.common.client.RegistrationProxyBuilder;
import com.sun.xml.ws.tx.coord.v11.types.RegisterResponseType;
import com.sun.xml.ws.tx.coord.v11.types.RegisterType;
import com.sun.xml.ws.tx.coord.v11.types.RegistrationCoordinatorPortType;

import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.wsaddressing.W3CEndpointReference;


public class RegistrationProxyBuilderImpl extends RegistrationProxyBuilder{

    public RegistrationProxyBuilderImpl() {
        super();
        this.feature(new AddressingFeature());
    }

    public RegistrationIF<W3CEndpointReference, RegisterType,RegisterResponseType> build() {
        super.build();
        return new RegistrationProxyImpl();
    }

    protected String getDefaultCallbackAddress() {
        return WSATHelper.V11.getRegistrationRequesterAddress();
    }

    protected EndpointReferenceBuilder getEndpointReferenceBuilder() {
        return EndpointReferenceBuilder.W3C();
    }

    public class RegistrationProxyImpl extends RegistrationProxyF<W3CEndpointReference, RegisterType,RegisterResponseType,RegistrationCoordinatorPortType> {


        private RegistrationServiceV11 service = new RegistrationServiceV11();
        private RegistrationCoordinatorPortType port;

        RegistrationProxyImpl() {
            port = service.getRegistrationCoordinatorPort(to,getEnabledFeatures());
        }

         public RegistrationCoordinatorPortType getDelegate(){
            return port;
        }

        public void asyncRegister(RegisterType parameters) {
            port.registerOperation(parameters);
        }

        public AddressingVersion getAddressingVersion() {
            return AddressingVersion.W3C;
        }
    }
}
