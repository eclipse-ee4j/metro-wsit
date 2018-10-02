/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v11.client;

import com.sun.xml.ws.tx.at.common.client.ParticipantProxyBuilder;
import com.sun.xml.ws.tx.at.common.ParticipantIF;
import com.sun.xml.ws.tx.at.common.WSATVersion;
import com.sun.xml.ws.tx.at.v11.types.Notification;
import com.sun.xml.ws.tx.at.v11.types.ParticipantPortType;


/**
 * 
 * This is the base class for building client proxy for invoking WSAT11 Participant services..
 */
public class ParticipantProxyBuilderImpl extends ParticipantProxyBuilder<Notification> {

    public ParticipantProxyBuilderImpl() {
      super(WSATVersion.v11);
    }

    @Override
    public ParticipantIF<Notification> build() {
        return new ParticipantProxyImpl();
    }

    class ParticipantProxyImpl implements ParticipantIF<Notification> {
       ParticipantPortType port;
       WSAT11Service service = new WSAT11Service();

        ParticipantProxyImpl() {
            port = service.getParticipantPort(to,getEnabledFeatures());
        }

        public void prepare(Notification parameters) {
            port.prepareOperation(parameters);
        }

        public void commit(Notification parameters) {
            port.commitOperation(parameters);
        }

        public void rollback(Notification parameters) {
            port.rollbackOperation(parameters);
        }
    }

}
