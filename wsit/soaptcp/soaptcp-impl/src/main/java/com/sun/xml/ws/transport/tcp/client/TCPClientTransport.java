/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.client;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.DistributedPropertySet;
import com.sun.xml.ws.transport.tcp.io.Connection;
import com.sun.xml.ws.transport.tcp.io.DataInOutUtils;
import com.sun.xml.ws.transport.tcp.util.ChannelContext;
import com.sun.xml.ws.transport.tcp.util.FrameType;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import com.sun.xml.ws.transport.tcp.util.WSTCPError;
import com.sun.xml.ws.transport.tcp.util.WSTCPException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

/**
 * @author Alexey Stashok
 */
public class TCPClientTransport extends DistributedPropertySet {
    private ChannelContext channelContext;
    private Connection connection;

    private InputStream inputStream;
    private OutputStream outputStream;

    // Response status
    private int status;
    // Request/response content type
    private String contentType;

    private WSTCPError error;

    public TCPClientTransport() {
    }

    public TCPClientTransport(final @NotNull ChannelContext channelContext) {
        setup(channelContext);
    }

    public void setup(final @Nullable ChannelContext channelContext) {
        this.channelContext = channelContext;
        if (channelContext != null) {
            this.connection = channelContext.getConnection();
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    /*
     * Getting output stream.
     * Making some stream preparation before
     */
    public @NotNull OutputStream openOutputStream() throws IOException, WSTCPException {
        connection.setChannelId(channelContext.getChannelId());
        connection.setMessageId(FrameType.MESSAGE);
        channelContext.setContentType(contentType);

        outputStream = connection.openOutputStream();
        return outputStream;
    }

    /*
     * Getting input stream.
     * Making some stream preparation before
     */
    public @NotNull InputStream openInputStream() throws IOException, WSTCPException {
        connection.prepareForReading();
        inputStream = connection.openInputStream();
        final int messageId = connection.getMessageId();
        status = convertToReplyStatus(messageId);
        if (FrameType.isFrameContainsParams(messageId)) {
            contentType = channelContext.getContentType();
        }

        if (status == TCPConstants.ERROR) {
            error = parseErrorMessagePayload();
        }

        return inputStream;
    }

    public void send() throws IOException {
        connection.flush();
    }

    public void close() {
        error = null;
        // Perform some cleanings
    }

    public void setContentType(final @NotNull String contentType) {
        this.contentType = contentType;
    }

    public @Nullable String getContentType() {
        return contentType;
    }

    public @Nullable WSTCPError getError() {
        return error;
    }

    private @Nullable WSTCPError parseErrorMessagePayload() throws IOException {
        final int[] params = new int[3];
        DataInOutUtils.readInts4(inputStream, params, 3);
        final int errorCode = params[0];
        final int errorSubCode = params[1];
        final int errorDescriptionBufferLength = params[2];

        final byte[] errorDescriptionBuffer = new byte[errorDescriptionBufferLength];
        DataInOutUtils.readFully(inputStream, errorDescriptionBuffer);

        String errorDescription = new String(errorDescriptionBuffer, StandardCharsets.UTF_8);
        return WSTCPError.createError(errorCode, errorSubCode, errorDescription);
    }

    private int convertToReplyStatus(final int messageId) {
        if (messageId == FrameType.NULL) {
            return TCPConstants.ONE_WAY;
        } else if (messageId == FrameType.ERROR) {
            return TCPConstants.ERROR;
        }

        return TCPConstants.OK;
    }

    @com.sun.xml.ws.api.PropertySet.Property(TCPConstants.CHANNEL_CONTEXT)
    public ChannelContext getConnectionContext() {
        return channelContext;
    }

    private static final PropertyMap model;
    static {
        model = parse(TCPClientTransport.class);
    }

    @Override
    public DistributedPropertySet.PropertyMap getPropertyMap() {
        return model;
    }

    // TODO - remove when these are added to DistributedPropertySet
    public SOAPMessage getSOAPMessage() throws SOAPException {
       throw new UnsupportedOperationException();
    }

    public void setSOAPMessage(SOAPMessage soap) {
       throw new UnsupportedOperationException();
    }

}
