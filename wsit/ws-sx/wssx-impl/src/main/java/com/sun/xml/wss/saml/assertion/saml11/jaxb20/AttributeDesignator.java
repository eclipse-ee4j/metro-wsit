/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: AttributeDesignator.java,v 1.2 2010-10-21 15:38:00 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml11.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.AttributeDesignatorType;
import com.sun.xml.wss.saml.util.SAMLJAXBUtil;
import org.w3c.dom.Element;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;

/**
 * The <code>AttributeDesignator</code> element identifies an attribute
 * name within an attribute namespace. The element is used in an attribute query
 * to request that attribute values within a specific namespace be returned.
 */
public class AttributeDesignator extends AttributeDesignatorType
    implements com.sun.xml.wss.saml.AttributeDesignator {
    
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);


    /**
     *Default constructor
     */
    protected AttributeDesignator() {
        super();
    }

    /**
     * Constructs an attribute designator element from an existing XML block.
     *
     * @param element representing a DOM tree element.
     * @exception SAMLException if that there is an error in the sender or
     *            in the element definition.
     */
    public static AttributeDesignatorType fromElement(Element element) throws SAMLException {
        try {
            JAXBContext jc = SAMLJAXBUtil.getJAXBContext();
                    
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (AttributeDesignatorType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }

    /**
     * Constructs an instance of <code>AttributeDesignator</code>.
     *
     * @param name the name of the attribute.
     * @param nameSpace the namespace in which <code>AttributeName</code>
     *        elements are interpreted.
     * @exception SAMLException if there is an error in the sender or in the
     *            element definition.
     */
    public AttributeDesignator(String name, String nameSpace) {
        setAttributeName(name);
        setAttributeNamespace(nameSpace);
    }
}
