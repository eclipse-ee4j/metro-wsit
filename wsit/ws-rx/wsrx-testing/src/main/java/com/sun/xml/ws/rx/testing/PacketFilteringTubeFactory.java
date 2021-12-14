/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.testing;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.TubeFactory;
import com.sun.xml.ws.rx.rm.runtime.RmConfiguration;
import com.sun.xml.ws.rx.rm.runtime.RmConfigurationFactory;
import jakarta.xml.ws.WebServiceException;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class PacketFilteringTubeFactory implements TubeFactory {

    @Override
    public Tube createTube(ClientTubelineAssemblyContext context) throws WebServiceException {
        if (isPacketFilteringEnabled(context.getBinding())) {
        RmConfiguration configuration = RmConfigurationFactory.INSTANCE.createInstance(context);

            return new PacketFilteringTube(configuration, context.getTubelineHead(), context);
        } else {
            return context.getTubelineHead();
        }
    }

    @Override
    public Tube createTube(ServerTubelineAssemblyContext context) throws WebServiceException {
        if (isPacketFilteringEnabled(context.getEndpoint().getBinding())) {
            RmConfiguration configuration = RmConfigurationFactory.INSTANCE.createInstance(context);

            return new PacketFilteringTube(configuration, context.getTubelineHead(), context);
        } else {
            return context.getTubelineHead();
        }
    }

    private boolean isPacketFilteringEnabled(WSBinding binding) {
        PacketFilteringFeature pfFeature = binding.getFeature(PacketFilteringFeature.class);
        return pfFeature != null && pfFeature.isEnabled() && pfFeature.hasFilters();
    }
}
