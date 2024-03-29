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

import java.util.Map;

import jakarta.xml.soap.SOAPMessage;

import com.sun.xml.ws.api.message.Packet;


/**
 *
 */
public class PacketMapMessageInfo implements PacketMessageInfo {

    private SOAPAuthParam soapAuthParam;

    private Map<String, Object> infoMap;

    public PacketMapMessageInfo(Packet reqPacket, Packet resPacket) {
    soapAuthParam = new SOAPAuthParam(reqPacket,resPacket,0);
    }

    @Override
    public Map<String, Object> getMap() {
    if (this.infoMap == null) {
        this.infoMap = soapAuthParam.getMap();
    }
    return this.infoMap;
    }

    @Override
    public Object getRequestMessage() {
    return soapAuthParam.getRequest();
    }

    @Override
    public Object getResponseMessage() {
    return soapAuthParam.getResponse();
    }

    @Override
    public void setRequestMessage(Object request) {
    soapAuthParam.setRequest((SOAPMessage)request);
    }

    @Override
    public void setResponseMessage(Object response) {
    soapAuthParam.setResponse((SOAPMessage)response);
    }

    @Override
    public SOAPAuthParam getSOAPAuthParam() {
    return soapAuthParam;
    }

    @Override
    public Packet getRequestPacket() {
    return (Packet) soapAuthParam.getRequestPacket();
    }

    @Override
    public Packet getResponsePacket() {
    return (Packet) soapAuthParam.getResponsePacket();
    }

    @Override
    public void setRequestPacket(Packet p) {
    soapAuthParam.setRequestPacket(p);
    }

    @Override
    public void setResponsePacket(Packet p) {
    soapAuthParam.setResponsePacket(p);
    }
}











