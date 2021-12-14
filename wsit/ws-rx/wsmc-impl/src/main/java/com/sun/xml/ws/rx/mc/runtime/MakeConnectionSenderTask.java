/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.runtime;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Packet;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.commons.ScheduledTaskManager;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.mc.localization.LocalizationMessages;
import com.sun.xml.ws.rx.mc.protocol.wsmc200702.MakeConnectionElement;
import com.sun.xml.ws.rx.mc.dev.ProtocolMessageHandler;
import com.sun.xml.ws.rx.util.Communicator;
import com.sun.xml.ws.rx.util.SuspendedFiberStorage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 *
 */
final class MakeConnectionSenderTask implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(MakeConnectionSenderTask.class);
    //
    private final String wsmcAnonymousAddress;
    private final Header wsmcAnnonymousReplyToHeader;
    private final Header wsmcAnnonymousFaultToHeader;
    private long lastMcMessageTimestamp;
    private final AtomicBoolean isMcRequestPending;
    private int scheduledMcRequestCounter;
    private final McConfiguration configuration;
    private final Communicator communicator;
    private final SuspendedFiberStorage suspendedFiberStorage;
    private final Map<String, ProtocolMessageHandler> mapOfRegisteredProtocolMessageHandlers;
    //
    private final ScheduledTaskManager scheduler;
    private final AtomicBoolean isRunning;
    private final AtomicBoolean wasShutdown;

    MakeConnectionSenderTask(
            final Communicator communicator,
            final SuspendedFiberStorage suspendedFiberStorage,
            final String wsmcAnonymousAddress,
            final Header wsmcAnnonymousReplyToHeader,
            final Header wsmcAnnonymousFaultToHeader,
            final McConfiguration configuration) {
        this.communicator = communicator;
        this.suspendedFiberStorage = suspendedFiberStorage;
        this.wsmcAnonymousAddress = wsmcAnonymousAddress;
        this.wsmcAnnonymousReplyToHeader = wsmcAnnonymousReplyToHeader;
        this.wsmcAnnonymousFaultToHeader = wsmcAnnonymousFaultToHeader;
        this.configuration = configuration;
        this.mapOfRegisteredProtocolMessageHandlers = new HashMap<>();

        this.lastMcMessageTimestamp = System.currentTimeMillis();
        this.isMcRequestPending = new AtomicBoolean(false);
        this.scheduledMcRequestCounter = 0;

        this.scheduler = new ScheduledTaskManager("MakeConnectionSenderTask", communicator.getContainer());
        this.isRunning = new AtomicBoolean(false);
        this.wasShutdown = new AtomicBoolean(false);
    }

    /**
     * This task can only be started once and then shut down. It cannot be stopped. Once it has been shut down, it cannot be restarted.
     */
    public void start() {
        if (wasShutdown.get()) {
            throw new IllegalStateException("This task instance has already been shut down in the past.");
        }

        if (isRunning.compareAndSet(false, true)) {
            // TODO P2 make it configurable
            this.scheduler.startTask(this, 2000, 500);
        }
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public boolean wasShutdown() {
        return wasShutdown.get();
    }

    public void shutdown() {
        if (isRunning.compareAndSet(true, false) && wasShutdown.compareAndSet(false, true)) {
            this.scheduler.shutdown();
        }
    }

    /**
     * This method is resumed periodicaly by a Timer. First, it checks if ALL of the following conditions
     * are satisfied:
     * <ul>
     *   <li>There is no MakeConnection request already pending</li>
     *   <li>A preconfigured interval has passed since last MakeConnection request</li>
     *   <li>There are suspended fibers waiting for a response or there are pending MC
     *       requests that were scheduled programatically via {@link #scheduleMcRequest()} method</li>
     * </ul>
     * If all the above conditions are astisfied a new MakeConnection request is sent. If not,
     * method terminates without any further action.
     */
    @Override
    public synchronized void run() {
        if (!isMcRequestPending.get() && resendMakeConnectionIntervalPassed() && (scheduledMcRequestCounter > 0 || suspendedFibersReadyForResend())) {
            sendMcRequest();
        }
    }

    private boolean suspendedFibersReadyForResend() {
        // TODO P3 enable exponential backoff algorithm
        while (!suspendedFiberStorage.isEmpty()) {
            final long oldestRegistrationAge = System.currentTimeMillis() - suspendedFiberStorage.getOldestRegistrationTimestamp();

            if (oldestRegistrationAge > configuration.getFeature().getResponseRetrievalTimeout()) {
                suspendedFiberStorage.removeOldest().resume(new RxRuntimeException(LocalizationMessages.WSMC_0123_RESPONSE_RETRIEVAL_TIMED_OUT()));
            } else {
                return oldestRegistrationAge > configuration.getFeature().getBaseMakeConnectionRequetsInterval();
            }
        }

        return false;
    }

    private synchronized boolean resendMakeConnectionIntervalPassed() {
        // TODO P3 enable exponential backoff algorithm
        return System.currentTimeMillis() - lastMcMessageTimestamp > configuration.getFeature().getBaseMakeConnectionRequetsInterval();
    }

    synchronized void register(ProtocolMessageHandler handler) {
        for (String wsaAction : handler.getSuportedWsaActions()) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(String.format(
                        "Registering ProtocolMessageHandler of class [ %s ] to process WS-A action [ %s ]",
                        handler.getClass().getName(),
                        wsaAction));
            }

            final ProtocolMessageHandler oldHandler = mapOfRegisteredProtocolMessageHandlers.put(wsaAction, handler);

            if (oldHandler != null && LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning(LocalizationMessages.WSMC_0101_DUPLICATE_PROTOCOL_MESSAGE_HANDLER(
                        wsaAction,
                        oldHandler.getClass().getName(),
                        handler.getClass().getName()));
            }
        }
    }

    synchronized void scheduleMcRequest() {
        scheduledMcRequestCounter++;
    }

    private void sendMcRequest() {
        Packet mcRequest = communicator.createRequestPacket(new MakeConnectionElement(wsmcAnonymousAddress), configuration.getRuntimeVersion().protocolVersion.wsmcAction, true);
        McClientTube.setMcAnnonymousHeaders(
                mcRequest.getMessage().getHeaders(),
                configuration.getAddressingVersion(),
                wsmcAnnonymousReplyToHeader,
                wsmcAnnonymousFaultToHeader);

        isMcRequestPending.set(true);
        try {
            communicator.sendAsync(mcRequest, new WsMcResponseHandler(configuration, this, suspendedFiberStorage, mapOfRegisteredProtocolMessageHandlers));
        } finally {
            lastMcMessageTimestamp = System.currentTimeMillis();
            if (--scheduledMcRequestCounter < 0) {
                scheduledMcRequestCounter = 0;
            }
        }
    }

    synchronized void clearMcRequestPendingFlag() {
        isMcRequestPending.set(false);
    }
}
