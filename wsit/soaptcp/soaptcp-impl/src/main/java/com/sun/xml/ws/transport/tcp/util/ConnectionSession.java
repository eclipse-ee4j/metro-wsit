/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.util;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.transport.tcp.io.Connection;
import java.io.IOException;

/**
 * @author Alexey Stashok
 */
@SuppressWarnings({"unchecked"})
public abstract class ConnectionSession implements com.sun.xml.ws.transport.tcp.connectioncache.spi.transport.Connection {
    /* package */ ChannelZeroContext channelZeroContext;

    private Connection connection;
    
    private boolean isClosed;
    
    private final SessionCloseListener sessionCloseListener;
    
    public abstract void registerChannel(@NotNull final ChannelContext context);
    
    public abstract void deregisterChannel(@NotNull final ChannelContext context);
    
    public abstract int getChannelsAmount();
    
    public ConnectionSession(final Connection connection, final SessionCloseListener sessionCloseListener) {
        this.connection = connection;
        this.sessionCloseListener = sessionCloseListener;
    }
    
    protected void init() {
        channelZeroContext = new ChannelZeroContext(this);
        registerChannel(channelZeroContext);
    }
    
    // Stub for getAttribute
    public @Nullable Object getAttribute(@NotNull final String name) {return null;}
    
    // Stub for setAttribute
    public void setAttribute(@NotNull final String name, @Nullable final Object value) {}
    
    // Stub for read completed event processing
    public void onReadCompleted() {}
    
    public @Nullable ChannelContext findWSServiceContextByURI(@NotNull final WSTCPURI wsTCPURI) {return null;}
    
    public @Nullable ChannelContext findWSServiceContextByChannelId(final int channelId) {return null;}
    
    public @NotNull ChannelContext getServiceChannelContext() {
        return channelZeroContext;
    }
    
    public void close() {
        if (sessionCloseListener != null) {
            sessionCloseListener.notifySessionClose(this);
        }
        
        synchronized(this) {
            if (isClosed) return;
            isClosed = true;
        }
        
        try {
            connection.close();
        } catch (IOException ex) {
        }
        
        connection = null;
    }
    
    public Connection getConnection() {
        return connection;
    }

}
