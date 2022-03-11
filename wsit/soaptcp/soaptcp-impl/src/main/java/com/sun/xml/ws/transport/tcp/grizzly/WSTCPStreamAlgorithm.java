/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.grizzly;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.IOEvent;
import org.glassfish.grizzly.strategies.AbstractIOStrategy;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * @author Alexey Stashok
 */
public final class WSTCPStreamAlgorithm extends AbstractIOStrategy {

    @Override
    public boolean executeIoEvent(Connection connection, IOEvent ioEvent, boolean isIoEventEnabled) throws IOException {

        final boolean isReadOrWriteEvent = isReadWrite(ioEvent);
        if (isReadOrWriteEvent) {
            if (isIoEventEnabled) {
                connection.disableIOEvent(ioEvent);
            }

        }
        final Executor threadPool = getThreadPoolFor(connection, ioEvent);
        return threadPool == null;

    }
}
