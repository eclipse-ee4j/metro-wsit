/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class TrustServerConfigAssertionCreator  extends SecurityPolicyAssertionCreator{
    private static final String [] nsSupportedList = new String[]{Constants.SUN_TRUST_SERVER_SECURITY_POLICY_NS};
    /**
     * Creates a new instance of TrustServerConfigAssertionCreator
     */
    public TrustServerConfigAssertionCreator() {
    }

    @Override
    public String[] getSupportedDomainNamespaceURIs() {
        return nsSupportedList;
    }

}
