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
 * DirectReference.java
 *
 * Created on August 7, 2006, 1:46 PM
 */

package com.sun.xml.ws.security.opt.impl.reference;

import com.sun.istack.NotNull;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import java.util.Collections;
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

import com.sun.xml.ws.security.secext10.ReferenceType;
import com.sun.xml.ws.security.secext10.ObjectFactory;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.wss.impl.MessageConstants;

import java.util.Map;
import java.io.OutputStream;
import com.sun.xml.ws.api.SOAPVersion;

/**
 * Class for DirectReference reference type inside a STR
 * @author Ashutosh.Shahi@sun.com
 */
public class DirectReference extends ReferenceType
        implements com.sun.xml.ws.security.opt.api.reference.DirectReference,
        SecurityHeaderElement, SecurityElementWriter {

    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    
    /** Creates a new instance of DirectReference */
    public DirectReference(SOAPVersion sv) {
        this.soapVersion = sv;
    }
    
    /**
     * 
     * @return the valueType attribute of direct reference
     */
    public String getValueType() {
        return super.getValueType();
    }
    
    /**
     * 
     * @param valueType sets the valueType attribute
     */
    public void setValueType(final String valueType) {
        super.setValueType(valueType);
    }
    
    /**
     * 
     * @return the URI attribute
     */
    public String getURI() {
        return super.getURI();
    }
    
    /**
     * 
     * @param uri sets the uri attribute
     */
    public void setURI(final String uri) {
        super.setURI(uri);
    }
    
    /**
     * 
     * @return the reference type used
     */
    public String getType() {
        return MessageConstants.DIRECT_REFERENCE_TYPE;
    }
    
    public String getId() {
        QName qname = new QName(MessageConstants.WSU_NS, "Id", MessageConstants.WSU_PREFIX);
        Map<QName, String> otherAttributes = this.getOtherAttributes();
        return otherAttributes.get(qname);
    }
    
    public void setId(final String id) {
        QName qname = new QName(MessageConstants.WSU_NS, "Id", MessageConstants.WSU_PREFIX);
        Map<QName, String> otherAttributes = this.getOtherAttributes();
        otherAttributes.put(qname, id);
    }
    
    
    public String getNamespaceURI() {
        return MessageConstants.WSSE_NS;
    }
    
    
    public String getLocalPart() {
        return "Reference";
    }
    
    public String setAttribute(@NotNull String nsUri, @NotNull String localName, @NotNull String value) {
        QName qname = new QName(nsUri, localName);
        Map<QName, String> otherAttributes = this.getOtherAttributes();
        return otherAttributes.put(qname, value);
    }
    
    public String setAttribute(@NotNull QName name, @NotNull String value) {
        Map<QName, String> otherAttributes = this.getOtherAttributes();
        return otherAttributes.put(name, value);
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
        JAXBElement<ReferenceType> deirectRefElem = new ObjectFactory().createReference(this);
        try{
            getMarshaller().marshal(deirectRefElem, xbr);
            
        } catch(JAXBException je){
            throw new XMLStreamException(je);
        }
        return xbr.getXMLStreamBuffer().readAsXMLStreamReader();
    }
    
    /**
     * Writes out the header.
     *
     * @throws XMLStreamException
     *      if the operation fails for some reason. This leaves the
     *      writer to an undefined state.
     */
    public void writeTo(XMLStreamWriter streamWriter) throws XMLStreamException {
        JAXBElement<ReferenceType> deirectRefElem = new ObjectFactory().createReference(this);
        try {
            // If writing to Zephyr, get output stream and use JAXB UTF-8 writer
            if (streamWriter instanceof Map) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    getMarshaller().marshal(deirectRefElem, os);
                    return;
                }
            }
            
            getMarshaller().marshal(deirectRefElem,streamWriter);
        } catch (JAXBException e) {
            throw new XMLStreamException(e);
        }
    }
    
    /**
     * 
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
    
    private Marshaller getMarshaller() throws JAXBException{
        return JAXBUtil.createMarshaller(soapVersion);
    }
    
    /**
     * 
     * @param os 
     */
    public void writeTo(OutputStream os) {
    }
    
    public List<String> getReferencedSecHeaderElements() {
        return Collections.emptyList();
    }
    
    public void addReferencedSecHeaderElement(String id) {
    }
    
    /**
     * 
     * @param id 
     * @return 
     */
    public boolean refersToSecHdrWithId(String id) {
        return false;
    }
    
}
