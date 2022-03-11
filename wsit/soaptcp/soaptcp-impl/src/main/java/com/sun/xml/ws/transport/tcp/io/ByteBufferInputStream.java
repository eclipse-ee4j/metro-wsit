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

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Stream wrapper around a <code>ByteBuffer</code>
 */
public class ByteBufferInputStream extends InputStream {

    /**
     * The wrapped <code>ByteBuffer</code<
     */
    private ByteBuffer byteBuffer;

    // ------------------------------------------------- Constructor -------//


    public ByteBufferInputStream(final ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    /**
     * Set the wrapped <code>ByteBuffer</code>
     * @param byteBuffer The wrapped byteBuffer
     */
    public void setByteBuffer(final ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }


    /**
     * Return the available bytes
     * @return the wrapped byteBuffer.remaining()
     */
    @Override
    public int available() {
        return byteBuffer.remaining();
    }


    /**
     * Close this stream.
     */
    @Override
    public void close() {
    }


    /**
     * Return true if mark is supported.
     */
    @Override
    public boolean markSupported() {
        return false;
    }


    /**
     * Read the first byte from the wrapped <code>ByteBuffer</code>.
     */
    @Override
    public int read() {
        if (!byteBuffer.hasRemaining()){
            return -1;
        }

        return (byteBuffer.get() & 0xff);
    }


    /**
     * Read the bytes from the wrapped <code>ByteBuffer</code>.
     */
    @Override
    public int read(final byte[] b) {
        return (read(b, 0, b.length));
    }


    /**
     * Read the first byte of the wrapped <code>ByteBuffer</code>.
     */
    @Override
    public int read(final byte[] b, final int offset, int length) {
        if (!byteBuffer.hasRemaining()) {
            return -1;
        }

        if (length > available()) {
            length = available();
        }

        byteBuffer.get(b, offset, length);

        return length;
    }


}

