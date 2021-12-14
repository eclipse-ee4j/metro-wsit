/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: SecurityPolicy.java,v 1.2 2010-10-21 15:37:33 snajper Exp $
 */

package com.sun.xml.wss.impl.policy;

/**
 * Tagging interface for the following Security Policy types (or any custom defined security policy type)
 *
 *  <UL>
 *   <LI>MLSPolicy
 *   <LI>SecurityPolicyContainer
 *   <LI>DynamicSecurityPolicy
 * </UL>
 */
public interface SecurityPolicy {

    /**
     * Get the type of the policy.
     *<P>
     * Implementation Note: Useful to avoid instanceof checks and String.equals checks
     * @return the type of the policy
     */
    String getType();
}
