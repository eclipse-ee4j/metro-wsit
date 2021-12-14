/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * @author ashutosh.shahi@sun.com
 */
public class KerberosConfig extends PolicyAssertion implements com.sun.xml.ws.security.policy.KerberosConfig {

    private static QName loginModule = new QName("loginModule");
    private static QName servicePrincipal = new QName("servicePrincipal");
    private static QName credentialDelegation = new QName("credentialDelegation");
    
    /** Creates a new instance of KerberosConfig */
    public KerberosConfig() {
    }
    
    public KerberosConfig(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }

    @Override
    public String getLoginModule() {
         return this.getAttributeValue(loginModule);
    }

    @Override
    public String getServicePrincipal() {
        return this.getAttributeValue(servicePrincipal);
    }

    @Override
    public String getCredentialDelegation() {
        return this.getAttributeValue(credentialDelegation);
    }
    
}
