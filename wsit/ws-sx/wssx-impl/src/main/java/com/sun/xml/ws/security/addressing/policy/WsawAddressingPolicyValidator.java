/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.addressing.policy;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;

import java.util.ArrayList;
import javax.xml.namespace.QName;

/**
 * This class validates the wsaw:UsingAddressing assertion.
 * This class exists in WSIT to provide functionality for backwards compatibility with previously generated
 * wsaw:UsingAddressing assertion.
 *
 * @author Rama Pulavarthi
 */
public class WsawAddressingPolicyValidator implements PolicyAssertionValidator{

    private static final ArrayList<QName> supportedAssertions = new ArrayList<>();

    static {
        supportedAssertions.add(new QName(AddressingVersion.W3C.policyNsUri,"UsingAddressing"));
    }

    /**
     * Creates a new instance of AddressingPolicyValidator
     */
    public WsawAddressingPolicyValidator() {
    }

    @Override
    public Fitness validateClientSide(PolicyAssertion assertion) {
        return supportedAssertions.contains(assertion.getName()) ? Fitness.SUPPORTED : Fitness.UNKNOWN;
    }

    @Override
    public Fitness validateServerSide(PolicyAssertion assertion) {
        return supportedAssertions.contains(assertion.getName()) ? Fitness.SUPPORTED : Fitness.UNKNOWN;
    }

    @Override
    public String[] declareSupportedDomains() {
        return new String[] {AddressingVersion.W3C.policyNsUri};
    }
}
