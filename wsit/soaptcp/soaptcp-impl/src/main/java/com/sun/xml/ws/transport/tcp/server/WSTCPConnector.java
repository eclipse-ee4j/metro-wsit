/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.server;

import java.io.IOException;

/**
 * @author Alexey Stashok
 */
public interface WSTCPConnector {
    public void listen() throws IOException;
    public String getHost();
    public void setHost(String host);
    public int getPort();
    public void setPort(int port);
    public TCPMessageListener getListener();
    public void setListener(TCPMessageListener listener);
    public void setFrameSize(int frameSize);
    public int getFrameSize();
    public void close();
}
