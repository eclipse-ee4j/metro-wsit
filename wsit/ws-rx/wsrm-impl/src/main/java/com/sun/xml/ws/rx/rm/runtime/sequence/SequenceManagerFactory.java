/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence;

import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.commons.WSEndpointCollectionBasedMOMListener;
import com.sun.xml.ws.rx.rm.runtime.LocalIDManager;
import com.sun.xml.ws.rx.rm.runtime.RmConfiguration;
import com.sun.xml.ws.rx.rm.runtime.delivery.DeliveryQueueBuilder;
import com.sun.xml.ws.rx.rm.runtime.sequence.invm.InVmSequenceManager;
import com.sun.xml.ws.rx.rm.runtime.sequence.persistent.PersistentSequenceManager;
import com.sun.xml.ws.server.WSEndpointImpl;
import com.sun.xml.ws.server.WSEndpointMOMProxy;
import org.glassfish.gmbal.ManagedObjectManager;

import java.util.WeakHashMap;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public enum SequenceManagerFactory {
    INSTANCE;

    private final WSEndpointCollectionBasedMOMListener listener;
    private final WeakHashMap<WSEndpoint, SequenceManager> sequenceManagersForDeferredRegistration = new WeakHashMap<WSEndpoint, SequenceManager>();
        
    private SequenceManagerFactory() {
        // TODO: load from external configuration and revert to default if not present

        listener = new WSEndpointCollectionBasedMOMListener(this, SequenceManager.MANAGED_BEAN_NAME, sequenceManagersForDeferredRegistration);
        listener.initialize();
    }

    /**
     * Creates new {@link SequenceManager} instance. This operation should be called only once per endpoint and/or endpoint client.
     *
     * @param persistent specifies whether returned {@link SequenceManager} instance should support persistent message storage
     * @param uniqueEndpointId unique identifier of the WS endpoint for which this particular sequence manager will be used. The endpoint
     * identifier must be different for the client and for the server side.
     * @param inboundQueueBuilder delivery queue builder that will be used to create delivery queue for all newly created inbound sequences
     * @param outboundQueueBuilder delivery queue builder that will be used to create delivery queue for all newly created outbound sequences
     * @return newly created {@link SequenceManager} instance
     */
    public SequenceManager createSequenceManager(boolean persistent, String uniqueEndpointId, DeliveryQueueBuilder inboundQueueBuilder, DeliveryQueueBuilder outboundQueueBuilder, RmConfiguration configuration, Container container, LocalIDManager localIDManager) {
        synchronized (INSTANCE) {
            SequenceManager result;
            if (persistent) {
                result = new PersistentSequenceManager(uniqueEndpointId, inboundQueueBuilder, outboundQueueBuilder, configuration, container, localIDManager);
            } else {
                result = new InVmSequenceManager(uniqueEndpointId, inboundQueueBuilder, outboundQueueBuilder, configuration, container, localIDManager);
            }
            
            ManagedObjectManager mom = configuration.getManagedObjectManager();
            handleMOMRegistration(result, mom, true);

            return result;
        }
    }
    
    /**
     * Disposes the sequence manager properly and unregisters it from the ManagedOnjectManager
     * 
     * @param manager {@link SequenceManager} instance to be disposed
     */
    public void dispose(SequenceManager manager, RmConfiguration configuration) {
        synchronized (INSTANCE) {
            manager.dispose();

            ManagedObjectManager mom = configuration.getManagedObjectManager();
            handleMOMRegistration(manager, mom, false);
        }
    }

    /**
     * Handles (un)registration process of {@code SequenceManager} in {@link ManagedObjectManager}.
     * 
     * @param manager SequenceManager to be (un)registered
     * @param managedObjectManager ManagedObjectManager instance where SequenceManager will be registered at
     * @param register {@code true} if the manager should be registered, {@code false} for unregistration
     */
    private void handleMOMRegistration(final SequenceManager manager, final ManagedObjectManager managedObjectManager, final boolean register) {
        if (manager == null || managedObjectManager == null) {
            return;
        }

        if (!listener.canRegisterAtMOM() && (managedObjectManager instanceof WSEndpointMOMProxy)) {
            // SequenceManager cannot be (un)registered directly so postpone its (un)registration until JMX connection
            // is created
            final WSEndpointMOMProxy endpointMOMProxy = (WSEndpointMOMProxy) managedObjectManager;
            final WSEndpointImpl wsEndpoint = endpointMOMProxy.getWsEndpoint();

            if (register) {
                sequenceManagersForDeferredRegistration.put(wsEndpoint, manager);
            } else {
                sequenceManagersForDeferredRegistration.remove(wsEndpoint);
            }
        } else {
            // 1) SequenceManager can be (un)registered directly - process its (un)registration
            // 2) (not expected) SequenceManager cannot be (un)registered directly but also it cannot be postponed hence
            //    the (un)registration is going to be forced (managedObjectManager (un)registration as well)

            if (register) {
                listener.registerAtMOM(manager, managedObjectManager);
            } else {
                listener.unregisterFromMOM(manager, managedObjectManager);
            }
        }
    }
    
}
