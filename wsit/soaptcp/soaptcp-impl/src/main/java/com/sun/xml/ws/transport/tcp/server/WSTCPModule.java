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

import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.transport.tcp.servicechannel.ServiceChannelWSImpl;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

/**
 * WSTCPModule. Singlton class, which contains SOAP/TCP related information.
 * 
 * @author Alexey Stashok
 */
public abstract class WSTCPModule {
    private static volatile WSTCPModule instance;
    
    protected static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain + ".server");
    
    /**
     * Method returns initialized WSTCPModule instance
     * @throws IllegalStateException if instance was not initialized
     */
    public static @NotNull WSTCPModule getInstance() {
        if (instance == null) {
            throw new IllegalStateException(MessagesMessages.WSTCP_0007_TRANSPORT_MODULE_NOT_INITIALIZED());
        }
        
        return instance;
    }
    
    protected static void setInstance(WSTCPModule instance) {
        WSTCPModule.instance = instance;
    }
    
    public WSEndpoint<ServiceChannelWSImpl> createServiceChannelEndpoint() {
        final QName serviceName = WSEndpoint.getDefaultServiceName(ServiceChannelWSImpl.class);
        final QName portName = WSEndpoint.getDefaultPortName(serviceName, ServiceChannelWSImpl.class);
        final BindingID bindingId = BindingID.parse(ServiceChannelWSImpl.class);
        final WSBinding binding = bindingId.createBinding();

        return WSEndpoint.create(ServiceChannelWSImpl.class, true,
                    null,
                    serviceName, portName, null, binding,
                    null, null, null, true);
    }

    public abstract void register(@NotNull final String contextPath,
            @NotNull final List<TCPAdapter> adapters);
    
    public abstract void free(@NotNull final String contextPath,
            @NotNull final List<TCPAdapter> adapters);
    
    /**
     * Returns port, SOAP/TCP is listening on.
     * 
     * @return the port, SOAP/TCP is linstening on. -1 if SOAP/TCP doesn't open
     * own TCP port, but uses connections provided by runtime.
     */
    public abstract int getPort();
}
