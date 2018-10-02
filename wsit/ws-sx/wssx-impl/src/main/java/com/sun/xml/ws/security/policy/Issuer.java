/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: Issuer.java,v 1.2 2010-10-21 15:36:31 snajper Exp $
 */

package com.sun.xml.ws.security.policy;

import com.sun.xml.ws.security.addressing.policy.Address;
import com.sun.xml.ws.policy.PolicyAssertion;
import org.w3c.dom.Element;


/**
 * Specifies the issuer of the security token that is presented
 * in the message. The element's type is an endpoint reference as defined
 * in WS-Addressing.
 *
 * @author WS-Trust Implementation Team
 */
public interface Issuer {
    
    public Address getAddress();
    
    public String getPortType();
    
    public PolicyAssertion getServiceName();
    
    public PolicyAssertion getReferenceParameters();
    public PolicyAssertion getReferenceProperties();
    
    public Element getIdentity();
    public Address getMetadataAddress();
    
}
