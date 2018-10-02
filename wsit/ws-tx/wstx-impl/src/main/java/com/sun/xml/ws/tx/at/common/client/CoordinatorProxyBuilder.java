/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.common.client;

import com.sun.xml.ws.tx.at.common.CoordinatorIF;
import com.sun.xml.ws.tx.at.common.WSATVersion;

/**
 *
 * This is the base class for building client proxy for invoking coordinator services..
 */
public abstract class CoordinatorProxyBuilder<T> extends BaseProxyBuilder<T,CoordinatorProxyBuilder<T>> {

    protected CoordinatorProxyBuilder(WSATVersion<T> twsatVersion) {
        super(twsatVersion);
    }

    public abstract CoordinatorIF<T> build();

    @Override
    protected String getDefaultCallbackAddress() {
        return version.getWSATHelper().getParticipantAddress();
    }
}
