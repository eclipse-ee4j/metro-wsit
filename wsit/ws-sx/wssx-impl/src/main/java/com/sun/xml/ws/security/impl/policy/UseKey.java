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

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.logging.Level;
import javax.xml.namespace.QName;

/**
 *
 * @author Abhijit Das
 */

public class UseKey extends PolicyAssertion implements com.sun.xml.ws.security.policy.UseKey, SecurityAssertionValidator {

    private static QName sig = new QName("sig");
    private URI signatureID;
    private boolean populated = false;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;

    /** Creates a new instance of UseKeyIMpl */

    public UseKey(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }

    @Override
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }


    private synchronized AssertionFitness populate(boolean isServer) {
        if(!populated){
            try {
                this.signatureID = new URI(this.getAttributeValue(sig));
            } catch (URISyntaxException ex) {
                Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0102_INVALID_URI_VALUE(this.getAttributeValue(sig)),ex);
                fitness = AssertionFitness.HAS_INVALID_VALUE;
            }
            populated = true;
        }
        return fitness;
    }
}
