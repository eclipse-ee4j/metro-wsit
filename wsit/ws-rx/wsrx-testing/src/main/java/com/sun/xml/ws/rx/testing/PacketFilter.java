/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.testing;

import com.sun.xml.ws.api.message.Packet;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.rm.runtime.RmRuntimeVersion;
import com.sun.xml.ws.rx.rm.runtime.JaxwsApplicationMessage;
import com.sun.xml.ws.rx.rm.runtime.RuntimeContext;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public abstract class PacketFilter {

    protected static final long UNSPECIFIED = -1;
    private static final Logger LOGGER = Logger.getLogger(PacketFilter.class);

    private RuntimeContext rc;

    protected PacketFilter() {
    }

    /**
     * Method is called during the client-side request packet processing, which means that it is called BEFORE the request
     * is sent to the service.
     *
     * @param request original request packet to be filtered
     *
     * @return filtered packet
     *
     * @exception java.lang.Exception any exception that may occur during processing.
     */
    public abstract Packet filterClientRequest(Packet request) throws Exception;

    /**
     * Method is called during the server-side response packet processing, which means that it is called BEFORE the response
     * is sent to the client.
     *
     * @param response original response packet to be filtered
     *
     * @return filtered packet
     *
     * @exception java.lang.Exception any exception that may occur during processing.
     */
    public abstract Packet filterServerResponse(Packet response) throws Exception;

    /**
     * Retrieves RM sequence identifier form the message stored in the packet.
     *
     * @param packet packet to be checked for the RM sequence identifier
     *
     * @return RM sequence identifier. May return {@code null} if there is no RM sequence identifier
     * associated with this packet.
     */
    protected final String getSequenceId(Packet packet) {
        try {
            if (notInitialized(packet) || isRmProtocolMessage(packet)) {
                return null;
            }

            JaxwsApplicationMessage message = new JaxwsApplicationMessage(packet, packet.getMessage().getID(rc.addressingVersion, rc.soapVersion));
            rc.protocolHandler.loadSequenceHeaderData(message, message.getJaxwsMessage());
            return message.getSequenceId();
        } catch (Exception ex) {
            LOGGER.warning("Unexpected exception occured", ex);
            return null;
        }
    }

    /**
     * Retrieves RM sequence message identifier form the message stored in the packet.
     *
     * @param packet packet to be checked for the RM message identifier
     *
     * @return RM sequence message identifier. May return {@link #UNSPECIFIED} if there is no RM message identifier
     * associated with this packet.
     */
    protected final long getMessageId(Packet packet) {
        try {
            if (notInitialized(packet) || isRmProtocolMessage(packet)) {
                return UNSPECIFIED;
            }

            JaxwsApplicationMessage message = new JaxwsApplicationMessage(packet, packet.getMessage().getID(rc.addressingVersion, rc.soapVersion));
            rc.protocolHandler.loadSequenceHeaderData(message, message.getJaxwsMessage());
            return message.getMessageNumber();
        } catch (Exception ex) {
            LOGGER.warning("Unexpected exception occured", ex);
            return UNSPECIFIED;
        }
    }

    /**
     * Provides information on RM version configured on the current web service port.
     * May return {@code null} if RM is not enabled on the port.
     *
     * @return RM version configured on the current WS port or {@code null} if RM is not enabled.
     */
    protected final RmRuntimeVersion getRmVersion() {
        return rc.rmVersion;
    }

    protected final boolean isRmProtocolMessage(Packet packet) {
        return rc.rmVersion.protocolVersion.isProtocolAction(rc.communicator.getWsaAction(packet));
    }

    final void configure(RuntimeContext context) {
        this.rc = context;
    }

    private boolean notInitialized(Packet packet) {
        return rc == null || packet == null || packet.getMessage() == null;
    }
}
