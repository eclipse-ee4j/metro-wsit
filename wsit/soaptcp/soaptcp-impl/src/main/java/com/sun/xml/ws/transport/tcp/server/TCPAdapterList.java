/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.transport.http.DeploymentDescriptorParser.AdapterFactory;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.server.PortAddressResolver;
import com.sun.xml.ws.api.server.WSEndpoint;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 * @author jax-ws team
 */
public final class TCPAdapterList extends AbstractList<TCPAdapter> implements AdapterFactory<TCPAdapter> {
    private final List<TCPAdapter> adapters = new ArrayList<>();
    private final Map<PortInfo, String> addressMap = new HashMap<>();
    
    // TODO: documented because it's used by AS
    @Override
    public TCPAdapter createAdapter(final String name, final String urlPattern, final WSEndpoint<?> endpoint) {
        final TCPAdapter tcpAdapter = new TCPAdapter(name, urlPattern, endpoint);
        adapters.add(tcpAdapter);
        final WSDLPort port = endpoint.getPort();
        if (port != null) {
            PortInfo portInfo = new PortInfo(port.getOwner().getName(),port.getName().getLocalPart(), endpoint.getImplementationClass());
            addressMap.put(portInfo, getValidPath(urlPattern));
        }
        return tcpAdapter;
    }
    
    /**
     * @return urlPattern without "/*"
     */
    private String getValidPath(@NotNull final String urlPattern) {
        if (urlPattern.endsWith("/*")) {
            return urlPattern.substring(0, urlPattern.length() - 2);
        } else {
            return urlPattern;
        }
    }
    
    /**
     * Creates a PortAddressResolver that maps portname to its address
     */
    protected PortAddressResolver createPortAddressResolver(final String baseAddress, final Class<?> endpointImpl) {
        return new PortAddressResolver() {
            @Override
            public String getAddressFor(@NotNull QName serviceName, @NotNull String portName) {
                String urlPattern = addressMap.get(new PortInfo(serviceName,portName, endpointImpl));
                if (urlPattern == null) {
                    //if a WSDL defines more ports, urlpattern is null (portName does not match endpointImpl)
                    //so fallback to the default behaviour where only serviceName/portName is checked
                    for (Map.Entry<PortInfo, String> e : addressMap.entrySet()) {
                        if (serviceName.equals(e.getKey().serviceName) && portName.equals(e.getKey().portName)) {
                                urlPattern = e.getValue();
                                break;
                        }
                    }
                }
                return (urlPattern == null) ? null : baseAddress+urlPattern;
            }
        };
    }
    
    
    @Override
    public TCPAdapter get(final int index) {
        return adapters.get(index);
    }
    
    @Override
    public int size() {
        return adapters.size();
    }

    private static class PortInfo {
        private final QName serviceName;
        private final String portName;
        private final Class<?> implClass;

        PortInfo(@NotNull QName serviceName, @NotNull String portName, Class<?> implClass) {
            this.serviceName = serviceName;
            this.portName = portName;
            this.implClass = implClass;
        }

        @Override
        public boolean equals(Object portInfo) {
            if (portInfo instanceof PortInfo) {
                PortInfo that = (PortInfo)portInfo;
                if (this.implClass == null) {
                    return this.serviceName.equals(that.serviceName) && this.portName.equals(that.portName) && that.implClass == null;
                }
                return this.serviceName.equals(that.serviceName) && this.portName.equals(that.portName) && this.implClass.equals(that.implClass);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int retVal = serviceName.hashCode()+portName.hashCode();
            return implClass != null ? retVal + implClass.hashCode() : retVal;
        }
    }
}
