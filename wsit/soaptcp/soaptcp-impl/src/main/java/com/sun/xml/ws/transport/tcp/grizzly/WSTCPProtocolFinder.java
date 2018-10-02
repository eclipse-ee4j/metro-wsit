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

import com.sun.enterprise.web.portunif.ProtocolFinder;
import com.sun.enterprise.web.portunif.util.ProtocolInfo;
import com.sun.istack.NotNull;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * A <code>ProtocolFinder</code> implementation that parse the available
 * SocketChannel bytes looking for the PROTOCOL_ID bytes. An SOAP/TCP request will
 * always start with: vnd.sun.ws.tcp
 *
 * This object shoudn't be called by several threads simultaneously.
 *
 * @author Jeanfrancois Arcand
 * @author Alexey Stashok
 */
public final class WSTCPProtocolFinder implements ProtocolFinder {
    
    public WSTCPProtocolFinder() {
    }
    
    
    /**
     * Try to find the protocol from the <code>SocketChannel</code> bytes.
     *
     * @param selectionKey The key from which the SocketChannel can be retrieved.
     * @return ProtocolInfo The ProtocolInfo that contains the information about the
     *                   current protocol.
     */
    public void find(@NotNull final ProtocolInfo protocolInfo) throws IOException {
        final SelectionKey key = protocolInfo.key;
        final SocketChannel socketChannel = (SocketChannel)key.channel();
        final ByteBuffer byteBuffer = protocolInfo.byteBuffer;
        
        int loop = 0;
        int count = -1;
        
        if (protocolInfo.bytesRead == 0) {
            try {
                while ( socketChannel.isOpen() &&
                        ((count = socketChannel.read(byteBuffer))> -1)){
                    
                    if ( count == 0 ){
                        loop++;
                        if (loop > 2){
                            break;
                        }
                    } else if (count > 0) {
                        protocolInfo.bytesRead += count;
                    }
                }
            } catch (IOException ex){
            } finally {
                if ( count == -1 ){
                    return;
                }
            }
        }

        final int curPosition = byteBuffer.position();
        final int curLimit = byteBuffer.limit();
        
        // Rule a - If read length < PROTOCOL_ID.length, return to the Selector.
        if (curPosition < TCPConstants.PROTOCOL_SCHEMA.length()){
            return;
        }
        
        byteBuffer.flip();
        
        // Rule b - check protocol id
        try {
            final byte[] protocolBytes = new byte[TCPConstants.PROTOCOL_SCHEMA.length()];
            byteBuffer.get(protocolBytes);
            final String incomeProtocolId = new String(protocolBytes);
            if (TCPConstants.PROTOCOL_SCHEMA.equals(incomeProtocolId)) {
                protocolInfo.protocol = TCPConstants.PROTOCOL_SCHEMA;
                protocolInfo.byteBuffer = byteBuffer;
                protocolInfo.socketChannel =
                        (SocketChannel)key.channel();
                protocolInfo.isSecure = false;
            }
        } catch (BufferUnderflowException bue) {
        } finally {
            byteBuffer.limit(curLimit);
            byteBuffer.position(curPosition);
        }
    }
    
}
