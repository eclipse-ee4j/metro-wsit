/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence;

import com.sun.istack.NotNull;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.commons.DelayedTaskManager;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class SequenceMaintenanceTask implements DelayedTaskManager.DelayedTask {

    private static final Logger LOGGER = Logger.getLogger(SequenceMaintenanceTask.class);
    private final WeakReference<SequenceManager> smReference;
    private final long period;
    private final TimeUnit timeUnit;
    private final String endpointUid;

    public SequenceMaintenanceTask(@NotNull SequenceManager sequenceManager, long period, @NotNull TimeUnit timeUnit) {
        assert sequenceManager != null;
        assert period > 0;
        assert timeUnit != null;

        this.smReference = new WeakReference<SequenceManager>(sequenceManager);
        this.period = period;
        this.timeUnit = timeUnit;
        this.endpointUid = sequenceManager.uniqueEndpointId();
    }

    public void run(DelayedTaskManager manager) {
        SequenceManager sequenceManager = smReference.get();
        if (sequenceManager != null && sequenceManager.onMaintenance()) {
            if (!manager.isClosed()) {
                boolean registrationSuccesfull = manager.register(this, period, timeUnit);

                if (!registrationSuccesfull) {
                    LOGGER.config(LocalizationMessages.WSRM_1150_UNABLE_TO_RESCHEDULE_SEQUENCE_MAINTENANCE_TASK(endpointUid));
                }
            }
        } else {
            LOGGER.config(LocalizationMessages.WSRM_1151_TERMINATING_SEQUENCE_MAINTENANCE_TASK(endpointUid));
        }
    }

    public String getName() {
        return "sequence maintenance task";
    }
}
