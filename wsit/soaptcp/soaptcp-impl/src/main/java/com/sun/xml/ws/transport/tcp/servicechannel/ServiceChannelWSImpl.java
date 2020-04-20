/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.servicechannel;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.transport.tcp.server.ServerConnectionSession;
import com.sun.xml.ws.transport.tcp.util.BindingUtils;
import com.sun.xml.ws.transport.tcp.util.ChannelContext;
import com.sun.xml.ws.transport.tcp.util.ChannelSettings;
import com.sun.xml.ws.transport.tcp.util.ConnectionSession;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import com.sun.xml.ws.transport.tcp.util.WSTCPURI;
import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import com.sun.xml.ws.transport.tcp.server.TCPAdapter;
import com.sun.xml.ws.transport.tcp.server.WSTCPAdapterRegistry;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.annotation.Resource;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.ws.Holder;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

/**
 * @author Alexey Stashok
 */

@SuppressWarnings({"unchecked"})
@WebService
public class ServiceChannelWSImpl {
    private static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain + ".server");
    
    @Resource
    private WebServiceContext wsContext;
    
    public void initiateSession() throws ServiceChannelException {
        final ChannelContext serviceChannelContext = getChannelContext();
        final ConnectionSession connectionSession = serviceChannelContext.getConnectionSession();
        logger.log(Level.FINE, MessagesMessages.WSTCP_1140_SOAPTCP_SESSION_OPEN(connectionSession.hashCode()));
    }
    
    @WebResult(name = "channelId")
    public int openChannel(
            @WebParam(name="targetWSURI", mode=WebParam.Mode.IN)String targetWSURI,
            @WebParam(name="negotiatedMimeTypes", mode=WebParam.Mode.INOUT) Holder<List<String>> negotiatedMimeTypes,
            @WebParam(name="negotiatedParams", mode=WebParam.Mode.INOUT) Holder<List<String>> negotiatedParams)
            throws ServiceChannelException {
        final ChannelContext serviceChannelContext = getChannelContext();
        final ServerConnectionSession connectionSession = (ServerConnectionSession) serviceChannelContext.getConnectionSession();
        
        final WSTCPAdapterRegistry adapterRegistry = getAdapterRegistry();
        
        final WSTCPURI tcpURI = WSTCPURI.parse(targetWSURI);
        final TCPAdapter adapter = adapterRegistry.getTarget(tcpURI);
        if (adapter == null) throw new ServiceChannelException(ServiceChannelErrorCode.UNKNOWN_ENDPOINT_ADDRESS, MessagesMessages.WSTCP_0034_WS_ENDPOINT_NOT_FOUND(targetWSURI));
        
        final BindingUtils.NegotiatedBindingContent serviceSupportedContent =
                BindingUtils.getNegotiatedContentTypesAndParams(adapter.getEndpoint().getBinding());
        
        negotiatedMimeTypes.value.retainAll(serviceSupportedContent.negotiatedMimeTypes);
        if (negotiatedMimeTypes.value.isEmpty()) {
            throw new ServiceChannelException(ServiceChannelErrorCode.CONTENT_NEGOTIATION_FAILED, MessagesMessages.WSTCP_0033_CONTENT_NEGOTIATION_FAILED(targetWSURI, serviceSupportedContent.negotiatedMimeTypes));
        }
        
        negotiatedParams.value.retainAll(serviceSupportedContent.negotiatedParams);
        
        int channelId = connectionSession.getNextAvailChannelId();
        ChannelSettings channelSettings = new ChannelSettings(negotiatedMimeTypes.value, negotiatedParams.value, channelId, adapter.getEndpoint().getServiceName(), tcpURI);
        final ChannelContext openedChannelContext = new ChannelContext(connectionSession, channelSettings);
        final SOAPVersion soapVersion = adapter.getEndpoint().getBinding().getSOAPVersion();
        final Codec defaultCodec = adapter.getEndpoint().createCodec();
        ChannelContext.configureCodec(openedChannelContext, soapVersion, defaultCodec);
        
        connectionSession.registerChannel(openedChannelContext);
        
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, MessagesMessages.WSTCP_1141_SOAPTCP_CHANNEL_OPEN(connectionSession.hashCode(), openedChannelContext.getChannelId(), targetWSURI));
        }
        return channelId;
    }
    
    public void closeChannel(
            @WebParam(name="channelId", mode=WebParam.Mode.IN) int channelId)  throws ServiceChannelException {
        final ChannelContext serviceChannelContext = getChannelContext();
        final ServerConnectionSession connectionSession = (ServerConnectionSession) serviceChannelContext.getConnectionSession();
        
        if (connectionSession.findWSServiceContextByChannelId(channelId) != null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, MessagesMessages.WSTCP_1142_SOAPTCP_CHANNEL_CLOSE(connectionSession.hashCode(), channelId));
            }
            connectionSession.deregisterChannel(channelId);
        } else {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, MessagesMessages.WSTCP_0035_UNKNOWN_CHANNEL_UD("Session: " + connectionSession.hashCode() + " Channel-id: " + channelId));
            }
            throw new ServiceChannelException(ServiceChannelErrorCode.UNKNOWN_CHANNEL_ID, MessagesMessages.WSTCP_0035_UNKNOWN_CHANNEL_UD(channelId));
        }
    }
    
    private @NotNull ChannelContext getChannelContext() {
        final MessageContext messageContext = wsContext.getMessageContext();
        return (ChannelContext) messageContext.get(TCPConstants.CHANNEL_CONTEXT);
    }
    
    private @NotNull WSTCPAdapterRegistry getAdapterRegistry() {
        final MessageContext messageContext = wsContext.getMessageContext();
        return (WSTCPAdapterRegistry) messageContext.get(TCPConstants.ADAPTER_REGISTRY);
    }
}
