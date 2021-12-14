/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.ha.HighAvailabilityProvider;
import com.sun.xml.ws.rx.RxConfigurationBase;
import com.sun.xml.ws.rx.mc.api.MakeConnectionSupportedFeature;
import org.glassfish.gmbal.ManagedObjectManager;

/**
 *
 */
class RmConfigurationImpl extends RxConfigurationBase implements RmConfiguration {
    private final com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature rmFeature;
    private final com.oracle.webservices.oracle_internal_api.rm.ReliableMessagingFeature internalRmFeature;
    private final RmRuntimeVersion runtimeVersion;

    RmConfigurationImpl(
            final com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature rmFeature,
            final com.oracle.webservices.oracle_internal_api.rm.ReliableMessagingFeature internalRmFeature,
            final MakeConnectionSupportedFeature mcSupportedFeature,
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

        this.rmFeature = rmFeature;
        this.internalRmFeature = internalRmFeature;
        this.runtimeVersion = (rmFeature != null) ? RmRuntimeVersion.forProtocolVersion(rmFeature.getProtocolVersion()) : null;
    }

    @Override
    public com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature getRmFeature() {
        checkState();
        return rmFeature;
    }
    
    @Override
    public @Nullable com.oracle.webservices.oracle_internal_api.rm.ReliableMessagingFeature getInternalRmFeature() {
        return internalRmFeature;
    }

    @Override
    public RmRuntimeVersion getRuntimeVersion() {
        checkState();
        return runtimeVersion;
    }

    private void checkState() {
        if (rmFeature == null || !rmFeature.isEnabled()) {
            throw new IllegalStateException("Reliable messaging feature is not enabled");
        }
    }
    
    @Override
    public String toString() {
        return "RmConfigurationImpl" + 
                "{\nrmFeature=" + rmFeature + 
                "{\ninternalRmFeature=" + internalRmFeature + 
                ",\nruntimeVersion=" + runtimeVersion + 
                "\n}";
    }
    
    
}
