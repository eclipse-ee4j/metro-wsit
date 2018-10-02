/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.grizzly;

import com.sun.enterprise.web.connector.grizzly.SelectorThread;
import com.sun.istack.NotNull;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import com.sun.xml.ws.transport.tcp.server.IncomeMessageProcessor;
import com.sun.xml.ws.transport.tcp.server.TCPMessageListener;
import com.sun.xml.ws.transport.tcp.server.WSTCPConnector;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

/**
 * @author Alexey Stashok
 */
public class GrizzlyTCPConnector implements WSTCPConnector {
    private SelectorThread selectorThread;
    
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
    
    public void listenOnNewPort() throws IOException {
        try {
            IncomeMessageProcessor.registerListener(port, listener, properties);
            
            selectorThread = new SelectorThread();
            selectorThread.setClassLoader(WSTCPStreamAlgorithm.class.getClassLoader());
            selectorThread.setAlgorithmClassName(WSTCPStreamAlgorithm.class.getName());
            selectorThread.setAddress(InetAddress.getByName(host));
            selectorThread.setPort(port);
            selectorThread.setBufferSize(TCPConstants.DEFAULT_FRAME_SIZE);
            selectorThread.setMaxKeepAliveRequests(-1);
            selectorThread.initEndpoint();
            selectorThread.start();
        } catch (IOException e) {
            close();
            throw e;
        } catch (InstantiationException e) {
            close();
            throw new IOException(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    public void listenOnUnifiedPort() {
        WSTCPProtocolHandler.setIncomingMessageProcessor(IncomeMessageProcessor.registerListener(0, listener, properties));
    }
    
    public void close() {
        if (selectorThread != null) {
            selectorThread.stopEndpoint();
            IncomeMessageProcessor.releaseListener(selectorThread.getPort());
            selectorThread = null;
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
        selectorThread.setBufferSize(frameSize);
    }
    
    public int getFrameSize() {
        return selectorThread.getBufferSize();
    }
}
