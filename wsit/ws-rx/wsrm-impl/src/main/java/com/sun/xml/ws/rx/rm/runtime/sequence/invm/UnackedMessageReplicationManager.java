/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence.invm;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.ha.ReplicationManager;
import com.sun.xml.ws.api.ha.HaInfo;
import com.sun.xml.ws.api.ha.HighAvailabilityProvider;
import com.sun.xml.ws.commons.ha.HaContext;
import com.sun.xml.ws.commons.ha.StickyKey;
import com.sun.xml.ws.rx.rm.runtime.ApplicationMessage;
import com.sun.xml.ws.rx.rm.runtime.JaxwsApplicationMessage;
import com.sun.xml.ws.rx.rm.runtime.JaxwsApplicationMessage.JaxwsApplicationMessageState;
import java.util.logging.Level;
import org.glassfish.ha.store.api.BackingStore;

final class UnackedMessageReplicationManager implements ReplicationManager<String, ApplicationMessage> {

    private static final Logger LOGGER = Logger.getLogger(UnackedMessageReplicationManager.class);
    private BackingStore<StickyKey, JaxwsApplicationMessageState> unackedMesagesBs;
    private final String loggerProlog;

    public UnackedMessageReplicationManager(final String uniqueEndpointId) {
        this.loggerProlog = "[" + uniqueEndpointId + "_UNACKED_MESSAGES_MANAGER]: ";
        this.unackedMesagesBs = HighAvailabilityProvider.INSTANCE.createBackingStore(
                HighAvailabilityProvider.INSTANCE.getBackingStoreFactory(HighAvailabilityProvider.StoreType.IN_MEMORY),
                uniqueEndpointId + "_UNACKED_MESSAGES_BS",
                StickyKey.class,
                JaxwsApplicationMessageState.class);
    }

    @Override
    public ApplicationMessage load(String key) {
        JaxwsApplicationMessageState state = HighAvailabilityProvider.loadFrom(unackedMesagesBs, new StickyKey(key), null);
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(loggerProlog + "Message state loaded from unacked message backing store for key [" + key + "]: " + ((state == null) ? null : state.toString()));
        }

        JaxwsApplicationMessage message = null;
        if (state != null) {
            message = state.toMessage();
        }

        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(loggerProlog + "Message state converted to a unacked message: " + ((message == null) ? null : message.toString()));
        }
        return message;
    }

    @Override
    public void save(final String key, final ApplicationMessage _value, final boolean isNew) {
        if (!(_value instanceof JaxwsApplicationMessage)) {
            throw new IllegalArgumentException("Unsupported application message type: " + _value.getClass().getName());
        }
        JaxwsApplicationMessageState value = ((JaxwsApplicationMessage) _value).getState();

        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(loggerProlog + "Sending for replication unacked message with a key [" + key + "]: " + value.toString() + ", isNew=" + isNew);
        }

        HaInfo haInfo = HaContext.currentHaInfo();
        if (haInfo != null) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "Existing HaInfo found, using it for unacked message state replication: " + HaContext.asString(haInfo));
            }

            HaContext.udpateReplicaInstance(HighAvailabilityProvider.saveTo(unackedMesagesBs, new StickyKey(key, haInfo.getKey()), value, isNew));
        } else {
            final StickyKey stickyKey = new StickyKey(key);
            final String replicaId = HighAvailabilityProvider.saveTo(unackedMesagesBs, stickyKey, value, isNew);

            haInfo = new HaInfo(stickyKey.getHashKey(), replicaId, false);
            HaContext.updateHaInfo(haInfo);
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "No HaInfo found, created new after unacked message state replication: " + HaContext.asString(haInfo));
            }
        }
    }

    @Override
    public void remove(String key) {
        HighAvailabilityProvider.removeFrom(unackedMesagesBs, new StickyKey(key));
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(loggerProlog + "Removed unacked message from the backing store for key [" + key + "]");
        }
    }

    @Override
    public void close() {
        HighAvailabilityProvider.close(unackedMesagesBs);
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(loggerProlog + "Closed unacked message backing store");
        }
    }

    @Override
    public void destroy() {
        HighAvailabilityProvider.destroy(unackedMesagesBs);
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(loggerProlog + "Destroyed unacked message backing store");
        }
    }
}
