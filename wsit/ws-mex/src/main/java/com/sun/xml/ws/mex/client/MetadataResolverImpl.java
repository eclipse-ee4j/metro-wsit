/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.mex.client;

import java.net.URI;
import com.sun.xml.ws.mex.client.schema.Metadata;
import com.sun.xml.ws.api.wsdl.parser.MetaDataResolver;
import com.sun.xml.ws.api.wsdl.parser.ServiceDescriptor;

/**
 * Plugin to wsimport for mex/ws-transfer requests.
 */
public class MetadataResolverImpl extends MetaDataResolver {

    MetadataClient mClient;

    protected MetadataResolverImpl() {
        mClient = new MetadataClient();
    }

    /**
     * This method is called by JAX-WS code to retrieve metadata.
     * The contract is that, if there are problems trying to get the
     * metadata with mex, this method returns null and the JAX-WS
     * code can try retrieving it another way (for instance, with
     * a ?wsdl http GET call).
     */
    @Override
    public ServiceDescriptor resolve(final URI location) {
        final Metadata mData = mClient.retrieveMetadata(location.toString());
        if (mData == null) {
            return null;
        }
        return new ServiceDescriptorImpl(mData);
    }

}
