/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.commons;

import com.sun.istack.NotNull;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.Component;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 *
 */
public final class DelayedTaskManager extends AbstractTaskManager {

    private static final Logger LOGGER = Logger.getLogger(DelayedTaskManager.class);
    private final Component component;
    private final String threadPoolName;
    private final int coreThreadPoolSize;
    
    public interface DelayedTask {
        String getName();

        void run(DelayedTaskManager manager);
    }

    public static DelayedTaskManager createManager(String name, int coreThreadPoolSize, Component component){
        return new DelayedTaskManager(name, coreThreadPoolSize, component);
    }

    private static ThreadFactory createThreadFactory(String name) {
        return new NamedThreadFactory(name);
    }

    private class Worker implements Runnable {

        public final DelayedTask task;

        public Worker(DelayedTask handler) {
            this.task = handler;
        }

        /**
         * This method contains main task loop. It should not be called directly from outside.
         */
        @Override
        public void run() {
            LOGGER.entering();

            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(String.format("Starting delayed execution of [ %s ]", task.getName()));
            }
            try {
                task.run(DelayedTaskManager.this);
            } catch (Exception ex) {
                LOGGER.warning(String.format("An exception occured during execution of [ %s ]", task.getName()), ex);
            } finally {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.finer(String.format("Delayed execution of [ %s ] finished", task.getName()));
                }

                LOGGER.exiting();
            }
        }
    }
    //   

    private DelayedTaskManager(String name, int coreThreadPoolSize, Component component) {
        super();
        this.threadPoolName = name;
        this.coreThreadPoolSize = coreThreadPoolSize;
        this.component = component;
    }

    public boolean register(@NotNull DelayedTask task, long delay, TimeUnit timeUnit) {
        if (isClosed()) {
            LOGGER.finer(String.format("Attempt to register a new task has failed. This '%s' instance has already been closed", this.getClass().getName()));
            return false;
        }

        assert task != null;
        
        getExecutorService().schedule(new Worker(task), delay, timeUnit);
        return true; 
    }

    @Override
    protected Component getComponent() {
        return component;
    }

    @Override
    protected String getThreadPoolName() {
        return threadPoolName;
    }

    @Override
    protected ThreadFactory createThreadFactory() {
        return createThreadFactory(threadPoolName);
    }

    @Override
    protected int getThreadPoolSize() {
        return coreThreadPoolSize;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
