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
 * SignatureConfirmation.java
 *
 * Created on September 11, 2006, 3:06 PM
 */

package com.sun.xml.ws.security.opt.impl.tokens;

import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.secext11.ObjectFactory;
import com.sun.xml.ws.security.secext11.SignatureConfirmationType;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.ws.api.SOAPVersion;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class SignatureConfirmation extends SignatureConfirmationType
          implements SecurityHeaderElement, SecurityElementWriter{

    private final ObjectFactory objFac = new ObjectFactory();
    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    
    /** Creates a new instance of SignatureConfirmation */
    public SignatureConfirmation(String id, SOAPVersion sv) {
        setId(id);
        this.soapVersion = sv;
    }
    
    public String getNamespaceURI() {
        return MessageConstants.WSSE11_NS;
    }
    
    public String getLocalPart() {
        return MessageConstants.SIGNATURE_CONFIRMATION_LNAME;
    }
    
    public String getAttribute(String nsUri, String localName) {
        throw new UnsupportedOperationException();
    }
    
    public String getAttribute(QName name) {
        throw new UnsupportedOperationException();
    }
    
    public javax.xml.stream.XMLStreamReader readHeader() throws javax.xml.stream.XMLStreamException {
        XMLStreamBufferResult xbr = new XMLStreamBufferResult();
        JAXBElement<SignatureConfirmationType> scElem = objFac.createSignatureConfirmation(this);
        try{
            getMarshaller().marshal(scElem, xbr);
            
        } catch(JAXBException je){
            throw new XMLStreamException(je);
        }
        return xbr.getXMLStreamBuffer().readAsXMLStreamReader();
    }
    
    public void writeTo(OutputStream os) {
    }
    
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws javax.xml.stream.XMLStreamException {
        JAXBElement<SignatureConfirmationType> scElem = objFac.createSignatureConfirmation(this);
        try {
            // If writing to Zephyr, get output stream and use JAXB UTF-8 writer
            if (streamWriter instanceof Map) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    getMarshaller().marshal(scElem, os);
                    return;
                }
            }
            
            getMarshaller().marshal(scElem,streamWriter);
        } catch (JAXBException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private Marshaller getMarshaller() throws JAXBException{
        return JAXBUtil.createMarshaller(soapVersion);
    }
    
    /**
     * 
     * @param id 
     * @return 
     */
    public boolean refersToSecHdrWithId(String id) {
        return false;
    }
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
    
}
