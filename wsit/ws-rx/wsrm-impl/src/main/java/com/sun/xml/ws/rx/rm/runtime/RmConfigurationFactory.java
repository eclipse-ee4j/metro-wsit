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

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.api.ha.HighAvailabilityProvider;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.rx.mc.api.MakeConnectionSupportedFeature;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature;
import com.sun.xml.ws.rx.util.PortUtilities;
import org.glassfish.gmbal.ManagedObjectManager;

/**
 * Reliable messaging run-time configuration factory
 *
 */
public enum RmConfigurationFactory {
    INSTANCE;

    public RmConfiguration createInstance(ServerTubelineAssemblyContext context) {
        return createInstance(
                context.getWsdlPort(),
                context.getEndpoint().getBinding(),
                context.getWrappedContext().getEndpoint().getManagedObjectManager());
    }

    public RmConfiguration createInstance(ClientTubelineAssemblyContext context) {
        return createInstance(
                context.getWsdlPort(),
                context.getBinding(),
                context.getWrappedContext().getBindingProvider().getManagedObjectManager());
    }

    private RmConfiguration createInstance(final WSDLPort wsdlPort, final WSBinding binding, final ManagedObjectManager managedObjectManager) {

        return new RmConfigurationImpl(
                binding.getFeature(com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature.class),
                binding.getFeature(com.oracle.webservices.oracle_internal_api.rm.ReliableMessagingFeature.class),
                binding.getFeature(MakeConnectionSupportedFeature.class),
                binding.getSOAPVersion(),
                binding.getAddressingVersion(),
                PortUtilities.checkForRequestResponseOperations(wsdlPort),
		managedObjectManager,
                HighAvailabilityProvider.INSTANCE);
    }

}
