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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import org.glassfish.jaxb.core.Locatable;
import org.glassfish.jaxb.core.annotation.XmlLocation;
import org.xml.sax.Locator;


/**
 *
 *
 *         The lifecycle-callback type specifies a method on a
 *         class to be called when a lifecycle event occurs.
 *         Note that each class may have only one lifecycle callback
 *         method for any given event and that the method may not
 *         be overloaded.
 *
 *         If the lifefycle-callback-class element is missing then
 *         the class defining the callback is assumed to be the
 *         component class in scope at the place in the descriptor
 *         in which the callback definition appears.
 *
 *
 *
 * <p>Java class for lifecycle-callbackType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="lifecycle-callbackType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="lifecycle-callback-class" type="{http://java.sun.com/xml/ns/javaee}fully-qualified-classType" minOccurs="0"/>
 *         <element name="lifecycle-callback-method" type="{http://java.sun.com/xml/ns/javaee}java-identifierType"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lifecycle-callbackType", propOrder = {
    "lifecycleCallbackClass",
    "lifecycleCallbackMethod"
})
public class LifecycleCallbackType
    implements Locatable
{

    @XmlElement(name = "lifecycle-callback-class")
    protected FullyQualifiedClassType lifecycleCallbackClass;
    @XmlElement(name = "lifecycle-callback-method", required = true)
    protected JavaIdentifierType lifecycleCallbackMethod;
    @XmlLocation
    @XmlTransient
    protected Locator locator;

    /**
     * Gets the value of the lifecycleCallbackClass property.
     *
     * @return
     *     possible object is
     *     {@link FullyQualifiedClassType }
     *
     */
    public FullyQualifiedClassType getLifecycleCallbackClass() {
        return lifecycleCallbackClass;
    }

    /**
     * Sets the value of the lifecycleCallbackClass property.
     *
     * @param value
     *     allowed object is
     *     {@link FullyQualifiedClassType }
     *
     */
    public void setLifecycleCallbackClass(FullyQualifiedClassType value) {
        this.lifecycleCallbackClass = value;
    }

    /**
     * Gets the value of the lifecycleCallbackMethod property.
     *
     * @return
     *     possible object is
     *     {@link JavaIdentifierType }
     *
     */
    public JavaIdentifierType getLifecycleCallbackMethod() {
        return lifecycleCallbackMethod;
    }

    /**
     * Sets the value of the lifecycleCallbackMethod property.
     *
     * @param value
     *     allowed object is
     *     {@link JavaIdentifierType }
     *
     */
    public void setLifecycleCallbackMethod(JavaIdentifierType value) {
        this.lifecycleCallbackMethod = value;
    }

    @Override
    public Locator sourceLocation() {
        return locator;
    }

    public void setSourceLocation(Locator newLocator) {
        locator = newLocator;
    }

}
