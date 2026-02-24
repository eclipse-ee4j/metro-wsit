/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module org.glassfish.metro.wsit.api {

    requires java.logging;
    requires transitive java.xml;
    requires transitive jakarta.xml.ws;
    requires transitive jakarta.xml.bind;

    requires transitive com.sun.xml.ws.rt;
    requires transitive com.sun.istack.runtime;
    requires transitive com.sun.xml.ws.policy;
    requires transitive org.glassfish.gmbal.impl;
    requires transitive org.glassfish.ha.api;

    exports com.oracle.webservices.oracle_internal_api.rm;
    exports com.sun.xml.ws.api.transport.tcp;
    exports com.sun.xml.ws.api.tx.at;
    exports com.sun.xml.ws.commons;
    exports com.sun.xml.ws.commons.ha;
    exports com.sun.xml.ws.config.metro;
    exports com.sun.xml.ws.metro.api.config.management;
    exports com.sun.xml.ws.policy.config;
    exports com.sun.xml.ws.transport.tcp.dev;
    exports com.sun.xml.ws.rx.mc.api;
    exports com.sun.xml.ws.rx.mc.dev;
    exports com.sun.xml.ws.rx.rm.api;
    exports com.sun.xml.ws.tx.dev;

}
