/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v11.client;

import com.sun.xml.ws.tx.at.common.client.CoordinatorProxyBuilder;
import com.sun.xml.ws.tx.at.common.CoordinatorIF;
import com.sun.xml.ws.tx.at.common.WSATVersion;
import com.sun.xml.ws.tx.at.v11.types.CoordinatorPortType;
import com.sun.xml.ws.tx.at.v11.types.Notification;

import jakarta.xml.ws.WebServiceException;

/**
 *
 * This is the base class for building client proxy for invoking WSAT11 coordinator services.. 
 */
public class CoordinatorProxyBuilderImpl extends CoordinatorProxyBuilder<Notification> {


    public CoordinatorProxyBuilderImpl() {
        super(WSATVersion.v11);
    }

    @Override
    public CoordinatorIF<Notification> build() {
        return new CoordinatorProxyImpl();
    }

    class CoordinatorProxyImpl implements CoordinatorIF<Notification> {

        CoordinatorPortType port;
        WSAT11Service service  = new WSAT11Service();

        CoordinatorProxyImpl() {
            port = service.getCoordinatorPort(to,getEnabledFeatures());
        }

        @Override
        public void preparedOperation(Notification parameters) {
            port.preparedOperation(parameters);
        }

        @Override
        public void abortedOperation(Notification parameters) {
            port.abortedOperation(parameters);
        }

        @Override
        public void readOnlyOperation(Notification parameters) {
            port.readOnlyOperation(parameters);
        }

        @Override
        public void committedOperation(Notification parameters) {
            port.committedOperation(parameters);
        }

        @Override
        public void replayOperation(Notification parameters) {
           throw new WebServiceException("replayOperation is not supported by WS-AT 1.1 and 1.2");
        }
    }
}
