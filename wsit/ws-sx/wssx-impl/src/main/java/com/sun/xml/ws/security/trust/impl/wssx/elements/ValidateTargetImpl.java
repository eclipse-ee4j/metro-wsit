/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.ws.security.trust.elements.ValidateTarget;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.ValidateTargetType;
import javax.xml.bind.JAXBElement;
import org.w3c.dom.Element;

/**
 *
 * @author Jiandong Guo
 */
public class ValidateTargetImpl extends ValidateTargetType implements ValidateTarget {
    
    private SecurityTokenReference str = null;
    
    public ValidateTargetImpl(Token token){
        final Element element = (Element)token.getTokenValue();
        setAny(element);
    }
    
    public ValidateTargetImpl(ValidateTargetType vtType){
        Object vt = vtType.getAny();
        if (vt != null){
            this.setAny((Element)vt);
        }
    }
    
    public void setSecurityTokenReference(final SecurityTokenReference ref) {
        str = ref;
        if (ref != null) {
            final JAXBElement<SecurityTokenReferenceType> strElement=
                    (new com.sun.xml.ws.security.secext10.ObjectFactory()).createSecurityTokenReference((SecurityTokenReferenceType)ref);
            setAny(strElement);
        }
    }
    
    public SecurityTokenReference getSecurityTokenReference() {
        return str;
    }
}
