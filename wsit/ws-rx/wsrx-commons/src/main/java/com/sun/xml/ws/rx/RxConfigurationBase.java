/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.ha.HighAvailabilityProvider;
import org.glassfish.gmbal.ManagedObjectManager;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public abstract class RxConfigurationBase implements RxConfiguration {

    private final boolean rmEnabled;
    private final boolean mcSupportEnabled;
    private final SOAPVersion soapVersion;
    private final AddressingVersion addressingVersion;
    private final boolean requestResponseDetected;
    private final ManagedObjectManager managedObjectManager;
    private final HighAvailabilityProvider haProvider;

    protected RxConfigurationBase(
            final boolean isRmEnabled,
            final boolean isMcEnabled,
            final SOAPVersion soapVersion,
            final AddressingVersion addressingVersion,
            final boolean requestResponseDetected,
            final ManagedObjectManager managedObjectManager,
            final HighAvailabilityProvider haProvider) {
        this.rmEnabled = isRmEnabled;
        this.mcSupportEnabled = isMcEnabled;

        this.soapVersion = soapVersion;
        this.addressingVersion = addressingVersion;
        this.requestResponseDetected = requestResponseDetected;
	this.managedObjectManager = managedObjectManager;
        this.haProvider = haProvider;
    }

    public boolean isReliableMessagingEnabled() {
        return rmEnabled;
    }

    public boolean isMakeConnectionSupportEnabled() {
        return mcSupportEnabled;
    }

    public SOAPVersion getSoapVersion() {
        return soapVersion;
    }

    public AddressingVersion getAddressingVersion() {
        return addressingVersion;
    }

    public boolean requestResponseOperationsDetected() {
        return requestResponseDetected;
    }

    public ManagedObjectManager getManagedObjectManager() {
	return managedObjectManager;
    }

    public HighAvailabilityProvider getHighAvailabilityProvider() {
        return haProvider;
    }
}
