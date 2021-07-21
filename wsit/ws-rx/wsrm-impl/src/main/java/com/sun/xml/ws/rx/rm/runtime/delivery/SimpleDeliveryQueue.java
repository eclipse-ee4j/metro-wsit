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
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.runtime.ApplicationMessage;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
final class SimpleDeliveryQueue implements DeliveryQueue {

    private final Postman postman;
    private final Postman.Callback deliveryCallback;
    private final AtomicBoolean isClosed;

    SimpleDeliveryQueue(Postman postman, Postman.Callback deliveryCallback) {
        this.postman = postman;
        this.deliveryCallback = deliveryCallback;
        this.isClosed = new AtomicBoolean(false);
    }

    public void put(ApplicationMessage message) throws RxRuntimeException {
        if (isClosed.get()) {
            throw new RxRuntimeException(LocalizationMessages.WSRM_1160_DELIVERY_QUEUE_CLOSED());
        }

        postman.deliver(message, deliveryCallback);
    }

    public long getRemainingMessageBufferSize() {
        return DeliveryQueue.UNLIMITED_BUFFER_SIZE;
    }

    public void onSequenceAcknowledgement() {
        // do nothing
    }

    public void close() {
        isClosed.set(true);
    }
}
