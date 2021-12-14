/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v10.client;

import com.sun.xml.ws.tx.at.common.client.CoordinatorProxyBuilder;
import com.sun.xml.ws.tx.at.common.CoordinatorIF;
import com.sun.xml.ws.tx.at.common.WSATVersion;
import com.sun.xml.ws.tx.at.v10.types.CoordinatorPortType;
import com.sun.xml.ws.tx.at.v10.types.Notification;

import java.io.Closeable;
import java.io.IOException;

/**
 * This is the base class for building client proxy for invoking WSAT10 coordinator services..
 */
public class CoordinatorProxyBuilderImpl extends CoordinatorProxyBuilder<Notification> {
    private static final WSAT10Service service  = new WSAT10Service();

    public CoordinatorProxyBuilderImpl() {
        super(WSATVersion.v10);
    }

    @Override
    public CoordinatorIF<Notification> build() {
        return new CoordinatorProxyImpl();
    }

    class CoordinatorProxyImpl implements CoordinatorIF<Notification> {

        CoordinatorPortType port;

        CoordinatorProxyImpl() {
            port = service.getCoordinatorPortTypePort(to,getEnabledFeatures());
        }

        @Override
        public void preparedOperation(Notification parameters) {
            port.preparedOperation(parameters);
            closePort();
        }

        @Override
        public void abortedOperation(Notification parameters) {
            port.abortedOperation(parameters);
            closePort();
        }

        @Override
        public void readOnlyOperation(Notification parameters) {
            port.readOnlyOperation(parameters);
            closePort();
        }

        @Override
        public void committedOperation(Notification parameters) {
            port.committedOperation(parameters);
            closePort();
        }

        @Override
        public void replayOperation(Notification parameters) {
           port.replayOperation(parameters);
           closePort();
        }

        private void closePort() {
            try {
                ((Closeable)port).close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
