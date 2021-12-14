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
 * Action.java
 *
 * Created on August 18, 2005, 11:57 AM
 *
 * 
 */

package com.sun.xml.wss.saml;

/**
 *This interface is designed for <code>Action</code> element in SAML core assertion.
 *The Action Element specifies an action on specified resource for which
 *permission is sought.
 *
 * <p>The following schema fragment specifies the expected content contained within SAML Action element.
 * <pre>
 * &lt;complexType name="ActionType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *       &lt;attribute name="Namespace" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 */
public interface Action {

    /**
     * Gets the value of the value property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    String getValue();

    /**
     * Gets the value of the namespace property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    String getNamespace();
}
