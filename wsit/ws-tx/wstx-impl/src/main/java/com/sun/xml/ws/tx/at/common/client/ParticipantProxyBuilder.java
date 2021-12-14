/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.common.client;

import com.sun.xml.ws.tx.at.common.ParticipantIF;
import com.sun.xml.ws.tx.at.common.WSATVersion;

/**
 * 
 * This is the base class for building client proxy for invoking Participant services..
 */
public abstract class ParticipantProxyBuilder<T> extends BaseProxyBuilder<T,ParticipantProxyBuilder<T>> {
    protected ParticipantProxyBuilder(WSATVersion<T> version) {
        super(version);
    }

    @Override
    protected String getDefaultCallbackAddress() {
        return version.getWSATHelper().getCoordinatorAddress();
    }

    public abstract ParticipantIF<T> build();
}
