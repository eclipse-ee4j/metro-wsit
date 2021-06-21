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
 * $Id: RequestSecurityTokenResponse.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import com.sun.xml.ws.api.security.trust.Status;

/**
 * @author Kumar Jayanti
 */
public interface RequestSecurityTokenResponse extends WSTrustElementBase, BaseSTSResponse {
    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.w3c.dom.Element }
     * {@link Object }
     * 
     * 
     */
    List<Object> getAny();

    /**
     * Gets the value of the context property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    String getContext();

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    Map<QName, String> getOtherAttributes();

  
    /**
     * set a SignChallengeResponse
     */
    void setSignChallengeResponse(SignChallengeResponse challenge);
    
    /**
     * get SignChallengeResponse element if any, null otherwise
     */
    SignChallengeResponse getSignChallengeResponse();
    
    /**
     * set an Authenticator
     */
    void setAuthenticator(Authenticator authenticator);

    /**
     * get Authenticator if set, null otherwise
     */
    Authenticator getAuthenticator();

    /**
     * Set the requestedProofToken on the security token response
     *
     * @param proofToken
     */
    void setRequestedProofToken(RequestedProofToken proofToken);

    /**
     * Get the requestedProofToken
     *
     * @return RequestedProofToken, null if none present
     */
    RequestedProofToken getRequestedProofToken();

    /**
     * Set the requestedSecurityToken on the security token response
     *
     * @param securityToken
     */
    void setRequestedSecurityToken(RequestedSecurityToken securityToken);

    /**
     * Get the requested Security Token
     *
     * @return RequestedSecurityToken
     */
    RequestedSecurityToken getRequestedSecurityToken();

    /**
     * Set the requestedAttachedReference on the security token response
     * @param reference
     */
    void setRequestedAttachedReference(RequestedAttachedReference reference);

    /**
     * Get the requestedAttachedReference.
     *
     * @return RequestedAttachedReference, null if none present
     */
    RequestedAttachedReference getRequestedAttachedReference();

    /**
     * Set the requestedUnattachedReference on the security token response
     * @param reference
     */
    void setRequestedUnattachedReference(RequestedUnattachedReference reference);

    /**
     * Get the requestedUnattachedReference.
     *
     * @return RequestedUnattachedReference, null if none present
     */
    RequestedUnattachedReference getRequestedUnattachedReference();
    
    void setRequestedTokenCancelled(RequestedTokenCancelled rtc);
    
    RequestedTokenCancelled getRequestedTokenCancelled();
    
    Status getStatus();
    
    void setStatus(Status status);
}

