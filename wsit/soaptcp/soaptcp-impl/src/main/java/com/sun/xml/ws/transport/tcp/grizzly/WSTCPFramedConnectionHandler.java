/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.grizzly;

import com.sun.xml.ws.transport.tcp.server.IncomeMessageProcessor;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.memory.HeapBuffer;
import org.glassfish.grizzly.nio.NIOConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author Alexey Stashok
 */

public final class WSTCPFramedConnectionHandler extends BaseFilter {


    public NextAction handle(final FilterChainContext ctx) throws IOException {
        final HeapBuffer messageBuffer = ctx.getMessage();
        if (!(ctx.getConnection() instanceof NIOConnection)
                || messageBuffer == null) {
            return ctx.getStopAction();
        }
        final NIOConnection connection = (NIOConnection) ctx.getConnection();
        final IncomeMessageProcessor messageProcessor =
                IncomeMessageProcessor.getMessageProcessorForPort(
                        ((InetSocketAddress) connection.getLocalAddress()).getPort()
                );


        final SocketChannel socketChannel = (SocketChannel) connection.getChannel();
        messageProcessor.process(messageBuffer.toByteBuffer(), socketChannel);

        return ctx.getStopAction();
    }

    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        return handle(ctx);
    }

    @Override
    public NextAction handleWrite(FilterChainContext ctx) throws IOException {
        return handle(ctx);
    }

    @Override
    public NextAction handleConnect(FilterChainContext ctx) throws IOException {
        return handle(ctx);
    }

    public void attachChannel(final SocketChannel socketChannel) {
    }
}
