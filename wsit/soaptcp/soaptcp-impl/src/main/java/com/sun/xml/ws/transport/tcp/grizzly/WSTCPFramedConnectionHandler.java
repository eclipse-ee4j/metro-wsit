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

import com.sun.istack.NotNull;
import com.sun.xml.ws.transport.tcp.server.IncomeMessageProcessor;
import com.sun.enterprise.web.connector.grizzly.Handler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Alexey Stashok
 */

public final class WSTCPFramedConnectionHandler implements Handler {
    private final WSTCPStreamAlgorithm streamAlgorithm;
    private final IncomeMessageProcessor messageProcessor;
    
    public WSTCPFramedConnectionHandler(@NotNull final WSTCPStreamAlgorithm streamAlgorithm) {
        this.streamAlgorithm = streamAlgorithm;
        this.messageProcessor = IncomeMessageProcessor.getMessageProcessorForPort(streamAlgorithm.getPort());
    }
    
    public int handle(final Object request, final int code) throws IOException {
        if (code == REQUEST_BUFFERED) {
            final ByteBuffer messageBuffer = streamAlgorithm.getByteBuffer();
            final SocketChannel socketChannel = streamAlgorithm.getSocketChannel();
            messageProcessor.process(messageBuffer, socketChannel);
        }
        
        return BREAK;
    }
    
    public void attachChannel(final SocketChannel socketChannel) {
    }
}
