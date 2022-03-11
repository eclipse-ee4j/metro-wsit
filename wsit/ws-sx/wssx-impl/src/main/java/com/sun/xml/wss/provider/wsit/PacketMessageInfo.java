/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider.wsit;

import jakarta.security.auth.message.MessageInfo;

import com.sun.xml.ws.api.message.Packet;

/**
 *
 */
public interface PacketMessageInfo extends MessageInfo {

    SOAPAuthParam getSOAPAuthParam();

    Packet getRequestPacket();

    Packet getResponsePacket();

    void setRequestPacket(Packet p);

    void setResponsePacket(Packet p);

}











