/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.reference;

import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.api.keyinfo.SecurityTokenReference;
import com.sun.xml.ws.security.opt.api.reference.Reference;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.wss.impl.MessageConstants;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.security.core.dsig.ObjectFactory;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class X509Data extends com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.X509Data
        implements Reference, SecurityHeaderElement, SecurityElementWriter{
    
    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    
    /** Creates a new instance of X509Data */
    public X509Data(SOAPVersion sv) {
        this.soapVersion = sv;
    }
    
    @Override
    public String getType() {
        return SecurityTokenReference.X509DATA_ISSUERSERIAL;
    }
    
    @Override
    public boolean refersToSecHdrWithId(final String id) {
        return false;
    }
    
    @Override
    public String getId() {
        throw new UnsupportedOperationException("Id attribute not allowed for X509Data");
    }
    
    @Override
    public void setId(final String id) {
        throw new UnsupportedOperationException("Id attribute not allowed for X509Data");
    }
    
    @Override
    public String getNamespaceURI() {
        return MessageConstants.DSIG_NS;
    }
    
    @Override
    public String getLocalPart() {
        return "X509Data".intern();
    }
    
    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        XMLStreamBufferResult xbr = new XMLStreamBufferResult();
        JAXBElement<com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.X509Data>
                x509DataElem = new ObjectFactory().createX509Data(this);
        try{
            getMarshaller().marshal(x509DataElem, xbr);
            
        } catch(JAXBException je){
            throw new XMLStreamException(je);
        }
        return xbr.getXMLStreamBuffer().readAsXMLStreamReader();
    }
    
    @Override
    public void writeTo(XMLStreamWriter streamWriter) throws XMLStreamException {
        JAXBElement<com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.X509Data>
                x509DataElem = new ObjectFactory().createX509Data(this);
        try {
            // If writing to Zephyr, get output stream and use JAXB UTF-8 writer
            if (streamWriter instanceof Map) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    getMarshaller().marshal(x509DataElem, os);
                    return;
                }
            }
            
            getMarshaller().marshal(x509DataElem,streamWriter);
        } catch (JAXBException e) {
            throw new XMLStreamException(e);
        }
    }
    @Override
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
    
    @Override
    public void writeTo(OutputStream os) {
    }
    
    private Marshaller getMarshaller() throws JAXBException{
        return JAXBUtil.createMarshaller(soapVersion);
    }
    
}
