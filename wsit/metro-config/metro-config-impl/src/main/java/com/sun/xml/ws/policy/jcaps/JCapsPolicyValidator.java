/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.policy.jcaps;

import com.sun.xml.ws.policy.spi.AbstractQNameValidator;
import java.util.ArrayList;
import javax.xml.namespace.QName;

/**
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class JCapsPolicyValidator extends AbstractQNameValidator {
    public static final String NS_URI_BASIC_AUTHENTICATION_SECURITY_POLICY = "http://sun.com/ws/httpbc/security/BasicauthSecurityPolicy";    
    private static final ArrayList<QName> supportedAssertions = new ArrayList<>(2);
    
    static{
        supportedAssertions.add(new QName(NS_URI_BASIC_AUTHENTICATION_SECURITY_POLICY, "MustSupportBasicAuthentication"));
        supportedAssertions.add(new QName(NS_URI_BASIC_AUTHENTICATION_SECURITY_POLICY, "UsernameToken"));
    }
    
    /**
     * Creates new instance of JCapsPolicyValidator
     */
    public JCapsPolicyValidator() {
        super(supportedAssertions, supportedAssertions);
    }
}
