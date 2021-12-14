/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence.persistent;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.commons.AbstractMOMRegistrationAware;
import com.sun.xml.ws.commons.MaintenanceTaskExecutor;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.runtime.LocalIDManager;
import com.sun.xml.ws.rx.rm.runtime.RmConfiguration;
import com.sun.xml.ws.rx.rm.runtime.delivery.DeliveryQueueBuilder;
import com.sun.xml.ws.rx.rm.runtime.sequence.AbstractSequence;
import com.sun.xml.ws.rx.rm.runtime.sequence.DuplicateSequenceException;
import com.sun.xml.ws.rx.rm.runtime.sequence.InboundSequence;
import com.sun.xml.ws.rx.rm.runtime.sequence.OutboundSequence;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.runtime.sequence.SequenceMaintenanceTask;
import com.sun.xml.ws.rx.rm.runtime.sequence.SequenceManager;
import com.sun.xml.ws.rx.rm.runtime.sequence.UnknownSequenceException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class PersistentSequenceManager extends AbstractMOMRegistrationAware implements SequenceManager {

    private static final Logger LOGGER = Logger.getLogger(PersistentSequenceManager.class);
    /**
     * JDBC connection manager
     */
    private ConnectionManager cm;
    /**
     * Internal in-memory data access lock
     */
    private final ReadWriteLock dataLock = new ReentrantReadWriteLock();
    /**
     * Internal in-memory cache of sequence data
     */
    private final Map<String, AbstractSequence> sequences = new HashMap<>();
    /**
     * Internal in-memory map of bound sequences
     */
    private final Map<String, String> boundSequences = new HashMap<>();
    /**
     * Inbound delivery queue builder
     */
    private final DeliveryQueueBuilder inboundQueueBuilder;
    /**
     * Outbound delivery queue builder
     */
    private final DeliveryQueueBuilder outboundQueueBuilder;
    /**
     * Inactivity timeout for a sequence
     */
    private final long sequenceInactivityTimeout;
    /**
     * Maximum number of concurrent inbound sequences
     */
    private final long maxConcurrentInboundSequences;
    /**
     * Actual number of concurrent inbound sequences
     */
    private final AtomicLong actualConcurrentInboundSequences;
    /**
     * Unique identifier of the WS endpoint for which this particular sequence manager will be used
     */
    private final String uniqueEndpointId;
    /**
     * Internal variable to store information about whether or not this instance 
     * of the SequenceManager is still valid.
     */
    private volatile boolean disposed = false;

    private final LocalIDManager localIDManager;

    public PersistentSequenceManager(final String uniqueEndpointId, final DeliveryQueueBuilder inboundQueueBuilder, final DeliveryQueueBuilder outboundQueueBuilder, final RmConfiguration configuration, Container container, final LocalIDManager localIDManager) {
        this.uniqueEndpointId = uniqueEndpointId;
        this.inboundQueueBuilder = inboundQueueBuilder;
        this.outboundQueueBuilder = outboundQueueBuilder;
        this.localIDManager = localIDManager;

        this.sequenceInactivityTimeout = configuration.getRmFeature().getSequenceInactivityTimeout();

        this.actualConcurrentInboundSequences = new AtomicLong(0);
        this.maxConcurrentInboundSequences = configuration.getRmFeature().getMaxConcurrentSessions();

        this.cm = ConnectionManager.getInstance(new DefaultDataSourceProvider());

        MaintenanceTaskExecutor.register(
                new SequenceMaintenanceTask(this, configuration.getRmFeature().getSequenceManagerMaintenancePeriod(), TimeUnit.MILLISECONDS),
                configuration.getRmFeature().getSequenceManagerMaintenancePeriod(),
                TimeUnit.MILLISECONDS,
                container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean persistent() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String uniqueEndpointId() {
        return uniqueEndpointId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Sequence> sequences() {
        try {
            dataLock.readLock().lock();

            return new HashMap<>(sequences);
        } finally {
            dataLock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> boundSequences() {
        try {
            dataLock.readLock().lock();

            return new HashMap<>(boundSequences);
        } finally {
            dataLock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long concurrentlyOpenedInboundSequencesCount() {
        return actualConcurrentInboundSequences.longValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sequence createOutboundSequence(final String sequenceId, final String strId, final long expirationTime) throws DuplicateSequenceException {
        PersistentSequenceData data = PersistentSequenceData.newInstance(this, cm, uniqueEndpointId, sequenceId, PersistentSequenceData.SequenceType.Outbound, strId, expirationTime, Sequence.State.CREATED, false, OutboundSequence.INITIAL_LAST_MESSAGE_ID, currentTimeInMillis(), 0L);
        return registerSequence(new OutboundSequence(data, outboundQueueBuilder, this), data.getBoundSequenceId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sequence createInboundSequence(final String sequenceId, final String strId, final long expirationTime) throws DuplicateSequenceException {
        final long actualSessions = actualConcurrentInboundSequences.incrementAndGet();
        if (maxConcurrentInboundSequences >= 0) {
            if (maxConcurrentInboundSequences < actualSessions) {
                actualConcurrentInboundSequences.decrementAndGet();
                throw new RxRuntimeException(LocalizationMessages.WSRM_1156_MAX_CONCURRENT_SESSIONS_REACHED(maxConcurrentInboundSequences));
            }
        }

        PersistentSequenceData data = PersistentSequenceData.newInstance(this, cm, uniqueEndpointId, sequenceId, PersistentSequenceData.SequenceType.Inbound, strId, expirationTime, Sequence.State.CREATED, false, InboundSequence.INITIAL_LAST_MESSAGE_ID, currentTimeInMillis(), 0L);
        return registerSequence(new InboundSequence(data, inboundQueueBuilder, this), data.getBoundSequenceId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateSequenceUID() {
        return "uuid:" + UUID.randomUUID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sequence closeSequence(final String sequenceId) throws UnknownSequenceException {
        Sequence sequence = getSequence(sequenceId);
        sequence.close();
        return sequence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sequence getSequence(final String sequenceId) throws UnknownSequenceException {
        checkIfExist(sequenceId);

        try {
            dataLock.readLock().lock();
            Sequence sequence = sequences.get(sequenceId);

            if (shouldTeminate(sequence)) {
                dataLock.readLock().unlock();
                tryTerminateSequence(sequenceId);
                dataLock.readLock().lock();
            }

            return sequence;
        } finally {
            dataLock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sequence getInboundSequence(String sequenceId) throws UnknownSequenceException {
        final Sequence sequence = getSequence(sequenceId);

        if (!(sequence instanceof InboundSequence)) {
            throw new UnknownSequenceException(sequenceId);
        }

        return sequence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sequence getOutboundSequence(String sequenceId) throws UnknownSequenceException {
        final Sequence sequence = getSequence(sequenceId);

        if (!(sequence instanceof OutboundSequence)) {
            throw new UnknownSequenceException(sequenceId);
        }

        return sequence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String sequenceId) {
        Sequence s;
        try {
            dataLock.readLock().lock();
            s = sequences.get(sequenceId);
        } finally {
            dataLock.readLock().unlock();
        }

        if (s == null) {
            s = fetch(sequenceId);
        }

        return s != null && s.getState() != Sequence.State.TERMINATING;
    }

    /**
     * Method checks if a sequence is valid and if yes it also loads sequence into
     * an in-memory cache if necessary.
     * <p>
     * This Method must be called from within a write data lock or outside of any data lock.
     * It must not be called from within a read data lock.
     *
     * @param sequenceIds list of sequence identifiers to check.
     * @throws UnknownSequenceException in case no such sequence is found
     */
    private void checkIfExist(final String... sequenceIds) throws UnknownSequenceException {
        for (String sequenceId : sequenceIds) {
            Sequence s;
            try {
                dataLock.readLock().lock();
                s = sequences.get(sequenceId);
            } finally {
                dataLock.readLock().unlock();
            }

            if (s == null) {
                s = fetch(sequenceId);
            }

            if (s == null) {
                throw new UnknownSequenceException(sequenceId);
            }
        }
    }

    private Sequence fetch(final String sequenceId) {
        dataLock.writeLock().lock();
        try {
            if (sequences.containsKey(sequenceId)) { // re-checking
                return sequences.get(sequenceId);
            }

            PersistentSequenceData sequenceData = PersistentSequenceData.loadInstance(this, cm, uniqueEndpointId, sequenceId);
            if (sequenceData != null) {
                switch (sequenceData.getType()) {
                    case Inbound:
                        if (sequenceData.getState() != Sequence.State.TERMINATING) {
                            actualConcurrentInboundSequences.incrementAndGet();
                        }
                        return registerSequence(new InboundSequence(sequenceData, inboundQueueBuilder, this), sequenceData.getBoundSequenceId());
                    case Outbound:
                        return registerSequence(new OutboundSequence(sequenceData, outboundQueueBuilder, this), sequenceData.getBoundSequenceId());
                }

            }

            return null;
        } finally {
            dataLock.writeLock().unlock();
        }
    }

    private Sequence tryTerminateSequence(String sequenceId) {
        try {
            dataLock.writeLock().lock();

            final AbstractSequence sequence = sequences.get(sequenceId);

            if (sequence != null && sequence.getState() != Sequence.State.TERMINATING) {
                if (sequence instanceof InboundSequence) {
                    actualConcurrentInboundSequences.decrementAndGet();
                }
                sequence.preDestroy();
            }

            return sequence;
        } finally {
            dataLock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sequence terminateSequence(final String sequenceId) throws UnknownSequenceException {
        try {
            dataLock.writeLock().lock();

            checkIfExist(sequenceId); // Check if valid and prefetch sequence to a in-memory cache if not ready

            return tryTerminateSequence(sequenceId);
        } finally {
            dataLock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindSequences(final String referenceSequenceId, final String boundSequenceId) throws UnknownSequenceException {
        try {
            dataLock.writeLock().lock();
            checkIfExist(referenceSequenceId, boundSequenceId);

            PersistentSequenceData.bind(cm, uniqueEndpointId, referenceSequenceId, boundSequenceId);
            boundSequences.put(referenceSequenceId, boundSequenceId);
        } finally {
            dataLock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sequence getBoundSequence(final String referenceSequenceId) throws UnknownSequenceException {
        checkIfExist(referenceSequenceId);

        try {
            dataLock.readLock().lock();
            return (boundSequences.containsKey(referenceSequenceId)) ? sequences.get(boundSequences.get(referenceSequenceId)) : null;
        } finally {
            dataLock.readLock().unlock();
        }
    }

    /**
     * Registers a new sequence in the internal sequence storage
     *
     * @param sequence sequence object to be registered within the internal sequence storage
     */
    private AbstractSequence registerSequence(final AbstractSequence sequence, final String boundSequenceId) {
        try {
            dataLock.writeLock().lock();

            // no need to check for a duplicate:
            // if we were able to create PersistentSequenceData instance, it means that there is no duplicate
            sequences.put(sequence.getId(), sequence);

            if (boundSequenceId != null) {
                checkIfExist(boundSequenceId);

                boundSequences.put(sequence.getId(), boundSequenceId);
            }

            return sequence;
        } finally {
            dataLock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long currentTimeInMillis() {
        // TODO sync time with database
        return System.currentTimeMillis();
    }

    @Override
    public boolean onMaintenance() {
        LOGGER.entering();
        final boolean continueMaintenance = !disposed;

        try {
            dataLock.writeLock().lock();

            if (continueMaintenance) {

                Iterator<String> sequenceKeyIterator = sequences.keySet().iterator();
                while (sequenceKeyIterator.hasNext()) {
                    String key = sequenceKeyIterator.next();

                    AbstractSequence sequence = sequences.get(key);
                    if (shouldRemove(sequence)) {
                        LOGGER.config(LocalizationMessages.WSRM_1152_REMOVING_SEQUENCE(sequence.getId()));
                        sequenceKeyIterator.remove();
                        PersistentSequenceData.remove(cm, uniqueEndpointId, sequence.getId());
                        if (boundSequences.containsKey(sequence.getId())) {
                            boundSequences.remove(sequence.getId());
                        }
                        
                        if (localIDManager != null) {
                            // In RM_LOCALIDS table, mark all the LocalIDs tied with sequence as terminated
                            // In future, consider add some config/logic to purege those long been terminated
                            localIDManager.markSequenceTermination(sequence.getId());
                        }
                    } else if (shouldTeminate(sequence)) {
                        LOGGER.config(LocalizationMessages.WSRM_1153_TERMINATING_SEQUENCE(sequence.getId()));
                        tryTerminateSequence(sequence.getId());
                    }
                }
            }

            return continueMaintenance;
        } finally {
            dataLock.writeLock().unlock();
            LOGGER.exiting(continueMaintenance);
        }
    }

    private boolean shouldTeminate(Sequence sequence) {
        return sequence.getState() != Sequence.State.TERMINATING && (sequence.isExpired() || sequence.getLastActivityTime() + sequenceInactivityTimeout < currentTimeInMillis());
    }

    private boolean shouldRemove(Sequence sequence) {
        // Right now we are going to remove all terminated sequences.
        // Later we may decide to introduce a timeout before a terminated
        // sequence is removed from the sequence storage
        return sequence.getState() == Sequence.State.TERMINATING;
    }

    @Override
    public void invalidateCache() {
        // do nothing
    }

    @Override
    public void dispose() {
        this.disposed = true;
    }
}
