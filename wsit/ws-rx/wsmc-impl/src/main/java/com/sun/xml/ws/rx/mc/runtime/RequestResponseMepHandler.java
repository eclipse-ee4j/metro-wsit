/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.runtime;

import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.rx.util.SuspendedFiberStorage;

/**
 *
 */
class RequestResponseMepHandler extends McResponseHandlerBase {

    public RequestResponseMepHandler(McConfiguration configuration, MakeConnectionSenderTask mcSenderTask, SuspendedFiberStorage suspendedFiberStorage, String correlationId) {
        super(configuration, mcSenderTask, suspendedFiberStorage, correlationId);
    }

    public void onCompletion(Packet response) {
        Message responseMessage = response.getMessage();
        if (responseMessage != null) {
            processMakeConnectionHeaders(responseMessage);

            if (responseMessage.hasPayload()) {
                resumeParentFiber(response);
            }
        }
        // otherwise do nothing; we'll keep the fiber suspended until a non-empty response
        // arrives as a response to WS-MakeConnection request
    }

    public void onCompletion(Throwable error) {
        resumeParentFiber(error);
    }
}
