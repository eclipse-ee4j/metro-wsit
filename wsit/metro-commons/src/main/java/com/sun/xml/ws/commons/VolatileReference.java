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

/**
 * This class is useful in case you need to pass a reference to a variable that 
 * might have not been initialized yet. The referenced variable is volatile to
 * ensure that any change of the reference will get synchronized among threads.
 *
 * @param <V> type of the wrapped variable
 */
public final class VolatileReference<V> {

    public volatile V value;

    public VolatileReference(V value) {
        super();
        this.value = value;
    }
}
