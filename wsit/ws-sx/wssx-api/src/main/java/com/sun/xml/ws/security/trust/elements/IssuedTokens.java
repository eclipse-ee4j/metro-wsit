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
 * $Id: IssuedTokens.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;
import java.util.List;

/**
 * When Tokens are issued as part of Protocol other than RST/RSTR
 * In such cases the Tokens are passed in a SOAP Header called &lt;wst:IssuedTokens&gt;
 *
 * @author Kumar Jayanti
 */
public interface IssuedTokens {

    /**
     * Gets the RequestSecurityTokenResponseCollection
     * @return RequestSecurityTokenResponseCollection if set, null otherwise
     */
     RequestSecurityTokenResponseCollection getIssuedTokens();

    /**
      * Sets the RequestSecurityTokenResponseCollection
      */
     void setIssuedTokens(RequestSecurityTokenResponseCollection rcollection);


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
}
