/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.delivery;

import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.runtime.ApplicationMessage;

/**
 *
 */
public interface DeliveryQueue {
    public static long UNLIMITED_BUFFER_SIZE = -1;

    public void put(ApplicationMessage message) throws RxRuntimeException;

    public long getRemainingMessageBufferSize();

    public void onSequenceAcknowledgement();

    public void close();
}
