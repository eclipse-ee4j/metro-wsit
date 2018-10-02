/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: DynamicSecurityPolicy.java,v 1.2 2010-10-21 15:37:33 snajper Exp $
 */

package com.sun.xml.wss.impl.policy;

import com.sun.xml.wss.impl.PolicyTypeUtil;

/**
 * Represents a dynamically generable SecurityPolicy
 */
public abstract class DynamicSecurityPolicy implements SecurityPolicy {
    
    /*
     * Associate static application context
     */
    StaticPolicyContext ctx;
    
    /**
     * Default constructor
     */
    public DynamicSecurityPolicy () {}
    
    /**
     * Instantiate and associate DynamicSecurityPolicy with StaticPolicyContext
     *
     * @param ctx static security context used for implying dynamic policy generation
     */
    public DynamicSecurityPolicy (StaticPolicyContext ctx) {
        this.ctx = ctx;
    }
    
    /**
     * @return the StaticPolicyContext associated with this DynamicSecurityPolicy, null otherwise
     */
    public StaticPolicyContext getStaticPolicyContext () {
        return ctx;
    }
    
    /**
     * set the StaticPolicyContext for this DynamicSecurityPolicy
     * @param ctx the StaticPolicyContext for this DynamicSecurityPolicy.
     */
    public void setStaticPolicyContext (StaticPolicyContext ctx) {
        this.ctx = ctx;
    }
    
    /**
     * Associate a SecurityPolicy generator
     * @return SecurityPolicyGenerator that can be used to generate concrete SecurityPolicies
     * @see com.sun.xml.wss.impl.callback.DynamicPolicyCallback
     */
    public abstract SecurityPolicyGenerator policyGenerator ();

    /**
     * @return the type of the policy
     */
    public String getType() {
        return PolicyTypeUtil.DYN_SEC_POLICY_TYPE;
    }
    
}
