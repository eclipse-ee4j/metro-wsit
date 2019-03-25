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

import com.sun.istack.NotNull;
import com.sun.xml.ws.transport.tcp.server.IncomeMessageProcessor;
import com.sun.xml.ws.transport.tcp.server.TCPMessageListener;
import com.sun.xml.ws.transport.tcp.server.WSTCPConnector;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Properties;

/**
 * @author Alexey Stashok
 */
public class GrizzlyTCPConnector implements WSTCPConnector {
    private TCPNIOTransport transport;

    private static final Integer UNIFICATION_PORT=0;

    private String host;
    private int port;
    private TCPMessageListener listener;
    private final Properties properties;

    private final boolean isPortUnificationMode;

    public GrizzlyTCPConnector(@NotNull final String host, final int port,
                               @NotNull final TCPMessageListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
        isPortUnificationMode = false;
        properties = new Properties();
    }

    public GrizzlyTCPConnector(@NotNull final TCPMessageListener listener, @NotNull final Properties properties) {
        this.listener = listener;
        isPortUnificationMode = true;
        this.properties = properties;
        port = -1;
    }

    public void listen() throws IOException {
        if (isPortUnificationMode) {
            listenOnUnifiedPort();
        } else {
            listenOnNewPort();
        }
    }

    private TCPNIOTransport prepareTransport() throws IOException {
        final FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        filterChainBuilder
                .addFirst(new TransportFilter())
                .addLast(new WSTCPFramedConnectionHandler());
        if (isPortUnificationMode) {
            IncomeMessageProcessor.registerListener(UNIFICATION_PORT, listener, properties);
        } else {
            IncomeMessageProcessor.registerListener(port, listener, properties);
        }

        transport = TCPNIOTransportBuilder.newInstance().
                setProcessor(filterChainBuilder.build()).
                setReadBufferSize(TCPConstants.DEFAULT_FRAME_SIZE).
                setKeepAlive(true).
                setIOStrategy(SameThreadIOStrategy.getInstance()).
                build();

        transport.start();

        return transport;
    }

    public void listenOnNewPort() throws IOException {

        prepareTransport().bind(host, port);

    }

    public void listenOnUnifiedPort() throws IOException {
        final SocketAddress addr = new InetSocketAddress(UNIFICATION_PORT);
        prepareTransport().bind(addr);
    }

    public void close() {
        if (transport != null) {
            transport.unbindAll();
            IncomeMessageProcessor.releaseListener(port);
            transport = null;
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public TCPMessageListener getListener() {
        return listener;
    }

    public void setListener(final TCPMessageListener listener) {
        this.listener = listener;
    }


    public void setFrameSize(final int frameSize) {
        transport.setReadBufferSize(frameSize);
    }

    public int getFrameSize() {
        return transport.getReadBufferSize();
    }
}
