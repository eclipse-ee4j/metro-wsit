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

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * A generic immutable reference holder that implements {@link Delayed} interface
 * and thus is suitable for use in a {@link java.util.concurrent.DelayQueue}
 * instances.
 *</p>
 *
 * <p>
 * Instances of this {@code DelayedReference} class work with a milliseconds precision.
 *</p>
 *
 */
public class DelayedReference<V> implements Delayed {

    private final V data;
    private final long resumeTimeInMilliseconds;

    private DelayedReference(V data, long resumeTimeInMilliseconds) {
        this.data = data;
        this.resumeTimeInMilliseconds = resumeTimeInMilliseconds;
    }

    public DelayedReference(V data, long delay, TimeUnit timeUnit) {
        this(data, timeUnit.toMillis(delay) + System.currentTimeMillis());
    }

    public V getValue() {
        return data;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(resumeTimeInMilliseconds - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        long thisDelay = resumeTimeInMilliseconds - System.currentTimeMillis();
        long thatDelay = other.getDelay(TimeUnit.MILLISECONDS);

        return (thisDelay < thatDelay) ? -1 : ((thisDelay == thatDelay) ? 0 : 1);
    }

    public DelayedReference<V> updateData(V data) {
        return new DelayedReference<>(data, resumeTimeInMilliseconds);
    }

    public DelayedReference<V> updateDelay(long newDelay, TimeUnit timeUnit) {
        return new DelayedReference<>(data, timeUnit.toMillis(newDelay) + System.currentTimeMillis());
    }
}
