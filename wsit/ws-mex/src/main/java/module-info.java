/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module org.glassfish.metro.ws.mex {

    requires com.sun.xml.ws.rt;
    requires com.sun.xml.ws.servlet;

    provides com.sun.xml.ws.api.wsdl.parser.MetadataResolverFactory with
            com.sun.xml.ws.mex.client.MetadataResolverFactoryImpl;
}