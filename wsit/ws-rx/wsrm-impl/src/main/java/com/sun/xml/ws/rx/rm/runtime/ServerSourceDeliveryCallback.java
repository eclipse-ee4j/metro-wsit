/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.sun.xml.ws.api.message.Packet;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.runtime.delivery.Postman;

/**
 *
 */
class ServerSourceDeliveryCallback implements Postman.Callback {

    private static final Logger LOGGER = Logger.getLogger(ServerSourceDeliveryCallback.class);
    private final RuntimeContext rc;

    public ServerSourceDeliveryCallback(RuntimeContext rc) {
        this.rc = rc;
    }

    @Override
    public void deliver(ApplicationMessage message) {
        if (message instanceof JaxwsApplicationMessage) {
            deliver((JaxwsApplicationMessage) message);
        } else {
            throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRM_1141_UNEXPECTED_MESSAGE_CLASS(
                    message.getClass().getName(),
                    JaxwsApplicationMessage.class.getName())));
        }
    }
    
    @Override
    public RuntimeContext getRuntimeContext() {
        return rc;
    }

    public void deliver(JaxwsApplicationMessage message) {
        rc.sourceMessageHandler.attachAcknowledgementInfo(message);

        Packet outboundPacketCopy = message.getPacket().copy(true);

        rc.protocolHandler.appendSequenceHeader(outboundPacketCopy.getMessage(), message);
        rc.protocolHandler.appendAcknowledgementHeaders(outboundPacketCopy, message.getAcknowledgementData());

        rc.suspendedFiberStorage.resumeFiber(message.getCorrelationId(), outboundPacketCopy);
    }
}
