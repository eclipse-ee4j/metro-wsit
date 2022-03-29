/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common.client;

import org.w3c.dom.Element;
import com.sun.xml.ws.tx.coord.common.types.BaseRegisterType;
import com.sun.xml.ws.tx.coord.common.EndpointReferenceBuilder;
import com.sun.xml.ws.tx.coord.common.types.BaseRegisterResponseType;
import com.sun.xml.ws.tx.coord.common.WSCUtil;

import jakarta.xml.ws.EndpointReference;


public abstract class RegistrationMessageBuilder {
    protected boolean durable = true;
    protected Element txIdElement;
    protected Element routingElement;
    protected String participantAddress;
    protected String protocolIdentifier;

    protected RegistrationMessageBuilder() {}

    public RegistrationMessageBuilder durable(boolean durable) {
        this.durable = durable;
        return this;
    }

    public RegistrationMessageBuilder txId(String txId) {
        txIdElement = WSCUtil.referenceElementTxId(txId);
        return this;
    }

    public RegistrationMessageBuilder routing() {
        routingElement = WSCUtil.referenceElementRoutingInfo();
        return this;
    }

    public RegistrationMessageBuilder participantAddress(String address) {
        this.participantAddress = address;
        return this;
    }

    public RegistrationMessageBuilder protocolIdentifier(String protocolIdentifier) {
        this.protocolIdentifier = protocolIdentifier;
        return this;
    }

    public BaseRegisterType build() {
        if (participantAddress == null)
            participantAddress = getDefaultParticipantAddress();
        BaseRegisterType registerType = newRegistrationRequest();
        registerType.setParticipantProtocolService(getParticipantProtocolService());
        registerType.setProtocolIdentifier(protocolIdentifier);
        return registerType;
    }

    protected EndpointReference getParticipantProtocolService() {
        EndpointReferenceBuilder eprBuilder = getEndpointReferenceBuilder();
        return eprBuilder.address(participantAddress).
                referenceParameter(txIdElement).
                referenceParameter(routingElement).
                build();
    }

    protected abstract BaseRegisterType newRegistrationRequest();

    protected abstract String getDefaultParticipantAddress();

    protected abstract BaseRegisterResponseType buildRegistrationResponse();

    protected abstract EndpointReferenceBuilder getEndpointReferenceBuilder();


}
