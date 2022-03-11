/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.delivery;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.commons.ha.HaContext;
import com.sun.xml.ws.rx.rm.runtime.ApplicationMessage;
import com.sun.xml.ws.rx.rm.runtime.RuntimeContext;

import java.util.concurrent.Executor;
import java.util.logging.Level;

/**
 *
 */
public final class Postman {

    private static final Logger LOGGER = Logger.getLogger(Postman.class);

    public interface Callback {

        /**
         * Implementation of this method is responsible for processing RM data in a
         * protocol dependent way and delivering the application message
         * using underlying message transport and processing framework
         *
         */
        void deliver(ApplicationMessage message);

        RuntimeContext getRuntimeContext();
    }

    private final Executor executor;

    Postman() {
        // In-line Executor runs the task in the caller's thread
        // (so as to prevent thread hopping)
        executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };
    }

    public void deliver(final ApplicationMessage message, final Callback deliveryCallback) {
        final HaContext.State state = HaContext.currentState();

        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(String.format(
                    "Scheduling delivery execution of a message with number [ %d ] on a sequence [ %s ] "
                    + "using current HA context state [ %s ]",
                    message.getMessageNumber(),
                    message.getSequenceId(),
                    state.toString()));
        }

        executor.execute(new Runnable()  {

            @Override
            public void run() {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.finer(String.format(
                            "Executing delivery of a message with number [ %d ] on a sequence [ %s ]",
                            message.getMessageNumber(),
                            message.getSequenceId()));
                }

                final HaContext.State oldState = HaContext.initFrom(state);

                try {
                    deliveryCallback.deliver(message);
                } finally {
                    HaContext.initFrom(oldState);
                }
            }
        });
    }
}
