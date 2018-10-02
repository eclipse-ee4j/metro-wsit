/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.server.tomcat.grizzly10;

import com.sun.enterprise.web.connector.grizzly.ByteBufferInputStream;
import com.sun.enterprise.web.portunif.ProtocolHandler;
import com.sun.enterprise.web.portunif.util.ProtocolInfo;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * Redirect the request to the proper protocol, which can be http or https.
 *
 * @author Jeanfrancois Arcand
 */
public class HttpRedirectorProtocolHandler implements ProtocolHandler{
    
    private static final int DEFAULT_HTTP_HEADER_BUFFER_SIZE = 48 * 1024;
    
    /**
     * The protocols supported by this handler.
     */
    protected String[] protocols = {"redirect-https", "redirect-http"};
    
    
    /**
     * Util to redirect protocol.
     */
    private HttpRedirector redirector;
    
    private int redirectPort;
    
    public HttpRedirectorProtocolHandler(int redirectPort) {
        this.redirectPort = redirectPort;
    }
    
    
    /**
     * Redirect the request to the protocol defined in the
     * <code>protocolInfo</code>. Protocols supported are http and https.
     *
     * @param protocolInfo The protocol that needs to be redirected.
     */
    public void handle(ProtocolInfo protocolInfo) throws IOException {
        if (redirector == null){
            redirector = new HttpRedirector(redirectPort);
        }
        
        if (protocolInfo.protocol.equalsIgnoreCase("https")) {
            redirector.redirectSSL(protocolInfo);
        } else {
            redirector.redirect(protocolInfo);
        }
        protocolInfo.keepAlive = false;
        
        /* ======================================================
         * Java HTTP(S) client sends request in 2 chunks: header, payload
         * We need to make sure client started to send payload before redirecting/closing
         * the connection. Otherwise client can not receive "HTTP 302 redirect" response.
         */ 
        ByteBuffer tmpBuffer = protocolInfo.byteBuffer;
        tmpBuffer.clear();
        ByteBufferInputStream is = new ByteBufferInputStream(tmpBuffer);
        try {
            is.setReadTimeout(2);
            is.setSelectionKey(protocolInfo.key);
            int count = 0;
            while (tmpBuffer.hasRemaining() && count < DEFAULT_HTTP_HEADER_BUFFER_SIZE) {
                tmpBuffer.position(tmpBuffer.limit());
                int readBytes = is.read();
                if (readBytes == -1) break;
                count += readBytes;
            }
        } catch(IOException e) {
            // ignore
        } finally {
            is.close();
        }
        //=========================================================
    }
    
    
    /**
     * Returns an array of supported protocols.
     * @return an array of supported protocols.
     */
    public String[] getProtocols() {
        return protocols;
    }
    
    
    /**
     * Invoked when the SelectorThread is about to expire a SelectionKey.
     * @return true if the SelectorThread should expire the SelectionKey, false
     *              if not.
     */
    public boolean expireKey(SelectionKey key){
        return true;
    }
}

