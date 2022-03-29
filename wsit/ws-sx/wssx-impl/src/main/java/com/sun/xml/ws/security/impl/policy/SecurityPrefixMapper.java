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

import com.sun.xml.ws.policy.PolicyConstants;
import com.sun.xml.ws.policy.spi.PrefixMapper;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fabian Ritzmann
 */
public class SecurityPrefixMapper implements PrefixMapper {

    private static final Map<String, String> prefixMap = new HashMap<>();

    static {
        prefixMap.put(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, "sp");
        prefixMap.put(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, "sp");
        prefixMap.put(Constants.TRUST_NS, "wst");
        prefixMap.put(Constants.UTILITY_NS, PolicyConstants.WSU_NAMESPACE_PREFIX);
        prefixMap.put(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS, "csp");
        prefixMap.put(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS, "ssp");
        prefixMap.put(Constants.SUN_TRUST_CLIENT_SECURITY_POLICY_NS, "ctp");
        prefixMap.put(Constants.SUN_TRUST_SERVER_SECURITY_POLICY_NS, "stp");
        prefixMap.put(Constants.SUN_SECURE_CLIENT_CONVERSATION_POLICY_NS, "cscp");
        prefixMap.put(Constants.SUN_SECURE_SERVER_CONVERSATION_POLICY_NS, "sscp");
    }

    public SecurityPrefixMapper() {}

    @Override
    public Map<String, String> getPrefixMap() {
        return prefixMap;
    }

}
