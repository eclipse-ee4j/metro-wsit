/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.assembler.metro.jaxws;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.transport.tcp.SelectOptimalTransportFeature;
import com.sun.xml.ws.api.transport.tcp.TcpTransportFeature;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.TubeFactory;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import com.sun.xml.ws.transport.tcp.wsit.TCPTransportPipeFactory;

import jakarta.xml.ws.WebServiceException;

/**
 * TubeFactory implementation creating one of the standard JAX-WS RI tubes
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class TransportTubeFactory implements TubeFactory {

    @Override
    public Tube createTube(ClientTubelineAssemblyContext context) throws WebServiceException {
        if (isOptimizedTransportEnabled(context.getWsdlPort(), context.getPortInfo(), context.getBinding())) {
            return TCPTransportPipeFactory.doCreate(context.getWrappedContext(), false);
        } else {
            return context.getWrappedContext().createTransportTube();
        }
    }

    @Override
    public Tube createTube(ServerTubelineAssemblyContext context) throws WebServiceException {
        return context.getTubelineHead();
    }

    /**
     * Checks to see whether OptimizedTransport is enabled or not.
     *
     * @param port the WSDLPort object
     * @param portInfo the WSPortInfo object
     * @return true if OptimizedTransport is enabled, false otherwise
     */
    private boolean isOptimizedTransportEnabled(WSDLPort port, WSPortInfo portInfo, WSBinding binding) {
        if (port == null && portInfo == null) {
            return false;
        }

        String schema;
        if (port != null) {
            schema = port.getAddress().getURI().getScheme();
        } else {
            schema = portInfo.getEndpointAddress().getURI().getScheme();
        }

        if (TCPConstants.PROTOCOL_SCHEMA.equals(schema)) {
            // if target endpoint URI starts with TCP schema - dont check policies, just return true
            return true;
        } else if (binding == null) {
            return false;
        }

        TcpTransportFeature tcpTransportFeature = binding.getFeature(TcpTransportFeature.class);
        SelectOptimalTransportFeature optimalTransportFeature = binding.getFeature(SelectOptimalTransportFeature.class);

        return (tcpTransportFeature != null && tcpTransportFeature.isEnabled()) &&
                (optimalTransportFeature != null && optimalTransportFeature.isEnabled());
    }
}
