/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;


import java.util.Collection;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;

/**
 *
 * @author K.Venugopal@sun.com
 */

public class SignedEndorsingSupportingTokens extends SupportingTokens implements  com.sun.xml.ws.security.policy.SignedEndorsingSupportingTokens{

    /**
     * Creates a new instance of SignedEndorsingSupportingTokens
     */
    public SignedEndorsingSupportingTokens() {
    }

    public SignedEndorsingSupportingTokens(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }

}
