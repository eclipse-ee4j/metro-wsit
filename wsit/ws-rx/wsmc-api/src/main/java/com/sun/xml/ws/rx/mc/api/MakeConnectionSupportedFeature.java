/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.api;

import com.sun.xml.ws.api.FeatureConstructor;
import com.sun.xml.ws.api.ha.StickyFeature;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
@ManagedData
public class MakeConnectionSupportedFeature extends WebServiceFeature implements StickyFeature {

    public static final String ID = "http://docs.oasis-open.org/ws-rx/wsmc/";
    /**
     * Default response retrieval timeout value [milliseconds]
     */
    public static final long DEFAULT_RESPONSE_RETRIEVAL_TIMEOUT = 600000;
    /**
     * Default base interval between two subsequent MakeConnection requests [milliseconds]
     */
    public static final long DEFAULT_MAKE_CONNECTION_REQUEST_INTERVAL = 2000;

    private final long responseRetrievalTimeout;
    private final long mcRequestBaseInterval;

    /**
     * This constructor is here to satisfy JAX-WS specification requirements
     */
    public MakeConnectionSupportedFeature() {
        this(
                true,
                DEFAULT_MAKE_CONNECTION_REQUEST_INTERVAL,
                DEFAULT_RESPONSE_RETRIEVAL_TIMEOUT);
    }

    /**
     * This constructor is here to satisfy JAX-WS specification requirements
     */
    @FeatureConstructor({
        "enabled"
    })
    public MakeConnectionSupportedFeature(boolean enabled) {
        this(
                enabled,
                DEFAULT_MAKE_CONNECTION_REQUEST_INTERVAL,
                DEFAULT_RESPONSE_RETRIEVAL_TIMEOUT);
    }

    MakeConnectionSupportedFeature(
            boolean enabled,
            long mcRequestBaseInterval,
            long responseRetrievalTimeout) {

        super.enabled = enabled;

        this.mcRequestBaseInterval = mcRequestBaseInterval;
        this.responseRetrievalTimeout = responseRetrievalTimeout;
    }

    @Override
    @ManagedAttribute
    public String getID() {
        return ID;
    }

    /**
     * Specifies which WS-MC version protocol SOAP messages and SOAP message headers should
     * be used for communication between MC source and MC destination
     *
     * @return WS-MC protocol version currently configured for the feature.
     */
    public McProtocolVersion getProtocolVersion() {
        return McProtocolVersion.WSMC200702;
    }

    /**
     * Specifies a timeout for consecutive unsuccessfull response retrievals.
     *
     * @return currently configured timeout for consecutive unsuccessfull response 
     *         retrievals. If not set explicitly, the default value is specified by
     *         {@link #DEFAULT_RESPONSE_RETRIEVAL_TIMEOUT} constant.
     */
    public long getResponseRetrievalTimeout() {
        return responseRetrievalTimeout;
    }

    /**
     * Specifies a base interval between two consecutive MakeConnection requests
     *
     * @return currently configured base interval (in milliseconds) of time that
     *         must pass between two consecutive MakeConnection request messages.
     *         If not set explicitly, the default value is specified by
     *         {@link #DEFAULT_MAKE_CONNECTION_REQUEST_INTERVAL} constant.
     */
    public long getBaseMakeConnectionRequetsInterval() {
        return mcRequestBaseInterval;
    }
}
