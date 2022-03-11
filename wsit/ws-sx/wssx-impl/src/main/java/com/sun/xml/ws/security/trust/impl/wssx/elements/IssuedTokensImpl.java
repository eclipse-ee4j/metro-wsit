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
 * $Id: IssuedTokensImpl.java,v 1.2 2010-10-21 15:37:04 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import com.sun.xml.ws.security.trust.elements.IssuedTokens;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponseCollection;
import java.util.List;

/**
 * @author Manveen Kaur.
 */
public class IssuedTokensImpl implements IssuedTokens {

    RequestSecurityTokenResponseCollection collection = null;

    public IssuedTokensImpl() {
        // empty c'tor
    }

    public IssuedTokensImpl(RequestSecurityTokenResponseCollection rcollection) {
        setIssuedTokens(rcollection);
    }

    @Override
    public RequestSecurityTokenResponseCollection getIssuedTokens() {
        return collection;
    }

    @Override
    public void setIssuedTokens(RequestSecurityTokenResponseCollection rcollection) {
        collection = rcollection;
    }

    @Override
    public List<Object> getAny() {
        return null;
    }
}
