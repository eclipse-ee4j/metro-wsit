/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.runtime;

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
 *
 */
public enum McConfigurationFactory {
    INSTANCE;

    public McConfiguration createInstance(ServerTubelineAssemblyContext context) {
        final String uniqueEndpointId = context.getEndpoint().getServiceName() + "::" + context.getEndpoint().getPortName();
        return createInstance(uniqueEndpointId, context.getWsdlPort(), context.getEndpoint().getBinding(), context.getWrappedContext().getEndpoint().getManagedObjectManager(), HighAvailabilityProvider.INSTANCE);
    }

    public McConfiguration createInstance(ClientTubelineAssemblyContext context) {
        return createInstance(context.getAddress().getURI().toString(), context.getWsdlPort(), context.getBinding(), context.getWrappedContext().getBindingProvider().getManagedObjectManager(), HighAvailabilityProvider.INSTANCE);
    }

    private McConfiguration createInstance(final String uniqueEndpointId, final WSDLPort wsdlPort, final WSBinding binding, final ManagedObjectManager managedObjectManager, final HighAvailabilityProvider haProvider) {

        return new McConfigurationImpl(
                binding.getFeature(ReliableMessagingFeature.class),
                binding.getFeature(MakeConnectionSupportedFeature.class),
                uniqueEndpointId,
                binding.getSOAPVersion(),
                binding.getAddressingVersion(),
                PortUtilities.checkForRequestResponseOperations(wsdlPort),
		managedObjectManager,
                haProvider);
    }

}
