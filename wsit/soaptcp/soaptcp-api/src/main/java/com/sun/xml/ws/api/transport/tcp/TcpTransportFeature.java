/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.transport.tcp;

import com.sun.xml.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 * TCP transport {@link javax.xml.ws.WebServiceFeature}
 *
 * @author Alexey Stashok
 */
@ManagedData
public class TcpTransportFeature extends WebServiceFeature {

    public static final String ID = "com.sun.xml.ws.transport.TcpTransportFeature";

    /**
     * This constructor is here to satisfy JAX-WS specification requirements
     */
    public TcpTransportFeature() {
        this(true);
    }

    /**
     * This constructor is here to satisfy JAX-WS specification requirements
     */
    @FeatureConstructor({
        "enabled"
    })
    public TcpTransportFeature(boolean enabled) {
        super.enabled = enabled;
    }

    @Override
    @ManagedAttribute
    public String getID() {
        return ID;
    }
}
