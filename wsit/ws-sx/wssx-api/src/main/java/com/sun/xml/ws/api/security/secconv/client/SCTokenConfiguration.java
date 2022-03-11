/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.security.secconv.client;

import java.util.HashMap;
import java.util.Map;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.security.trust.client.IssuedTokenConfiguration;
import com.sun.xml.ws.security.policy.Token;

/**
 *
 * @author Shyam Rao
 */
public abstract class SCTokenConfiguration implements IssuedTokenConfiguration{

    public static final String PROTOCOL_10 = "http://schemas.xmlsoap.org/ws/2005/02/sc";
    public static final String PROTOCOL_13 = "http://docs.oasis-open.org/ws-sx/ws-secureconversation/200512";
    public static final String MAX_CLOCK_SKEW = "maxClockSkew";

    protected String protocol;

    protected boolean renewExpiredSCT = false;

    protected boolean requireCancelSCT = false;

    protected long scTokenTimeout = -1;

    private Map<String, Object> otherOptions = new HashMap<>();

    protected SCTokenConfiguration(){
        this(PROTOCOL_10);
    }

    protected SCTokenConfiguration(String protocol){
        this.protocol = protocol;
    }

    @Override
    public String getProtocol(){
        return protocol;
    }

    public boolean isRenewExpiredSCT(){
        return renewExpiredSCT;
    }

    public boolean isRequireCancelSCT(){
        return requireCancelSCT;
    }

    public long getSCTokenTimeout(){
        return this.scTokenTimeout;
    }

    public abstract String getTokenId();

    public abstract boolean checkTokenExpiry();

    public abstract boolean isClientOutboundMessage();

    public abstract boolean addRenewPolicy();

    public abstract boolean getReqClientEntropy();

    public abstract boolean isSymmetricBinding();

    public abstract int getKeySize();

    public abstract Token getSCToken();

    public abstract Packet getPacket();

    public abstract Tube getClientTube();

    public abstract Tube getNextTube();

    public abstract WSDLPort getWSDLPort();

    public abstract WSBinding getWSBinding();

    public abstract AddressingVersion getAddressingVersion();

    public Map<String, Object> getOtherOptions(){
        return this.otherOptions;
    }
}
