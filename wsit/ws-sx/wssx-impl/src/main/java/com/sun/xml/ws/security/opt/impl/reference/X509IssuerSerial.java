/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * X509IssuerSerial.java
 *
 * Created on August 16, 2006, 4:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.opt.impl.reference;

import com.sun.xml.security.core.dsig.X509IssuerSerialType;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.api.reference.Reference;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.wss.impl.MessageConstants;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.security.core.dsig.ObjectFactory;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class X509IssuerSerial extends com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.X509IssuerSerial 
        implements SecurityHeaderElement, SecurityElementWriter{
    
    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    
    /** Creates a new instance of X509IssuerSerial */
    public X509IssuerSerial(SOAPVersion sv) {
        this.soapVersion = sv;
    }

    public boolean refersToSecHdrWithId(final String id) {
        return false;
    }

    public String getId() {
        throw new UnsupportedOperationException();
    }

    public void setId(final String id) {
        throw new UnsupportedOperationException();
    }

    public String getNamespaceURI() {
        return MessageConstants.DSIG_NS;
    }

    public String getLocalPart() {
        return "X509IssuerSerial".intern();
    }

    public XMLStreamReader readHeader() throws XMLStreamException {
        XMLStreamBufferResult xbr = new XMLStreamBufferResult();
        JAXBElement<com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.X509IssuerSerial> 
                issuerSerialElem = new ObjectFactory().createX509DataTypeX509IssuerSerial(this);
        try{
            getMarshaller().marshal(issuerSerialElem, xbr);
            
        } catch(JAXBException je){
            throw new XMLStreamException(je);
        }
        return xbr.getXMLStreamBuffer().readAsXMLStreamReader();
    }

    public void writeTo(XMLStreamWriter streamWriter) throws XMLStreamException {
        JAXBElement<com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.X509IssuerSerial> 
                issuerSerialElem = new ObjectFactory().createX509DataTypeX509IssuerSerial(this);
        try {
            // If writing to Zephyr, get output stream and use JAXB UTF-8 writer
            if (streamWriter instanceof Map) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    getMarshaller().marshal(issuerSerialElem, os);
                    return;
                }
            }
            
            getMarshaller().marshal(issuerSerialElem,streamWriter);
        } catch (JAXBException e) {
            throw new XMLStreamException(e);
        }
    }
    @SuppressWarnings("unchecked")
    public void writeTo(XMLStreamWriter streamWriter, HashMap props) throws XMLStreamException {
        try{
           Marshaller marshaller = getMarshaller();
           Iterator<Map.Entry<Object, Object>> itr = props.entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry<Object, Object> entry = itr.next();
                marshaller.setProperty((String)entry.getKey(), entry.getValue());
            }
           writeTo(streamWriter);
        } catch(JAXBException jbe){
            throw new XMLStreamException(jbe);
        }
    }

    public void writeTo(OutputStream os) {
    }
    
    private Marshaller getMarshaller() throws JAXBException{
        return JAXBUtil.createMarshaller(soapVersion);
    }
    
}
