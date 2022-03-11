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

import com.sun.xml.ws.api.security.trust.config.TrustSPMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jiandong Guo
 */
public class DefaultTrustSPMetadata implements TrustSPMetadata{

    //private String endpoint;
    private String tokenType;
    private String keyType;
    private String certAlias;
    private Map<String, Object> otherOptions = new HashMap<>();

    /** Creates a new instance of DefaultTrustSPMetedata */
    public DefaultTrustSPMetadata(String endpoint) {
        //this.endpoint = endpoint;
    }

    public void setCertAlias(final String certAlias){
        this.certAlias = certAlias;
    }

    @Override
    public String getCertAlias(){
        return this.certAlias;
    }

    public void setTokenType(final String tokenType){
        this.tokenType = tokenType;
    }

     @Override
     public String getTokenType(){
        return this.tokenType;
    }

    public void setKeyType(final String keyType){
        this.keyType = keyType;
    }

    @Override
    public String getKeyType(){
        return this.keyType;
    }

    @Override
    public Map<String, Object> getOtherOptions(){
        return this.otherOptions;
    }
}
