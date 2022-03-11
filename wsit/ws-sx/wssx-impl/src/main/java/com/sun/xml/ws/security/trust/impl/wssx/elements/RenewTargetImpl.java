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
 * $Id: RenewTargetImpl.java,v 1.2 2010-10-21 15:37:04 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.trust.impl.elements.str.SecurityTokenReferenceImpl;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.WSTrustConstants;
import com.sun.xml.ws.security.trust.elements.RenewTarget;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.RenewTargetType;
import jakarta.xml.bind.JAXBElement;

/**
 * Target specifying the Security token to be renewed.
 *
 * @author Manveen Kaur
 */
public class RenewTargetImpl extends RenewTargetType implements RenewTarget {

    private String targetType = null;

    private SecurityTokenReference str = null;
    private Token token = null;

    public RenewTargetImpl(SecurityTokenReference str) {
        setSecurityTokenReference(str);
        setTargetType(WSTrustConstants.STR_TYPE);
    }

    public RenewTargetImpl(Token token) {
        setToken(token);
        setTargetType(WSTrustConstants.TOKEN_TYPE);
    }

    public RenewTargetImpl (RenewTargetType rnType) {
        JAXBElement obj = (JAXBElement)rnType.getAny();
        String local = obj.getName().getLocalPart();
        if ("SecurityTokenReference".equals(local)) {
            SecurityTokenReference str =
                        new SecurityTokenReferenceImpl((SecurityTokenReferenceType)obj.getValue());
            setSecurityTokenReference(str);
            setTargetType(WSTrustConstants.STR_TYPE);
        } else {
            //ToDo
        }
    }

    @Override
    public String getTargetType() {
        return targetType;
    }

    @Override
    public void setTargetType(String ttype) {
        targetType = ttype;
    }

    @Override
    public void setSecurityTokenReference(SecurityTokenReference ref) {
        if (ref != null) {
            str = ref;
            JAXBElement<SecurityTokenReferenceType> strElement=
                    (new com.sun.xml.ws.security.secext10.ObjectFactory()).createSecurityTokenReference((SecurityTokenReferenceType)ref);
            setAny(strElement);
        }
        setTargetType(WSTrustConstants.STR_TYPE);
        token = null;
    }

    @Override
    public SecurityTokenReference getSecurityTokenReference() {
        return str;
    }

    @Override
    public void setToken(Token token) {
        if (token != null) {
            this.token = token;
            setAny(token);
        }
        setTargetType(WSTrustConstants.TOKEN_TYPE);
        str = null;
    }

    @Override
    public Token getToken() {
        return token;
    }

}
