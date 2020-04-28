/*
 * Copyright (c) 2013, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: SecurityTokenReferenceImpl.java,v 1.2 2010-10-21 15:36:57 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements.str;

import com.sun.xml.ws.security.secext10.KeyIdentifierType;
import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.ws.security.secext10.ObjectFactory;
import com.sun.xml.ws.security.secext10.ReferenceType;
import com.sun.xml.ws.security.trust.elements.str.Reference;
import com.sun.xml.ws.security.trust.WSTrustConstants;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;


import java.util.List;
import jakarta.xml.bind.JAXBElement;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * SecurityTokenReference implementation
 */
public class SecurityTokenReferenceImpl extends SecurityTokenReferenceType implements SecurityTokenReference {
    
    
    public SecurityTokenReferenceImpl(final Reference ref){
        setReference(ref);
    }
    
    public SecurityTokenReferenceImpl(final SecurityTokenReferenceType strType)
    {
        final Reference ref = getReference(strType);
        setReference(ref);
        this.getOtherAttributes().putAll(strType.getOtherAttributes());
    }
    
    public final void setReference(final Reference ref){
        
        JAXBElement rElement = null;
        final String type = ref.getType();
        final ObjectFactory objFac = new ObjectFactory();
        if (KEYIDENTIFIER.equals(type)){
            rElement = objFac.createKeyIdentifier((KeyIdentifierType)ref);
        }
        else if (REFERENCE.equals(type)){
            rElement = objFac.createReference((ReferenceType)ref);
        }else{
            //ToDo
        }
        
        if (rElement != null){
            getAny().clear();
            getAny().add(rElement);
        }
    }
    
    public Reference getReference (){
        return getReference((SecurityTokenReferenceType)this);
    }
    
    private Reference getReference(final SecurityTokenReferenceType strType){
        final List<Object> list = strType.getAny();
        final JAXBElement obj = (JAXBElement)list.get(0);
        final String local = obj.getName().getLocalPart();
        //final Reference ref = null;
        if (REFERENCE.equals(local)) {
            return new DirectReferenceImpl((ReferenceType)obj.getValue());
        }
            
        if (KEYIDENTIFIER.equalsIgnoreCase(local)) {
            return new KeyIdentifierImpl((KeyIdentifierType)obj.getValue());
        }
            
        return null;       
    }

    public void setTokenType(final String tokenType){
        getOtherAttributes().put(TOKEN_TYPE, tokenType);
    }

    public String getTokenType(){
       return getOtherAttributes().get(TOKEN_TYPE);
    }
    
    public String getType() {
        return WSTrustConstants.STR_TYPE;
    }
    
    public Object getTokenValue() {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            final DocumentBuilder builder = dbf.newDocumentBuilder();
            final Document doc = builder.newDocument();
            
            final jakarta.xml.bind.Marshaller marshaller = WSTrustElementFactory.getContext().createMarshaller();
            final JAXBElement<SecurityTokenReferenceType> rstElement =  (new ObjectFactory()).createSecurityTokenReference((SecurityTokenReferenceType)this);
            marshaller.marshal(rstElement, doc);
            return doc.getDocumentElement();
            
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
}
