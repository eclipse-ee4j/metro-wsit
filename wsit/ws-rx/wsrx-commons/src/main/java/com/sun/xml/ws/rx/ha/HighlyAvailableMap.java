/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.ha;

import com.sun.istack.logging.Logger;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sun.xml.ws.api.ha.HaInfo;
import com.sun.xml.ws.api.ha.HighAvailabilityProvider;
import com.sun.xml.ws.commons.ha.HaContext;
import com.sun.xml.ws.commons.ha.StickyKey;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

import org.glassfish.ha.store.api.BackingStore;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class HighlyAvailableMap<K extends Serializable, V> implements Map<K, V> {
    private static final Logger LOGGER = Logger.getLogger(HighlyAvailableMap.class);

    public static final class NoopReplicationManager<K extends Serializable, V> implements ReplicationManager<K, V> {
        private final String loggerProlog;

        public NoopReplicationManager(String name) {
            this.loggerProlog = "[" + name + "]: ";
        }
        @Override
        public V load(K key) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "load() method invoked for key: " + key);
            }
            return null;
        }

        @Override
        public void save(K key, V value, boolean isNew) {
            // noop
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "save() method invoked for [key=" + key + ", value=" + value + ", isNew=" + isNew + "]");
            }
        }

        @Override
        public void remove(K key) {
            // noop
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "remove() method invoked for key: " + key);
            }
        }

        @Override
        public void close() {
            // noop
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "close() invoked");
            }
        }

        @Override
        public void destroy() {
            // noop
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "destroy() invoked");
            }
        }
    }

    public static final class SimpleReplicationManager<K extends Serializable, V extends Serializable> implements ReplicationManager<K, V> {

        private final BackingStore<K, V> backingStore;
        private final String loggerProlog;

        public SimpleReplicationManager(String name, BackingStore<K, V> backingStore) {
            this.backingStore = backingStore;
            this.loggerProlog = "[" + name + "]: ";
        }

        @Override
        public V load(K key) {
            final V data = HighAvailabilityProvider.loadFrom(backingStore, key, null);
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "loaded data for key [" + key + "]: " + data);
            }
            return data;
        }

        @Override
        public void save(K key, V value, boolean isNew) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "sending for replication [key=" + key + ", value=" + value + ", isNew=" + isNew + "]");
            }
            HighAvailabilityProvider.saveTo(backingStore, key, value, isNew);
        }

        @Override
        public void remove(K key) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "removing data for key: " + key);
            }
            HighAvailabilityProvider.removeFrom(backingStore, key);
        }

        @Override
        public void close() {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "closing backing store");
            }
            HighAvailabilityProvider.close(backingStore);
        }

        @Override
        public void destroy() {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "destroying backing store");
            }
            HighAvailabilityProvider.destroy(backingStore);
        }
    }

    public static final class StickyReplicationManager<K extends Serializable, V extends Serializable> implements ReplicationManager<K, V> {

        private final BackingStore<StickyKey, V> backingStore;
        private final String loggerProlog;

        public StickyReplicationManager(String name, BackingStore<StickyKey, V> backingStore) {
            this.backingStore = backingStore;
            this.loggerProlog = "[" + name + "]: ";
        }

        @Override
        public V load(K key) {
            final V data = HighAvailabilityProvider.loadFrom(backingStore, new StickyKey(key), null);
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "loaded data for key [" + key + "]: " + data);
            }
            return data;
        }

        @Override
        public void save(final K key, final V value, final boolean isNew) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "sending for replication [key=" + key + ", value=" + value + ", isNew=" + isNew + "]");
            }

            HaInfo haInfo = HaContext.currentHaInfo();
            if (haInfo != null) {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.finer(loggerProlog + "Existing HaInfo found, using it for data replication: " + HaContext.asString(haInfo));
                }
                HaContext.udpateReplicaInstance(HighAvailabilityProvider.saveTo(backingStore, new StickyKey(key, haInfo.getKey()), value, isNew));
            } else {
                final StickyKey stickyKey = new StickyKey(key);
                final String replicaId = HighAvailabilityProvider.saveTo(backingStore, stickyKey, value, isNew);

                haInfo = new HaInfo(stickyKey.getHashKey(), replicaId, false);
                HaContext.updateHaInfo(haInfo);
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.finer(loggerProlog + "No HaInfo found, created new after data replication: " + HaContext.asString(haInfo));
                }
            }
        }

        @Override
        public void remove(K key) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "removing data for key: " + key);
            }
            HighAvailabilityProvider.removeFrom(backingStore, new StickyKey(key));
        }

        @Override
        public void close() {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "closing backing store");
            }
            HighAvailabilityProvider.close(backingStore);
        }

        @Override
        public void destroy() {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "destroying backing store");
            }
            HighAvailabilityProvider.destroy(backingStore);
        }
    }

    private final Map<K, V> localMap;
    private final ReadWriteLock dataLock = new ReentrantReadWriteLock();
    private final ReplicationManager<K, V> replicationManager;
    private final String loggerProlog;

    public static <K extends Serializable, V extends Serializable> HighlyAvailableMap<K, V> create(final String name, BackingStore<K, V> backingStore) {
        return new HighlyAvailableMap<>(name, new HashMap<>(), new SimpleReplicationManager<>(name + "_MANAGER", backingStore));
    }

    public static <K extends Serializable, V extends Serializable> HighlyAvailableMap<K, V> createSticky(final String name, BackingStore<StickyKey, V> backingStore) {
        return new HighlyAvailableMap<>(name, new HashMap<>(), new StickyReplicationManager<>(name + "_MANAGER", backingStore));
    }

    public static <K extends Serializable, V> HighlyAvailableMap<K, V> create(final String name, ReplicationManager<K, V> replicationManager) {
        if (replicationManager == null) {
            replicationManager = new NoopReplicationManager<>(name + "_MANAGER");
        }

        return new HighlyAvailableMap<>(name, new HashMap<>(), replicationManager);
    }

    private HighlyAvailableMap(final String name, Map<K, V> wrappedMap, ReplicationManager<K, V> replicationManager) {
        this.loggerProlog = "[" + name + "]: ";
        this.localMap = wrappedMap;
        this.replicationManager = replicationManager;
    }

    @Override
    public int size() {
        dataLock.readLock().lock();
        try {

            return localMap.size();
        } finally {
            dataLock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        dataLock.readLock().lock();
        try {

            return localMap.isEmpty();
        } finally {
            dataLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        @SuppressWarnings("unchecked")
        K _key = (K) key;

        dataLock.readLock().lock();
        try {

            if (localMap.containsKey(_key)) {
                return true;
            }

            return tryLoad(_key) != null;
        } finally {
            dataLock.readLock().unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(Object key) {
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(loggerProlog + "Retrieving data for key ["+ key + "]");
        }

        @SuppressWarnings("unchecked")
        K _key = (K) key;

        dataLock.readLock().lock();
        try {

            V value = localMap.get(_key);
            if (value != null) {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.finer(loggerProlog + "Data for key ["+ key + "] found in a local cache: " + value);
                }
                return value;
            }

            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "Data for key ["+ key + "] not found in the local cache - consulting replication manager");
            }
            return tryLoad(_key);
        } finally {
            dataLock.readLock().unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(loggerProlog + "Storing data for key ["+ key + "]: " + value);
        }

        dataLock.writeLock().lock();
        try {
            V oldValue = localMap.put(key, value);
            replicationManager.save(key, value, oldValue == null);
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "Old data replaced for key ["+ key + "]: " + oldValue);
            }

            return oldValue;
        } finally {
            dataLock.writeLock().unlock();
        }
    }

    @Override
    public V remove(Object key) {
        @SuppressWarnings("unchecked")
        K _key = (K) key;

        V oldValue = get(_key);

        dataLock.writeLock().lock();
        try {
            localMap.remove(_key);
            replicationManager.remove(_key);
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "Removing data for key ["+ key + "]: " + oldValue);
            }

            return oldValue;
        } finally {
            dataLock.writeLock().unlock();
        }

    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        dataLock.writeLock().lock();
        try {
            for (Entry<? extends K, ? extends V> e : m.entrySet()) {
                put(e.getKey(), e.getValue());
            }
        } finally {
            dataLock.writeLock().unlock();
        }
    }

    private V tryLoad(K key) {
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(loggerProlog + "Using replication manager to load data for key ["+ key + "]");
        }

        dataLock.readLock().unlock();
        dataLock.writeLock().lock();
        try {
            V value = localMap.get(key);
            if (value != null) {
                return value;
            }

            value = replicationManager.load(key);
            if (value != null) {
                localMap.put(key, value);
            }

            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "Replication manager returned data for key ["+ key + "]: " + value);
            }

            return value;
        } finally {
            dataLock.readLock().lock();
            dataLock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {
        dataLock.writeLock().lock();
        try {
            for (K key : localMap.keySet()) {
                replicationManager.remove(key);
            }

            localMap.clear();
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "HA map cleared");
            }
        } finally {
            dataLock.writeLock().unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        dataLock.readLock().lock();
        try {

            return localMap.keySet();
        } finally {
            dataLock.readLock().unlock();
        }
    }

    @Override
    public Collection<V> values() {
        dataLock.readLock().lock();
        try {

            return localMap.values();
        } finally {
            dataLock.readLock().unlock();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        dataLock.readLock().lock();
        try {

            return localMap.entrySet();
        } finally {
            dataLock.readLock().unlock();
        }
    }

    public Map<K, V> getLocalMapCopy() {
        dataLock.readLock().lock();
        try {

            return new HashMap<>(localMap);
        } finally {
            dataLock.readLock().unlock();
        }
    }

    public void invalidateCache() {
        dataLock.writeLock().lock();
        try {

            localMap.clear();
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(loggerProlog + "local cache invalidated");
            }
        } finally {
            dataLock.writeLock().unlock();
        }
    }

    public ReplicationManager<K, V> getReplicationManager() {
        return replicationManager;
    }

    public void close() {
        replicationManager.close();
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(loggerProlog + "HA map closed");
        }
    }

    public void destroy() {
        replicationManager.destroy();
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(loggerProlog + "HA map destroyed");
        }
    }
}
