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
 * SecurityTokenReference.java
 *
 * Created on August 2, 2006, 5:15 PM
 */

package com.sun.xml.ws.security.opt.impl.keyinfo;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.reference.Reference;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.reference.X509Data;
import com.sun.xml.ws.security.opt.impl.reference.X509IssuerSerial;
import com.sun.xml.wss.impl.c14n.AttributeNS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.ws.security.opt.impl.reference.DirectReference;
import com.sun.xml.ws.security.opt.impl.reference.KeyIdentifier;
import com.sun.xml.ws.security.secext10.ObjectFactory;

import java.util.Map;
import java.io.OutputStream;


/**
 *
 * @author Ashutosh.Shahi@sun.com
 */

public class SecurityTokenReference extends SecurityTokenReferenceType
          implements com.sun.xml.ws.security.opt.api.keyinfo.SecurityTokenReference, 
        SecurityHeaderElement, SecurityElementWriter, Token {
    
    //private SecurityTokenReferenceType str = null;
    
    private boolean isCanonicalized = false;
    SOAPVersion sv = SOAPVersion.SOAP_11;
    
    /** Creates a new instance of SecurityTokenReference */
    public SecurityTokenReference(SOAPVersion soapVersion) {
        this.sv = soapVersion;
    }
    /**
     * sets the reference element into the security token reference
     * @param ref Reference
     */
    public void setReference(Reference ref) {
        JAXBElement refElem = null;
        String type = ref.getType();
        ObjectFactory objFac = new ObjectFactory();
        if (KEYIDENTIFIER.equals(type)){
            refElem = objFac.createKeyIdentifier((KeyIdentifier)ref);
        } else if (REFERENCE.equals(type) || DIRECT_REFERENCE.equals(type)){
            refElem = objFac.createReference((DirectReference)ref);
        } else if(X509DATA_ISSUERSERIAL.equals(type)){
            refElem = new com.sun.xml.security.core.dsig.ObjectFactory().createX509Data((X509Data)ref);
        }
        
        if(refElem != null){
            List<Object> list = this.getAny();
            list.clear();
            list.add(refElem);
        }
    }
    /**
     * gets the reference element from the security token reference
     * @return
     */
    public Reference getReference() {
        List<Object> list = this.getAny();
        JAXBElement obj = (JAXBElement)list.get(0);
        String local = obj.getName().getLocalPart();
        if (REFERENCE.equals(local) || DIRECT_REFERENCE.equals(local)) {
            return (DirectReference)obj.getValue();
        } else if("KeyIdentifier".equalsIgnoreCase(local)) {
            return (KeyIdentifier)obj.getValue();
        } else if(X509DATA_ISSUERSERIAL.equals(local)){
            return (X509Data)obj.getValue();
        }
        //anything else??
        return null;
    }
    
    public void setTokenType(String tokenType) {
        QName qname = new QName(MessageConstants.WSSE11_NS,
                  MessageConstants.TOKEN_TYPE_LNAME, MessageConstants.WSSE11_PREFIX);
        Map<QName, String> otherAttributes = this.getOtherAttributes();
        otherAttributes.put(qname, tokenType);
    }
    
    public String getTokenType() {
        QName qname = new QName(MessageConstants.WSSE11_NS,
                  MessageConstants.TOKEN_TYPE_LNAME, MessageConstants.WSSE11_PREFIX);
        Map<QName, String> otherAttributes = this.getOtherAttributes();
        return otherAttributes.get(qname);
    }
    
    public String getNamespaceURI() {
        return MessageConstants.WSSE_NS;
    }
    
    
    public String getLocalPart() {
        return MessageConstants.WSSE_SECURITY_TOKEN_REFERENCE_LNAME;
    }
    
    
    public String getAttribute(@NotNull String nsUri, @NotNull String localName) {
        QName qname = new QName(nsUri, localName);
        Map<QName, String> otherAttributes = this.getOtherAttributes();
        return otherAttributes.get(qname);
    }
    
    
    public String getAttribute(@NotNull QName name) {
        Map<QName, String> otherAttributes = this.getOtherAttributes();
        return otherAttributes.get(name);
    }
    
    public XMLStreamReader readHeader() throws XMLStreamException {
        XMLStreamBufferResult xbr = new XMLStreamBufferResult();
        JAXBElement<SecurityTokenReferenceType> strElem = new ObjectFactory().createSecurityTokenReference(this);
        try{
            getMarshaller().marshal(strElem, xbr);
            
        } catch(JAXBException je){
            throw new XMLStreamException(je);
        }
        return xbr.getXMLStreamBuffer().readAsXMLStreamReader();
    }
    
    
    /**
     * writes the SecurityTokenReference element to the XMLStreamWriter
     * @param streamWriter
     * @throws javax.xml.stream.XMLStreamException
     */
    public void writeTo(XMLStreamWriter streamWriter) throws XMLStreamException {
        JAXBElement<SecurityTokenReferenceType> strElem = new ObjectFactory().createSecurityTokenReference(this);
        try {
            // If writing to Zephyr, get output stream and use JAXB UTF-8 writer
            if (streamWriter instanceof Map) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    getMarshaller().marshal(strElem, os);
                    return;
                }
            }
            
            getMarshaller().marshal(strElem,streamWriter);
        } catch (JAXBException e) {
            throw new XMLStreamException(e);
        }
    }
    
    
    public byte[] canonicalize(String algorithm, List<AttributeNS> namespaceDecls) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isCanonicalized() {
        return isCanonicalized;
    }
    
    private Marshaller getMarshaller() throws JAXBException{
        return JAXBUtil.createMarshaller(sv);
    }
    
    public void writeTo(OutputStream os) {
        throw new UnsupportedOperationException();
    }
    /**
     * checks whether this element refers to the security header element with the given id
     * @param id
     * @return
     */
    public boolean refersToSecHdrWithId(String id) {
        List list = super.getAny();
        if(list.size() > 0){
            JAXBElement je = (JAXBElement) list.get(0);
            Object obj = je.getValue();
            if(obj instanceof DirectReference ){
                StringBuffer sb = new StringBuffer();
                sb.append("#");
                sb.append(id);
                return ((DirectReference)obj).getURI().equals(sb.toString());
            }else if(obj instanceof KeyIdentifier){
                return ((KeyIdentifier)obj).refersToSecHdrWithId(id);
            }
        }
        return false;
    }
    /**
     * writes the SecurityTokenReference element to the XMLStreamWriter
     * @param streamWriter
     * @param props
     * @throws javax.xml.stream.XMLStreamException
     */
    @SuppressWarnings("unchecked")
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter, HashMap props) throws javax.xml.stream.XMLStreamException {
        try{
            Marshaller marshaller = getMarshaller();
            Iterator<Map.Entry<Object, Object>> itr = props.entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry<Object, Object> entry = itr.next();
                marshaller.setProperty((String)entry.getKey(), entry.getValue());
            }
            writeTo(streamWriter);
        }catch(JAXBException jbe){
            throw new XMLStreamException(jbe);
        }
    }

    public String getType() {
        return "SecurityTokenReference";
    }

    public Object getTokenValue() {
        return getReference();
    }
    
}
