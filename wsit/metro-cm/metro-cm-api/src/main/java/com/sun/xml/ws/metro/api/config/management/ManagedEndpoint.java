/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.metro.api.config.management;

import com.sun.istack.NotNull;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.config.management.EndpointCreationAttributes;
import com.sun.xml.ws.api.config.management.Reconfigurable;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ServiceDefinition;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSEndpoint.CompletionCallback;
import com.sun.xml.ws.api.server.WSEndpoint.PipeHead;
import com.sun.xml.ws.config.management.ManagementMessages;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.wsdl.OperationDispatcher;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.WebServiceException;

import org.glassfish.gmbal.ManagedObjectManager;
import org.w3c.dom.Element;

/**
 * Wraps an existing WSEndpoint instance and provides a method to swap the
 * WSEndpoint instance. This class also brings up the management communication
 * interfaces when it is instantiated.
 *
 * This class forwards all method invocations to the wrapped WSEndpoint instance.
 *
 * @param <T> The implementation class of the endpoint.
 *
 * @author Fabian Ritzmann, Martin Grebac
 */
public class ManagedEndpoint<T> extends WSEndpoint<T>{

    private static final Logger LOGGER = Logger.getLogger(ManagedEndpoint.class);

    private WSEndpoint<T> endpointDelegate;
    private final Collection<ReconfigNotifier> reconfigNotifiers = new LinkedList<>();

//     Delay before dispose is called on a replaced endpoint delegate. Defaults to 2 minutes.
    private static final long ENDPOINT_DISPOSE_DELAY_DEFAULT = 120000L;
    private long endpointDisposeDelay = ENDPOINT_DISPOSE_DELAY_DEFAULT;
    private volatile ScheduledExecutorService executorService;
    private final EndpointCreationAttributes creationAttributes;

    private boolean useContainerSpi = true;

    private final AtomicBoolean isClosed;

    /**
     * Initializes this endpoint.
     *
     * @param endpoint The wrapped WSEndpoint instance.
     * @param attributes Several attributes that were used to create the original WSEndpoint
     *   instance and that cannot be queried from WSEndpoint itself. This is used by
     *   the communication API to recreate WSEndpoint instances with the same parameters.
     */
    public ManagedEndpoint(final WSEndpoint<T> endpoint, final EndpointCreationAttributes attributes) {
            this.creationAttributes = attributes;
            this.endpointDelegate = endpoint;
            this.isClosed = new AtomicBoolean(false);
            for (ReconfigNotifier notifier : this.reconfigNotifiers) {
                notifier.sendNotification();
            }

            if (LOGGER.isLoggable(Level.CONFIG)) {
                LOGGER.config(ManagementMessages.WSM_5066_STARTING_ENDPOINT());
            }
    }

    /**
     * @param notifier Callback object allows us to send a notification when the
     *   endpoint was reconfigured.
     */
    public void addNotifier(final ReconfigNotifier notifier) {
        this.reconfigNotifiers.add(notifier);
    }

    /**
     * Returns attributes used for creation of this endpoint.
     */
    public EndpointCreationAttributes getCreationAttributes() {
        return this.creationAttributes;
    }

    /**
     * Sets a new WSEndpoint instance to which method calls will be forwarded from
     * then on.
     *
     * @param endpoint The WSEndpoint instance. May not be null.
     */
    synchronized public void swapEndpointDelegate(final WSEndpoint<T> endpoint) {
        // Plug in code that regenerates WSDL when the endpoint was reconfigured.
        final Set<Component> endpointComponents = endpoint.getComponents();

        final WSEndpoint<T> oldEndpoint = this.endpointDelegate;
        this.endpointDelegate = endpoint;
        for (Component component : endpointComponents) {
            final Reconfigurable reconfigurable = component.getSPI(Reconfigurable.class);
            if (reconfigurable != null) {
                reconfigurable.reconfigure();
            }
        }
        disposeDelegate(oldEndpoint);
        LOGGER.info(ManagementMessages.WSM_5000_RECONFIGURED_ENDPOINT(this/*.id*/));
    }

    @Override
    synchronized public void dispose() {
        this.isClosed.compareAndSet(false, true);
        //no need to dispose thread pool if we got thread pool from container
        if (!useContainerSpi) {
            if (this.executorService != null) {
                this.executorService.shutdown();
            }
        }
        //either way we should dispose the endpoint delegate
        if (this.endpointDelegate != null) {
            this.endpointDelegate.dispose();
        }
    }

    @Override
    public Codec createCodec() {
        return this.endpointDelegate.createCodec();
    }

    @Override
    public QName getServiceName() {
        return this.endpointDelegate.getServiceName();
    }

    @Override
    public QName getPortName() {
        return this.endpointDelegate.getPortName();
    }

    @Override
    public Class<T> getImplementationClass() {
        return this.endpointDelegate.getImplementationClass();
    }

    @Override
    public WSBinding getBinding() {
        return this.endpointDelegate.getBinding();
    }

    @Override
    public Container getContainer() {
        return this.endpointDelegate.getContainer();
    }

    @Override
    public WSDLPort getPort() {
        return this.endpointDelegate.getPort();
    }

    @Override
    public void setExecutor(Executor exec) {
        this.endpointDelegate.setExecutor(exec);
    }

    @Override
    public void schedule(Packet request, CompletionCallback callback, FiberContextSwitchInterceptor interceptor) {
        this.endpointDelegate.schedule(request, callback, interceptor);
    }

    @Override
    public void process(Packet request, CompletionCallback callback, FiberContextSwitchInterceptor interceptor) {
        this.endpointDelegate.process(request, callback, interceptor);
    }

    @Override
    public PipeHead createPipeHead() {
        return this.endpointDelegate.createPipeHead();
    }

    @Override
    public ServiceDefinition getServiceDefinition() {
        return this.endpointDelegate.getServiceDefinition();
    }

    @Override
    public @NotNull Set<Component> getComponents() {
            return this.endpointDelegate.getComponents();
    }

    @Override
    public SEIModel getSEIModel() {
        return this.endpointDelegate.getSEIModel();
    }

    @Override
    public PolicyMap getPolicyMap() {
        return this.endpointDelegate.getPolicyMap();
    }

    @Override
    public ManagedObjectManager getManagedObjectManager() {
        return this.endpointDelegate.getManagedObjectManager();
    }

    @Override
    public void closeManagedObjectManager() {
        this.endpointDelegate.closeManagedObjectManager();
    }

    @Override
    public ServerTubeAssemblerContext getAssemblerContext() {
        return this.endpointDelegate.getAssemblerContext();
    }

    /**
     * Call dispose on the endpoint delegate that was swapped out against a new
     * instance.
     *
     * @param endpoint The previous endpoint delegate
     */
    private void disposeDelegate(final WSEndpoint<T> endpoint) {
        final Runnable dispose = new Runnable() {
            final WSEndpoint<T> disposableEndpoint = endpoint;
            @Override
            public void run() {
                try {
                    disposableEndpoint.dispose();
                } catch (WebServiceException e) {
                    LOGGER.severe(ManagementMessages.WSM_5101_DISPOSE_FAILED(), e);
                }
            }
        };
        getExecutorService().schedule(dispose, this.endpointDisposeDelay, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean equalsProxiedInstance(WSEndpoint endpoint) {
        if (endpointDelegate == null) {
            return (endpoint == null);
        }
        if (endpoint instanceof ManagedEndpoint) {
            return false;
        }
        return endpointDelegate.equals(endpoint);
    }

    @Override
    public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, String address, String wsdlAddress, Element... referenceParameters) {
        return endpointDelegate.getEndpointReference(clazz, address, wsdlAddress, referenceParameters);
    }

    @Override
    public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, String address, String wsdlAddress, List<Element> metadata, List<Element> referenceParameters) {
        return endpointDelegate.getEndpointReference(clazz, address, wsdlAddress, metadata, referenceParameters);
    }

    @Override
    public OperationDispatcher getOperationDispatcher() {
        return endpointDelegate.getOperationDispatcher();
    }

    @Override
    public Packet createServiceResponseForException(ThrowableContainerPropertySet tcps, Packet packet, SOAPVersion soapv, WSDLPort wsdlp, SEIModel seim, WSBinding wsb) {
        return endpointDelegate.createServiceResponseForException(tcps, packet, soapv, wsdlp, seim, wsb);
    }

    /**
     * Return the appropriate ScheduledExecutorService - on initial access, check for container.getSPI
     * NOTE - THIS METHOD IS A COPY OF {@code com.sun.xml.ws.commons.AbstractTaskManager#getExecutorService() AbstractTaskManager.getExecutorService() } IN metro-commons!!!
     * IF A SUITABLE COMMON LOCATION CAN BE FOUND IT MUST BE REMOVED FROM HERE!
     *
     */
    private ScheduledExecutorService getExecutorService() {
        if (executorService == null) {
            synchronized (this) {
                if (executorService == null) {
                    if (getContainer() != null) {
                        executorService = getContainer().getSPI(ScheduledExecutorService.class);
                    }
                    if (executorService == null) {
                        //container did not return an SPI - create our own thread pool
                        LOGGER.finer("Container did not return SPI for ScheduledExecutorService - creating thread pool for dispose");
                        executorService = Executors.newScheduledThreadPool(getThreadPoolSize());
                        useContainerSpi = false;
                    } else {
                        LOGGER.finer("Using Container SPI for ScheduledExecutorService for dispose");
                        useContainerSpi = true;
                    }
                    this.isClosed.set(false);
                }
            }
        }
        return executorService;
    }

    private int getThreadPoolSize() {
        return 1;
    }
}
