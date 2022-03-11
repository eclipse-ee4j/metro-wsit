/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.client;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.transport.tcp.io.Connection;
import com.sun.xml.ws.transport.tcp.util.ChannelContext;
import com.sun.xml.ws.transport.tcp.util.ConnectionSession;
import com.sun.xml.ws.transport.tcp.util.SessionCloseListener;
import com.sun.xml.ws.transport.tcp.util.WSTCPURI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Stashok
 */
public final class ClientConnectionSession extends ConnectionSession {
    private Map<String, Object> attributes = new HashMap<>(2);
    private Map<String, ChannelContext> url2ChannelMap = new HashMap<>();

    public ClientConnectionSession(final Connection connection, final SessionCloseListener sessionCloseListener) {
        super(connection, sessionCloseListener);
        init();
    }

    @Override
    public void registerChannel(@NotNull final ChannelContext context) {
        url2ChannelMap.put(context.getTargetWSURI().toString(), context);
    }

    @Override
    public void deregisterChannel(@NotNull final ChannelContext context) {
        String wsTCPURLString = context.getTargetWSURI().toString();
        ChannelContext channelToRemove = url2ChannelMap.get(wsTCPURLString);
        if (channelToRemove.getChannelId() == context.getChannelId()) {
            url2ChannelMap.remove(wsTCPURLString);
        }
    }

    @Override
    public @Nullable ChannelContext findWSServiceContextByURI(@NotNull final WSTCPURI wsTCPURI) {
        return url2ChannelMap.get(wsTCPURI.toString());
    }

    @Override
    public void onReadCompleted() {
        WSConnectionManager.getInstance().freeConnection(this);
    }

    @Override
    public void close() {
        super.close();
        attributes = null;
    }

    @Override
    public void setAttribute(@NotNull final String name, final Object value) {
        attributes.put(name, value);
    }

    @Override
    public @Nullable Object getAttribute(@NotNull final String name) {
        return attributes.get(name);
    }

    @Override
    public int getChannelsAmount() {
        return url2ChannelMap.size();
    }
}
