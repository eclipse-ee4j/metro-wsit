/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.policy.spi_impl;

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator.Fitness;
import com.sun.xml.ws.rx.mc.policy.McAssertionNamespace;
import com.sun.xml.ws.rx.mc.policy.wsmc200702.MakeConnectionSupportedAssertion;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class McAssertionValidator implements PolicyAssertionValidator {

    private static final ArrayList<QName> SERVER_SIDE_ASSERTIONS = new ArrayList<QName>(1);
    private static final ArrayList<QName> CLIENT_SIDE_ASSERTIONS = new ArrayList<QName>(1);

    private static final List<String> SUPPORTED_DOMAINS = Collections.unmodifiableList(McAssertionNamespace.namespacesList());

    static {
        SERVER_SIDE_ASSERTIONS.add(MakeConnectionSupportedAssertion.NAME);

        // CLIENT_SIDE_ASSERTIONS.add();
        CLIENT_SIDE_ASSERTIONS.addAll(SERVER_SIDE_ASSERTIONS);
    }

    public McAssertionValidator() {
    }

    public Fitness validateClientSide(PolicyAssertion assertion) {
        return CLIENT_SIDE_ASSERTIONS.contains(assertion.getName()) ? Fitness.SUPPORTED : Fitness.UNKNOWN;
    }

    public Fitness validateServerSide(PolicyAssertion assertion) {
        QName assertionName = assertion.getName();
        if (SERVER_SIDE_ASSERTIONS.contains(assertionName)) {
            return Fitness.SUPPORTED;
        } else if (CLIENT_SIDE_ASSERTIONS.contains(assertionName)) {
            return Fitness.UNSUPPORTED;
        } else {
            return Fitness.UNKNOWN;
        }
    }

    public String[] declareSupportedDomains() {
        return SUPPORTED_DOMAINS.toArray(new String[SUPPORTED_DOMAINS.size()]);
    }
}
