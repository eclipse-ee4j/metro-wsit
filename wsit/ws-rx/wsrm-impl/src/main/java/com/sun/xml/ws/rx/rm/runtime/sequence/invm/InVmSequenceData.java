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

import com.sun.istack.NotNull;
import com.sun.xml.ws.rx.rm.runtime.ApplicationMessage;
import com.sun.xml.ws.rx.rm.runtime.sequence.DuplicateMessageRegistrationException;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.State;
import com.sun.xml.ws.rx.rm.runtime.sequence.SequenceData;
import com.sun.xml.ws.rx.util.TimeSynchronizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.glassfish.ha.store.annotations.StoreEntry;

@StoreEntry
final class InVmSequenceData implements SequenceData {

    // lock used to synchronize the access to the lastMessageId and unackedMessageIdentifiersStorage variables
    private final ReadWriteLock dataLock = new ReentrantReadWriteLock();
    //
    private final Map<String, ApplicationMessage> messageStore;
    private final SequenceDataPojo data;
    private final TimeSynchronizer timeSynchronizer;

    public static InVmSequenceData newInstace(@NotNull SequenceDataPojo data, @NotNull TimeSynchronizer timeSynchronizer, Map<String, ApplicationMessage> messageStore) {
        return new InVmSequenceData(data, timeSynchronizer, messageStore);
    }

    public static InVmSequenceData loadReplica(@NotNull SequenceDataPojo data, @NotNull TimeSynchronizer timeSynchronizer, Map<String, ApplicationMessage> messageStore) {

        InVmSequenceData replica = new InVmSequenceData(data, timeSynchronizer, messageStore);
        replica.initLocalCache();

        return replica;
    }

    private InVmSequenceData(@NotNull final SequenceDataPojo data, @NotNull final TimeSynchronizer timeSynchronizer, @NotNull final Map<String, ApplicationMessage> messageStore) {
        assert timeSynchronizer != null;
        assert data != null;

        this.timeSynchronizer = timeSynchronizer;
        this.data = data;

        this.messageStore = messageStore;
    }

    private void lockRead() {
        dataLock.readLock().lock();
    }

    private void unlockRead() {
        dataLock.readLock().unlock();
    }

    private void lockWrite() {
        dataLock.writeLock().lock();
    }

    private void unlockWrite() {
        dataLock.writeLock().unlock();
    }

    @Override
    public String getSequenceId() {
        return data.getSequenceId();
    }

    @Override
    public String getBoundSecurityTokenReferenceId() {
        return data.getBoundSecurityTokenReferenceId();
    }

    @Override
    public long getLastMessageNumber() {
        try {
            lockRead();
            return data.getLastMessageNumber();
        } finally {
            unlockRead();
        }
    }

    @Override
    public State getState() {
        return data.getState();
    }

    @Override
    public void setState(State newState) {
        updateLastActivityTime();

        data.setState(newState);
        data.replicate();
    }

    @Override
    public boolean getAckRequestedFlag() {
        return data.getAckRequestedFlag();
    }

    @Override
    public void setAckRequestedFlag(boolean newValue) {
        updateLastActivityTime();

        data.setAckRequestedFlag(newValue);
        data.replicate();
    }

    @Override
    public long getLastAcknowledgementRequestTime() {
        return data.getLastAcknowledgementRequestTime();
    }

    @Override
    public void setLastAcknowledgementRequestTime(long newTime) {
        updateLastActivityTime();

        data.setLastAcknowledgementRequestTime(newTime);
        data.replicate();
    }

    @Override
    public long getLastActivityTime() {
        return data.getLastActivityTime();
    }

    private void updateLastActivityTime() {
        data.setLastActivityTime(timeSynchronizer.currentTimeInMillis());
    }

    @Override
    public long getExpirationTime() {
        return data.getExpirationTime();
    }

    @Override
    public void attachMessageToUnackedMessageNumber(ApplicationMessage message) {
        updateLastActivityTime();

        try {
            lockWrite();
            Long msgNumberKey = getUnackedMessageIdentifierKey(message.getMessageNumber());

            data.getUnackedNumberToCorrelationIdMap().put(msgNumberKey, message.getCorrelationId());
            data.replicate();

            messageStore.put(decorateForSequence(message.getCorrelationId()), message);
        } finally {
            unlockWrite();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long incrementAndGetLastMessageNumber(boolean received) {
        updateLastActivityTime();

        try {
            dataLock.writeLock().lock();

            data.setLastMessageNumber(data.getLastMessageNumber() + 1);
            addUnackedMessageNumber(data.getLastMessageNumber(), received);
            data.replicate();

            return data.getLastMessageNumber();
        } finally {
            dataLock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerReceivedUnackedMessageNumber(long messageNumber) throws DuplicateMessageRegistrationException {
        updateLastActivityTime();

        try {
            lockWrite();

            if (messageNumber > data.getLastMessageNumber()) {
                while (messageNumber > data.getLastMessageNumber() + 1) {
                    // new message - note that this will work even for the first message that arrives
                    // some message(s) got lost, add to all unacked message number set...
                    incrementAndGetLastMessageNumber(false);
                }

                incrementAndGetLastMessageNumber(true);
            } else {
                if (data.getReceivedUnackedMessageNumbers().contains(messageNumber) || // we already have such received unacked registration
                        !data.getAllUnackedMessageNumbers().contains(messageNumber)) { // not found among all unacked messages => has been already acknowledged
                    throw new DuplicateMessageRegistrationException(data.getSequenceId(), messageNumber);
                }

                addUnackedMessageNumber(messageNumber, true);
            }

            data.replicate();
        } finally {
            unlockWrite();
        }
    }

    /*
     * This method must be called from within a data write lock only.
     */
    private void addUnackedMessageNumber(long messageNumber, boolean received) {
        final Long newUnackedInstance = messageNumber;

        data.getAllUnackedMessageNumbers().add(newUnackedInstance);
        if (received) {
            data.getReceivedUnackedMessageNumbers().add(newUnackedInstance);
        }
    }

    void markUnackedAsFailedOver() {
        lockWrite();
        try {
            for (Long unackedNumber : data.getReceivedUnackedMessageNumbers()) {
                data.getFailedOverUnackedMessageNumbers().add(unackedNumber);
            }
        } finally {
            unlockWrite();
        }

    }

    @Override
    public boolean isFailedOver(long messageNumber) {
        final Long value = messageNumber;
        lockRead();
        try {
            return data.getFailedOverUnackedMessageNumbers().contains(value);
        } finally {
            unlockRead();
        }
    }

    @Override
    public void markAsAcknowledged(long messageNumber) {
        updateLastActivityTime();

        try {
            lockWrite();
            data.getFailedOverUnackedMessageNumbers().remove(messageNumber);
            data.getReceivedUnackedMessageNumbers().remove(messageNumber);
            data.getAllUnackedMessageNumbers().remove(messageNumber);
            final String correlationId = data.getUnackedNumberToCorrelationIdMap().remove(messageNumber);
            data.replicate();

            messageStore.remove(decorateForSequence(correlationId));
        } finally {
            unlockWrite();
        }
    }

    @Override
    public ApplicationMessage retrieveMessage(String correlationId) {
        updateLastActivityTime();

        try {
            lockRead();
            return messageStore.get(decorateForSequence(correlationId));
        } finally {
            unlockRead();
        }
    }

    @Override
    public List<Long> getUnackedMessageNumbers() {
        try {
            lockRead();
            return new ArrayList<>(data.getAllUnackedMessageNumbers());
        } finally {
            unlockRead();
        }

    }

    @Override
    public List<Long> getLastMessageNumberWithUnackedMessageNumbers() {
        try {
            lockRead();

            LinkedList<Long> result = new LinkedList<>(data.getAllUnackedMessageNumbers());
            result.addFirst(data.getLastMessageNumber());

            return result;
        } finally {
            unlockRead();
        }
    }

    private Long getUnackedMessageIdentifierKey(long messageNumber) {
        try {
            lockRead();
            Long msgNumberKey = null;
            Iterator<Long> iterator = data.getReceivedUnackedMessageNumbers().iterator();
            while (iterator.hasNext()) {
                msgNumberKey = iterator.next();
                if (msgNumberKey == messageNumber) {
                    break;
                }
            }

            return msgNumberKey;
        } finally {
            unlockRead();
        }
    }

    SequenceDataPojo getSequenceStatePojo() {
        return data;
    }

    private void initLocalCache() {
        for (Long unackedMessageNumber : data.getReceivedUnackedMessageNumbers()) {
            final String correlationId = data.getUnackedNumberToCorrelationIdMap().get(unackedMessageNumber);
            messageStore.get(decorateForSequence(correlationId));
        }
    }

    private String decorateForSequence(String correlationId) {
        return data.getSequenceId() + "_" + correlationId;
    }
}
