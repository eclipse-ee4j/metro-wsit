/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class EndorsingEncryptedSupportingTokens extends EndorsingSupportingTokens implements com.sun.xml.ws.security.policy.EndorsingEncryptedSupportingTokens{
    
    /** Creates a new instance of EndorsingEncryptedSupportingTokens */
    public EndorsingEncryptedSupportingTokens() {
    }
    
    public EndorsingEncryptedSupportingTokens(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }
    
}
