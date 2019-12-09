/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.dev;

import com.sun.xml.ws.api.transport.tcp.TcpTransportFeature;
import com.sun.xml.ws.config.metro.SimpleFeatureReader;

/**
 *
 * @author Fabian Ritzmann
 */
public class TcpTransportFeatureReader extends SimpleFeatureReader<TcpTransportFeature> {

    @Override
    protected TcpTransportFeature createFeature(boolean enabled) {
        return new TcpTransportFeature(enabled);
    }

}
