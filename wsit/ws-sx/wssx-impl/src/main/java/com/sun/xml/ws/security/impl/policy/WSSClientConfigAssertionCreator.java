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

/**
 *
 * @author K.Venugopal@sun.com
 */
public class WSSClientConfigAssertionCreator extends SecurityPolicyAssertionCreator {
    
    private static final String [] nsSupportedList = new String[]{Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS};
    
    /**
     * Creates a new instance of WSSClientConfigAssertionCreator
     */
    public WSSClientConfigAssertionCreator() {
    }
    
    public String[] getSupportedDomainNamespaceURIs() {
        return nsSupportedList;
    }
}
