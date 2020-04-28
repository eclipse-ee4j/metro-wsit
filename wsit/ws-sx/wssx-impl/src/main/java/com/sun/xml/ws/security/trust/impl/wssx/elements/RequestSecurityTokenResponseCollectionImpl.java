/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: RequestSecurityTokenResponseCollectionImpl.java,v 1.2 2010-10-21 15:37:05 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.ws.security.trust.elements.Entropy;
import com.sun.xml.ws.security.trust.elements.Lifetime;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponse;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponseCollection;
import com.sun.xml.ws.security.trust.elements.RequestedAttachedReference;
import com.sun.xml.ws.security.trust.elements.RequestedProofToken;
import com.sun.xml.ws.security.trust.elements.RequestedSecurityToken;
import com.sun.xml.ws.security.trust.elements.RequestedUnattachedReference;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.RequestSecurityTokenResponseCollectionType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.ObjectFactory;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.RequestSecurityTokenResponseType;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;

/**
 * @author Manveen Kaur.
 */
public class RequestSecurityTokenResponseCollectionImpl extends RequestSecurityTokenResponseCollectionType
        implements RequestSecurityTokenResponseCollection {
    
    protected List<RequestSecurityTokenResponse> requestSecurityTokenResponseList;
    
    public RequestSecurityTokenResponseCollectionImpl() {
        // empty ctor
    }

    public RequestSecurityTokenResponseCollectionImpl(RequestSecurityTokenResponse rstr) {
        addRequestSecurityTokenResponse(rstr);        
    }
    public RequestSecurityTokenResponseCollectionImpl(URI tokenType, URI context, RequestedSecurityToken token, AppliesTo scopes,
            RequestedAttachedReference attached, RequestedUnattachedReference unattached, RequestedProofToken proofToken, Entropy entropy, Lifetime lt) {
        RequestSecurityTokenResponse rstr = new RequestSecurityTokenResponseImpl(tokenType, context, token, scopes,
                attached, unattached, proofToken, entropy, lt, null);
        addRequestSecurityTokenResponse(rstr);
        
    }
    
    public RequestSecurityTokenResponseCollectionImpl(RequestSecurityTokenResponseCollectionType rstrcType)
    throws Exception {
        List<Object> list = rstrcType.getRequestSecurityTokenResponse();
        for (int i = 0; i < list.size(); i++) {

            RequestSecurityTokenResponseType rstr = null;
            Object object = list.get(i);
            if (object instanceof JAXBElement){
                JAXBElement obj = (JAXBElement)object;

                String local = obj.getName().getLocalPart();
                if (local.equalsIgnoreCase("RequestSecurityTokenResponse")) {
                    rstr = (RequestSecurityTokenResponseType)obj.getValue();
                }
            } else{
                if(object instanceof RequestSecurityTokenResponseType) {
                    rstr = (RequestSecurityTokenResponseType)object;
                }
                
            }
            if (rstr != null){
                addRequestSecurityTokenResponse(new RequestSecurityTokenResponseImpl(rstr));
            }
        }
    }
    
    public List<RequestSecurityTokenResponse> getRequestSecurityTokenResponses() {
        if (requestSecurityTokenResponseList == null) {
            requestSecurityTokenResponseList = new ArrayList<RequestSecurityTokenResponse>();
        }
        return this.requestSecurityTokenResponseList;
    }
    
    public void addRequestSecurityTokenResponse(RequestSecurityTokenResponse rstr){
         getRequestSecurityTokenResponses().add(rstr);
         
        JAXBElement<RequestSecurityTokenResponseType> rstrEl =
                (new ObjectFactory()).createRequestSecurityTokenResponse((RequestSecurityTokenResponseType)rstr);
         getRequestSecurityTokenResponse().add(rstrEl);
    }
    
}
