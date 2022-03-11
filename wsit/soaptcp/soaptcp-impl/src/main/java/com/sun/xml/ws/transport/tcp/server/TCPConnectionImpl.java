/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.server;

import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.transport.tcp.io.Connection;
import com.sun.xml.ws.transport.tcp.io.DataInOutUtils;
import com.sun.xml.ws.transport.tcp.util.ChannelContext;
import com.sun.xml.ws.transport.tcp.util.FrameType;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.transport.tcp.util.WSTCPError;
import com.sun.xml.ws.transport.tcp.util.WSTCPException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import com.sun.istack.NotNull;

/**
 * @author Alexey Stashok
 */
public class TCPConnectionImpl implements WebServiceContextDelegate {
    private final ChannelContext channelContext;
    private final Connection connection;

    private String contentType;
    private int replyStatus;

    private InputStream inputStream;
    private OutputStream outputStream;

    private boolean isHeaderSerialized;

    public TCPConnectionImpl(final ChannelContext channelContext) {
        this.channelContext = channelContext;
        this.connection = channelContext.getConnection();
    }

    public InputStream openInput() throws IOException, WSTCPException {
        inputStream = connection.openInputStream();
        contentType = channelContext.getContentType();
        return inputStream;
    }

    public OutputStream openOutput() throws IOException, WSTCPException {
        setMessageHeaders();

        outputStream = connection.openOutputStream();
        return outputStream;
    }

    public int getStatus() {
        return replyStatus;
    }

    public void setStatus(final int statusCode) {
        replyStatus = statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public void flush() throws IOException, WSTCPException {
        if (outputStream == null) {
            setMessageHeaders();
            outputStream = connection.openOutputStream();
        }

        connection.flush();
    }

    public void close() {
    }

    // Not supported
    @Override
    public Principal getUserPrincipal(final Packet request) {
        return null;
    }

    // Not supported
    @Override
    public boolean isUserInRole(final Packet request, final String role) {
        return false;
    }

    @Override
    public @NotNull String getEPRAddress(@NotNull final Packet request, @NotNull final WSEndpoint endpoint) {
        return channelContext.getTargetWSURI().toString();
    }

    @Override
    public String getWSDLAddress(@NotNull final Packet request,
                                 @NotNull final WSEndpoint endpoint) {
        return null;
    }

    public void sendErrorMessage(WSTCPError message) throws IOException, WSTCPException {
        setStatus(TCPConstants.ERROR);
        OutputStream output = openOutput();
        String description = message.getDescription();
        DataInOutUtils.writeInts4(output, message.getCode(), message.getSubCode(), description.length());
        output.write(description.getBytes(StandardCharsets.UTF_8));
        flush();
    }

    private void setMessageHeaders() throws WSTCPException {
        if (!isHeaderSerialized) {
            isHeaderSerialized = true;

            final int messageId = getMessageId();
            connection.setMessageId(messageId);
            if (FrameType.isFrameContainsParams(messageId)) {
                channelContext.setContentType(contentType);
            }
        }
    }

    private int getMessageId() {
        if (getStatus() == TCPConstants.ONE_WAY) {
            return FrameType.NULL;
        } else if (getStatus() != TCPConstants.OK) {
            return FrameType.ERROR;
        }

        return FrameType.MESSAGE;
    }

    public ChannelContext getChannelContext() {
        return channelContext;
    }
}
