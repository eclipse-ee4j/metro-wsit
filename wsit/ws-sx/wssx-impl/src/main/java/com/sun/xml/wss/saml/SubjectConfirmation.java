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
 * SubjectConfirmation.java
 *
 * Created on August 18, 2005, 12:34 PM
 *
 */

package com.sun.xml.wss.saml;

import java.util.List;

/**
 * The <code>SubjectConfirmation</code> element specifies a subject by specifying data that
 * authenticates the subject.
 *
 * <p>The following schema fragment specifies the expected content contained within 
 * SAML SubjectConfirmation element.
 * <pre>
 * &lt;complexType name="SubjectConfirmationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}ConfirmationMethod" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}SubjectConfirmationData" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}KeyInfo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
public interface SubjectConfirmation {

    /**
     * Gets the value of the confirmationMethod property.
     * 
     * @return  Objects of the following type(s) allowed in the list {@link java.lang.String }
     * 
     * 
     */
    List<String> getConfirmationMethod();

    /**
     * Gets the value of the subjectConfirmationData property for SAML 1.1 and SAML 1.0.
     * 
     * @return object is {@link Object }
     *     
     */
    Object getSubjectConfirmationDataForSAML11();

    /**
     * Gets the value of the subjectConfirmationData property for SAML 2.0
     * 
     * @return object is {@link SubjectConfirmationData }
     *     
     */
    SubjectConfirmationData getSubjectConfirmationDataForSAML20();

    /**
     * Gets the value of the nameID property for SAML 2.0
     * 
     * @return object is {@link NameID }
     *     
     */
    NameID getNameId();
}
