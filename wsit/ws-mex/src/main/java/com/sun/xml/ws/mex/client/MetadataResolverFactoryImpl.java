/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.mex.client;

import org.xml.sax.EntityResolver;
import com.sun.xml.ws.api.wsdl.parser.MetadataResolverFactory;
import com.sun.xml.ws.api.wsdl.parser.MetaDataResolver;

/**
 * Factory class for metadata resolver. The JAX-WS code uses this
 * class to instantiate a MetadataResolver object, and then uses
 * that object to get a ServiceDescriptor by passing in the URL
 * of a service.
 */
public class MetadataResolverFactoryImpl extends MetadataResolverFactory {

    // not currently using EntityResolver, but may later
    @Override
    public MetaDataResolver metadataResolver(final EntityResolver resolver) {
        return new MetadataResolverImpl();
    }
}
