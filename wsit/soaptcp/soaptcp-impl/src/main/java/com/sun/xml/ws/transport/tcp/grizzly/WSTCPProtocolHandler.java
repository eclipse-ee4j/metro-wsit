/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.grizzly;

import com.sun.enterprise.web.portunif.ProtocolHandler;
import com.sun.enterprise.web.portunif.util.ProtocolInfo;
import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import com.sun.xml.ws.transport.tcp.server.IncomeMessageProcessor;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alexey Stashok
 */
public final class WSTCPProtocolHandler implements ProtocolHandler {
    private static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain + ".server");
    
    private static IncomeMessageProcessor processor;
    
    public static void setIncomingMessageProcessor(final IncomeMessageProcessor processor) {
        WSTCPProtocolHandler.processor = processor;
    }
    
    public String[] getProtocols() {
        return new String[] {TCPConstants.PROTOCOL_SCHEMA};
    }
    
    public void handle(final ProtocolInfo tupple) throws IOException {
        if (processor != null) {
            tupple.byteBuffer.flip();
            processor.process(tupple.byteBuffer, (SocketChannel) tupple.key.channel());
        } else {
            logger.log(Level.WARNING, MessagesMessages.WSTCP_0013_TCP_PROCESSOR_NOT_REGISTERED());
        }
    }
    
    /**
     * Invoked when the SelectorThread is about to expire a SelectionKey.
     * @return true if the SelectorThread should expire the SelectionKey, false
     * if not.
     */
    public boolean expireKey(SelectionKey key) {
        if (processor != null) {
            processor.notifyClosed((SocketChannel) key.channel());
        }
        
        return true;
    }
}
