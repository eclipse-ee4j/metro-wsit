/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.testing;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.rx.rm.runtime.RmConfiguration;
import com.sun.xml.ws.rx.rm.runtime.RuntimeContext;
import com.sun.xml.ws.rx.util.Communicator;
import java.io.IOException;
import java.util.List;
import jakarta.xml.ws.WebServiceException;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
class PacketFilteringTube extends AbstractFilterTubeImpl {

    private static final Logger LOGGER = Logger.getLogger(PacketFilteringTube.class);
    private final boolean isClientSide;
    private final RuntimeContext rc;
    private final List<PacketFilter> filters;

    public PacketFilteringTube(PacketFilteringTube original, TubeCloner cloner) {
        super(original, cloner);
        this.isClientSide = original.isClientSide;
        this.rc = original.rc;
        this.filters = original.filters;
    }
    public PacketFilteringTube(RmConfiguration configuration, Tube tubelineHead, ClientTubelineAssemblyContext context) {
        super(tubelineHead);
        this.isClientSide = true;

        RuntimeContext.Builder rcBuilder = RuntimeContext.builder(
                configuration,
                Communicator.builder("packet-filtering-client-tube-communicator")
                .tubelineHead(super.next)
                .addressingVersion(configuration.getAddressingVersion())
                .soapVersion(configuration.getSoapVersion())
                .jaxbContext(configuration.getRuntimeVersion().getJaxbContext(configuration.getAddressingVersion()))
                .container(context.getContainer())
                .build());

        this.rc = rcBuilder.build();

        this.filters = getConfiguredFilters(context.getBinding(), rc);
    }

    public PacketFilteringTube(RmConfiguration configuration, Tube tubelineHead, ServerTubelineAssemblyContext context) {
        super(tubelineHead);
        this.isClientSide = false;

        RuntimeContext.Builder rcBuilder = RuntimeContext.builder(
                configuration,
                Communicator.builder("packet-filtering-server-tube-communicator")
                .tubelineHead(super.next)
                .addressingVersion(configuration.getAddressingVersion())
                .soapVersion(configuration.getSoapVersion())
                .jaxbContext(configuration.getRuntimeVersion().getJaxbContext(configuration.getAddressingVersion()))
                .container(context.getEndpoint().getContainer())
                .build());

        this.rc = rcBuilder.build();

        this.filters = getConfiguredFilters(context.getEndpoint().getBinding(), rc);
    }

    @Override
    public PacketFilteringTube copy(TubeCloner cloner) {
        LOGGER.entering();
        try {
            return new PacketFilteringTube(this, cloner);
        } finally {
            LOGGER.exiting();
        }
    }

    @Override
    public void preDestroy() {
        rc.close();

        super.preDestroy();
    }

    @Override
    public NextAction processRequest(Packet request) {
        if (isClientSide) {
            try {
                for (PacketFilter filter : filters) {
                    if (request != null) {
                        request = filter.filterClientRequest(request);
                    } else {
                        break;
                    }
                }
            } catch (Exception ex) {
                LOGGER.logSevereException(ex);
                if (ex instanceof RuntimeException) {
                    return doThrow(ex);
                } else {
                    return doThrow(new WebServiceException(ex));
                }
            }

            if (request == null) {
                // simulate IO error
                return doThrow(new WebServiceException(new IOException("Simulated IO error while sending a request")));
            }
        }
        return super.processRequest(request);
    }

    @Override
    public NextAction processResponse(Packet response) {
        if (!isClientSide) {
            try {
                for (PacketFilter filter : filters) {
                    if (response != null) {
                        response = filter.filterServerResponse(response);
                    } else {
                        break;
                    }
                }
            } catch (Exception ex) {
                LOGGER.logSevereException(ex);
                if (ex instanceof RuntimeException) {
                    return doThrow(ex);
                } else {
                    return doThrow(new WebServiceException(ex));
                }
            }
        }
        return super.processResponse(response);
    }

    private List<PacketFilter> getConfiguredFilters(WSBinding binding, RuntimeContext context) {
        PacketFilteringFeature pfFeature = binding.getFeature(PacketFilteringFeature.class);
        return pfFeature.createFilters(context);
    }
}
