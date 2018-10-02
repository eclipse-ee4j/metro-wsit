/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v10;

import com.sun.xml.ws.tx.at.WSATConstants;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.coord.common.WSATCoordinationContextBuilder;


public class WSATCoordinationContextBuilderImpl extends WSATCoordinationContextBuilder {


    @Override
    protected CoordinationContextBuilderImpl newCoordinationContextBuilder() {
        return new CoordinationContextBuilderImpl();
    }

    @Override
    protected String getCoordinationType() {
        return WSATConstants.HTTP_SCHEMAS_XMLSOAP_ORG_WS_2004_10_WSAT;
    }

    @Override
    protected String getDefaultRegistrationCoordinatorAddress() {
        return WSATHelper.V10.getRegistrationCoordinatorAddress();
    }

}
