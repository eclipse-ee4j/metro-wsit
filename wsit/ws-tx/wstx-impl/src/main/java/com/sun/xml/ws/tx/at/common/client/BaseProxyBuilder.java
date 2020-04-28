/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.common.client;

import com.sun.xml.ws.api.addressing.OneWayFeature;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.tx.coord.common.WSCUtil;
import com.sun.xml.ws.tx.at.common.WSATVersion;

import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.WebServiceFeature;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * This is the base class for building client proxy for participant and coordinator.
 */
public abstract class BaseProxyBuilder<T, B extends BaseProxyBuilder<T,B>> {
    protected WSATVersion<T> version;
    protected EndpointReference to;
    protected EndpointReference replyTo;
    protected List<WebServiceFeature> features;


    protected BaseProxyBuilder(WSATVersion<T> version) {
        this.version = version;
        feature(version.newAddressingFeature());
    }

    /**
     * Add feature to be enabled on the proxy built by this builder.
     * @param feature WebServiceFeature
     */
    public void feature(WebServiceFeature feature){
        if(feature == null) return;
        if(features == null) features = new ArrayList<WebServiceFeature>();
        features.add(feature);
   }

    /**
     * specifiy the wsa:to and endpoint reference parameters of the proxy built by this builder
     * @param to EndpointReference
     * @return
     */
    public B to(EndpointReference to) {
        this.to = to;
        return (B) this;
    }

    /**
     * specifiy the wsa:replyTo of the proxy built by this builder
     * @param replyTo
     * @return
     */
    public B replyTo(EndpointReference replyTo) {
        this.replyTo = replyTo;
        if(replyTo!=null)
          feature(new OneWayFeature(true, WSEndpointReference.create(replyTo)));
        return (B) this;
    }

    /**
     * specify the transaction ID as the reference parameters
     * @param txId
     * @param bqual
     * @return
     */
    public B txIdForReference(String txId, String bqual) {
        EndpointReference endpointReference = version.newEndpointReferenceBuilder().address(getDefaultCallbackAddress()).
                referenceParameter(
                        WSCUtil.referenceElementTxId(txId), WSCUtil.referenceElementBranchQual(bqual), WSCUtil.referenceElementRoutingInfo()
                ).build();
        replyTo(endpointReference);
        return (B) this;
    }


    protected WebServiceFeature[] getEnabledFeatures() {
        return features.toArray(new WebServiceFeature[0]);
    }

    /**
     * the replyto address for the corresponding endpoints.
     * @return
     */
    protected abstract String getDefaultCallbackAddress();
}
