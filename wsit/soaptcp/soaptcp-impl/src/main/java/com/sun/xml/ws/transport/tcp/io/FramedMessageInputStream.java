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

import com.sun.xml.ws.transport.tcp.pool.LifeCycle;
import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import com.sun.xml.ws.transport.tcp.util.FrameType;
import com.sun.xml.ws.transport.tcp.util.SelectorFactory;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stream wrapper around a <code>ByteBuffer</code>
 */
public final class FramedMessageInputStream extends InputStream implements LifeCycle {
    private static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain + ".streams");

    private ByteBuffer byteBuffer;

    private SocketChannel socketChannel;

    /**
     * The time to wait before timing out when reading bytes
     */
    final private static int READ_TIMEOUT = 30000;

    /**
     * Number of times to retry before return EOF
     */
    final static int READ_TRY = 10;

    private final int[] headerTmpArray = new int[2];

    /** is message framed or direct mode is used */
    private boolean isDirectMode;

    private int frameSize;
    private int frameBytesRead;
    private boolean isLastFrame;
    private int currentFrameDataSize;    // for last frame actual data size could be smaller than frame size

    private int channelId;
    private int contentId;
    private int messageId;
    private final Map<Integer, String> contentProps = new HashMap<>(8);

    private boolean isReadingHeader;

    /**
     * could be useful for debug reasons
     */
    private long receivedMessageLength;

    // ------------------------------------------------- Constructor -------//


    public FramedMessageInputStream() {
        this(TCPConstants.DEFAULT_FRAME_SIZE);
    }

    public FramedMessageInputStream(int frameSize) {
        setFrameSize(frameSize);
    }
    // ---------------------------------------------------------------------//


    public void setSocketChannel(final SocketChannel socketChannel){
        this.socketChannel = socketChannel;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getMessageId() {
        return messageId;
    }

    public int getContentId() {
        return contentId;
    }

    public Map<Integer, String> getContentProperties() {
        return contentProps;
    }

    public boolean isDirectMode() {
        return isDirectMode;
    }

    public void setDirectMode(final boolean isDirectMode) {
        reset();
        this.isDirectMode = isDirectMode;
    }

    public void setFrameSize(final int frameSize) {
        this.frameSize = frameSize;
    }

    public void setByteBuffer(final ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    /**
     * Return the available bytes
     * @return the wrapped byteBuffer.remaining()
     */
    @Override
    public int available() {
        return remaining();
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
        if (!isDirectMode) {
            if (isLastFrame && frameBytesRead >= currentFrameDataSize) {
                // check if last frame and there is no data
                return -1;
            }

            int eof = 0;
            if (!byteBuffer.hasRemaining()) {
                eof = readFromChannel();
            }

            if (eof == -1 || readFrameHeaderIfRequired() == -1) {
                return -1;
            }

            if (byteBuffer.hasRemaining()) {
                frameBytesRead++;
                receivedMessageLength++;
                return byteBuffer.get() & 0xff;
            }

            return read();
        } else {
            if (!byteBuffer.hasRemaining()) {
                int eof = readFromChannel();
                if (eof == -1) {
                    return -1;
                }
            }

            return byteBuffer.get() & 0xff;
        }
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
        if (!isDirectMode) {
            if (isLastFrame && frameBytesRead >= currentFrameDataSize) {
                // check if last frame and there is no data
                return -1;
            }

            int eof = 0;
            if (!byteBuffer.hasRemaining()) {
                eof = readFromChannel();
            }

            if (eof == -1 || readFrameHeaderIfRequired() == -1) {
                return -1;
            }

            //@TODO add logic for reading from several frames if required
            int remaining = remaining();
            if (remaining == 0) {
                // if header was read, but payload is empty - read it
                return read(b, offset, length);
            }

            if (length > remaining) {
                length = remaining;
            }

            byteBuffer.get(b, offset, length);
            frameBytesRead += length;
            receivedMessageLength += length;

            return length;
        } else {
            if (!byteBuffer.hasRemaining()) {
                int eof = readFromChannel();
                if (eof == -1) {
                    return -1;
                }
            }
            int remaining = remaining();
            if (length > remaining) {
                length = remaining;
            }
            byteBuffer.get(b, offset, length);
            return length;
        }
    }


    public void forceHeaderRead() throws IOException {
        readHeader();
    }

    private int readFrameHeaderIfRequired() {
        if (!isDirectMode && !isLastFrame && !isReadingHeader && (frameBytesRead == 0 || frameBytesRead == currentFrameDataSize)) {
            try {
                readHeader();
            } catch (IOException ex) {
                return -1;
            }
        }

        return 0;
    }

    private void readHeader() throws IOException {
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, MessagesMessages.WSTCP_1060_FRAMED_MESSAGE_IS_READ_HEADER_ENTER());
        }
        frameBytesRead = 0;
        isReadingHeader = true;
        // Read channel-id and message-id
        int lowNeebleValue = DataInOutUtils.readInts4(this, headerTmpArray, 2, 0);
        channelId = headerTmpArray[0];
        messageId = headerTmpArray[1];

        if (FrameType.isFrameContainsParams(messageId)) {  //message types have description
            // Read content-id and number-of-parameters
            lowNeebleValue = DataInOutUtils.readInts4(this, headerTmpArray, 2, lowNeebleValue);
            contentId = headerTmpArray[0];
            final int paramNumber = headerTmpArray[1];
            for(int i=0; i<paramNumber; i++) {
                // Read parameter-id and length of parameter-value buffer
                DataInOutUtils.readInts4(this, headerTmpArray, 2, lowNeebleValue);
                final int paramId = headerTmpArray[0];
                final int paramValueLen = headerTmpArray[1];
                byte[] paramValueBytes = new byte[paramValueLen];
                // Read parameter-value
                DataInOutUtils.readFully(this, paramValueBytes);
                final String paramValue = new String(paramValueBytes, StandardCharsets.UTF_8);
                contentProps.put(paramId, paramValue);
                lowNeebleValue = 0;
            }
        }

        // Read payload-size
        currentFrameDataSize = DataInOutUtils.readInt8(this);
        isLastFrame = FrameType.isLastFrame(messageId);
        currentFrameDataSize += frameBytesRead;
        isReadingHeader = false;

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, MessagesMessages.WSTCP_1061_FRAMED_MESSAGE_IS_READ_HEADER_DONE(channelId, messageId, contentId, contentProps, currentFrameDataSize, isLastFrame));
        }
    }

    private int readFromChannel() {
        int eof = 0;
        for (int i=0; i < READ_TRY; i++) {
            eof = doRead();

            if (eof != 0) {
                break;
            }
        }

        return eof;
    }

    public void skipToEndOfMessage() throws EOFException {
        do {
            readFrameHeaderIfRequired();
            skipToEndOfFrame();
            frameBytesRead = 0;
        } while(!isLastFrame);
    }

    private void skipToEndOfFrame() throws EOFException {
        if (currentFrameDataSize > 0) {
            if (byteBuffer.hasRemaining()) {
                final int remainFrameBytes = currentFrameDataSize - frameBytesRead;
                if (remainFrameBytes <= byteBuffer.remaining()) {
                    byteBuffer.position(byteBuffer.position() + remainFrameBytes);
                    return;
                }

                frameBytesRead += byteBuffer.remaining();
                byteBuffer.position(byteBuffer.limit());
            }

            while(frameBytesRead < currentFrameDataSize) {
                final int eof = readFromChannel();
                if (eof == -1) {
                    String errorMessage = MessagesMessages.WSTCP_1062_FRAMED_MESSAGE_IS_READ_UNEXPECTED_EOF(isLastFrame, frameBytesRead, frameSize, currentFrameDataSize);
                    logger.log(Level.SEVERE, errorMessage);
                    throw new EOFException(errorMessage);
                }
                frameBytesRead += eof;
                byteBuffer.position(byteBuffer.position() + eof);
            }

            // if extra frame bytes were read - move position backwards
            byteBuffer.position(byteBuffer.position() - (frameBytesRead - currentFrameDataSize));
        }
    }

    /**
     * Read bytes using the read <code>ReadSelector</code>
     */
    private int doRead(){
        if ( socketChannel == null ) return -1;
        if (isEOF()) {
            return -1;
        }

        byteBuffer.clear();

        int count;
        int byteRead = 0;
        Selector readSelector = null;
        SelectionKey tmpKey = null;

        try{
            do {
                count = socketChannel.read(byteBuffer);
                byteRead += count;
            } while (count > 0);

            if (count == -1 && byteRead >= 0) byteRead++;

            if ( byteRead == 0 ){
                readSelector = SelectorFactory.getSelector();

                if ( readSelector == null ){
                    return 0;
                }
                tmpKey = socketChannel
                        .register(readSelector,SelectionKey.OP_READ);
                tmpKey.interestOps(tmpKey.interestOps() | SelectionKey.OP_READ);
                final int code = readSelector.select(READ_TIMEOUT);

                //Nothing so return.
                tmpKey.interestOps(tmpKey.interestOps() & (~SelectionKey.OP_READ));
                if ( code == 0 ){
                    return 0;
                }

                do {
                    count = socketChannel.read(byteBuffer);
                    byteRead += count;
                } while (count > 0);
                if (count == -1 && byteRead >= 0) byteRead++;
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, MessagesMessages.WSTCP_0018_ERROR_READING_FROM_SOCKET(), e);
            return -1;
        } finally {
            if (tmpKey != null)
                tmpKey.cancel();

            if ( readSelector != null){
                try{
                    readSelector.selectNow();
                } catch (IOException ex){
                }
                SelectorFactory.returnSelector(readSelector);
            }

            byteBuffer.flip();
        }

        return byteRead;
    }

    public boolean isMessageInProcess() {
        if (currentFrameDataSize == 0 || isEOF()) return false;

        return true;
    }

    private boolean isEOF() {
        return isLastFrame && frameBytesRead >= currentFrameDataSize;
    }

    private int remaining() {
        if (isReadingHeader || isDirectMode) {
            return byteBuffer.remaining();
        }

        return Math.min(currentFrameDataSize - frameBytesRead, byteBuffer.remaining());
    }

    @Override
    public void reset() {
        frameBytesRead = 0;
        currentFrameDataSize = 0;
        isLastFrame = false;
        isReadingHeader = false;
        contentId = -1;
        messageId = -1;
        contentProps.clear();
        receivedMessageLength = 0;
    }

    @Override
    public void activate() {
    }

    @Override
    public void passivate() {
        reset();
        setSocketChannel(null);
        setByteBuffer(null);
    }

    @Override
    public String toString() {
        String buffer = "ByteBuffer: " +
                byteBuffer +
                " FrameBytesRead: " +
                frameBytesRead +
                " CurrentFrameDataSize: " +
                currentFrameDataSize +
                " isLastFrame: " +
                isLastFrame +
                " isDirectMode: " +
                isDirectMode +
                " isReadingHeader: " +
                isReadingHeader;

        return buffer;
    }
}

