/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v11;

import com.sun.xml.ws.tx.coord.common.WSATCoordinationContextBuilder;
import com.sun.xml.ws.tx.coord.common.WSCBuilderFactory;
import com.sun.xml.ws.tx.coord.common.client.RegistrationProxyBuilder;
import com.sun.xml.ws.tx.coord.common.client.RegistrationMessageBuilder;
import com.sun.xml.ws.tx.coord.v11.client.RegistrationMessageBuilderImpl;
import com.sun.xml.ws.tx.coord.v11.client.RegistrationProxyBuilderImpl;


public class WSCBuilderFactoryImpl extends WSCBuilderFactory {
    @Override
    public WSATCoordinationContextBuilder newWSATCoordinationContextBuilder() {
        return new WSATCoordinationContextBuilderImpl();
    }

    @Override
    public RegistrationProxyBuilder newRegistrationProxyBuilder() {
        return new RegistrationProxyBuilderImpl();
    }

    @Override
    public RegistrationMessageBuilder newWSATRegistrationRequestBuilder() {
        return new RegistrationMessageBuilderImpl();
    }

}
