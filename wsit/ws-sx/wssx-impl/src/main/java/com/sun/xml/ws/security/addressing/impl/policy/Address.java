/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * Address.java
 *
 * Created on February 17, 2006, 12:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.addressing.impl.policy;


import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.logging.Level;
import static com.sun.xml.ws.security.addressing.impl.policy.Constants.logger;
/**
 *
 * @author Abhijit Das
 */
public class Address extends com.sun.xml.ws.policy.PolicyAssertion implements com.sun.xml.ws.security.addressing.policy.Address {
    
    private boolean populated = false;
    private URI address;
    
    /**
     * Creates a new instance of Address
     */
    public Address() {
    }
    
    public Address(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }
    private void populate() {
        if ( !populated ) {
            try {
                if(this.getValue() != null){
                    this.address = new URI(this.getValue().trim());
                }
                populated = true;
            } catch (URISyntaxException ex) {
                if(logger.getLevel() == Level.SEVERE){
                    logger.log(Level.SEVERE,LocalizationMessages.WSA_0004_INVALID_EPR_ADDRESS(),ex);
                }
            }
        }
    }
    
    public URI getURI() {
        populate();
        return address;
    }
    
    
    public String getNamespaceURI() {
        throw new UnsupportedOperationException();
    }
    
}
