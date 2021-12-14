/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.util;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.SOAPBindingCodec;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.ws.transport.tcp.encoding.WSTCPFastInfosetStreamCodec;
import com.sun.xml.ws.transport.tcp.encoding.WSTCPFastInfosetStreamReaderRecyclable;
import com.sun.xml.ws.transport.tcp.io.Connection;
import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;


public class ChannelContext implements WSTCPFastInfosetStreamReaderRecyclable.RecycleAwareListener {
    private static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain);
    
    // tcp connection session this channel belongs to
    private final ConnectionSession connectionSession;
    
    /**
     * Channel settings aggreed during client-service handshaking
     */
    private final ChannelSettings channelSettings;
    
    /**
     * Codec used to encode/decode messages on this channel
     */
    private Codec codec;
    
    // Temp storage for decode content type from String representation
    private final ContentType contentType = new ContentType();
    
    public ChannelContext(@NotNull final ConnectionSession connectionSession,
            @NotNull final ChannelSettings channelSettings) {
        this.connectionSession = connectionSession;
        this.channelSettings = channelSettings;
    }
    
    /**
     * Return TCP session object where which this virual channel is open on
     */
    public @NotNull ConnectionSession getConnectionSession() {
        return connectionSession;
    }
    
    /**
     * Return channel settings, which were aggreed during handshake phase
     */
    public @NotNull ChannelSettings getChannelSettings() {
        return channelSettings;
    }
    
    /**
     * Return message Codec, which is used for encoding/decoding messages
     * on this virtual channel
     */
    public @Nullable Codec getCodec() {
        return codec;
    }
    
    private void setCodec(@NotNull final Codec codec) {
        this.codec = codec;
    }
    
    /**
     * Return TCP connection object, where this virtual channel is acting on
     */
    public @NotNull Connection getConnection() {
        return connectionSession.getConnection();
    }
    
    /**
     * Return channel id
     */
    public int getChannelId() {
        return channelSettings.getChannelId();
    }
    
    /**
     * Return virtual channel's correspondent service name
     */
    public @NotNull QName getWSServiceName() {
        return channelSettings.getWSServiceName();
    }
    
    public void setWSServiceName(@NotNull final QName wsServiceName) {
        channelSettings.setWSServiceName(wsServiceName);
    }
    
    /**
     * Return correspondent WS's URI
     */
    public @Nullable WSTCPURI getTargetWSURI() {
        return channelSettings.getTargetWSURI();
    }
    
    /**
     * Sets message's content type to TCP protocol specific representation
     */
    public void setContentType(@NotNull final String contentTypeS) throws WSTCPException {
        Connection connection = connectionSession.getConnection();
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, MessagesMessages.WSTCP_1120_CHANNEL_CONTEXT_ENCODE_CT(contentTypeS));
        }
        contentType.parse(contentTypeS);
        
        int mt = encodeMimeType(contentType.getMimeType());
        
        connection.setContentId(mt);
        final Map<String, String> parameters = contentType.getParameters();
        for(Map.Entry<String, String> parameter : parameters.entrySet()) {
            final int paramId = encodeParam(parameter.getKey());
            connection.setContentProperty(paramId, parameter.getValue());
        }
        
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, MessagesMessages.WSTCP_1121_CHANNEL_CONTEXT_ENCODED_CT(mt, parameters));
        }
    }
    
    /**
     * Gets message's content type from TCP protocol specific representation
     */
    public @NotNull String getContentType() throws WSTCPException {
        Connection connection = connectionSession.getConnection();
        final int mimeId = connection.getContentId();
        Map<Integer, String> params = connection.getContentProperties();
        
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, MessagesMessages.WSTCP_1122_CHANNEL_CONTEXT_DECODE_CT(mimeId, params));
        }
        
        String mimeType = decodeMimeType(mimeId);
        
        String contentTypeStr = mimeType;
        if (params.size() > 0) {
            final StringBuilder ctBuf = new StringBuilder(contentTypeStr);
            for(Map.Entry<Integer, String> parameter : params.entrySet()) {
                ctBuf.append(';');
                final String paramKey = decodeParam(parameter.getKey());
                final String paramValue = parameter.getValue();
                ctBuf.append(paramKey);
                ctBuf.append('=');
                ctBuf.append(paramValue);
            }
            contentTypeStr = ctBuf.toString();
        }
        
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, MessagesMessages.WSTCP_1123_CHANNEL_CONTEXT_DECODED_CT(contentTypeStr));
        }
        return contentTypeStr;
    }
    
    public int encodeMimeType(@NotNull final String mimeType) throws WSTCPException {
        int contentId = channelSettings.getNegotiatedMimeTypes().indexOf(mimeType);
        if (contentId != -1) {
            return contentId;
        }
        
        throw new WSTCPException(WSTCPError.createNonCriticalError(TCPConstants.UNKNOWN_CONTENT_ID,
                MessagesMessages.WSTCP_0011_UNKNOWN_CONTENT_TYPE(mimeType)));
    }
    
    public @NotNull String decodeMimeType(final int contentId) throws WSTCPException {
        String mimeType = channelSettings.getNegotiatedMimeTypes().get(contentId);
        
        if (mimeType != null) {
            return mimeType;
        }
        throw new WSTCPException(WSTCPError.createNonCriticalError(TCPConstants.UNKNOWN_CONTENT_ID,
                MessagesMessages.WSTCP_0011_UNKNOWN_CONTENT_TYPE(contentId)));
    }

    public int encodeParam(@NotNull final String paramStr) throws WSTCPException {
        int paramId = channelSettings.getNegotiatedParams().indexOf(paramStr);
        if (paramId != -1) {
            return paramId;
        }
        
        throw new WSTCPException(WSTCPError.createNonCriticalError(TCPConstants.UNKNOWN_PARAMETER_ID,
                MessagesMessages.WSTCP_0010_UNKNOWN_PARAMETER(paramStr)));
    }
    
    public @NotNull String decodeParam(final int paramId) throws WSTCPException {
        String paramStr = channelSettings.getNegotiatedParams().get(paramId);
        
        if (paramStr != null) {
            return paramStr;
        }
        throw new WSTCPException(WSTCPError.createNonCriticalError(TCPConstants.UNKNOWN_PARAMETER_ID,
                MessagesMessages.WSTCP_0010_UNKNOWN_PARAMETER(paramId)));
    }
    
    /**
     * Configure Codec according to channel settings
     */
    public static void configureCodec(@NotNull final ChannelContext channelContext,
            @NotNull final SOAPVersion soapVersion,
    @NotNull final Codec defaultCodec) {
        final List<String> supportedMimeTypes = channelContext.getChannelSettings().getNegotiatedMimeTypes();
        if (supportedMimeTypes != null) {
            if (supportedMimeTypes.contains(MimeTypeConstants.FAST_INFOSET_STATEFUL_SOAP11) ||
                    supportedMimeTypes.contains(MimeTypeConstants.FAST_INFOSET_STATEFUL_SOAP12)) {
                logger.log(Level.FINEST, "ChannelContext.configureCodec: FI Stateful");
                StreamSOAPCodec streamSoapCodec = defaultCodec instanceof SOAPBindingCodec ?
                    ((SOAPBindingCodec) defaultCodec).getXMLCodec() : null;
                channelContext.setCodec(WSTCPFastInfosetStreamCodec.create(streamSoapCodec, soapVersion, channelContext, true));
                return;
            } else if (supportedMimeTypes.contains(MimeTypeConstants.FAST_INFOSET_SOAP11) ||
                    supportedMimeTypes.contains(MimeTypeConstants.FAST_INFOSET_SOAP12)) {
                logger.log(Level.FINEST, "ChannelContext.configureCodec: FI Stateless");
                StreamSOAPCodec streamSoapCodec = defaultCodec instanceof SOAPBindingCodec ?
                    ((SOAPBindingCodec) defaultCodec).getXMLCodec() : null;
                channelContext.setCodec(WSTCPFastInfosetStreamCodec.create(streamSoapCodec, soapVersion, channelContext, false));
                return;
            }
        }
        
        logger.log(Level.FINEST, "ChannelContext.configureCodec: default");
        channelContext.setCodec(defaultCodec);
    }
    
    @Override
    public String toString() {
        return String.format("ID: %d\nURI: %s\nCodec:%s", getChannelId(), getTargetWSURI(), getCodec());
    }
    
    @Override
    public void onRecycled() {
        connectionSession.onReadCompleted();
    }
}
