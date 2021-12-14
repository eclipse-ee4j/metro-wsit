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
 * $Id: Subject.java,v 1.2 2010-10-21 15:38:04 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.NameIdentifier;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.NameIDType;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.ObjectFactory;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.SubjectConfirmationType;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.SubjectType;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import java.util.Iterator;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;

/**
 * The <code>Subject</code> element specifies one or more subjects. It contains either or
both of the following elements:<code>NameID</code>;
An identification of a subject by its name and security domain.
<code>SubjectConfirmation</code>;
Information that allows the subject to be authenticated.

If a <code>Subject</code> element contains more than one subject specification,
the issuer is asserting that the surrounding statement is true for
all of the subjects specified. For example, if both a
<code>NameID</code> and a <code>SubjectConfirmation</code> element are
present, the issuer is asserting that the statement is true of both subjects
being identified. A {@code <Subject>} element SHOULD NOT identify more than one
principal.
*/
public class Subject extends SubjectType implements com.sun.xml.wss.saml.Subject {
    
    private NameID nameId = null;
    private SubjectConfirmation subjectConfirmation = null;
    
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);


    
    /**
     * Constructs a Subject object from a <code>NameID</code>
     * object and a <code>SubjectConfirmation</code> object.
     *
     * @param nameId <code>NameID</code> object.
     * @param subjectConfirmation <code>SubjectConfirmation</code> object.
     */
    public Subject(NameID nameId, SubjectConfirmation subjectConfirmation){
        ObjectFactory factory = new ObjectFactory();
        
        if ( nameId != null)
            getContent().add(factory.createNameID(nameId));
        
        if ( subjectConfirmation != null)
            getContent().add(factory.createSubjectConfirmation(subjectConfirmation));
    }

    /**
     * This constructor builds a subject element from an existing XML block
     * which has already been built into a DOM.
     *
     * @param element An Element representing DOM tree for Subject object
     * @exception SAMLException if it could not process the Element properly,
     *            implying that there is an error in the sender or in the
     *            element definition.
     */
    public static SubjectType fromElement(org.w3c.dom.Element element) throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();
                    
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (SubjectType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }
    
    public Subject(SubjectType subjectType){
        this.content = subjectType.getContent();
        Iterator it = subjectType.getContent().iterator();
        
        while(it.hasNext()){
            Object obj = it.next();
            if(obj instanceof JAXBElement){
                Object object = ((JAXBElement)obj).getValue();
                if(object instanceof NameIDType){
                    nameId = new NameID((NameIDType)object);
                }else if(object instanceof SubjectConfirmationType){
                    subjectConfirmation = new SubjectConfirmation((SubjectConfirmationType)object);
                }
            }else{                
                if(obj instanceof NameIDType){
                    nameId = new NameID((NameIDType)obj);
                }else if(obj instanceof SubjectConfirmationType){
                    subjectConfirmation = new SubjectConfirmation((SubjectConfirmationType)obj);
                }
            }
        }
    }
    
    @Override
    public NameIdentifier getNameIdentifier(){
        return null;
    }
    
    @Override
    public NameID getNameId(){
        return nameId;
    }
    
    @Override
    public SubjectConfirmation getSubjectConfirmation(){
        return subjectConfirmation;
    }
}
