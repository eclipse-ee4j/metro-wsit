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

import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.TubeFactory;
import com.sun.xml.ws.rx.mc.api.MakeConnectionSupportedFeature;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature;
import javax.xml.ws.WebServiceException;

/**
 * This factory class is responsible for instantiating RX tubes based on 
 * the actual configuration of RX-related web services features.
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 *
 * @see ReliableMessagingFeature
 * @see MakeConnectionSupportedFeature
 */
public final class McTubeFactory implements TubeFactory {
    /**
     * Adds RM tube to the client-side tubeline, depending on whether RM is enabled or not.
     * 
     * @param context Metro client tubeline assembler context
     * @return new tail of the client-side tubeline
     */
    public Tube createTube(ClientTubelineAssemblyContext context) throws WebServiceException {
        McConfiguration configuration = McConfigurationFactory.INSTANCE.createInstance(context);

        if (configuration.isMakeConnectionSupportEnabled()) {
            return new McClientTube(configuration, context.getTubelineHead(), context.getContainer());
        }

        return context.getTubelineHead();
    }

    /**
     * Adds RM tube to the service-side tubeline, depending on whether RM is enabled or not.
     * 
     * @param context Metro service tubeline assembler context
     * @return new head of the service-side tubeline
     */
    public Tube createTube(ServerTubelineAssemblyContext context) throws WebServiceException {
        McConfiguration configuration = McConfigurationFactory.INSTANCE.createInstance(context);

        if (configuration.isMakeConnectionSupportEnabled()) {
            return new McServerTube(configuration, context.getTubelineHead(), context.getEndpoint().getContainer());
        }
        
        return context.getTubelineHead();
    }
}
