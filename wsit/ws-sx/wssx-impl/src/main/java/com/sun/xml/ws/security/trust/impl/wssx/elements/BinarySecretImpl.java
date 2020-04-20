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
 * $Id: BinarySecretImpl.java,v 1.2 2010-10-21 15:37:04 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import org.w3c.dom.Element;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;

import javax.xml.namespace.QName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlAccessorType;

import com.sun.xml.ws.api.security.trust.WSTrustException;

import com.sun.xml.ws.security.trust.impl.wssx.bindings.BinarySecretType;

import com.sun.xml.ws.security.trust.elements.BinarySecret;

import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.exceptions.Base64DecodingException;

/**
 *
 * @author WS-Trust Implementation Team
 */
public class BinarySecretImpl extends BinarySecretType implements BinarySecret {
    
   public BinarySecretImpl(byte[] rawValue, String type) {        
        setRawValue(rawValue);
        setType(type);
        
    }
    
    public BinarySecretImpl(BinarySecretType bsType){
        this(bsType.getValue(), bsType.getType());
        
    }
    
    /**
     * Constructs a <code>BinarySecret</code> element from
     * an existing XML block.
     *
     * @param element
     *        <code>org.w3c.dom.Element</code> representing DOM tree
     *        for <code>BinarySecret</code> object.
     * @exception WSTrustException if it could not process the
     *            <code>org.w3c.dom.Element</code> properly, implying that
     *            there is an error in the sender or in the element definition.
     */
    public static BinarySecretType fromElement(org.w3c.dom.Element element)
        throws WSTrustException {
        try {
            JAXBContext jc =
                JAXBContext.newInstance("com.sun.xml.ws.security.trust.impl.bindings");
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (BinarySecretType)((JAXBElement)u.unmarshal(element)).getValue();
        } catch (Exception ex) {
            throw new WSTrustException(ex.getMessage(), ex);
        }
    }

     public byte[] getRawValue() {
        return super.getValue();
     }
     
     
     public String getTextValue() {
        return Base64.encode(getRawValue());         
     }
     
     public void setRawValue(byte[] rawText) {
        setValue(rawText);
     }
      
     public void setTextValue(String encodedText) {
         try {
             setValue(Base64.decode(encodedText));
         } catch (Base64DecodingException de) {
             throw new RuntimeException("Error while decoding " + 
                                        de.getMessage()); 
         }
     }
     
 /*    public void setType(String type) {
        if (!(this.ASYMMETRIC_KEY_TYPE.equalsIgnoreCase(type)  
              || this.NONCE_KEY_TYPE.equalsIgnoreCase(type) 
              || this.SYMMETRIC_KEY_TYPE.equalsIgnoreCase(type))) {
            throw new RuntimeException("Invalid BinarySecret Type: " + type);
        }
        
        setType(type);
        
     }
     
     public String getType(){
         return this.type;
     }
     */
     
}
