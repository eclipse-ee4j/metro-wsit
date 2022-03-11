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

import com.sun.xml.ws.transport.tcp.util.ByteBufferFactory;

import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author Alexey Stashok
 */
public class ByteBufferOutputStream extends OutputStream {
    private static final boolean USE_DIRECT_BUFFER = false;

    private ByteBuffer outputBuffer;

    public ByteBufferOutputStream() {
        outputBuffer = ByteBufferFactory.allocateView(USE_DIRECT_BUFFER);
    }

    public ByteBufferOutputStream(int initSize) {
        outputBuffer = ByteBufferFactory.allocateView(initSize, USE_DIRECT_BUFFER);
    }

    public ByteBufferOutputStream(final ByteBuffer outputBuffer) {
        this.outputBuffer = outputBuffer;
    }

    public void reset() {
        outputBuffer.clear();
    }

    public ByteBuffer getByteBuffer() {
        outputBuffer.flip();
        return outputBuffer;
    }

    @Override
    public void write(final int data) {
        if (outputBuffer.position() == outputBuffer.capacity() - 1) {
            final ByteBuffer tmpBuffer = ByteBufferFactory.allocateView(outputBuffer.capacity() * 2, USE_DIRECT_BUFFER);
            tmpBuffer.put(outputBuffer);
            outputBuffer = tmpBuffer;
        }

        outputBuffer.put((byte) data);
    }

    @Override
    public void close() {
    }
}
