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

import com.sun.xml.ws.transport.tcp.util.ChannelContext;
import com.sun.xml.ws.transport.tcp.util.WSTCPError;
import java.io.IOException;

/**
 * @author Alexey Stashok
 */
public interface TCPMessageListener {
    public void onMessage(ChannelContext channelContext) throws IOException;
    public void onError(ChannelContext channelContext, WSTCPError error) throws IOException;
}
