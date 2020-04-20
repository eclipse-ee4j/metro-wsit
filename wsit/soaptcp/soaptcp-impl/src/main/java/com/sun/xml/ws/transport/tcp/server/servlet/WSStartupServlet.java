/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.server.servlet;

import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.transport.http.DeploymentDescriptorParser;
import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import com.sun.xml.ws.transport.tcp.server.TCPAdapter;
import com.sun.xml.ws.transport.tcp.server.TCPContext;
import com.sun.xml.ws.transport.tcp.server.TCPResourceLoader;
import com.sun.xml.ws.transport.tcp.server.TCPServletContext;
import com.sun.xml.ws.transport.tcp.server.WSTCPModule;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jakarta.xml.ws.WebServiceException;

/**
 * WS startup servlet for Servlet based deployment
 * @author JAX-WS team
 */
@SuppressWarnings({"unchecked"})
public final class WSStartupServlet extends HttpServlet
        implements ServletContextAttributeListener, ServletContextListener {
    
    private static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain + ".server");
    
    private static final String JAXWS_RI_RUNTIME = "/WEB-INF/sun-jaxws.xml";
    
    private WSTCPModule registry;
    
    private List<TCPAdapter> adapters;
    
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    }
    
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    }
    
    public void contextInitialized(final ServletContextEvent contextEvent) {
        logger.log(Level.FINE, "WSStartupServlet.contextInitialized");
        final ServletContext servletContext = contextEvent.getServletContext();
        final TCPContext context = new TCPServletContext(servletContext);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        final ServletContainer container = new ServletContainer(servletContext);
        
        try {
            registry = WSTCPModule.getInstance();
            final DeploymentDescriptorParser<TCPAdapter> parser = new DeploymentDescriptorParser<TCPAdapter>(
                    classLoader, new TCPResourceLoader(context), container, TCPAdapter.FACTORY);
            final URL sunJaxWsXml = context.getResource(JAXWS_RI_RUNTIME);
            if(sunJaxWsXml==null)
                throw new WebServiceException(MessagesMessages.WSTCP_0014_NO_JAXWS_DESCRIPTOR());
            adapters = parser.parse(sunJaxWsXml.toExternalForm(), sunJaxWsXml.openStream());
            
            registry.register(servletContext.getContextPath(), adapters);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException("listener.parsingFailed", e);
        }
    }
    
    public void contextDestroyed(final ServletContextEvent contextEvent) {
        logger.log(Level.FINE, "WSStartupServlet.contextDestroyed");
        if (registry != null && adapters != null) {
            registry.free(contextEvent.getServletContext().getContextPath(),
                    adapters);
        }
    }
    
    public void attributeAdded(final ServletContextAttributeEvent scab) {
    }
    
    public void attributeRemoved(final ServletContextAttributeEvent scab) {
    }
    
    public void attributeReplaced(final ServletContextAttributeEvent scab) {
    }
    
    /**
     * Provides access to {@link ServletContext} via {@link Container}. Pipes
     * can get ServletContext from Container and use it to load some resources.
     */
    private static final class ServletContainer extends Container {
        private final ServletContext servletContext;
        
        ServletContainer(final ServletContext servletContext) {
            this.servletContext = servletContext;
        }
        
        public <T> T getSPI(final Class<T> spiType) {
            if (spiType == ServletContext.class) {
                return (T) servletContext;
            }
            return null;
        }
    }
}
