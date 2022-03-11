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
 * $Id: RequestSecurityTokenResponseCollectionImpl.java,v 1.2 2010-10-21 15:36:55 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.security.trust.elements.Entropy;
import com.sun.xml.ws.security.trust.elements.Lifetime;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponse;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponseCollection;
import com.sun.xml.ws.security.trust.elements.RequestedAttachedReference;
import com.sun.xml.ws.security.trust.elements.RequestedProofToken;
import com.sun.xml.ws.security.trust.elements.RequestedSecurityToken;
import com.sun.xml.ws.security.trust.elements.RequestedUnattachedReference;
import com.sun.xml.ws.security.trust.impl.bindings.RequestSecurityTokenResponseCollectionType;
import com.sun.xml.ws.security.trust.impl.bindings.RequestSecurityTokenResponseType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Manveen Kaur.
 */
public class RequestSecurityTokenResponseCollectionImpl extends RequestSecurityTokenResponseCollectionType
        implements RequestSecurityTokenResponseCollection {

    protected List<RequestSecurityTokenResponse> requestSecurityTokenResponses;

    public RequestSecurityTokenResponseCollectionImpl() {
        // empty ctor
    }

    public RequestSecurityTokenResponseCollectionImpl(URI tokenType, URI context, RequestedSecurityToken token, AppliesTo scopes,
            RequestedAttachedReference attached, RequestedUnattachedReference unattached, RequestedProofToken proofToken, Entropy entropy, Lifetime lt) {
        final RequestSecurityTokenResponse rstr = new RequestSecurityTokenResponseImpl(tokenType, context, token, scopes,
                attached, unattached, proofToken, entropy, lt, null);
        addRequestSecurityTokenResponse(rstr);

    }

    public RequestSecurityTokenResponseCollectionImpl(RequestSecurityTokenResponseCollectionType rstrcType)
    throws URISyntaxException,WSTrustException{
        final List<RequestSecurityTokenResponseType> list = rstrcType.getRequestSecurityTokenResponse();
        for (int i = 0; i < list.size(); i++) {
            addRequestSecurityTokenResponse(new RequestSecurityTokenResponseImpl(list.get(i)));
        }
    }

    @Override
    public List<RequestSecurityTokenResponse> getRequestSecurityTokenResponses() {
        if (requestSecurityTokenResponses == null) {
            requestSecurityTokenResponses = new ArrayList<>();
        }
        return this.requestSecurityTokenResponses;
    }

    public final void addRequestSecurityTokenResponse(final RequestSecurityTokenResponse rstr){
         getRequestSecurityTokenResponses().add(rstr);

        //JAXBElement<RequestSecurityTokenResponseType> rstrEl =
               // (new ObjectFactory()).createRequestSecurityTokenResponse((RequestSecurityTokenResponseType)rstr);
         getRequestSecurityTokenResponse().add((RequestSecurityTokenResponseType)rstr);
    }
}
