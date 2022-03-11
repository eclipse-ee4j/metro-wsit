/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.io;

import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import com.sun.xml.ws.transport.tcp.util.DumpUtils;
import com.sun.xml.ws.transport.tcp.util.SelectorFactory;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NIO utility to flush <code>ByteBuffer</code>
 *
 * @author Scott Oaks
 * @author Alexey Stashok
 */
public final class OutputWriter {
    private static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain + ".dump");

    /**
     * Flush the buffer by looping until the <code>ByteBuffer</code> is empty
     * @param bb the ByteBuffer to write.
     */
    public static void flushChannel(final SocketChannel socketChannel, final ByteBuffer bb)
    throws IOException{
        if (logger.isLoggable(Level.FINEST)) {
            Socket socket = socketChannel.socket();
            logger.log(Level.FINEST, MessagesMessages.WSTCP_1070_OUTPUT_WRITER_DUMP(socket.getInetAddress().getHostAddress(), socketChannel.socket().getPort()));
            logger.log(Level.FINEST, DumpUtils.dumpBytes(bb));
        }

        SelectionKey key = null;
        Selector writeSelector = null;
        int attempts = 0;
        try {
            while ( bb.hasRemaining() ) {
                final int len = socketChannel.write(bb);
                attempts++;
                if (len < 0){
                    throw new EOFException();
                }

                if (len == 0) {
                    if ( writeSelector == null ){
                        writeSelector = SelectorFactory.getSelector();
                        if ( writeSelector == null){
                            // Continue using the main one.
                            continue;
                        }
                    }

                    key = socketChannel.register(writeSelector, SelectionKey.OP_WRITE);

                    if (writeSelector.select(30 * 1000) == 0) {
                        if (attempts > 2) {
                            Socket socket = socketChannel.socket();
                            throw new IOException(MessagesMessages.WSTCP_0019_PEER_DISCONNECTED(socket.getInetAddress().getHostAddress(), socketChannel.socket().getPort()));
                        }
                    } else {
                        attempts--;
                    }
                } else {
                    attempts = 0;
                }
            }
        } finally {
            if (key != null) {
                key.cancel();
                key = null;
            }

            if ( writeSelector != null ) {
                // Cancel the key.
                writeSelector.selectNow();
                SelectorFactory.returnSelector(writeSelector);
            }
        }
    }

    /**
     * Flush the buffer by looping until the <code>ByteBuffer</code> is empty
     * @param bb the ByteBuffer to write.
     */
    public static void flushChannel(final SocketChannel socketChannel, final ByteBuffer[] bb)
    throws IOException{
        if (logger.isLoggable(Level.FINEST)) {
            Socket socket = socketChannel.socket();
            logger.log(Level.FINEST, MessagesMessages.WSTCP_1070_OUTPUT_WRITER_DUMP(socket.getInetAddress().getHostAddress(), socketChannel.socket().getPort()));
            logger.log(Level.FINEST, DumpUtils.dumpBytes(bb));
        }
        SelectionKey key = null;
        Selector writeSelector = null;
        int attempts = 0;
        try {
            while (hasRemaining(bb)) {
                final long len = socketChannel.write(bb);
                attempts++;
                if (len < 0){
                    throw new EOFException();
                }

                if (len == 0) {
                    if ( writeSelector == null ){
                        writeSelector = SelectorFactory.getSelector();
                        if ( writeSelector == null){
                            // Continue using the main one.
                            continue;
                        }
                    }

                    key = socketChannel.register(writeSelector, SelectionKey.OP_WRITE);

                    if (writeSelector.select(30 * 1000) == 0) {
                        if (attempts > 2) {
                            Socket socket = socketChannel.socket();
                            throw new IOException(MessagesMessages.WSTCP_0019_PEER_DISCONNECTED(socket.getInetAddress().getHostAddress(), socketChannel.socket().getPort()));
                        }
                    } else {
                        attempts--;
                    }
                } else {
                    attempts = 0;
                }
            }
        } finally {
            if (key != null) {
                key.cancel();
                key = null;
            }

            if ( writeSelector != null ) {
                // Cancel the key.
                writeSelector.selectNow();
                SelectorFactory.returnSelector(writeSelector);
            }
        }
    }

    private static boolean hasRemaining(final ByteBuffer[] bb) {
        for(int i=bb.length - 1; i>=0; i--) {
            if (bb[i].hasRemaining()) {
                return true;
            }
        }

        return false;
    }
}
