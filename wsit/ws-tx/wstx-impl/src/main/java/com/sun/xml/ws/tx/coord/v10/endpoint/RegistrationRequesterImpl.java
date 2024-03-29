/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v10.endpoint;

import com.sun.xml.ws.tx.coord.common.endpoint.RegistrationRequester;
import com.sun.xml.ws.tx.at.WSATHelper;

import jakarta.xml.ws.WebServiceContext;

public class RegistrationRequesterImpl extends RegistrationRequester {
    public RegistrationRequesterImpl(WebServiceContext m_context) {
        super(m_context);
    }

    @Override
    protected WSATHelper getWSATHelper() {
        return WSATHelper.V10;
    }
}
