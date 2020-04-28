/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.Attribute;
import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.Subject;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.AttributeStatementType;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.AttributeType;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;

import java.util.List;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;

/**
 *The <code>AttributeStatement</code> element supplies a statement by the issuer that the
 *specified subject is associated with the specified attributes.
 */
public class AttributeStatement extends AttributeStatementType
    implements com.sun.xml.wss.saml.AttributeStatement {
    
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    private List<Attribute> attValueList = null;
    @SuppressWarnings("unchecked")
    private void setAttributes(List attr) {
        this.attributeOrEncryptedAttribute = attr;
    }
    
    /**
     *Dafault constructor
     */
    public AttributeStatement(List attr) {        
        setAttributes(attr);
    }
    
    public AttributeStatement(AttributeStatementType attStmtType) {        
        setAttributes(attStmtType.getAttributeOrEncryptedAttribute());
    }

    /**
     * Constructs an <code>AttributStatement</code> element from an existing
     * XML block
     * @param element representing a DOM tree element
     * @exception SAMLException if there is an error in the sender or in the
     *            element definition.
     */
    public static AttributeStatementType fromElement(Element element) throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();
                    
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (AttributeStatementType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }

    public List<Attribute> getAttributes(){
        if(attValueList == null){
            attValueList = new ArrayList<Attribute>();
        }else{
            return attValueList;
        }
        Iterator it = super.getAttributeOrEncryptedAttribute().iterator();
        while(it.hasNext()){
            com.sun.xml.wss.saml.assertion.saml20.jaxb20.Attribute obj = 
                    new com.sun.xml.wss.saml.assertion.saml20.jaxb20.Attribute((AttributeType)it.next());
            attValueList.add(obj);
        }
        return attValueList;                 
    }

    public Subject getSubject() {
        throw new UnsupportedOperationException("getSubject() on statement object is not supported for SAML 2.0 "+
                "Make the direct call of getSubject() method on SAML2.0 assertion");
    }
   
}
