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

/**
 * SecurityPolicy Assertion implementation classes should implement
 * this interface. This is enable WSPolicy framework to select valid
 * policy assertion set from a set of policy alternatives.
 *
 * @author K.Venugopal@sun.com
 */
public interface SecurityAssertionValidator {

    enum AssertionFitness {
        HAS_UNKNOWN_ASSERTION,
        IS_VALID,
        HAS_UNSUPPORTED_ASSERTION,
        HAS_INVALID_VALUE
    }
    /**
     * returns true if all the assertions embeeded under a SecurityPolicy
     * assertion are valid and supported by the implementation.
     */
    AssertionFitness validate(boolean isServer);
}
