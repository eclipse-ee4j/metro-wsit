/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module org.glassfish.metro.soaptcp.api {

    requires transitive org.glassfish.metro.config.api;

    exports com.sun.xml.ws.api.transport.tcp;
    exports com.sun.xml.ws.transport.tcp.dev;
}