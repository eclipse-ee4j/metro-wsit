/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;

import java.util.Set;

/**
 * Represents WSTrust Assertion.
 * @author K.Venugopal@sun.com
 */
public interface TrustAssertion{


    /**
     * Properties (MUST_SUPPORT_CLIENT_CHALLENGE,MUST_SUPPORT_SERVER_CHALLENGE,MUST_SUPPORT_ISSUED_TOKENS )present in the policy.
     */
    Set getRequiredProperties();
    /**
     *
     * @return 1.0
     */
    String getType();
}
