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
 * $Id: SecurityPolicyGenerator.java,v 1.2 2010-10-21 15:37:33 snajper Exp $
 */

package com.sun.xml.wss.impl.policy;

/**
 * A Factory interface for Generating Concrete Security Policies
 * @see com.sun.xml.wss.impl.policy.mls.WSSPolicyGenerator
 */
public interface SecurityPolicyGenerator {

    /**
     * Create and return a new Concrete MLS policy
     * @return a new Concrete MLS policy
     * @exception PolicyGenerationException if an MLS Policy cannot be generated
     */
    MLSPolicy newMLSPolicy() throws PolicyGenerationException;


    /**
     * Create and return a new Security Policy Configuration
     * @return a new Security Policy Configuration
     * @exception PolicyGenerationException if a Configuration cannot be generated
     */
    SecurityPolicy configuration() throws PolicyGenerationException;
}
