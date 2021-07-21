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

import com.sun.xml.ws.rx.util.AbstractResponseHandler;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.mc.protocol.wsmc200702.MessagePendingElement;
import com.sun.xml.ws.rx.util.SuspendedFiberStorage;
import jakarta.xml.bind.JAXBException;
import javax.xml.namespace.QName;

/**
 *
 */
abstract class McResponseHandlerBase extends AbstractResponseHandler implements Fiber.CompletionCallback {

    private static final Logger LOGGER = Logger.getLogger(McResponseHandlerBase.class);
    //
    protected final McConfiguration configuration;
    protected final MakeConnectionSenderTask mcSenderTask;

    protected McResponseHandlerBase(McConfiguration configuration, MakeConnectionSenderTask mcSenderTask, SuspendedFiberStorage suspendedFiberStorage, String correlationId) {
        super(suspendedFiberStorage, correlationId);

        this.configuration = configuration;
        this.mcSenderTask = mcSenderTask;
    }

    protected McResponseHandlerBase(McConfiguration configuration, MakeConnectionSenderTask mcSenderTask, SuspendedFiberStorage suspendedFiberStorage) {
        super(suspendedFiberStorage, null);

        this.configuration = configuration;
        this.mcSenderTask = mcSenderTask;
    }

    protected final void processMakeConnectionHeaders(@NotNull Message responseMessage) throws RxRuntimeException {
        assert responseMessage != null;

        // process WS-MC header
        if (responseMessage.hasHeaders()) {
            MessagePendingElement messagePendingHeader = readHeaderAsUnderstood(responseMessage, configuration.getRuntimeVersion().protocolVersion.messagePendingHeaderName);
            if (messagePendingHeader != null && messagePendingHeader.isPending()) {
                mcSenderTask.scheduleMcRequest();
            }
        }
    }

    private final <T> T readHeaderAsUnderstood(Message message, QName headerName) throws RxRuntimeException {
        // TODO P3 merge this method with PacketAdapter method
        Header header = message.getHeaders().get(headerName, true);
        if (header == null) {
            return null;
        }

        try {
            @SuppressWarnings("unchecked")
            T result = (T) header.readAsJAXB(configuration.getRuntimeVersion().getUnmarshaller(configuration.getAddressingVersion()));
            return result;
        } catch (JAXBException ex) {
            throw LOGGER.logSevereException(new RxRuntimeException(String.format("Error unmarshalling header %s", headerName), ex));
        }
    }
}
