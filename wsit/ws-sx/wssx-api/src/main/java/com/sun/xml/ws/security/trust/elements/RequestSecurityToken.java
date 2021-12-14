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
 * $Id: RequestSecurityToken.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import java.net.URI;

import com.sun.xml.ws.api.security.trust.Claims;

/**
 * @author Kumar Jayanti
 */
public interface RequestSecurityToken extends WSTrustElementBase, BaseSTSRequest {
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
     */
    List<Object> getAny();

    /**
     * Gets the value of the context property.
     * 
     * 
     * @return {@link String }
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
     * 
     * @return always non-null
     */
    Map<QName, String> getOtherAttributes();

   /**
    * Get the type of request, specified as a URI.
    * The URI indicates the class of function that is requested.
    * @return {@link URI}
    */
    URI getRequestType();

   /**
     * Set the type of request, specified as a URI.
     * @param requestType {@link URI}
     */
    void setRequestType(URI requestType);


    /**
      * Set the desired claims settings for the requested token
      */
     void setClaims(Claims claims);

     /**
      * Get the desired claims settings for the token if specified, null otherwise
      */
     Claims getClaims();

     /**
      * Set the Participants Sharing the requested Token
      */
     void setParticipants(Participants participants);
     
     void setValidateTarget(ValidateTarget valigateTarget);
     
     void setRenewTarget(RenewTarget renewTarget);
     
     void setCancelTarget(CancelTarget cancelTarget);
     
     /**
      * Get the participants sharing the token if specified, null otherwise 
      */
     Participants getParticipants();
     
     RenewTarget getRenewTarget();
     
     CancelTarget getCancelTarget();
     
     ValidateTarget getValidateTarget();
     
     void setSecondaryParameters(SecondaryParameters sp);
     
     SecondaryParameters getSecondaryParameters(); 
     
     List<Object> getExtensionElements();
     
     void setActAs(ActAs actAs);

     ActAs getActAs();

}

