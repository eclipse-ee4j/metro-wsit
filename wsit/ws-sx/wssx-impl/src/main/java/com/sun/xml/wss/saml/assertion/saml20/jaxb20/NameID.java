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
 * $Id: NameID.java,v 1.2 2010-10-21 15:38:03 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.NameIDType;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;

/**
 *The NameID element specifies a <code>Subject</code> by a combination
 * of a name and a security domain governing the name of the <code>Subject</code>.
 */
public class NameID extends NameIDType implements com.sun.xml.wss.saml.NameID {

    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);


    /**
     * Constructs a <code>NameIdentifer</code> element from an existing XML
     * block.
     *
     * @param element A <code>org.w3c.dom.Element</code>
     *        representing DOM tree for <code>NameID</code> object
     * @exception SAMLException if it could not process the
     *            <code>org.w3c.dom.Element</code> properly, implying that there
     *            is an error in the sender or in the element definition.
     */
    public static NameIDType fromElement(org.w3c.dom.Element element)
        throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();
                
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (NameIDType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }

    
    /**
     * Constructs a <code>NameQualifier</code> instance.
     *
     * @param name The string representing the name of the Subject
     * @param nameQualifier The security or administrative domain that qualifies
     *        the name of the <code>Subject</code>. This is optional could be
     *        null or "".
     * @param format The syntax used to describe the name of the
     *        <code>Subject</code>. This optional, could be null or "".
     * @exception SAMLException if the input has an error.
     */
    public NameID(String name, String nameQualifier, String format)
        {
        if ( name != null)
            setValue(name);
        
        if ( nameQualifier != null)
            setNameQualifier(nameQualifier);
        
        if ( format != null)
            setFormat(format);
    }
    
    public NameID(NameIDType nameIdType){        
        setValue(nameIdType.getValue());
        setNameQualifier(nameIdType.getNameQualifier());
        setFormat(nameIdType.getFormat());
    }
}
