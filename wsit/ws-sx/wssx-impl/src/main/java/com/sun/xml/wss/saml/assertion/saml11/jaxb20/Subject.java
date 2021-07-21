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
 * $Id: Subject.java,v 1.2 2010-10-21 15:38:00 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml11.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.NameID;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.NameIdentifierType;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.ObjectFactory;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.SubjectConfirmationType;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.SubjectType;
import com.sun.xml.wss.saml.util.SAMLJAXBUtil;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;

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
*/
public class Subject extends SubjectType
    implements com.sun.xml.wss.saml.Subject {
    
    private NameIdentifier nameIdentifier;
    private SubjectConfirmation subjectConfirmation;
    
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    /**
     * Constructs a Subject object from a <code>NameIdentifier</code>
     * object and a <code>SubjectConfirmation</code> object.
     *
     * @param nameIdentifier <code>NameIdentifier</code> object.
     * @param subjectConfirmation <code>SubjectConfirmation</code> object.
     */
    public Subject(
        NameIdentifier nameIdentifier, SubjectConfirmation subjectConfirmation)
        {
        ObjectFactory factory = new ObjectFactory();
        
        if ( nameIdentifier != null)
            getContent().add(factory.createNameIdentifier(nameIdentifier));
        
        if ( subjectConfirmation != null)
            getContent().add(factory.createSubjectConfirmation(subjectConfirmation));
    }

    public Subject(SubjectType subjectType){
        Iterator it = subjectType.getContent().iterator();
        
        while(it.hasNext()){
            Object obj = it.next();
            if(obj instanceof JAXBElement){
                Object object = ((JAXBElement)obj).getValue();
                if(object instanceof NameIdentifierType){
                    nameIdentifier = new NameIdentifier((NameIdentifierType)object);
                }else if(object instanceof SubjectConfirmationType){
                    subjectConfirmation = new SubjectConfirmation((SubjectConfirmationType)object);
                }
            }else{                
                if(obj instanceof NameIdentifierType){
                    nameIdentifier = new NameIdentifier((NameIdentifierType)obj);
                }else if(obj instanceof SubjectConfirmationType){
                    subjectConfirmation = new SubjectConfirmation((SubjectConfirmationType)obj);
                }
            }
        }
    }
    
    
    public NameIdentifier getNameIdentifier(){
        return nameIdentifier;
    }
    
    public NameID getNameId(){
        return null;
    }
    
    public SubjectConfirmation getSubjectConfirmation(){
        return subjectConfirmation;
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
    public static SubjectType fromElement(org.w3c.dom.Element element)
        throws SAMLException {
        try {
            JAXBContext jc = SAMLJAXBUtil.getJAXBContext();
                    
            javax.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (SubjectType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }
}
