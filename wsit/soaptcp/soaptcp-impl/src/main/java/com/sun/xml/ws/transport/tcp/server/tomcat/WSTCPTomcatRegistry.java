/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.server.tomcat;

import com.sun.xml.ws.transport.tcp.server.TCPAdapter;
import com.sun.xml.ws.transport.tcp.server.TCPMessageListener;
import com.sun.xml.ws.transport.tcp.server.WSTCPDelegate;
import com.sun.xml.ws.transport.tcp.server.WSTCPModule;
import com.sun.xml.ws.transport.tcp.util.ChannelContext;
import com.sun.xml.ws.transport.tcp.util.WSTCPError;
import java.io.IOException;
import java.util.List;

/**
 * @author Alexey Stashok
 */
public class WSTCPTomcatRegistry extends WSTCPModule implements TCPMessageListener {
    private WSTCPDelegate delegate;
    
    private int listeningPort = -1;
    
    protected static void setInstance(WSTCPModule instance) {
        WSTCPModule.setInstance(instance);
    }

    WSTCPTomcatRegistry(int port) {
        listeningPort = port;
        delegate = new WSTCPDelegate();
    }
    
    @Override
    public int getPort() {
        return listeningPort;
    }
    
    @Override
    public void onMessage(ChannelContext channelContext) throws IOException {
        delegate.onMessage(channelContext);
    }

    @Override
    public void onError(ChannelContext channelContext, WSTCPError error) throws IOException {
        delegate.onError(channelContext, error);
    }

    @Override
    public void register(String contextPath, List<TCPAdapter> adapters) {
        delegate.registerAdapters(contextPath, adapters);
    }

    @Override
    public void free(String contextPath, List<TCPAdapter> adapters) {
        delegate.freeAdapters(contextPath, adapters);
    }
}
