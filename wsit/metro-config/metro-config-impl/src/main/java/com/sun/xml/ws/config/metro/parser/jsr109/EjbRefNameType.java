/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
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
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *         [
 *         The ejb-ref-name element contains the name of an EJB
 *         reference. The EJB reference is an entry in the
 *         Deployment Component's environment and is relative to the
 *         java:comp/env context.  The name must be unique within the
 *         Deployment Component.
 *
 *         It is recommended that name is prefixed with "ejb/".
 *
 *         Example:
 *         {@code
 *         <ejb-ref-name>ejb/Payroll</ejb-ref-name>
 *         }
 *
 *
 *
 * <p>Java class for ejb-ref-nameType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="ejb-ref-nameType">
 *   <simpleContent>
 *     <restriction base="<http://java.sun.com/xml/ns/javaee>jndi-nameType">
 *     </restriction>
 *   </simpleContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ejb-ref-nameType")
public class EjbRefNameType
    extends JndiNameType
{


}
