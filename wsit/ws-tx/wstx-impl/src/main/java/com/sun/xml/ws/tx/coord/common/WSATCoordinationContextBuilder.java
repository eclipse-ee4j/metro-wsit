/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common;

import com.sun.xml.ws.tx.coord.common.types.CoordinationContextIF;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.tx.at.Transactional;


public abstract class WSATCoordinationContextBuilder {
    protected String coordinationType;
    protected String identifier;
    protected long expires;
    protected String registrationCoordinatorAddress;
    protected String txId;
    protected boolean mustUnderstand = false;
    protected SOAPVersion soapVersion = SOAPVersion.SOAP_11;

    protected WSATCoordinationContextBuilder() {}

    public static WSATCoordinationContextBuilder newInstance(Transactional.Version version) {
        if(Transactional.Version.WSAT10 == version)
        return new com.sun.xml.ws.tx.coord.v10.WSATCoordinationContextBuilderImpl();
        else if(Transactional.Version.WSAT11 == version || Transactional.Version.WSAT12 == version) {
          return new com.sun.xml.ws.tx.coord.v11.WSATCoordinationContextBuilderImpl();
        }else {
            throw new IllegalArgumentException(version + "is not a supported ws-at version");
        }
    }


    public WSATCoordinationContextBuilder txId(String txId) {
        this.txId = txId;
        return this;
    }

    public WSATCoordinationContextBuilder registrationCoordinatorAddress(String registrationCoordinatorAddress) {
        this.registrationCoordinatorAddress = registrationCoordinatorAddress;
        return this;
    }

    public WSATCoordinationContextBuilder soapVersion(SOAPVersion soapVersion){
        if(soapVersion == null)
            throw new IllegalArgumentException("SOAP version can't null!");
        this.soapVersion = soapVersion;
        return this;
    }

    public WSATCoordinationContextBuilder mustUnderstand(boolean mustUnderstand){
        this.mustUnderstand = mustUnderstand;
        return this;
    }

    public WSATCoordinationContextBuilder expires(long expires) {
        this.expires = expires;
        return this;
    }


    public CoordinationContextIF build() {
        CoordinationContextBuilder builder = configBuilder();
        return builder.build();
    }


    private CoordinationContextBuilder configBuilder() {
        if (registrationCoordinatorAddress == null)
            registrationCoordinatorAddress = getDefaultRegistrationCoordinatorAddress();
        CoordinationContextBuilder builder = newCoordinationContextBuilder();
        builder.coordinationType(getCoordinationType()).
                address(registrationCoordinatorAddress).
                identifier("urn:uuid:" + txId).
                txId(txId).
                expires(expires).
                soapVersion(soapVersion).
                mustUnderstand(mustUnderstand);
        return builder;
    }

    protected abstract CoordinationContextBuilder newCoordinationContextBuilder();

    protected abstract String getCoordinationType();

    protected abstract String getDefaultRegistrationCoordinatorAddress();
}
