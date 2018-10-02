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

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class MakeConnectionSupportedFeatureBuilder {

    public static MakeConnectionSupportedFeatureBuilder getBuilder() {
        return new MakeConnectionSupportedFeatureBuilder();
    }

    private long mcRequestBaseInterval = MakeConnectionSupportedFeature.DEFAULT_MAKE_CONNECTION_REQUEST_INTERVAL;
    private long responseRetrievalTimeout = MakeConnectionSupportedFeature.DEFAULT_RESPONSE_RETRIEVAL_TIMEOUT;

    public MakeConnectionSupportedFeatureBuilder mcRequestBaseInterval(long value) {
        this.mcRequestBaseInterval = value;

        return this;
    }

    public MakeConnectionSupportedFeatureBuilder responseRetrievalTimeout(long value) {
        this.responseRetrievalTimeout = value;

        return this;
    }

    public MakeConnectionSupportedFeature build() {
        return new MakeConnectionSupportedFeature(
                true,
                mcRequestBaseInterval,
                responseRetrievalTimeout);
    }
}
