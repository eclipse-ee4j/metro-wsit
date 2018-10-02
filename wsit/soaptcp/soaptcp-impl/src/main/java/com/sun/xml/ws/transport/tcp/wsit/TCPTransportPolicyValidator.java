/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.wsit;

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator.Fitness;
import java.util.ArrayList;
import javax.xml.namespace.QName;

import static com.sun.xml.ws.transport.tcp.wsit.TCPConstants.*;

/**
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class TCPTransportPolicyValidator implements PolicyAssertionValidator {
    
    private static final ArrayList<QName> clientSupportedAssertions = new ArrayList<QName>(2);
    private static final ArrayList<QName> commonSupportedAssertions = new ArrayList<QName>(2);
    
    static {
        clientSupportedAssertions.add(SELECT_OPTIMAL_TRANSPORT_ASSERTION);
        commonSupportedAssertions.add(TCPTRANSPORT_POLICY_ASSERTION);
        commonSupportedAssertions.add(TCPTRANSPORT_CONNECTION_MANAGEMENT_ASSERTION);
    }
    
    /** Creates a new instance of TCPTransportPolicyValidator */
    public TCPTransportPolicyValidator() {
    }
    
    public PolicyAssertionValidator.Fitness validateClientSide(final PolicyAssertion assertion) {
        return clientSupportedAssertions.contains(assertion.getName()) ||
                commonSupportedAssertions.contains(assertion.getName()) ? Fitness.SUPPORTED : Fitness.UNKNOWN;
    }
    
    public PolicyAssertionValidator.Fitness validateServerSide(final PolicyAssertion assertion) {
        return commonSupportedAssertions.contains(assertion.getName()) ? Fitness.SUPPORTED : Fitness.UNKNOWN;
    }
    
    public String[] declareSupportedDomains() {
        return new String[] {TCPTRANSPORT_POLICY_NAMESPACE_URI,
        CLIENT_TRANSPORT_NS, TCPTRANSPORT_CONNECTION_MANAGEMENT_NAMESPACE_URI};
    }
    
}
