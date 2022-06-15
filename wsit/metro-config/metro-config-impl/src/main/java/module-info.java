/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module org.glassfish.metro.config.impl {

    requires transitive org.glassfish.metro.config.api;
    requires com.sun.xml.ws.policy;

    exports com.sun.xml.ws.policy.parser;

    opens com.sun.xml.ws.config.metro.parser.jsr109 to jakarta.xml.bind;

    uses com.sun.xml.ws.config.metro.ElementFeatureMapping;

    provides com.sun.xml.ws.api.policy.PolicyResolverFactory with
            com.sun.xml.ws.policy.parser.WsitPolicyResolverFactory;
    provides com.sun.xml.ws.policy.spi.PolicyAssertionValidator with
            com.sun.xml.ws.policy.jcaps.JCapsPolicyValidator;
}