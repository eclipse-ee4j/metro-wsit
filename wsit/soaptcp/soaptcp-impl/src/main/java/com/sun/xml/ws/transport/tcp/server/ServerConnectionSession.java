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

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.transport.tcp.io.Connection;
import com.sun.xml.ws.transport.tcp.util.ChannelContext;
import com.sun.xml.ws.transport.tcp.util.ConnectionSession;
import com.sun.xml.ws.transport.tcp.util.SessionCloseListener;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Stashok
 */
public final class ServerConnectionSession extends ConnectionSession {
    private Map<Integer, ChannelContext> channelId2context =
            new HashMap<>();

    private int channelCounter;

    public ServerConnectionSession(final Connection connection, final SessionCloseListener<ServerConnectionSession> sessionCloseListener) {
        super(connection, sessionCloseListener);
        channelCounter = 1;
        init();
    }

    @Override
    public void registerChannel(@NotNull final ChannelContext context) {
        channelId2context.put(context.getChannelId(), context);
    }

    @Override
    public @Nullable ChannelContext findWSServiceContextByChannelId(final int channelId) {
        return channelId2context.get(channelId);
    }

    public void deregisterChannel(final int channelId) {
        channelId2context.remove(channelId);
    }

    @Override
    public void deregisterChannel(@NotNull final ChannelContext context) {
        deregisterChannel(context.getChannelId());
    }

    @Override
    public void close() {
        super.close();

        channelId2context = null;
    }

    @Override
    public int getChannelsAmount() {
        return channelId2context.size();
    }

    public synchronized int getNextAvailChannelId() {
        return channelCounter++;
    }

}
