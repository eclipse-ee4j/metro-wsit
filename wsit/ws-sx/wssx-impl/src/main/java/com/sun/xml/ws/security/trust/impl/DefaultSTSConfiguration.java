/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.impl;

import com.sun.xml.ws.api.security.trust.config.STSConfiguration;
import com.sun.xml.ws.api.security.trust.config.TrustSPMetadata;

import java.util.HashMap;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;

/**
 *
 * @author Jiandong Guo
 */
public class DefaultSTSConfiguration implements STSConfiguration{
    private Map<String, TrustSPMetadata> spMap = new HashMap<>();
    private String type;
    private String issuer;
    private boolean encryptIssuedToken = false;
    private boolean encryptIssuedKey = true;
    private long issuedTokenTimeout;

    private CallbackHandler callbackHandler;

    private Map<String, Object> otherOptions = new HashMap<>();

    public DefaultSTSConfiguration() {}

    @Override
    public void addTrustSPMetadata(final TrustSPMetadata data, final String spEndpoint){
        spMap.put(spEndpoint, data);
    }

    @Override
    public TrustSPMetadata getTrustSPMetadata(final String spEndpoint){
        return spMap.get(spEndpoint);
    }

    public void setType(String type){
        this.type = type;
    }

    @Override
    public String getType(){
        return this.type;
    }

    public void setIssuer(String issuer){
        this.issuer = issuer;
    }

    @Override
    public String getIssuer(){
        return this.issuer;
    }

    public void setEncryptIssuedToken(boolean encryptIssuedToken){
        this.encryptIssuedToken = encryptIssuedToken;
    }

    @Override
    public boolean getEncryptIssuedToken(){
        return this.encryptIssuedToken;
    }

    public void setEncryptIssuedKey(boolean encryptIssuedKey){
        this.encryptIssuedKey = encryptIssuedKey;
    }

    @Override
    public boolean getEncryptIssuedKey(){
        return this.encryptIssuedKey;
    }

    public void setIssuedTokenTimeout(long issuedTokenTimeout){
        this.issuedTokenTimeout = issuedTokenTimeout;
    }

    @Override
    public long getIssuedTokenTimeout(){
        return this.issuedTokenTimeout;
    }

    @Override
    public void setCallbackHandler(CallbackHandler callbackHandler){
        this.callbackHandler = callbackHandler;
    }

    @Override
    public CallbackHandler getCallbackHandler(){
        return this.callbackHandler;
    }

    @Override
    public Map<String, Object> getOtherOptions(){
        return this.otherOptions;
    }
}
