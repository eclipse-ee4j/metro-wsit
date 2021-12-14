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
import java.io.IOException;

/**
 *
 */
class OneWayMepHandler extends McResponseHandlerBase {

    public OneWayMepHandler(McConfiguration configuration, MakeConnectionSenderTask mcSenderTask, SuspendedFiberStorage suspendedFiberStorage, String correlationId) {
        super(configuration, mcSenderTask, suspendedFiberStorage, correlationId);
    }

    @Override
    public void onCompletion(Packet response) {
        Message responseMessage = response.getMessage();

        if (responseMessage != null) {
            super.processMakeConnectionHeaders(responseMessage);
        } else if (configuration.isReliableMessagingEnabled()) {
            // FIXME: This is an temporary workaround to be interoperable with MSFT:
            // if response message is null with RM turned on, it means that MSFT did not
            // send back anything (not even a sequence acknowledgement) and is waiting
            // for us until we send a MakeConnection message.
            super.mcSenderTask.scheduleMcRequest();
        }

        resumeParentFiber(response);
    }

    @Override
    public void onCompletion(Throwable error) {
        if (configuration.isReliableMessagingEnabled() && isIOError(error)) {
            // FIXME: This is an temporary workaround to be interoperable with MSFT:
            // if response message is null with RM turned on, it means that MSFT did not
            // send back anything (not even a sequence acknowledgement) and is waiting
            // for us until we send a MakeConnection message.
            //
            // investigation shows that when MSFT returns HTTP 202, a SocketException is
            // raised in our transport layer
            super.mcSenderTask.scheduleMcRequest();
        }

        resumeParentFiber(error);
    }

    private boolean isIOError(Throwable error) {
        // normally the IOException comes wrapped into WebServiceException

        return error instanceof IOException || error.getCause() instanceof IOException;
    }
}
