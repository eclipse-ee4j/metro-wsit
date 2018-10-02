/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.runtime;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.ha.HighAvailabilityProvider;
import com.sun.xml.ws.rx.RxConfigurationBase;
import com.sun.xml.ws.rx.mc.api.MakeConnectionSupportedFeature;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature;
import org.glassfish.gmbal.ManagedObjectManager;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
class McConfigurationImpl extends RxConfigurationBase implements McConfiguration {
    private final MakeConnectionSupportedFeature mcSupportedFeature;
    private final McRuntimeVersion runtimeVersion;
    private final String uniqueEndpointId;

    McConfigurationImpl(
            final ReliableMessagingFeature rmFeature,
            final MakeConnectionSupportedFeature mcSupportedFeature,
            final String uniqueEndpointId,
            final SOAPVersion soapVersion,
            final AddressingVersion addressingVersion,
            final boolean requestResponseDetected,
            final ManagedObjectManager managedObjectManager,
            final HighAvailabilityProvider haProvider) {
        super(
                rmFeature != null && rmFeature.isEnabled(),
                mcSupportedFeature != null && mcSupportedFeature.isEnabled(),
                soapVersion,
                addressingVersion,
                requestResponseDetected,
                managedObjectManager,
                haProvider);

        this.mcSupportedFeature = mcSupportedFeature;
        this.runtimeVersion = (mcSupportedFeature != null) ? McRuntimeVersion.forProtocolVersion(mcSupportedFeature.getProtocolVersion()) : null;
        this.uniqueEndpointId = uniqueEndpointId;
    }

    public MakeConnectionSupportedFeature getFeature() {
        checkState();

        return mcSupportedFeature;
    }

    public McRuntimeVersion getRuntimeVersion() {
        checkState();

        return runtimeVersion;
    }

    public String getUniqueEndpointId() {
        checkState();

        return uniqueEndpointId;
    }

    private void checkState() {
        if (mcSupportedFeature == null || !mcSupportedFeature.isEnabled()) {
            throw new IllegalStateException("MakeConnectionSupportedFeature is not enabled");
        }
    }
}
