/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.TubeFactory;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature;
import java.util.logging.Level;
import javax.xml.ws.WebServiceException;

/**
 * This factory class is responsible for instantiating RM tubes based on
 * the actual configuration of a RM web services feature.
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 *
 * @see ReliableMessagingFeature
 */
public final class RmTubeFactory implements TubeFactory {

    private static final Logger LOGGER = Logger.getLogger(RmTubeFactory.class);

    /**
     * Adds RM tube to the client-side tubeline, depending on whether RM is enabled or not.
     * 
     * @param context Metro client tubeline assembler context
     * @return new tail of the client-side tubeline
     */
    public Tube createTube(ClientTubelineAssemblyContext context) throws WebServiceException {
        RmConfiguration configuration = RmConfigurationFactory.INSTANCE.createInstance(context);

        if (configuration.isReliableMessagingEnabled()) {
            if (LOGGER.isLoggable(Level.CONFIG)) {
                LOGGER.config("Creating client-side RM tube with configuration: " + configuration.toString());
            }

            return new ClientTube(configuration, context);
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
        RmConfiguration configuration = RmConfigurationFactory.INSTANCE.createInstance(context);

        if (configuration.isReliableMessagingEnabled()) {
            if (LOGGER.isLoggable(Level.CONFIG)) {
                LOGGER.config("Creating endpoint-side RM tube with configuration: " + configuration.toString());
            }

            return new ServerTube(configuration, context);
        }

        return context.getTubelineHead();
    }
}
