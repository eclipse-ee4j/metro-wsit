/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
import javax.xml.namespace.QName;

/**
 *
 * @author Nithya Subramanian
 */
public class SessionManagerStore extends PolicyAssertion implements com.sun.xml.ws.security.policy.SessionManagerStore {

    private static QName sessionTimeout = new QName("sessionTimeout");
    private static QName sessionThreshold = new QName("sessionThreshold");

    public SessionManagerStore(AssertionData name, Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name, nestedAssertions, nestedAlternative);
    }

    public SessionManagerStore() {
    }
    
    @Override
    public String getSessionTimeOut() {
        return this.getAttributeValue(sessionTimeout);
    }
    @Override
    public String getSessionThreshold() {
        return this.getAttributeValue(sessionThreshold);
    }
}
