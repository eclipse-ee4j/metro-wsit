/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.client;

import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import com.sun.xml.ws.transport.tcp.servicechannel.stubs.ServiceChannelWSImplService;
import javax.xml.namespace.QName;

/**
 * @author Alexey Stashok
 */
public class TCPTransportPipeFactory extends TransportTubeFactory {
    private static final QName serviceChannelServiceName = new ServiceChannelWSImplService().getServiceName();
    
    @Override
    public Tube doCreate(ClientTubeAssemblerContext context) {
        if (!TCPConstants.PROTOCOL_SCHEMA.equalsIgnoreCase(context.getAddress().getURI().getScheme())) {
            return null;
        }

        if (context.getService().getServiceName().equals(serviceChannelServiceName)) {
            return new ServiceChannelTransportPipe(context);
        }

        return new TCPTransportPipe(context);
    }
    
}
