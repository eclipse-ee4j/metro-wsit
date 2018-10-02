/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common.client;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.OneWayFeature;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.tx.coord.common.EndpointReferenceBuilder;
import com.sun.xml.ws.tx.coord.common.PendingRequestManager;
import com.sun.xml.ws.tx.coord.common.RegistrationIF;
import com.sun.xml.ws.tx.coord.common.WSCUtil;
import com.sun.xml.ws.tx.coord.common.types.BaseRegisterResponseType;
import com.sun.xml.ws.tx.coord.common.types.BaseRegisterType;

import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceFeature;
import java.util.ArrayList;
import java.util.List;


public abstract class RegistrationProxyBuilder {
    protected List<WebServiceFeature> features;
    protected EndpointReference to;
    protected String txId;
    protected long timeout;
    protected String callbackAddress;


    public RegistrationProxyBuilder feature(WebServiceFeature feature){
        if(feature == null) return  this;
        if(features == null) features = new ArrayList<WebServiceFeature>();
        features.add(feature);
        return  this;
   }

    public RegistrationProxyBuilder txIdForReference(String txId) {
        this.txId = txId;
        return  this;
    }

    public RegistrationProxyBuilder to(EndpointReference endpointReference){
        this.to = endpointReference;
        return this;
   }

    public RegistrationProxyBuilder timeout(long timeout){
        this.timeout = timeout;
        return this;
   }

    public RegistrationProxyBuilder callback(String callbackAddress){
        this.callbackAddress = callbackAddress;
        return this;
   }

   protected abstract String getDefaultCallbackAddress();

   protected abstract EndpointReferenceBuilder getEndpointReferenceBuilder();
   
    protected WebServiceFeature[] getEnabledFeatures(){
        return features.toArray(new WebServiceFeature[0]);
    }


    public RegistrationIF build(){
        if (callbackAddress == null)
            callbackAddress = getDefaultCallbackAddress();
        EndpointReference epr = getEndpointReferenceBuilder().address(callbackAddress).
                referenceParameter(WSCUtil.referenceElementTxId(txId), WSCUtil.referenceElementRoutingInfo()).build();
        WSEndpointReference wsepr = WSEndpointReference.create(epr);
        OneWayFeature oneway = new OneWayFeature(true, wsepr);
        this.feature(oneway);
        return null;
    }

    public abstract class RegistrationProxyF<T extends EndpointReference,K,P,D> implements RegistrationIF<T,K,P> {

        public BaseRegisterResponseType<T,P> registerOperation(BaseRegisterType<T,K> parameters){
            try {
                PendingRequestManager.ResponseBox box = PendingRequestManager.reqisterRequest(txId);
                asyncRegister(parameters.getDelegate());
                return box.getResponse(timeout);
            } finally {
                PendingRequestManager.removeRequest(txId);
            }
        }

        public abstract D getDelegate();

        public abstract void asyncRegister(K parameters);

        public abstract AddressingVersion getAddressingVersion();
    }
}
