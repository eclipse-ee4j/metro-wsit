/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.util;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.istack.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class SuspendedFiberStorage extends TimestampedCollection<String, Fiber> {
    private static final Logger LOGGER = Logger.getLogger(SuspendedFiberStorage.class);

    @Override
    public Fiber register(String correlationId, Fiber subject) {
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(String.format("Registering fiber [ %s ] with correlationId [ %s ] for suspend", subject.toString(), correlationId));
        }

        return super.register(correlationId, subject);
    }

    @Override
    public boolean register(long timestamp, Fiber subject) {
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(String.format("Registering fiber [ %s ] with timestamp [ %d ] for suspend", subject.toString(), timestamp));
        }

        return super.register(timestamp, subject);
    }



    public void resumeFiber(String correlationId, Packet response) throws ResumeFiberException {
        Fiber fiber = remove(correlationId);
        if (fiber == null) {
            throw LOGGER.logSevereException(new ResumeFiberException(String.format("Unable to resume fiber with a response packet: No registered fiber found for correlationId [ %s ].", correlationId)));
        }

        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(String.format("Resuming fiber [ %s ] with a response", fiber.toString()));
        }

        fiber.resume(response);
    }

    public void resumeFiber(String correlationId, Throwable error) throws ResumeFiberException {
        Fiber fiber = remove(correlationId);
        if (fiber == null) {
            throw LOGGER.logSevereException(new ResumeFiberException(String.format("Unable to resume fiber with a response packet: No registered fiber found for correlationId [ %s ].", correlationId)));
        }

        if (LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning(String.format("Resuming fiber [ %s ] with an exception", fiber.toString()));
        }

        fiber.resume(error);
    }

    public void resumeAllFibers(Throwable error) {
        for (Fiber fiber : removeAll()) {
            fiber.resume(error);
        }
    }
}
