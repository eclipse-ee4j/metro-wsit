/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v11;

import com.sun.xml.ws.tx.at.WSATConstants;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.coord.common.WSATCoordinationContextBuilder;


public class WSATCoordinationContextBuilderImpl extends WSATCoordinationContextBuilder {
    @Override
    protected String getCoordinationType() {
        return WSATConstants.WSAT11_NS_URI;
    }

    @Override
    protected String getDefaultRegistrationCoordinatorAddress(){
        return WSATHelper.V11.getRegistrationCoordinatorAddress();
    }

    @Override
    protected CoordinationContextBuilderImpl newCoordinationContextBuilder() {
        CoordinationContextBuilderImpl builder = new CoordinationContextBuilderImpl();
        return builder;
    }
}
