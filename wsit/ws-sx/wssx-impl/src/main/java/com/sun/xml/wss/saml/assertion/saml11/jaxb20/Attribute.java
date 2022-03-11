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
 * $Id: Attribute.java,v 1.2 2010-10-21 15:38:00 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml11.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.AttributeType;
import com.sun.xml.wss.saml.util.SAMLJAXBUtil;
import org.w3c.dom.Element;

import java.util.List;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;

/**
 * The <code>Attribute</code> element specifies an attribute of the assertion subject.
 * The <code>Attribute</code> element is an extension of the <code>AttributeDesignator</code> element
 * that allows the attribute value to be specified.
 */
public class Attribute extends AttributeType
    implements com.sun.xml.wss.saml.Attribute {

    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);


    /**
     * Constructs an attribute element from an existing XML block.
     *
     * @param element representing a DOM tree element.
     * @exception SAMLException if there is an error in the sender or in the
     *            element definition.
     */
    public static AttributeType fromElement(Element element) throws SAMLException {
        try {
            JAXBContext jc =  SAMLJAXBUtil.getJAXBContext();

            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (AttributeType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    private void setAttributeValue(List values) {
        this.attributeValue = values;
    }

    /**
     * Constructs an instance of <code>Attribute</code>.
     *
     * @param name A String representing <code>AttributeName</code> (the name
     *        of the attribute).
     * @param nameSpace A String representing the namespace in which
     *        <code>AttributeName</code> elements are interpreted.
     * @param values A List of DOM element representing the
     *        <code>AttributeValue</code> object.
     */
    public Attribute(String name, String nameSpace, List values) {
        setAttributeName(name);
        setAttributeNamespace(nameSpace);
        setAttributeValue(values);
    }

    public Attribute(AttributeType attType) {
        setAttributeName(attType.getAttributeName());
        setAttributeNamespace(attType.getAttributeNamespace());
        setAttributeValue(attType.getAttributeValue());
    }

    @Override
    public String getFriendlyName() {
        return null;
    }

    @Override
    public String getName() {
        return super.getAttributeName();
    }

    @Override
    public String getNameFormat() {
        return super.getAttributeNamespace();
    }

    @Override
    public List<Object> getAttributes() {
        return super.getAttributeValue();
    }
}
