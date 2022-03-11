/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.server.tomcat;

import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.coyote.Adapter;
import org.apache.coyote.ProtocolHandler;

/**
 * SOAP/TCP implementation of Tomcat ProtocolHandler, based on Grizzly 1.0
 * @author Alexey Stashok
 */
public abstract class WSTCPTomcatProtocolHandlerBase implements ProtocolHandler, Runnable {
    private static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain + ".server");

    private Map<String, Object> atts = new HashMap<>();

    private Adapter adapter;

    protected int port;
    protected int redirectHttpPort = 8080;
    protected int readThreadsCount;
    protected int maxWorkerThreadsCount = -1;
    protected int minWorkerThreadsCount = -1;

    public void setAttribute(String string, Object object) {
        atts.put(string, object);
    }

    public Object getAttribute(String string) {
        return atts.get(string);
    }

    public Iterator getAttributeNames() {
        return atts.keySet().iterator();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public Adapter getAdapter() {
        return adapter;
    }

    @Override
    public void init() {
        if (logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, MessagesMessages.WSTCP_1170_INIT_SOAPTCP(port));
        }

        WSTCPTomcatRegistry.setInstance(new WSTCPTomcatRegistry(port));
    }

    @Override
    public void start() {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, MessagesMessages.WSTCP_1171_START_SOAPTCP_LISTENER());
        }
        new Thread(this).start();
    }

    @Override
    public void resume() throws Exception {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, MessagesMessages.WSTCP_1173_RESUME_SOAPTCP_LISTENER());
        }
        start();
    }

    @Override
    public void pause() throws Exception {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, MessagesMessages.WSTCP_1172_PAUSE_SOAPTCP_LISTENER());
        }
        WSTCPTomcatRegistry.setInstance(new WSTCPTomcatRegistry(-1));
        destroy();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setReadThreadsCount(int readThreadsCount) {
        this.readThreadsCount = readThreadsCount;
    }

    public int getReadThreadsCount() {
        return readThreadsCount;
    }

    public void setMaxWorkerThreadsCount(int maxWorkerThreadsCount) {
        this.maxWorkerThreadsCount = maxWorkerThreadsCount;
    }

    public int getMaxWorkerThreadsCount() {
        return maxWorkerThreadsCount;
    }

    public void setMinWorkerThreadsCount(int minWorkerThreadsCount) {
        this.minWorkerThreadsCount = minWorkerThreadsCount;
    }

    public int getMinWorkerThreadsCount() {
        return minWorkerThreadsCount;
    }

    public void setRedirectHttpPort(int redirectHttpPort) {
        this.redirectHttpPort = redirectHttpPort;
    }

    public int getRedirectHttpPort() {
        return redirectHttpPort;
    }

    @Override
    public String toString() {
        return MessagesMessages.WSTCP_1174_TOMCAT_SOAPTCP_LISTENER(port);
    }
}
