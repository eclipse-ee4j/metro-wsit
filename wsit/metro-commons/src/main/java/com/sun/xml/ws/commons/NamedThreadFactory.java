/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.commons;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class NamedThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean createDeamenoThreads;

    public NamedThreadFactory(String namePrefix) {
        this (namePrefix, true);
    }

    public NamedThreadFactory(String namePrefix, boolean createDaemonThreads) {
        SecurityManager securityManager = System.getSecurityManager();
        this.group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix + "-thread-";
        this.createDeamenoThreads = createDaemonThreads;
    }

    public Thread newThread(Runnable task) {
        Thread newThread = new Thread(group, task, namePrefix + threadNumber.getAndIncrement());

        if (newThread.getPriority() != Thread.NORM_PRIORITY) {
            newThread.setPriority(Thread.NORM_PRIORITY);
        }

        if (createDeamenoThreads && !newThread.isDaemon()) {
            newThread.setDaemon(true);
        }

        return newThread;
    }
}
