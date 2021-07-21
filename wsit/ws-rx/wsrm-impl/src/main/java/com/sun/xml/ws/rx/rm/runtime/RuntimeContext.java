/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.runtime.sequence.SequenceManager;
import com.sun.xml.ws.rx.rm.runtime.sequence.UnknownSequenceException;
import com.sun.xml.ws.rx.util.Communicator;
import com.sun.xml.ws.commons.ScheduledTaskManager;
import com.sun.xml.ws.rx.rm.runtime.sequence.SequenceManagerFactory;
import com.sun.xml.ws.rx.rm.runtime.transaction.TransactionHandler;
import com.sun.xml.ws.rx.rm.runtime.transaction.TransactionHandlerImpl;
import com.sun.xml.ws.rx.util.SuspendedFiberStorage;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public final class RuntimeContext {

    public static Builder builder(@NotNull RmConfiguration configuration, @NotNull Communicator communicator) {
        return new Builder(configuration, communicator);
    }

    public static final class Builder {

        private final 
        @NotNull
        RmConfiguration configuration;
        private final 
        @NotNull
        Communicator communicator;
        private 
        @Nullable
        SequenceManager sequenceManager;
        private 
        @Nullable
        SourceMessageHandler sourceMessageHandler;
        private 
        @Nullable
        DestinationMessageHandler destinationMessageHandler;
        private
        @Nullable
        TransactionHandler transactionHandler;
        @Nullable
        OutboundDeliveredHandler outboundDeliveredHandler;

        public Builder(@NotNull RmConfiguration configuration, @NotNull Communicator communicator) {
            assert configuration != null;
            assert communicator != null;

            this.configuration = configuration;
            this.communicator = communicator;

            this.sourceMessageHandler = new SourceMessageHandler(null);
            this.destinationMessageHandler = new DestinationMessageHandler(null);
            this.transactionHandler = new TransactionHandlerImpl();
            this.outboundDeliveredHandler = new OutboundDeliveredHandler();
        }

        public Builder sequenceManager(SequenceManager sequenceManager) {
            this.sequenceManager = sequenceManager;

            this.sourceMessageHandler.setSequenceManager(sequenceManager);
            this.destinationMessageHandler.setSequenceManager(sequenceManager);

            return this;
        }

        public RuntimeContext build() {
            return new RuntimeContext(
                    configuration,
                    sequenceManager,
                    communicator,
                    new SuspendedFiberStorage(),
                    new ScheduledTaskManager("RM Runtime Context", communicator.getContainer()),
                    sourceMessageHandler,
                    destinationMessageHandler,
                    transactionHandler,
                    outboundDeliveredHandler);
        }
    }

    public final RmConfiguration configuration;
    public final AddressingVersion addressingVersion;
    public final SOAPVersion soapVersion;
    public final RmRuntimeVersion rmVersion;
    private volatile SequenceManager sequenceManager;
    public final Communicator communicator;
    public final SuspendedFiberStorage suspendedFiberStorage;
    public final WsrmProtocolHandler protocolHandler;
    public final ScheduledTaskManager scheduledTaskManager;
    final SourceMessageHandler sourceMessageHandler;
    final DestinationMessageHandler destinationMessageHandler;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    public final TransactionHandler transactionHandler;
    public final OutboundDeliveredHandler outboundDeliveredHandler;

    // It is used to retain user supplied information that needs to be
    // propagated with every RM message including protocol messages.
    // It is used only on the client side. It is optional (may never be set).
    // It can get reset with every application client request message
    // but last value is considered current and is propagated.
    private volatile String userStateID;

    @SuppressWarnings("LeakingThisInConstructor")
    private RuntimeContext(
            RmConfiguration configuration,
            SequenceManager sequenceManager,
            Communicator communicator,
            SuspendedFiberStorage suspendedFiberStorage,
            ScheduledTaskManager scheduledTaskManager,
            SourceMessageHandler srcMsgHandler,
            DestinationMessageHandler dstMsgHandler,
            TransactionHandler txHandler,
            OutboundDeliveredHandler outboundDeliveredHandler) {

        this.configuration = configuration;
        this.sequenceManager = sequenceManager;
        this.communicator = communicator;
        this.suspendedFiberStorage = suspendedFiberStorage;
        this.scheduledTaskManager = scheduledTaskManager;
        this.sourceMessageHandler = srcMsgHandler;
        this.destinationMessageHandler = dstMsgHandler;

        this.addressingVersion = configuration.getAddressingVersion();
        this.soapVersion = configuration.getSoapVersion();
        this.rmVersion = configuration.getRuntimeVersion();

        this.protocolHandler = WsrmProtocolHandler.getInstance(configuration, communicator, this);
        this.transactionHandler = txHandler;
        this.outboundDeliveredHandler = outboundDeliveredHandler;
    }

    public void close() {
        if (closed.compareAndSet(false, true)) {
            scheduledTaskManager.shutdown();
            communicator.close();

            if (sequenceManager != null) {
                SequenceManagerFactory.INSTANCE.dispose(sequenceManager, configuration);
            }
        }
    }

    public String getBoundSequenceId(String sequenceId) throws UnknownSequenceException {
        assert sequenceManager != null;

        Sequence boundSequence = sequenceManager.getBoundSequence(sequenceId);
        return boundSequence != null ? boundSequence.getId() : null;
    }

    public SequenceManager sequenceManager() {
        assert sequenceManager != null;

        return this.sequenceManager;
    }

    public void setSequenceManager(@NotNull SequenceManager newValue) {
        assert newValue != null;

        this.sequenceManager = newValue;

        this.sourceMessageHandler.setSequenceManager(newValue);
        this.destinationMessageHandler.setSequenceManager(newValue);
    }

    public String getUserStateID() {
        return userStateID;
    }

    public void setUserStateID(String userStateId) {
        assert userStateId != null && userStateId.length() <= 256;
        userStateID = userStateId;
    }
}
