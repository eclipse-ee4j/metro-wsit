/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.util;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Engine;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.Fiber.CompletionCallback;
import com.sun.xml.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.util.Pool;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * TODO javadoc
 *
 * <b>
 * WARNING: This class is a private utility class used by WS-RX implementation. Any usage outside
 * the intended scope is strongly discouraged. The API exposed by this class may be changed, replaced
 * or removed without any advance notice.
 * </b>
 *
 */
public final class FiberExecutor {

    private static class Schedule {

        private final Packet request;
        private final Fiber.CompletionCallback completionCallback;

        public Schedule(Packet request, CompletionCallback completionCallback) {
            this.request = request;
            this.completionCallback = completionCallback;
        }
    }
    private Pool<Tube> tubelinePool;
    private volatile Engine engine;
    private final List<Schedule> schedules = new LinkedList<Schedule>();
    private Executor executor;

    public FiberExecutor(String id, Tube masterTubeline) {
        this.tubelinePool = new Pool.TubePool(masterTubeline);

        // In-line Executor runs the task in the caller's thread
        // (so as to prevent thread hopping)
        executor =
                new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };

        this.engine = new Engine(id, executor);
    }

    public Packet runSync(Packet request) {
        final Tube tubeline = tubelinePool.take();
        try {
            return engine.createFiber().runSync(tubeline, request);
        } finally {
            tubelinePool.recycle(tubeline);
        }
    }

    public synchronized void schedule(Packet request, @NotNull final Fiber.CompletionCallback callback) {
        schedules.add(new Schedule(request, callback));
    }

    public synchronized void startScheduledFibers() {
        Iterator<Schedule> iterator = schedules.iterator();
        while (iterator.hasNext()) {
            Schedule schedule = iterator.next();
            iterator.remove();

            start(schedule.request, schedule.completionCallback, null);
        }
    }

    public void start(Packet request,
            @NotNull final Fiber.CompletionCallback callback,
            @Nullable FiberContextSwitchInterceptor interceptor) {
        Fiber fiber = engine.createFiber();

        if (interceptor != null) {
            fiber.addInterceptor(interceptor);
        }

        final Tube tube = tubelinePool.take();
        fiber.start(tube, request, new Fiber.CompletionCallback() {

            public void onCompletion(@NotNull Packet response) {
                tubelinePool.recycle(tube);
                callback.onCompletion(response);
            }

            public void onCompletion(@NotNull Throwable error) {
                // let's not reuse tubes as they might be in a wrong state, so not
                // calling tubePool.recycle()
                callback.onCompletion(error);
            }
        });
    }

    public void close() {
        Pool<Tube> tp = this.tubelinePool;
        if (tp != null) {
            // multi-thread safety of 'close' needs to be considered more carefully.
            // some calls might be pending while this method is invoked. Should we
            // block until they are complete, or should we abort them (but how?)
            Tube p = tp.take();
            p.preDestroy();
            this.tubelinePool = null;
            this.engine = null;
            this.schedules.clear();
        }

        Executor fes = this.executor;
        if (fes != null) {
            this.executor = null;
        }
    }
}
