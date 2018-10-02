/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * NameIdentifier.java
 *
 * Created on August 18, 2005, 12:32 PM
 *
 */

package com.sun.xml.wss.saml;

/**
 *
 * @author abhijit.das@Sun.COM
 */
/**
 *The NameIdentifier element specifies a <code>Subject</code> by a combination
 * of a name and a security domain governing the name of the <code>Subject</code>.
 *
 * <p>The following schema fragment specifies the expected content contained within 
 * SAML NameIdentifier element.
 *
 * <pre>
 * &lt;complexType name="NameIdentifierType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *       &lt;attribute name="Format" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *       &lt;attribute name="NameQualifier" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 */
public interface NameID {

    /**
     * Gets the value of the value property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    public String getValue();

    /**
     * Gets the value of the format property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    public String getFormat();

    /**
     * Gets the value of the nameQualifier property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    public String getNameQualifier();
}
