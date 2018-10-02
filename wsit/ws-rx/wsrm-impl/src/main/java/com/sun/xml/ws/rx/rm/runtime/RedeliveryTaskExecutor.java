/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.commons.DelayedTaskManager;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.commons.ha.HaContext;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
class RedeliveryTaskExecutor {
    private static final Logger LOGGER = Logger.getLogger(RedeliveryTaskExecutor.class);
    private static volatile DelayedTaskManager delayedTaskManager;

    private RedeliveryTaskExecutor() {
    }

    // This method delivers message using caller's thread. No thread switching.
    public static boolean deliverUsingCurrentThread(
            final ApplicationMessage message, long delay, TimeUnit timeUnit,
            final MessageHandler messageHandler) {

        try {
            Thread.sleep(timeUnit.toMillis(delay));
        } catch (InterruptedException e) {
            //ignore and redeliver
        }

        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(String.format(
                    "Attempting redelivery of a message with number [ %d ] on a sequence [ %s ]",
                    message.getMessageNumber(),
                    message.getSequenceId()));
        }

        messageHandler.putToDeliveryQueue(message);
        return true;
    }
    
    // Not used anymore in favor of deliverUsingCurrentThread.
    @Deprecated
    public static boolean register(final ApplicationMessage message, long delay, TimeUnit timeUnit, final MessageHandler messageHandler, Component container) {
        final HaContext.State state = HaContext.currentState();

        if (delayedTaskManager == null) {
            synchronized(RedeliveryTaskExecutor.class) {
                if (delayedTaskManager == null) {
                    delayedTaskManager = DelayedTaskManager.createManager("redelivery-task-executor", 5, container);
                }
            }
        }
        
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(String.format(
                    "A message with number [ %d ] has been scheduled for a redelivery "
                    + "on a sequence [ %s ] with a delay of %d %s "
                    + "using current HA context state [ %s ]",
                    message.getMessageNumber(),
                    message.getSequenceId(),
                    delay,
                    timeUnit.toString().toLowerCase(),
                    state.toString()));
        }

        return delayedTaskManager.register(new DelayedTaskManager.DelayedTask()   {

            public void run(DelayedTaskManager manager) {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.finer(String.format(
                            "Attempting redelivery of a message with number [ %d ] on a sequence [ %s ]",
                            message.getMessageNumber(),
                            message.getSequenceId()));
                }
                final HaContext.State oldState = HaContext.initFrom(state);
                try {
                    messageHandler.putToDeliveryQueue(message);
                } finally {
                    HaContext.initFrom(oldState);
                }
            }

            public String getName() {
                return String.format("redelivery of a message with number [ %d ] on a sequenece [ %s ]", message.getMessageNumber(), message.getSequenceId());
            }
        }, delay, timeUnit);
    }
}
