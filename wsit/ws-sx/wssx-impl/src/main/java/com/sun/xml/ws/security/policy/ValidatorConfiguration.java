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

import com.sun.xml.ws.policy.PolicyAssertion;
import java.util.Iterator;

/**
 *
 * @author K.Venugopal@sun.com
 */
public interface ValidatorConfiguration {
    Iterator<? extends PolicyAssertion>  getValidators();

    String getMaxClockSkew();

    String getTimestampFreshnessLimit();

    String getMaxNonceAge();

    String getRevocationEnabled();

    String getEnforceKeyUsage();
}
