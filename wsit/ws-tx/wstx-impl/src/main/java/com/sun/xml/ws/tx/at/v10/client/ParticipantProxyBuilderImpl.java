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

import com.sun.xml.ws.tx.at.common.client.ParticipantProxyBuilder;
import com.sun.xml.ws.tx.at.common.ParticipantIF;
import com.sun.xml.ws.tx.at.common.WSATVersion;
import com.sun.xml.ws.tx.at.v10.types.Notification;
import com.sun.xml.ws.tx.at.v10.types.ParticipantPortType;

import java.io.Closeable;
import java.io.IOException;

/**
 * This is the base class for building client proxy for invoking WSAT10 Participant services..
 */
public class ParticipantProxyBuilderImpl extends ParticipantProxyBuilder<Notification> {
    final static WSAT10Service service = new WSAT10Service();

    public ParticipantProxyBuilderImpl() {
        super(WSATVersion.v10);
    }

    @Override
    public ParticipantIF<Notification> build() {
        return new ParticipantProxyImpl();
    }

    class ParticipantProxyImpl implements ParticipantIF<Notification> {
       ParticipantPortType port;

        ParticipantProxyImpl() {
            port = service.getParticipantPortTypePort(to,getEnabledFeatures());
       }

        @Override
        public String toString() {
            return getClass().getName() + " hashcode:"+hashCode()+ " to(EndpointReference):"+to + " port:"+port;
        }

        @Override
        public void prepare(Notification parameters) {
            port.prepare(parameters);
            // do not close port as we will cache for commit or rollback
        }

        @Override
        public void commit(Notification parameters) {
            port.commit(parameters);
            closePort();
        }

        @Override
        public void rollback(Notification parameters) {
            port.rollback(parameters);
            closePort();
        }

        private void closePort() {
            try {
                ((Closeable)port).close();
            } catch (IOException e) {
                e.printStackTrace(); 
            }
        }
    }

}
