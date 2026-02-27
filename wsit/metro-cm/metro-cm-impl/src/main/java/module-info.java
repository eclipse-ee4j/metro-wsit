/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module org.glassfish.metro.cm.impl {

    requires com.sun.istack.runtime;
    requires com.sun.xml.ws.rt;
    requires org.glassfish.metro.cm.api;

    provides com.sun.xml.ws.api.config.management.ManagedEndpointFactory with
            com.sun.xml.ws.config.management.server.EndpointFactoryImpl;
}
