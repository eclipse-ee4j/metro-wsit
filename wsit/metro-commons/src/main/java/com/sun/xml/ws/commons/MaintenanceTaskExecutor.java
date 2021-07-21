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
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.commons.DelayedTaskManager.DelayedTask;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class MaintenanceTaskExecutor {  
    private static volatile DelayedTaskManager delayedTaskManager = null;

    private MaintenanceTaskExecutor() {}

    public static boolean register(@NotNull DelayedTask task, long delay, TimeUnit timeUnit, Component component) {
        if (delayedTaskManager == null) {
            synchronized(MaintenanceTaskExecutor.class) {
                if (delayedTaskManager == null) {
                    delayedTaskManager = DelayedTaskManager.createManager("maintenance-task-executor", 5, component);
                }
            }
        }
        return delayedTaskManager.register(task, delay, timeUnit);
    }

}
