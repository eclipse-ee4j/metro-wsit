/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: IssuerImpl.java,v 1.2 2010-10-21 15:37:04 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import jakarta.xml.ws.EndpointReference;
//import com.sun.xml.ws.addressing.v200408.EndpointReferenceImpl;

import com.sun.xml.ws.security.trust.elements.Issuer;

/**
 * Implementation of wst:Issuer. EndpointReference etc. is all part
 * of Addressing. Need to see how this will fit together.
 *
 * @author Manveen Kaur
 */
public class IssuerImpl implements Issuer {

    EndpointReference epr = null;

    public IssuerImpl() {
    }

    public IssuerImpl(EndpointReference epr) {
         setEndpointReference(epr);
    }

    //public IssuerImpl(EndpointReferenceImpl isType) throws Exception{
        // ToDo
    //}

    @Override
    public EndpointReference getEndpointReference() {
        return epr;
    }

    @Override
    public void setEndpointReference(EndpointReference endpointReference) {
        epr = endpointReference;
        // ToDo
    }
}
