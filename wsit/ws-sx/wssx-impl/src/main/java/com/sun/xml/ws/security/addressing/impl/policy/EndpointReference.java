/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.addressing.impl.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.impl.policy.PolicyUtil;
import java.util.Collection;

import java.util.Iterator;

/**
 *
 * @author Abhijit Das
 */
public class EndpointReference extends com.sun.xml.ws.policy.PolicyAssertion  {

    private Address address;
    private boolean populated = false;

    /**
     * Creates a new instance of EndpointReference
     */
    public EndpointReference(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }

    public Address getAddress() {
        populate();
        return address;
    }


    private void populate() {
        if(populated){
            return;
        }
        synchronized (this.getClass()){
            if(!populated){
                if ( this.hasNestedAssertions() ) {
                    Iterator <PolicyAssertion> it = this.getNestedAssertionsIterator();
                    while ( it.hasNext() ) {
                        PolicyAssertion assertion = it.next();
                        if ( PolicyUtil.isAddress(assertion)) {
                            this.address = (Address) assertion;
                        }
                    }
                }
                populated = true;
            }
        }
    }


}
