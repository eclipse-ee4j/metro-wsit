/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v10.client;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.coord.common.EndpointReferenceBuilder;
import com.sun.xml.ws.tx.coord.common.RegistrationIF;
import com.sun.xml.ws.tx.coord.common.client.RegistrationProxyBuilder;
import com.sun.xml.ws.tx.coord.v10.types.RegisterResponseType;
import com.sun.xml.ws.tx.coord.v10.types.RegisterType;
import com.sun.xml.ws.tx.coord.v10.types.RegistrationCoordinatorPortType;

import javax.xml.ws.BindingProvider;
import java.io.Closeable;
import java.io.IOException;




public class RegistrationProxyBuilderImpl extends RegistrationProxyBuilder{
    public RegistrationProxyBuilderImpl() {
        this.feature(new MemberSubmissionAddressingFeature());
    }

    protected String getDefaultCallbackAddress() {
        return WSATHelper.V10.getRegistrationRequesterAddress();
    }

    protected EndpointReferenceBuilder getEndpointReferenceBuilder() {
        return  EndpointReferenceBuilder.MemberSubmission();
    }

    public RegistrationIF<MemberSubmissionEndpointReference, RegisterType, RegisterResponseType> build() {
        super.build();
        return new RegistrationProxyImpl();
    }

    private static final RegistrationServiceV10 service = new RegistrationServiceV10();
    class RegistrationProxyImpl extends RegistrationProxyF<MemberSubmissionEndpointReference, RegisterType,RegisterResponseType,RegistrationCoordinatorPortType> {

        private RegistrationCoordinatorPortType port;


        RegistrationProxyImpl() {
            port = service.getRegistrationCoordinatorPortTypePort(to,getEnabledFeatures());
        }

        public RegistrationCoordinatorPortType getDelegate(){
            return port;
        }

        public void asyncRegister(RegisterType parameters) {
            port.registerOperation(parameters);
            closePort();
        }

        private void closePort() {
            try {
                ((Closeable)port).close();
            } catch (IOException e) {
                e.printStackTrace(); 
            }
        }

        public AddressingVersion getAddressingVersion() {
            return AddressingVersion.MEMBER;
        }
    }
}
