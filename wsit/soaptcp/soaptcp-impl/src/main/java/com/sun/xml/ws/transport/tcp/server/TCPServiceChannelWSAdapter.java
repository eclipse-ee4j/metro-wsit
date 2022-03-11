/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.server;

import com.sun.xml.ws.api.DistributedPropertySet;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.transport.tcp.util.ChannelContext;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import com.sun.xml.ws.transport.tcp.util.WSTCPException;
import java.io.IOException;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

/**
 * @author Alexey Stashok
 */
public final class TCPServiceChannelWSAdapter extends TCPAdapter {
    private final WSTCPAdapterRegistry adapterRegistry;

    public TCPServiceChannelWSAdapter(@NotNull final String name,
            @NotNull final String urlPattern,
    @NotNull final WSEndpoint endpoint,
    @NotNull final WSTCPAdapterRegistry adapterRegistry) {
        super(name, urlPattern, endpoint);
        this.adapterRegistry = adapterRegistry;
    }

    @Override
    protected TCPAdapter.TCPToolkit createToolkit() {
        return new ServiceChannelTCPToolkit();
    }

    class ServiceChannelTCPToolkit extends TCPAdapter.TCPToolkit {
        private final ServiceChannelWSSatellite serviceChannelWSSatellite;

        public ServiceChannelTCPToolkit() {
            serviceChannelWSSatellite = new ServiceChannelWSSatellite(TCPServiceChannelWSAdapter.this);
        }

        // Taking Codec from virtual connection's ChannelContext
        @Override
        protected @NotNull Codec getCodec(@NotNull final ChannelContext context) {
            return codec;
        }

        @Override
        protected void handle(@NotNull final TCPConnectionImpl con) throws IOException, WSTCPException {
            serviceChannelWSSatellite.setConnectionContext(con.getChannelContext());
            super.handle(con);
        }

        @Override
        public void addCustomPacketSattellites(@NotNull final Packet packet) {
            super.addCustomPacketSattellites(packet);
            packet.addSatellite(serviceChannelWSSatellite);
        }
    };


    public static final class ServiceChannelWSSatellite extends DistributedPropertySet {
        private final TCPServiceChannelWSAdapter serviceChannelWSAdapter;
        private ChannelContext channelContext;

        ServiceChannelWSSatellite(@NotNull final TCPServiceChannelWSAdapter serviceChannelWSAdapter) {
            this.serviceChannelWSAdapter = serviceChannelWSAdapter;
        }

        protected void setConnectionContext(final ChannelContext channelContext) {
            this.channelContext = channelContext;
        }

        @com.sun.xml.ws.api.PropertySet.Property(TCPConstants.ADAPTER_REGISTRY)
        public @NotNull WSTCPAdapterRegistry getAdapterRegistry() {
            return serviceChannelWSAdapter.adapterRegistry;
        }

        @com.sun.xml.ws.api.PropertySet.Property(TCPConstants.CHANNEL_CONTEXT)
        public ChannelContext getChannelContext() {
            return channelContext;
        }

        private static final PropertyMap model;
        static {
            model = parse(ServiceChannelWSSatellite.class);
        }

        @Override
        public DistributedPropertySet.PropertyMap getPropertyMap() {
            return model;
        }

        // TODO - remove when these are added to DistributedPropertySet
        public SOAPMessage getSOAPMessage() throws SOAPException {
           throw new UnsupportedOperationException();
        }

        public void setSOAPMessage(SOAPMessage soap) {
           throw new UnsupportedOperationException();
        }
    }
}
