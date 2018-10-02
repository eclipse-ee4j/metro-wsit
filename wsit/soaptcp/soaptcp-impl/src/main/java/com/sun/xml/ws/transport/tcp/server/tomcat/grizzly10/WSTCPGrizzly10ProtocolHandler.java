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

import com.sun.enterprise.web.connector.grizzly.SelectorThread;
import com.sun.enterprise.web.portunif.PortUnificationPipeline;
import com.sun.enterprise.web.portunif.TlsProtocolFinder;
import com.sun.xml.ws.transport.tcp.grizzly.WSTCPProtocolFinder;
import com.sun.xml.ws.transport.tcp.grizzly.WSTCPProtocolHandler;
import com.sun.xml.ws.transport.tcp.server.IncomeMessageProcessor;
import com.sun.xml.ws.transport.tcp.server.tomcat.WSTCPTomcatRegistry;
import com.sun.xml.ws.transport.tcp.server.tomcat.WSTCPTomcatProtocolHandlerBase;
import java.io.IOException;

/**
 * @author Alexey Stashok
 */
public class WSTCPGrizzly10ProtocolHandler extends WSTCPTomcatProtocolHandlerBase {
    private SelectorThread grizzlySelectorThread;
    
    @Override
    public void init() throws Exception {
        super.init();
        
        try {
            grizzlySelectorThread = createSelectorThread();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void run() {
        try {
            grizzlySelectorThread.startEndpoint();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void destroy() throws Exception {
        if (grizzlySelectorThread != null) {
            grizzlySelectorThread.stopEndpoint();
        }
    }
    
    private SelectorThread createSelectorThread() throws IOException, InstantiationException {
        final IncomeMessageProcessor messageProcessor = IncomeMessageProcessor.registerListener(port,
                (WSTCPTomcatRegistry) WSTCPTomcatRegistry.getInstance(), null);
        
        final SelectorThread selectorThread = new SelectorThread() {
            @Override
            protected void rampUpProcessorTask() {}
            @Override
            protected void registerComponents() {}
        };
        
        selectorThread.setPort(port);
        if (readThreadsCount > 0) {
            selectorThread.setSelectorReadThreadsCount(readThreadsCount);
        }
        
        if (maxWorkerThreadsCount >= 0) {
            selectorThread.setMaxThreads(maxWorkerThreadsCount);
        }
        
        if (minWorkerThreadsCount >= 0) {
            selectorThread.setMinThreads(minWorkerThreadsCount);
        }
        
        selectorThread.setPipelineClassName(PortUnificationPipeline.class.getName());
        selectorThread.initEndpoint();
        
        PortUnificationPipeline puPipeline = (PortUnificationPipeline) selectorThread.getProcessorPipeline();
        puPipeline.addProtocolFinder(new WSTCPProtocolFinder());
        puPipeline.addProtocolFinder(new TlsProtocolFinder());
        puPipeline.addProtocolFinder(new HttpRedirectorProtocolFinder());
        
        WSTCPProtocolHandler protocolHandler = new WSTCPProtocolHandler();
        WSTCPProtocolHandler.setIncomingMessageProcessor(messageProcessor);
        puPipeline.addProtocolHandler(protocolHandler);
        
        puPipeline.addProtocolHandler(new HttpRedirectorProtocolHandler(redirectHttpPort));
        
        return selectorThread;
    }
}
