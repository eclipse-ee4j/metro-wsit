/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module org.glassfish.metro.config.api {

    requires transitive com.sun.xml.ws.rt;
    requires com.sun.xml.ws.policy;

    exports com.sun.xml.ws.config.metro;
    exports com.sun.xml.ws.policy.config;
}