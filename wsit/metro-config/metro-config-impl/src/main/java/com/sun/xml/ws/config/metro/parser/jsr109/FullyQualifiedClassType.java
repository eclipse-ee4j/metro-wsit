/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-28
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2010.08.18 at 11:59:48 AM EEST
//


package com.sun.xml.ws.config.metro.parser.jsr109;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *
 *         The elements that use this type designate the name of a
 *         Java class or interface.  The name is in the form of a
 *         "binary name", as defined in the JLS.  This is the form
 *         of name used in Class.forName().  Tools that need the
 *         canonical name (the name used in source code) will need
 *         to convert this binary name to the canonical name.
 *
 *
 *
 * <p>Java class for fully-qualified-classType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="fully-qualified-classType">
 *   <simpleContent>
 *     <restriction base="<http://java.sun.com/xml/ns/javaee>string">
 *     </restriction>
 *   </simpleContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fully-qualified-classType")
@XmlSeeAlso({
    LocalHomeType.class,
    HomeType.class,
    MessageDestinationTypeType.class,
    LocalType.class,
    RemoteType.class,
    EnvEntryTypeValuesType.class
})
public class FullyQualifiedClassType
    extends String
{


}
