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
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.transport.tcp.util.ChannelContext;
import com.sun.xml.ws.transport.tcp.util.VersionMismatchException;
import com.sun.xml.ws.transport.tcp.util.WSTCPException;
import com.sun.xml.ws.transport.tcp.util.WSTCPURI;
import com.sun.xml.ws.transport.tcp.servicechannel.ServiceChannelException;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import java.io.InputStream;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;

/**
 * @author Alexey Stashok
 */
public class TCPTransportPipe extends AbstractTubeImpl {
    private static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain + ".client");

    protected TCPClientTransport clientTransport = new TCPClientTransport();

    final protected Codec defaultCodec;
    final protected WSBinding wsBinding;
    final protected WSService wsService;
    final protected int customTCPPort;

    public TCPTransportPipe(final ClientTubeAssemblerContext context) {
        this(context, -1);
    }

    public TCPTransportPipe(ClientTubeAssemblerContext context, int customTCPPort) {
        this(context.getService(), context.getBinding(), context.getCodec(), customTCPPort);
    }

    protected TCPTransportPipe(final WSService wsService, final WSBinding wsBinding,
            final Codec defaultCodec, final int customTCPPort) {
        this.wsService = wsService;
        this.wsBinding = wsBinding;
        this.defaultCodec = defaultCodec;
        this.customTCPPort = customTCPPort;
    }

    protected TCPTransportPipe(final TCPTransportPipe that, final TubeCloner cloner) {
        this(that.wsService, that.wsBinding, that.defaultCodec.copy(), that.customTCPPort);
        cloner.add(that, this);
    }

    @Override
    public void preDestroy() {
        if (clientTransport != null && clientTransport.getConnectionContext() != null) {
            WSConnectionManager.getInstance().closeChannel(clientTransport.getConnectionContext());
        }
    }

    @Override
    public AbstractTubeImpl copy(TubeCloner cloner) {
        return new TCPTransportPipe(this, cloner);
    }

    @Override
    public NextAction processRequest(Packet request) {
        return doReturnWith(process(request));
    }

    @Override
    public NextAction processResponse(Packet response) {
        throw new IllegalStateException("TCPTransportPipe's processResponse shouldn't be called.");
    }

    @Override
    public NextAction processException(Throwable t) {
        throw new IllegalStateException("TCPTransportPipe's processException shouldn't be called.");
    }

    @Override
    public Packet process(final Packet packet) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, MessagesMessages.WSTCP_1010_TCP_TP_PROCESS_ENTER(packet.endpointAddress));
        }
        ChannelContext channelContext = null;
        WebServiceException failure = null;
        final WSConnectionManager wsConnectionManager = WSConnectionManager.getInstance();

        int retryNum = 0;
        do {
            try {
                setupClientTransport(wsConnectionManager, packet.endpointAddress.getURI());
                channelContext = clientTransport.getConnectionContext();

                wsConnectionManager.lockConnection(channelContext.getConnectionSession());

                // Taking Codec from ChannelContext
                final Codec codec = channelContext.getCodec();
                final ContentType ct = codec.getStaticContentType(packet);
                clientTransport.setContentType(ct.getContentType());
                /* write transport SOAPAction header if required
                 * in HTTP this param is sent as HTTP header, in SOAP/TCP
                 * it is part of content-type (similar to SOAP 1.2) */
                writeTransportSOAPActionHeaderIfRequired(channelContext, ct, packet);

                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, MessagesMessages.WSTCP_1013_TCP_TP_PROCESS_ENCODE(ct.getContentType()));
                }
                codec.encode(packet, clientTransport.openOutputStream());

                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, MessagesMessages.WSTCP_1014_TCP_TP_PROCESS_SEND());
                }
                clientTransport.send();

                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, MessagesMessages.WSTCP_1015_TCP_TP_PROCESS_OPEN_PREPARE_READING());
                }
                final InputStream replyInputStream = clientTransport.openInputStream();

                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, MessagesMessages.WSTCP_1016_TCP_TP_PROCESS_OPEN_PROCESS_READING(clientTransport.getStatus(), clientTransport.getContentType()));
                }
                if (clientTransport.getStatus() != TCPConstants.ERROR) {
                    final Packet reply = packet.createClientResponse(null);
                    if (clientTransport.getStatus() != TCPConstants.ONE_WAY) {
                        final String contentTypeStr = clientTransport.getContentType();
                        codec.decode(replyInputStream, contentTypeStr, reply);
                    } else {
                        releaseSession(channelContext);
                    }
                    return reply;
                } else {
                    logger.log(Level.SEVERE, MessagesMessages.WSTCP_0016_ERROR_WS_EXECUTION_ON_SERVER(clientTransport.getError()));
                    throw new WSTCPException(clientTransport.getError());
                }
            } catch(ClientTransportException e) {
                abortSession(channelContext);
                failure = e;
            } catch(WSTCPException e) {
                if (e.getError().isCritical()) {
                    abortSession(channelContext);
                } else {
                    releaseSession(channelContext);
                }
                failure = new WebServiceException(MessagesMessages.WSTCP_0016_ERROR_WS_EXECUTION_ON_SERVER(e.getError()), e);
            } catch(IOException e) {
                abortSession(channelContext);
                failure = new WebServiceException(MessagesMessages.WSTCP_0017_ERROR_WS_EXECUTION_ON_CLIENT(), e);
            } catch(ServiceChannelException e) {
                releaseSession(channelContext);
                retryNum = TCPConstants.CLIENT_MAX_FAIL_TRIES + 1;
                failure = new WebServiceException(MessagesMessages.WSTCP_0016_ERROR_WS_EXECUTION_ON_SERVER(e.getFaultInfo().getErrorCode() + ":" + e.getMessage()), e);
            } catch(Exception e) {
                abortSession(channelContext);
                retryNum = TCPConstants.CLIENT_MAX_FAIL_TRIES + 1;
                failure = new WebServiceException(MessagesMessages.WSTCP_0017_ERROR_WS_EXECUTION_ON_CLIENT(), e);
            }

            if (logger.isLoggable(Level.FINE) && canRetry(retryNum + 1)) {
                logger.log(Level.FINE, MessagesMessages.WSTCP_0012_SEND_RETRY(retryNum), failure);
            }
        } while (canRetry(++retryNum));

        assert failure != null;
        logger.log(Level.SEVERE, MessagesMessages.WSTCP_0001_MESSAGE_PROCESS_FAILED(), failure);
        throw failure;
    }

    protected void writeTransportSOAPActionHeaderIfRequired(ChannelContext channelContext, ContentType ct, Packet packet) {
        String soapActionTransportHeader = getSOAPAction(ct.getSOAPActionHeader(), packet);
        if (soapActionTransportHeader != null) {
            try {
                int transportSoapActionParamId = channelContext.encodeParam(TCPConstants.TRANSPORT_SOAP_ACTION_PROPERTY);
                channelContext.getConnection().setContentProperty(transportSoapActionParamId, soapActionTransportHeader);
            } catch (WSTCPException ex) {
                logger.log(Level.WARNING, MessagesMessages.WSTCP_0032_UNEXPECTED_TRANSPORT_SOAP_ACTION(), ex);
            }
        }
    }

    protected void abortSession(final ChannelContext channelContext) {
        if (channelContext != null) {
            WSConnectionManager.getInstance().abortConnection(channelContext.getConnectionSession());
        }
    }

    protected void releaseSession(final ChannelContext channelContext) {
        if (channelContext != null) {
            WSConnectionManager.getInstance().freeConnection(channelContext.getConnectionSession());
        }
    }

    private @NotNull void setupClientTransport(@NotNull final WSConnectionManager wsConnectionManager,
            @NotNull final URI uri) throws InterruptedException, IOException, ServiceChannelException, VersionMismatchException {

        final WSTCPURI tcpURI = WSTCPURI.parse(uri);
        if (tcpURI == null) throw new WebServiceException(MessagesMessages.WSTCP_0005_INVALID_EP_URL(uri.toString()));
        tcpURI.setCustomPort(customTCPPort);
        final ChannelContext channelContext = wsConnectionManager.openChannel(tcpURI, wsService, wsBinding, defaultCodec);
        clientTransport.setup(channelContext);
    }

    /**
     * get SOAPAction header if the soapAction parameter is non-null or BindingProvider properties set.
     * BindingProvider properties take precedence.
     */
    private @Nullable String getSOAPAction(String soapAction, Packet packet) {
        Boolean useAction = (Boolean) packet.invocationProperties.get(BindingProvider.SOAPACTION_USE_PROPERTY);
        String sAction = null;
        boolean use = (useAction != null) ? useAction.booleanValue() : false;

        if (use) {
            //TODO check if it needs to be quoted
            sAction = packet.soapAction;
        }
        //request Property soapAction overrides wsdl
        if (sAction != null) {
            return sAction;
        } else {
            return soapAction;
        }
    }

    private static boolean canRetry(int retryNum) {
        return retryNum <= TCPConstants.CLIENT_MAX_FAIL_TRIES;
    }
}
