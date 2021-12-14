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

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import java.util.Iterator;

/**
 *
 * @author sk112103
 */
public class JMACAuthModuleConfiguration extends PolicyAssertion implements com.sun.xml.ws.security.policy.JMACAuthModuleConfiguration, SecurityAssertionValidator {
    
    /** Creates a new instance of JMACAuthModuleConfiguration */
    public JMACAuthModuleConfiguration() {
    }

    @Override
    public SecurityAssertionValidator.AssertionFitness validate(boolean isServer) {
        return null;
    }

    @Override
    public Iterator<? extends PolicyAssertion> getAuthModules() {
        return null;
    }

    @Override
    public String getOverrideDefaultTokenValidation() {
        return null;
    }

    @Override
    public String getOverrideDefaultAuthModules() {
        return null;
    }
    
}
