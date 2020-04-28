/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.runtime;

import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.mc.localization.LocalizationMessages;
import com.sun.xml.ws.rx.mc.dev.ProtocolMessageHandler;
import com.sun.xml.ws.rx.util.ResumeFiberException;
import com.sun.xml.ws.rx.util.SuspendedFiberStorage;
import java.util.Map;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFault;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
class WsMcResponseHandler extends McResponseHandlerBase {

    private static final Logger LOGGER = Logger.getLogger(WsMcResponseHandler.class);
    //
    private final Map<String, ProtocolMessageHandler> actionToProtocolHandlerMap;

    public WsMcResponseHandler(
            final McConfiguration configuration,
            final MakeConnectionSenderTask mcSenderTask,
            final SuspendedFiberStorage suspendedFiberStorage,
            final Map<String, ProtocolMessageHandler> protocolHandlerMap) {

        super(configuration, mcSenderTask, suspendedFiberStorage);

        this.actionToProtocolHandlerMap = protocolHandlerMap;
    }

    public void onCompletion(Packet response) {
        try {
            Message responseMessage = response.getMessage();

            if (responseMessage == null) {
                LOGGER.warning(LocalizationMessages.WSMC_0112_NO_RESPONSE_RETURNED());
                return;
            }

            if (!responseMessage.hasHeaders()) {
                LOGGER.severe(LocalizationMessages.WSMC_0113_NO_WSMC_HEADERS_IN_RESPONSE());
                return;
            }

            super.processMakeConnectionHeaders(responseMessage);

            if (responseMessage.isFault()) {
                // processing WS-MC SOAP faults
                String faultAction = AddressingUtils.getAction(responseMessage.getHeaders(), configuration.getAddressingVersion(), configuration.getSoapVersion());
                if (configuration.getRuntimeVersion().protocolVersion.isFault(faultAction)) {
                    SOAPFault fault = null;
                    try {
                        fault = responseMessage.readAsSOAPMessage().getSOAPBody().getFault();
                    } catch (SOAPException ex) {
                        throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSMC_0114_ERROR_UNMARSHALLING_SOAP_FAULT(), ex));
                    }

                    throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSMC_0115_UNEXPECTED_PROTOCOL_ERROR(fault.getFaultString())));
                }
            }

            Header wsaRelatesToHeader = responseMessage.getHeaders().get(configuration.getAddressingVersion().relatesToTag, false);
            if (wsaRelatesToHeader != null) {
                // find original request fiber
                setCorrelationId(wsaRelatesToHeader.getStringContent()); // initializing correlation id for getParentFiber()
                try {
                    resumeParentFiber(response);
                    return;
                } catch (ResumeFiberException ex) {
                    LOGGER.warning(LocalizationMessages.WSMC_0116_RESUME_PARENT_FIBER_ERROR(), ex);
                }
            }

            LOGGER.finer(LocalizationMessages.WSMC_0117_PROCESSING_RESPONSE_AS_PROTOCOL_MESSAGE());
            Header wsaActionHeader = responseMessage.getHeaders().get(configuration.getAddressingVersion().actionTag, false);
            if (wsaActionHeader != null) {
                String wsaAction = wsaActionHeader.getStringContent();
                ProtocolMessageHandler handler = actionToProtocolHandlerMap.get(wsaAction);
                if (handler != null) {
                    LOGGER.finer(LocalizationMessages.WSMC_0118_PROCESSING_RESPONSE_IN_PROTOCOL_HANDLER(
                            wsaAction,
                            handler.getClass().getName()));

                    handler.processProtocolMessage(response);
                } else {
                    LOGGER.warning(LocalizationMessages.WSMC_0119_UNABLE_TO_FIND_PROTOCOL_HANDLER(wsaAction));
                }
            } else {
                LOGGER.severe(LocalizationMessages.WSMC_0120_WSA_ACTION_HEADER_MISSING());
            }
        } finally {
            mcSenderTask.clearMcRequestPendingFlag();
        }
    }

    public void onCompletion(Throwable error) {
        try {
            LOGGER.warning(LocalizationMessages.WSMC_0121_FAILED_TO_SEND_WSMC_REQUEST(), error);
            suspendedFiberStorage.resumeAllFibers(error);
        } finally {
            mcSenderTask.clearMcRequestPendingFlag();
        }
    }
}
