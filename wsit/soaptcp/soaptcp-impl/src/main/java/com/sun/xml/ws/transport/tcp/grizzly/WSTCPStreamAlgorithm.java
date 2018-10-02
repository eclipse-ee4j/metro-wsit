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

import com.sun.enterprise.web.connector.grizzly.Handler;
import com.sun.enterprise.web.connector.grizzly.algorithms.StreamAlgorithmBase;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Alexey Stashok
 */
public final class WSTCPStreamAlgorithm extends StreamAlgorithmBase {
    
    private ByteBuffer resultByteBuffer;
    
    public Handler getHandler() {
        return handler;
    }
    
    public boolean parse(final ByteBuffer byteBuffer) {
        byteBuffer.flip();
        this.resultByteBuffer = byteBuffer;
        return true;
    }
    
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }
    
    public ByteBuffer getByteBuffer() {
        return resultByteBuffer;
    }
    
    /**
     * Algorith is usually created with Class.newInstance -> its port 
     * is not set before,
     * but port value is required in handler's constructor
     */
    public void setPort(final int port) {
        super.setPort(port);
        handler = new WSTCPFramedConnectionHandler(this);
    }

    public void recycle(){
        resultByteBuffer = null;
        socketChannel = null;
        
        super.recycle();
    }

}
