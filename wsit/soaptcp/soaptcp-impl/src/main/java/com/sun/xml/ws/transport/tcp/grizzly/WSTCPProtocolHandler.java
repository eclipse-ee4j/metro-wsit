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

import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import com.sun.xml.ws.transport.tcp.server.IncomeMessageProcessor;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.nio.NIOConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alexey Stashok
 */
public final class WSTCPProtocolHandler extends BaseFilter {
    private static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain + ".server");

    private final IncomeMessageProcessor processor;

    public WSTCPProtocolHandler(final IncomeMessageProcessor processor) {
        this.processor = processor;
    }

    public String[] getProtocols() {
        return new String[]{TCPConstants.PROTOCOL_SCHEMA};
    }

    public NextAction handle(final FilterChainContext ctx) throws IOException {
        if (processor != null && (ctx.getConnection() instanceof NIOConnection)) {
            final ByteBuffer messageBuffer = ctx.getMessage();
            messageBuffer.flip();
            processor.process(messageBuffer, (SocketChannel) ((NIOConnection) ctx.getConnection()).getChannel());
        } else {
            logger.log(Level.WARNING, MessagesMessages.WSTCP_0013_TCP_PROCESSOR_NOT_REGISTERED());
        }
        return null;
    }

    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        return handle(ctx);
    }

    /**
     * Invoked when the SelectorThread is about to expire a SelectionKey.
     *
     * @return true if the SelectorThread should expire the SelectionKey, false
     * if not.
     */
    public boolean expireKey(SelectionKey key) {
        if (processor != null) {
            processor.notifyClosed((SocketChannel) key.channel());
        }

        return true;
    }
}
