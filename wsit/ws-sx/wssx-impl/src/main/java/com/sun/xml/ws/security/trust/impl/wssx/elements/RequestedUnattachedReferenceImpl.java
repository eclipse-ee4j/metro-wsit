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
 * $Id: RequestedUnattachedReferenceImpl.java,v 1.2 2010-10-21 15:37:05 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.trust.impl.elements.str.SecurityTokenReferenceImpl;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;

import com.sun.xml.ws.security.trust.elements.RequestedUnattachedReference;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.RequestedReferenceType;

/**
 * Implementation for RequestedUnattachedReference.
 * 
 * @author Manveen Kaur
 */
public class RequestedUnattachedReferenceImpl extends RequestedReferenceType implements RequestedUnattachedReference {

    SecurityTokenReference str = null;
    
    public RequestedUnattachedReferenceImpl() {
        // empty constructor    
    }

    public RequestedUnattachedReferenceImpl(SecurityTokenReference str) {
        setSTR(str);
    }
    
    public RequestedUnattachedReferenceImpl(RequestedReferenceType rrType) {
        this(new SecurityTokenReferenceImpl(rrType.getSecurityTokenReference()));
    }
    
    @Override
    public SecurityTokenReference getSTR() {
        return str;
    }

    @Override
    public void setSTR(SecurityTokenReference str) {
        if (str != null) {
            setSecurityTokenReference((SecurityTokenReferenceType)str);
        }
        this.str = str;
    }    
}
