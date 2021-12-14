/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: OnBehalfOfImpl.java,v 1.2 2010-10-21 15:37:04 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import com.sun.xml.ws.security.Token;
import jakarta.xml.ws.EndpointReference;
import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.ws.security.trust.elements.OnBehalfOf;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.OnBehalfOfType;
import jakarta.xml.bind.JAXBElement;

/**
 *
 * @author Manveen Kaur
 */
public class OnBehalfOfImpl extends OnBehalfOfType implements OnBehalfOf {
    private EndpointReference epr = null;
    private SecurityTokenReference str = null;
    
    public OnBehalfOfImpl(Token oboToken){
        setAny(oboToken.getTokenValue());
    }
    
    public OnBehalfOfImpl(OnBehalfOfType oboType){
        Object ob = oboType.getAny();
        if (ob != null){
            this.setAny(ob);
        }
    }
    @Override
    public EndpointReference getEndpointReference() {
        return epr;
    }
    
    @Override
    public void setEndpointReference(final EndpointReference endpointReference) {
        epr = endpointReference;
       /* if (endpointReference != null) {
            JAXBElement<EndpointReferenceImpl> eprElement=
                    (new com.sun.xml.ws.security.trust.impl.bindings.ObjectFactory()).
                    createEndpointReference((EndpointReferenceImpl)endpointReference);
            setAny(eprElement);
        }*/
        str = null;
    }
    
    @Override
    public void setSecurityTokenReference(final SecurityTokenReference ref) {
        str = ref;
        if (ref != null) {
            final JAXBElement<SecurityTokenReferenceType> strElement=
                    (new com.sun.xml.ws.security.secext10.ObjectFactory()).createSecurityTokenReference((SecurityTokenReferenceType)ref);
            setAny(strElement);
        }
        epr = null;
    }
    
    @Override
    public SecurityTokenReference getSecurityTokenReference() {
        return str;
    }
    
}
