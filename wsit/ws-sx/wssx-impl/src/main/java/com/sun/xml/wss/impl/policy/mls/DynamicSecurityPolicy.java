/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: DynamicSecurityPolicy.java,v 1.2 2010-10-21 15:37:34 snajper Exp $
 */

package com.sun.xml.wss.impl.policy.mls;

import com.sun.xml.wss.impl.policy.SecurityPolicyGenerator;

/**
 * Represents  a dynamically generable SecurityPolicy.
 * It contains an associated Policy Generator that can be used to
 * generate appropriate Security Policies understood by the
 * XWS-Security framework.
 */
public class DynamicSecurityPolicy extends com.sun.xml.wss.impl.policy.DynamicSecurityPolicy {

    public DynamicSecurityPolicy() {}

    /**
     * Return the associated SecurityPolicy generator
     * @return SecurityPolicyGenerator, the associated generator
     */
    @Override
    public  SecurityPolicyGenerator policyGenerator () {
        return new WSSPolicyGenerator();
    }
}
