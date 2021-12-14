/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.transport.tcp;

import com.sun.xml.ws.api.FeatureConstructor;
import jakarta.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 * Optimized transport {@link jakarta.xml.ws.WebServiceFeature}
 *
 * @author Alexey Stashok
 */
@ManagedData
public class SelectOptimalTransportFeature extends WebServiceFeature {

    public static final String ID = "com.sun.xml.ws.transport.SelectOptimalTransportFeature";

    /**
     * This enumeration defines an optimized transport list
     */
    public enum Transport {

        /**
         * SOAP/TCP transport
         */
        TCP;

        /**
         * Provides a default optimized transport value.
         *
         * @return a default optimized transport value. Currently returns {@link #TCP}.
         */
        public static Transport getDefault() {
            return Transport.TCP;
        }
    }

    // Optimized transport to be used
    private Transport transport;

    /**
     * This constructor is here to satisfy JAX-WS specification requirements
     */
    public SelectOptimalTransportFeature() {
        this(true);
    }

    /**
     * This constructor is here to satisfy JAX-WS specification requirements
     */
    @FeatureConstructor({
        "enabled"
    })
    public SelectOptimalTransportFeature(boolean enabled) {
        this(enabled, Transport.getDefault());
    }

    @FeatureConstructor({
        "enabled",
        "transport"
    })
    public SelectOptimalTransportFeature(boolean enabled, Transport transport) {
        super.enabled = enabled;
        this.transport = transport;
    }


    @Override
    @ManagedAttribute
    public String getID() {
        return ID;
    }

    @ManagedAttribute
    public Transport getTransport() {
        return transport;
    }
}
