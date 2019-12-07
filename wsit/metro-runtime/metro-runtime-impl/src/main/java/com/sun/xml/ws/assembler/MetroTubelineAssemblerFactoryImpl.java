/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.assembler;

import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.TubelineAssembler;
import com.sun.xml.ws.api.pipe.TubelineAssemblerFactory;
import com.sun.xml.ws.api.server.ServiceDefinition;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.runtime.WsdlDocumentFilter;

/**
 * WSIT Tubeline assembler factory
 * 
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class MetroTubelineAssemblerFactoryImpl extends TubelineAssemblerFactory {

    static final MetroConfigName METRO_TUBES_CONFIG_NAME = new MetroConfigNameImpl("metro-default.xml", "metro.xml");

    @Override
    public TubelineAssembler doCreate(final BindingID bindingId) {

        return new MetroTubelineAssembler(bindingId, METRO_TUBES_CONFIG_NAME) {
            
            @Override
            protected ServerTubelineAssemblyContext createServerContext(ServerTubeAssemblerContext jaxwsContext) {
                ServerTubelineAssemblyContext context = super.createServerContext(jaxwsContext);
                // JAX-WS extension: adding metro WsdlDocumentFilter
                ServiceDefinition sd = context.getEndpoint().getServiceDefinition();
                if (sd != null) {
                    sd.addFilter(new WsdlDocumentFilter());
                }
                return context;
            }

            @Override
            protected MetroClientTubelineAssemblyContextImpl createClientContext(ClientTubeAssemblerContext jaxwsContext) {
                // JAX-WS extension: creating extended client context - it needs to have reference to SecureConversationInitiator
                return new MetroClientTubelineAssemblyContextImpl(jaxwsContext);
            }
        };
    }
}
