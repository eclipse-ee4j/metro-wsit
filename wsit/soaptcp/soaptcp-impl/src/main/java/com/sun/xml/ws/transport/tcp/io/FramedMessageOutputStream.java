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
import com.sun.xml.ws.transport.tcp.util.ByteBufferFactory;
import com.sun.xml.ws.transport.tcp.util.FrameType;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import com.sun.xml.ws.transport.tcp.util.TCPSettings;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Stashok
 */
public final class FramedMessageOutputStream extends OutputStream implements LifeCycle {
    private static final int MAX_GROW_SIZE = TCPSettings.getInstance().getOutputBufferGrowLimit();
    private static final int MAX_PAYLOAD_LENGTH_LENTGTH = calculatePayloadLengthLength(MAX_GROW_SIZE);
    private static final boolean IS_GROWABLE = TCPSettings.getInstance().isOutputBufferGrow();

    private boolean useDirectBuffer;

    private ByteBuffer outputBuffer;

    private SocketChannel socketChannel;
    private int frameNumber;
    private int frameSize;
    private boolean isFlushLast;

    // Fragment header attributes
    private int channelId;
    private int messageId;
    private int contentId;
    private Map<Integer, String> contentProps = new HashMap<>(8);
    private int payloadlengthLength;

    /** is message framed or direct mode is used */
    private boolean isDirectMode;
    // ByteBuffer for channel_id and message_id, which present in all messages
    private final ByteBuffer headerBuffer;

    private int frameMessageIdHighValue;
    private int frameMessageIdPosition;

    /**
     * could be useful for debug reasons
     */
    private long sentMessageLength;

    public FramedMessageOutputStream() {
        this(TCPConstants.DEFAULT_FRAME_SIZE, TCPConstants.DEFAULT_USE_DIRECT_BUFFER);
    }

    public FramedMessageOutputStream(int frameSize) {
        this(frameSize, TCPConstants.DEFAULT_USE_DIRECT_BUFFER);
    }

    public FramedMessageOutputStream(int frameSize, boolean useDirectBuffer) {
        this.useDirectBuffer = useDirectBuffer;
        headerBuffer = ByteBufferFactory.allocateView(frameSize, useDirectBuffer);
        setFrameSize(frameSize);
    }

    public void setFrameSize(final int frameSize) {
        this.frameSize = frameSize;
        payloadlengthLength = calculatePayloadLengthLength(frameSize);
        outputBuffer = ByteBufferFactory.allocateView(frameSize, useDirectBuffer);
    }

    public boolean isDirectMode() {
        return isDirectMode;
    }

    public void setDirectMode(final boolean isDirectMode) {
        reset();
        this.isDirectMode = isDirectMode;
        try {
            buildHeader();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setSocketChannel(final SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void setChannelId(final int channelId) {
        this.channelId = channelId;
    }

    public void setMessageId(final int messageId) {
        this.messageId = messageId;
    }

    public void setContentId(final int contentId) {
        this.contentId = contentId;
    }

    public void setContentProperty(int key, String value) {
        this.contentProps.put(key, value);
    }

    public void addAllContentProperties(Map<Integer, String> properties) {
        this.contentProps.putAll(properties);
    }

    @Override
    public void write(final int data) throws IOException {
        if (!outputBuffer.hasRemaining()) {
            flushFrame();
        }

        outputBuffer.put((byte) data);
    }

    @Override
    public void write(final byte[] data, int offset, int size) throws IOException {
        while(size > 0) {
            final int bytesToWrite = Math.min(size, outputBuffer.remaining());
            outputBuffer.put(data, offset, bytesToWrite);
            size -= bytesToWrite;
            offset += bytesToWrite;
            if (!outputBuffer.hasRemaining() && size > 0) {
                flushFrame();
            }
        }
    }

    public void flushLast() throws IOException {
        if (!isFlushLast) {
            outputBuffer.flip();
            isFlushLast = true;

            flushBuffer();
            outputBuffer.clear();
        }
    }

    public void buildHeader() throws IOException {
        headerBuffer.clear();
        if (!isDirectMode) {
            // Write channel-id
            frameMessageIdHighValue = DataInOutUtils.writeInt4(headerBuffer, channelId, 0, false);
            frameMessageIdPosition = headerBuffer.position();
            boolean isFrameWithParameters = FrameType.isFrameContainsParams(messageId) && frameNumber == 0;

            // Write message-id without counting with possible chunking
            int highValue = DataInOutUtils.writeInt4(headerBuffer, messageId, frameMessageIdHighValue, !isFrameWithParameters);

            if (isFrameWithParameters) {
                // If required - serialize frame content-id, content-parameters
                // Write content-id
                highValue = DataInOutUtils.writeInt4(headerBuffer, contentId, highValue, false);

                final int propsCount = contentProps.size();
                // Write number-of-parameters
                highValue = DataInOutUtils.writeInt4(headerBuffer, propsCount, highValue, propsCount == 0);

                for(Map.Entry<Integer, String> entry : contentProps.entrySet()) {
                    final String value = entry.getValue();
                    byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
                    // Write parameter-id
                    highValue = DataInOutUtils.writeInt4(headerBuffer, entry.getKey(), highValue, false);
                    // Write parameter-value buffer length
                    DataInOutUtils.writeInt4(headerBuffer, valueBytes.length, highValue, true);
                    // Write parameter-value
                    headerBuffer.put(valueBytes);
                    highValue = 0;
                }
            }

            initOutputBuffer();
        }
    }

    private void flushBuffer() throws IOException {
        if (!isDirectMode) {
            int approxHeaderSize = headerBuffer.position() + predictPayloadLengthLength();
            final int payloadLength = outputBuffer.remaining() - approxHeaderSize;

            if (messageId == FrameType.MESSAGE) {
                // If message will be chunked - update message-id
                updateMessageIdIfRequired(frameMessageIdPosition,
                        frameMessageIdHighValue,
                        isFlushLast);
            }

            // Write payload-length
            DataInOutUtils.writeInt8(headerBuffer, payloadLength);
            headerBuffer.flip();
            // if read header is less, than expected (because of sendingPayloadLength length)
            int diff = approxHeaderSize - headerBuffer.remaining();
            outputBuffer.position(diff);
            outputBuffer.put(headerBuffer);
            outputBuffer.position(diff);

            OutputWriter.flushChannel(socketChannel, outputBuffer);
            sentMessageLength += payloadLength;
            frameNumber++;
        } else {
            OutputWriter.flushChannel(socketChannel, outputBuffer);
        }
    }

    private void updateMessageIdIfRequired(int frameMessageIdPosition,
            int frameMessageIdHighValue, boolean isLastFrame) {

        int frameMessageId;
        if (isLastFrame) {
            if (frameNumber != 0) {
                frameMessageId = FrameType.MESSAGE_END_CHUNK;
            } else {
                // Serialized message-id is correct
                return;
            }
        } else if (frameNumber == 0) {
            frameMessageId = FrameType.MESSAGE_START_CHUNK;
        } else {
            frameMessageId = FrameType.MESSAGE_CHUNK;
        }

        // merge message-id Integer4 data with next value
        if (frameMessageIdHighValue != 0) {
            // merge message-id as lower octet nibble
            headerBuffer.put(frameMessageIdPosition, (byte) ((frameMessageIdHighValue & 0x70) | frameMessageId));
        } else {
            // merge message-id as higher octet nibble
            int value = headerBuffer.get(frameMessageIdPosition);
            headerBuffer.put(frameMessageIdPosition, (byte) ((frameMessageId << 4) | (value & 0xF)));
        }
    }

    public void reset() {
        outputBuffer.clear();
        headerBuffer.clear();
        messageId = -1;
        contentId = -1;
        contentProps.clear();
        frameNumber = 0;
        isFlushLast = false;
        sentMessageLength = 0;
    }

    @Override
    public void activate() {
    }

    @Override
    public void passivate() {
        reset();
        socketChannel = null;
    }

    @Override
    public void close() {
    }

    private void flushFrame() throws IOException {
        outputBuffer.flip();
        if (IS_GROWABLE && outputBuffer.capacity() < MAX_GROW_SIZE) {
            ByteBuffer newOutputByteBuffer = ByteBufferFactory.allocateView(
                    Math.min(outputBuffer.capacity() * 2, MAX_GROW_SIZE),
                    useDirectBuffer);
            newOutputByteBuffer.put(outputBuffer);
            outputBuffer = newOutputByteBuffer;
        } else {
            flushBuffer();
            buildHeader();
        }
    }

    private void initOutputBuffer() {
        outputBuffer.clear();
        outputBuffer.position(headerBuffer.position() + predictPayloadLengthLength());
    }

    private int predictPayloadLengthLength() {
        return IS_GROWABLE ? MAX_PAYLOAD_LENGTH_LENTGTH : payloadlengthLength;
    }

    private static int calculatePayloadLengthLength(int frameSize) {
        return (int) Math.ceil(Math.log(frameSize) / Math.log(2) / 7);
    }
}
