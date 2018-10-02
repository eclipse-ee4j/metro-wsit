/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.policy.parser;

import com.sun.xml.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.ws.api.policy.PolicyResolver;

/**
 * @author Rama Pulavarthi
 */
public class WsitPolicyResolverFactory extends PolicyResolverFactory {
    public PolicyResolver doCreate() {
        // return WSIT Policy resolver that parses the WSIT config files and return the effective policy.
        return new WsitPolicyResolver();
    }
}
