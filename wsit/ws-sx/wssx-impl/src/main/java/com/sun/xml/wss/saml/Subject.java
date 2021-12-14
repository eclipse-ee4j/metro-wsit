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
 * Subject.java
 *
 * Created on August 18, 2005, 12:33 PM
 *
 */

package com.sun.xml.wss.saml;

/**
 * The <code>Subject</code> element specifies one or more subjects. It contains either or
both of the following elements:<code>NameIdentifier</code>;
An identification of a subject by its name and security domain.
<code>SubjectConfirmation</code>;
Information that allows the subject to be authenticated.

If a <code>Subject</code> element contains more than one subject specification,
the issuer is asserting that the surrounding statement is true for
all of the subjects specified. For example, if both a
<code>NameIdentifier</code> and a <code>SubjectConfirmation</code> element are
present, the issuer is asserting that the statement is true of both subjects
being identified. A {@code <Subject>} element SHOULD NOT identify more than one
principal.
 *
 * <p>The following schema fragment specifies the expected content contained within 
 * SAML Subject element.
 *
 * <pre>{@code
 * <complexType name="SubjectType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <choice>
 *         <sequence>
 *           <element ref="{urn:oasis:names:tc:SAML:1.0:assertion}NameIdentifier"/>
 *           <element ref="{urn:oasis:names:tc:SAML:1.0:assertion}SubjectConfirmation" minOccurs="0"/>
 *         </sequence>
 *         <element ref="{urn:oasis:names:tc:SAML:1.0:assertion}SubjectConfirmation"/>
 *       </choice>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
*/
public interface Subject {

    SubjectConfirmation getSubjectConfirmation();
    
    NameIdentifier getNameIdentifier();
    
    NameID getNameId();
        
}
