/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.util;

import com.sun.xml.ws.api.message.Packet;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class AbstractResponseHandler {
    protected final SuspendedFiberStorage suspendedFiberStorage;
    private String correlationId;

    public AbstractResponseHandler(SuspendedFiberStorage suspendedFiberStorage, String correlationId) {
        this.suspendedFiberStorage = suspendedFiberStorage;
        this.correlationId = correlationId;
    }

    protected final String getCorrelationId() {
        return correlationId;
    }

    protected final void setCorrelationId(String newCorrelationId) {
        this.correlationId = newCorrelationId;
    }

    protected final void resumeParentFiber(Packet response) throws ResumeFiberException {
        suspendedFiberStorage.resumeFiber(correlationId, response);
    }

    protected final void resumeParentFiber(Throwable error) throws ResumeFiberException {
        suspendedFiberStorage.resumeFiber(correlationId, error);
    }
}
