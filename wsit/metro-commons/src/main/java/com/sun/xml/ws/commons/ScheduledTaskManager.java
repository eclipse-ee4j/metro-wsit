/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.commons;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scheduled task manager provides a higher-level API for scheduling and controlling
 * tasks that should run on a separate thread(s).
 *
 * <b>
 * WARNING: This class is a private utility class used by WSIT implementation. Any usage outside
 * the intedned scope is strongly discouraged. The API exposed by this class may be changed, replaced
 * or removed without any advance notice.
 * </b>
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class ScheduledTaskManager extends AbstractTaskManager {
    private static final Logger LOGGER = Logger.getLogger(ScheduledTaskManager.class);
    private static final AtomicInteger instanceNumber = new AtomicInteger(1);

    private static final long DELAY = 2000;
    private static final long PERIOD = 100;
    //
    private final String name;
    private final Queue<ScheduledFuture<?>> scheduledTaskHandles;
    private final Component component;
    private final String threadNamePrefix;

    /**
     * TODO javadoc
     */
    public ScheduledTaskManager(String name, Component component) {
        super();
        this.name = name.trim();
        this.component = component;

        // make all lowercase, replace all occurences of subsequent empty characters with a single dash and append some info
        this.threadNamePrefix = this.name.toLowerCase().replaceAll("\\s+", "-") + "-scheduler-" + instanceNumber.getAndIncrement();
        this.scheduledTaskHandles = new ConcurrentLinkedQueue<>();
    }

    public void stopAllTasks() {
        ScheduledFuture<?> handle;
        while ((handle = scheduledTaskHandles.poll()) != null) {
            handle.cancel(false);
        }
    }

    /**
     * Stops all the tasks and shuts down the scheduled task executor
     */
    public void shutdown() {
        stopAllTasks();
        //force close after waiting for period given by DELAY
        close(true, DELAY);
    }

    /**
     * Adds a new task for scheduled execution.
     *
     * @param task new task to be executed regularly at a defined rate
     * @param initialDelay the time to delay first execution (in milliseconds)
     * @param period the period between successive executions (in milliseconds)
     */
    public ScheduledFuture<?> startTask(Runnable task, long initialDelay, long period) {
        final ScheduledFuture<?> taskHandle = getExecutorService().scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
        if (!scheduledTaskHandles.offer(taskHandle)) {
            // TODO L10N
            LOGGER.warning(String.format("Unable to store handle for task of class [ %s ]", task.getClass().getName()));
        }
        return taskHandle;
    }

    /**
     * Adds a new task for scheduled execution.
     *
     * @param task new task to be executed regularly at a predefined rate
     */
    public ScheduledFuture<?> runOnce(Runnable task) {
        return startTask(task, DELAY, PERIOD);
    }

    @Override
    protected ThreadFactory createThreadFactory() {
        return new NamedThreadFactory(threadNamePrefix);
    }

    @Override
    protected String getThreadPoolName() {
        return threadNamePrefix;
    }

    @Override
    protected int getThreadPoolSize() {
        return 1;
    }

    @Override
    protected Component getComponent() {
        return component;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
