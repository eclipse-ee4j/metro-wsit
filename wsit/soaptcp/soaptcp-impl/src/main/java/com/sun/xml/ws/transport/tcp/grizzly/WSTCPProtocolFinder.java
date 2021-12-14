/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.grizzly;

import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.portunif.PUContext;
import org.glassfish.grizzly.portunif.ProtocolFinder;

import java.nio.BufferUnderflowException;

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
     * @param ctx filter chain context
     *
     * @return ProtocolInfo The ProtocolInfo that contains the information about the
     *                   current protocol.
     */
    @Override
    public Result find(final PUContext puContext,
                       final FilterChainContext ctx) {
        final Buffer buffer = ctx.getMessage();
        final Connection connection = ctx.getConnection();

        int loop = 0;
        int count = -1;

        if (buffer.remaining() > 0) {
            try {
                while ( connection.isOpen() &&
                        ((count = connection.getReadBufferSize())> -1)){

                    if ( count == 0 ){
                        loop++;
                        if (loop > 2){
                            break;
                        }
                    }
                }
            } finally {
                if ( count == -1 ){
                    return Result.NOT_FOUND;
                }
            }
        }

        final int curPosition = buffer.position();
        final int curLimit = buffer.limit();

        // Rule a - If read length < PROTOCOL_ID.length, return to the Selector.
        if (curPosition < TCPConstants.PROTOCOL_SCHEMA.length()){
            return Result.NOT_FOUND;
        }

        buffer.flip();

        // Rule b - check protocol id
        try {
            final byte[] protocolBytes = new byte[TCPConstants.PROTOCOL_SCHEMA.length()];
            buffer.get(protocolBytes);
            final String incomeProtocolId = new String(protocolBytes);
            if (TCPConstants.PROTOCOL_SCHEMA.equals(incomeProtocolId)) {
                return Result.FOUND;
            }
        } catch (BufferUnderflowException bue) {
        } finally {
            buffer.limit(curLimit);
            buffer.position(curPosition);
        }
        return Result.NEED_MORE_DATA;
    }

}
