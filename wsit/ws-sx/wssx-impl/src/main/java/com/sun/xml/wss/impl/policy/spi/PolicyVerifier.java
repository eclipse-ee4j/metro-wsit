/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy.spi;

import com.sun.xml.wss.impl.PolicyViolationException;
import com.sun.xml.wss.impl.policy.*;

/**
 * This is an internal interface not exposed to developer.
 *
 * @author K.Venugopal@sun.com
 */

public interface PolicyVerifier {

    /**
     * A concrete PolicyVerifier can indicate to the runtime the
     * ID of the alternative that satisfied the incoming request's
     * policy.
     *
     * The ID is then used by the runtime to get the correct response policy for
     * securing the response messages.
     */
    public static final String POLICY_ALTERNATIVE_ID="policy-alternative-id";
    /**
     *
     * @param configPolicy Policy configured for the incoming message, can be
     * a single MessagePolicy or PolicyAlternatives.
     * @param recvdPolicy policy inferred from the incoming message.
     * @throws com.sun.xml.wss.PolicyViolationException when policy inferred from incoming message does not match with what
     * is configured.
     *
     */
    public void verifyPolicy (SecurityPolicy recvdPolicy ,SecurityPolicy configPolicy )throws PolicyViolationException;
}
