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

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.glassfish.jaxb.core.Locatable;
import org.glassfish.jaxb.core.annotation.XmlLocation;
import org.xml.sax.Locator;


/**
 *
 *         [
 *         The persistence-context-ref element contains a declaration
 *         of Deployment Component's reference to a persistence context
 *         associated within a Deployment Component's
 *         environment. It consists of:
 *
 *         - an optional description
 *         - the persistence context reference name
 *         - an optional persistence unit name.  If not specified,
 *         the default persistence unit is assumed.
 *         - an optional specification as to whether
 *         the persistence context type is Transaction or
 *         Extended.  If not specified, Transaction is assumed.
 *         - an optional list of persistence properties
 *         - optional injection targets
 *
 *         Examples:
 *         {@code
 *         <persistence-context-ref>
 *         <persistence-context-ref-name>myPersistenceContext
 *         </persistence-context-ref-name>
 *         </persistence-context-ref>
 *
 *         <persistence-context-ref>
 *         <persistence-context-ref-name>myPersistenceContext
 *         </persistence-context-ref-name>
 *         <persistence-unit-name>PersistenceUnit1
 *         </persistence-unit-name>
 *         <persistence-context-type>Extended</persistence-context-type>
 *         </persistence-context-ref>
 *         }
 *
 *
 *
 * <p>Java class for persistence-context-refType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="persistence-context-refType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="description" type="{http://java.sun.com/xml/ns/javaee}descriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="persistence-context-ref-name" type="{http://java.sun.com/xml/ns/javaee}jndi-nameType"/>
 *         <element name="persistence-unit-name" type="{http://java.sun.com/xml/ns/javaee}string" minOccurs="0"/>
 *         <element name="persistence-context-type" type="{http://java.sun.com/xml/ns/javaee}persistence-context-typeType" minOccurs="0"/>
 *         <element name="persistence-property" type="{http://java.sun.com/xml/ns/javaee}propertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         <group ref="{http://java.sun.com/xml/ns/javaee}resourceBaseGroup"/>
 *       </sequence>
 *       <attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "persistence-context-refType", propOrder = {
    "description",
    "persistenceContextRefName",
    "persistenceUnitName",
    "persistenceContextType",
    "persistenceProperty",
    "mappedName",
    "injectionTarget"
})
public class PersistenceContextRefType
    implements Locatable
{

    protected List<DescriptionType> description;
    @XmlElement(name = "persistence-context-ref-name", required = true)
    protected JndiNameType persistenceContextRefName;
    @XmlElement(name = "persistence-unit-name")
    protected com.sun.xml.ws.config.metro.parser.jsr109.String persistenceUnitName;
    @XmlElement(name = "persistence-context-type")
    protected PersistenceContextTypeType persistenceContextType;
    @XmlElement(name = "persistence-property")
    protected List<PropertyType> persistenceProperty;
    @XmlElement(name = "mapped-name")
    protected XsdStringType mappedName;
    @XmlElement(name = "injection-target")
    protected List<InjectionTargetType> injectionTarget;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected java.lang.String id;
    @XmlLocation
    @XmlTransient
    protected Locator locator;

    /**
     * Gets the value of the description property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescription().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DescriptionType }
     *
     *
     */
    public List<DescriptionType> getDescription() {
        if (description == null) {
            description = new ArrayList<>();
        }
        return this.description;
    }

    /**
     * Gets the value of the persistenceContextRefName property.
     *
     * @return
     *     possible object is
     *     {@link JndiNameType }
     *
     */
    public JndiNameType getPersistenceContextRefName() {
        return persistenceContextRefName;
    }

    /**
     * Sets the value of the persistenceContextRefName property.
     *
     * @param value
     *     allowed object is
     *     {@link JndiNameType }
     *
     */
    public void setPersistenceContextRefName(JndiNameType value) {
        this.persistenceContextRefName = value;
    }

    /**
     * Gets the value of the persistenceUnitName property.
     *
     * @return
     *     possible object is
     *     {@link com.sun.xml.ws.config.metro.parser.jsr109.String }
     *
     */
    public com.sun.xml.ws.config.metro.parser.jsr109.String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    /**
     * Sets the value of the persistenceUnitName property.
     *
     * @param value
     *     allowed object is
     *     {@link com.sun.xml.ws.config.metro.parser.jsr109.String }
     *
     */
    public void setPersistenceUnitName(com.sun.xml.ws.config.metro.parser.jsr109.String value) {
        this.persistenceUnitName = value;
    }

    /**
     * Gets the value of the persistenceContextType property.
     *
     * @return
     *     possible object is
     *     {@link PersistenceContextTypeType }
     *
     */
    public PersistenceContextTypeType getPersistenceContextType() {
        return persistenceContextType;
    }

    /**
     * Sets the value of the persistenceContextType property.
     *
     * @param value
     *     allowed object is
     *     {@link PersistenceContextTypeType }
     *
     */
    public void setPersistenceContextType(PersistenceContextTypeType value) {
        this.persistenceContextType = value;
    }

    /**
     * Gets the value of the persistenceProperty property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the persistenceProperty property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersistenceProperty().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PropertyType }
     *
     *
     */
    public List<PropertyType> getPersistenceProperty() {
        if (persistenceProperty == null) {
            persistenceProperty = new ArrayList<>();
        }
        return this.persistenceProperty;
    }

    /**
     * Gets the value of the mappedName property.
     *
     * @return
     *     possible object is
     *     {@link XsdStringType }
     *
     */
    public XsdStringType getMappedName() {
        return mappedName;
    }

    /**
     * Sets the value of the mappedName property.
     *
     * @param value
     *     allowed object is
     *     {@link XsdStringType }
     *
     */
    public void setMappedName(XsdStringType value) {
        this.mappedName = value;
    }

    /**
     * Gets the value of the injectionTarget property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the injectionTarget property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInjectionTarget().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InjectionTargetType }
     *
     *
     */
    public List<InjectionTargetType> getInjectionTarget() {
        if (injectionTarget == null) {
            injectionTarget = new ArrayList<>();
        }
        return this.injectionTarget;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *
     */
    public java.lang.String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *
     */
    public void setId(java.lang.String value) {
        this.id = value;
    }

    @Override
    public Locator sourceLocation() {
        return locator;
    }

    public void setSourceLocation(Locator newLocator) {
        locator = newLocator;
    }

}
