/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.policy.spi_impl;

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.ws.api.tx.at.WsatNamespace;
import com.sun.xml.ws.tx.at.policy.AtAlwaysCapability;
import com.sun.xml.ws.tx.at.policy.AtAssertion;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Marek Potociar
 */
public class AtAssertionValidator implements PolicyAssertionValidator {

    private static final List<String> SUPPORTED_DOMAINS = Collections.unmodifiableList(WsatNamespace.namespacesList());
    private static final List<QName> SUPPORTED_ASSERTIONS;

    static {
        List<QName> tmpList = new ArrayList<QName>(3);

        for (WsatNamespace ns : WsatNamespace.values()) {
            tmpList.add(AtAssertion.nameForNamespace(ns));
        }
        tmpList.add(AtAlwaysCapability.NAME);

        SUPPORTED_ASSERTIONS = Collections.unmodifiableList(tmpList);
    }

    public Fitness validateClientSide(final PolicyAssertion assertion) {
        return SUPPORTED_ASSERTIONS.contains(assertion.getName()) ? Fitness.SUPPORTED : Fitness.UNKNOWN;
    }

    public Fitness validateServerSide(final PolicyAssertion assertion) {
        return SUPPORTED_ASSERTIONS.contains(assertion.getName()) ? Fitness.SUPPORTED : Fitness.UNKNOWN;
    }

    public String[] declareSupportedDomains() {
        return SUPPORTED_DOMAINS.toArray(new String[SUPPORTED_DOMAINS.size()]);
    }
}
