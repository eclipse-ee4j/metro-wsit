/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.config.management.server;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.config.management.EndpointCreationAttributes;
import com.sun.xml.ws.metro.api.config.management.ManagedEndpoint;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ServiceDefinition;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.parser.PolicyResourceLoader;
import com.sun.xml.ws.wsdl.OperationDispatcher;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import jakarta.xml.ws.EndpointReference;

import junit.framework.TestCase;
import org.glassfish.gmbal.ManagedObjectManager;
import org.w3c.dom.Element;

/**
 *
 * @author Fabian Ritzmann
 */
public class EndpointFactoryImplTest extends TestCase {

    public EndpointFactoryImplTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of createEndpoint method, of class EndpointFactoryImpl.
     */
    public void testCreateEndpointPolicyMapNull() {
        WSEndpoint<String> endpoint = new MockEndpoint(null, null, null);
        EndpointCreationAttributes attributes = null;
        EndpointFactoryImpl instance = new EndpointFactoryImpl();
        WSEndpoint<String> expResult = endpoint;
        WSEndpoint<String> result = instance.createEndpoint(endpoint, attributes);
        assertNotSame(expResult, result);
    }

    /**
     * Test of createEndpoint method, of class EndpointFactoryImpl.
     */
    public void testCreateEndpointNoManagedAssertion() throws Exception {
        final URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource("management/factory/managed.wsdl");
        final WSDLModel wsdlModel = PolicyResourceLoader.getWsdlModel(resourceUrl, true);
        final PolicyMap policyMap = wsdlModel.getPolicyMap();
        WSEndpoint<String> endpoint = new MockEndpoint(new QName("http://example.org/", "AddNumbersService"), new QName("http://example.org/", "AddNumbersPort"), policyMap);
        EndpointCreationAttributes attributes = null;
        EndpointFactoryImpl instance = new EndpointFactoryImpl();
        WSEndpoint<String> expResult = endpoint;
        WSEndpoint<String> result = instance.createEndpoint(endpoint, attributes);
        assertNotSame(expResult, result);
    }

    /**
     * Test of createEndpoint method, of class EndpointFactoryImpl.
     */
    public void testCreateEndpointManagedAssertion() throws Exception {
        final URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource("management/factory/unmanaged.wsdl");
        final WSDLModel wsdlModel = PolicyResourceLoader.getWsdlModel(resourceUrl, true);
        final PolicyMap policyMap = wsdlModel.getPolicyMap();
        WSEndpoint<String> endpoint = new MockEndpoint(new QName("http://example.org/", "AddNumbersService"), new QName("http://example.org/", "AddNumbersPort"), policyMap);
        EndpointCreationAttributes attributes = null;
        EndpointFactoryImpl instance = new EndpointFactoryImpl();
        WSEndpoint<String> result = instance.createEndpoint(endpoint, attributes);
        assertFalse(result instanceof ManagedEndpoint);
    }


    private static class MockEndpoint extends WSEndpoint<String> {

        private final QName serviceName;
        private final QName portName;
        private final PolicyMap policyMap;

        public MockEndpoint(final QName service, final QName port, final PolicyMap map) {
            this.serviceName = service;
            this.portName = port;
            this.policyMap = map;
        }

        @Override
        public Codec createCodec() {
            return null;
        }

        @Override
        public QName getServiceName() {
            return this.serviceName;
        }

        @Override
        public QName getPortName() {
            return this.portName;
        }

        @Override
        public Class<String> getImplementationClass() {
            return String.class;
        }

        @Override
        public WSBinding getBinding() {
            return null;
        }

        @Override
        public Container getContainer() {
            return null;
        }

        @Override
        public WSDLPort getPort() {
            return null;
        }

        @Override
        public void setExecutor(Executor exec) {
        }

        @Override
        public void schedule(Packet request, CompletionCallback callback, FiberContextSwitchInterceptor interceptor) {
        }

        @Override
        public PipeHead createPipeHead() {
            return null;
        }

        @Override
        public void dispose() {
        }

        @Override
        public ServiceDefinition getServiceDefinition() {
            return null;
        }

        @Override
        public SEIModel getSEIModel() {
            return null;
        }

        @Override
        public PolicyMap getPolicyMap() {
            return this.policyMap;
        }

        @Override
        public ManagedObjectManager getManagedObjectManager() {
            return null;
        }

        @Override
        public ServerTubeAssemblerContext getAssemblerContext() {
            return null;
        }

        @Override
        public void closeManagedObjectManager() {
        }

        @Override
        public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, String address, String wsdlAddress, Element... referenceParameters) {
            return null;
        }

        @Override
        public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, String address, String wsdlAddress, List<Element> metadata, List<Element> referenceParameters) {
            return null;
        }

        @Override
        public OperationDispatcher getOperationDispatcher() {
            return null;
        }

        @Override
        public Packet createServiceResponseForException(ThrowableContainerPropertySet tc, Packet responsePacket, SOAPVersion soapVersion, WSDLPort wsdlPort, SEIModel seiModel, WSBinding binding) {
            return null;
        }
    }
}
