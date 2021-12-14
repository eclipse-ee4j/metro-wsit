/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy;

import com.sun.istack.NotNull;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeatureBuilder;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import javax.xml.namespace.QName;

/**
 *
 */
public interface RmConfigurator {
    /**
     * TODO javadoc
     */
    @NotNull QName getName();

    /**
     * TODO javadoc
     */
    boolean isCompatibleWith(RmProtocolVersion version);
    
    /**
     * TODO javadoc
     */
    @NotNull ReliableMessagingFeatureBuilder update(@NotNull ReliableMessagingFeatureBuilder builder);

    // TODO need to solve the backwards translation but it should be via a static method
    //public PolicyAssertion createFrom(ReliableMessagingFeature feature);
}
