/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.policy;

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.SimpleAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.api.tx.at.Transactional;
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
abstract class WsatAssertionBase extends SimpleAssertion {
        /**
         * patch for WSIT 419
         */
        private static final QName WSP2002_OPTIONAL = new QName("http://schemas.xmlsoap.org/ws/2002/12/policy", "Optional");
        //
        private static AssertionData createAssertionData(final QName assertionQName, final boolean isOptional) {
            final AssertionData result = AssertionData.createAssertionData(assertionQName);
            result.setOptionalAttribute(isOptional);
            if (isOptional) {
                // patch for wsit 419
                result.setAttribute(WSP2002_OPTIONAL, "true");
            }
            return result;
        }

        WsatAssertionBase(final QName wsatPolicyAssertionName, final boolean isOptional) {
            super(createAssertionData(wsatPolicyAssertionName, isOptional), null);
        }

    public WsatAssertionBase(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super (data, assertionParameters);
        if (data.isOptionalAttributeSet()) {
            // patch for wsit 419
            data.setAttribute(WSP2002_OPTIONAL, "true");
        }
    }
}
