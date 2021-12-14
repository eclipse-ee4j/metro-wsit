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

import com.sun.xml.ws.transport.tcp.client.WSConnectionManager;
import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import com.sun.xml.ws.transport.tcp.servicechannel.ServiceChannelException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Alexey Stashok
 */
public final class WSTCPURI implements com.sun.xml.ws.transport.tcp.connectioncache.spi.transport.ContactInfo<ConnectionSession> {
    public String host;
    public int port;
    public String path;
    
    // The TCP port, where connection will be established.
    // If -1 then port value will be used.
    public int customPort = -1;

    private String uri2string;
    private Map<String, String> params;
    
    /**
     * This constructor should be used just by JAXB runtime
     */
    public WSTCPURI() {}
    
    private WSTCPURI(String host, int port, String path, Map<String, String> params, String uri2string) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.params = params;
        this.uri2string = uri2string;
    }
    
    public String getParameter(final String name) {
        if (params != null) {
            return params.get(name);
        }
        
        return null;
    }
    
    public static WSTCPURI parse(final String uri) {
        try {
            return parse(new URI(uri));
        } catch (URISyntaxException ex) {
            return null;
        }
    }
    
    public static WSTCPURI parse(final URI uri) {
        final String path = uri.getPath();
        final String query = uri.getQuery();
        Map<String, String> params = null;
        
        if (query != null && query.length() > 0) {
            final String[] paramsStr = query.split(";");
            params = new HashMap<>(paramsStr.length);
            for(String paramStr : paramsStr) {
                if (paramStr.length() > 0) {
                    final String[] paramAsgn = paramStr.split("=");
                    if (paramAsgn != null && paramAsgn.length == 2 && paramAsgn[0].length() > 0 && paramAsgn[1].length() > 0) {
                        params.put(paramAsgn[0], paramAsgn[1]);
                    }
                }
            }
        }
        
        return new WSTCPURI(uri.getHost(), uri.getPort(), path, params, uri.toASCIIString());
    }

    /**
     * Get custom TCP port, where connection should be established
     * @return custom TCP port
     */
    public int getCustomPort() {
        return customPort;
    }

    /**
     * Set custom TCP port, where connection should be established
     * @param customPort custom TCP port
     */
    public void setCustomPort(int customPort) {
        this.customPort = customPort;
    }
    
    public int getEffectivePort() {
        if (customPort == -1) {
            return port;
        }
        
        return customPort;
    }
    
    @Override
    public String toString() {
        return uri2string;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WSTCPURI) {
            WSTCPURI toCmp = (WSTCPURI) o;
            boolean basicResult = (port == toCmp.port && host.equals(toCmp.host));
            if (customPort == -1 && toCmp.customPort == -1) {
                return basicResult;
            } else {
                return basicResult && (customPort == toCmp.customPort);
            }
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return host.hashCode() + (port << 2) + customPort;
    }

    @Override
    public ConnectionSession createConnection() throws IOException {
        try {
            return WSConnectionManager.getInstance().createConnectionSession(this);
        } catch (VersionMismatchException e) {
            throw new IOException(e.getMessage());
        } catch (ServiceChannelException e) {
            throw new IOException(MessagesMessages.WSTCP_0024_SERVICE_CHANNEL_EXCEPTION(e.getFaultInfo().getErrorCode(), e.getMessage()));
        }
    }
    
    /**
     * Class is used to translate WSTCPURI to String and vice versa
     * This is used in JAXB serialization/deserialization
     */
    public static final class WSTCPURI2StringJAXBAdapter extends XmlAdapter<String, WSTCPURI> {
        @Override
        public String marshal(final WSTCPURI tcpURI) {
            return tcpURI.toString();
        }

        @Override
        public WSTCPURI unmarshal(final String uri) {
            return WSTCPURI.parse(uri);
        }
        
    }
}
