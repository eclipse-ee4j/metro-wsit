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

    public SecurityAssertionValidator.AssertionFitness validate(boolean isServer) {
        return null;
    }

    public Iterator<? extends PolicyAssertion> getAuthModules() {
        return null;
    }

    public String getOverrideDefaultTokenValidation() {
        return null;
    }

    public String getOverrideDefaultAuthModules() {
        return null;
    }
    
}
