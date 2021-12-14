/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.io;

import com.sun.xml.ws.util.ByteArrayBuffer;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Alexey Stashok
 */
public class BufferedMessageInputStream extends InputStream {
    private InputStream inputStream;
    
    private boolean isClosed;
    private boolean isBuffered;
    
    private int bufferedSize;
    
    public BufferedMessageInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
        isBuffered = false;
    }
    
    
    @Override
    public int read() throws IOException {
        return inputStream.read();
    }
    
    @Override
    public int read(final byte[] b, final int offset, final int length) throws IOException {
        return inputStream.read(b, offset, length);
    }
    
    public void bufferMessage() throws IOException {
        if (!isBuffered) {
            final ByteArrayBuffer baBuffer = new ByteArrayBuffer();
            try {
                baBuffer.write(inputStream);
                inputStream = baBuffer.newInputStream();
                bufferedSize = baBuffer.size();
                isBuffered = true;
            } finally {
                baBuffer.close();
            }
        }
    }
    
    public InputStream getSourceInputStream() {
        return inputStream;
    }
    
    public int getBufferedSize() {
        if (isBuffered) {
            return bufferedSize;
        }
        
        return 0;
    }
    
    public boolean isClosed() {
        return isClosed;
    }
    
    public boolean isBuffered() {
        return isBuffered;
    }
    
    @Override
    public void close() throws IOException {
        isClosed = true;
        inputStream.close();
    }
    
    
}
