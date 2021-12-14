/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * JAXBSignatureHeaderElement.java
 *
 * Created on August 18, 2006, 2:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.opt.impl.dsig;

import com.sun.xml.security.core.dsig.CustomStreamWriterImpl;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo;
import com.sun.xml.wss.impl.MessageConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.OutputStream;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.JAXBException;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import com.sun.xml.ws.security.opt.crypto.dsig.Signature;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import javax.xml.stream.XMLStreamException;
import org.jvnet.staxex.XMLStreamWriterEx;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class JAXBSignatureHeaderElement implements SecurityHeaderElement, SecurityElementWriter {
    
    /* true if this signature header element is canonicalized before*/
    private boolean isCanonicalized = false;
    /*canonicalized signature value - for future use*/
    private byte [] cs = null;
    
    private Signature signature = null;
    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    private Marshaller marshaller = null;
    private XMLSignContext signContext = null;
    /** Creates a new instance of JAXBSignatureHeaderElement */
    public JAXBSignatureHeaderElement(Signature signature,SOAPVersion soapVersion) {
        this.signature = signature;
        this.soapVersion = soapVersion;
        
    }
    
    public JAXBSignatureHeaderElement(Signature signature,SOAPVersion soapVersion,XMLSignContext signctx) {
        this.signature = signature;
        this.soapVersion = soapVersion;
        this.signContext = signctx;
    }
    
    @Override
    public String getId() {
        return signature.getId();
    }
    
    @Override
    public void setId(String id) {
        throw new  UnsupportedOperationException();
    }
    
    
    @Override
    public String getNamespaceURI() {
        return  MessageConstants.DSIG_NS;
    }
    
    
    @Override
    public String getLocalPart() {
        return MessageConstants.SIGNATURE_LNAME;
    }
    
    @Override
    public javax.xml.stream.XMLStreamReader readHeader() throws XMLStreamException {
        XMLStreamBufferResult xbr = new XMLStreamBufferResult();
        try{
            getMarshaller().marshal(signature, xbr);
        } catch(JAXBException je){
            //log
            throw new XMLStreamException(je);
        }
        return xbr.getXMLStreamBuffer().readAsXMLStreamReader();
    }
    
    /**
     * writes the jaxb signature header element to an XMLStreamWriter
     * @param streamWriter javax.xml.stream.XMLStreamWriter
     */
    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws XMLStreamException {
        try {
            // If writing to Zephyr, get output stream and use JAXB UTF-8 writer
            if (streamWriter instanceof Map) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    getMarshaller().marshal(signature, os);
                }
            }else if (streamWriter instanceof XMLStreamWriterEx) {
                CustomStreamWriterImpl swi = new CustomStreamWriterImpl(streamWriter);
                getMarshaller().marshal(signature, swi);
            } else {
                getMarshaller().marshal(signature, streamWriter);
            }

        } catch (JAXBException e) {
            throw new XMLStreamException(e);
        }
    }
    
   /**
    * writes the jaxb signature header element to an XMLStreamWriter
    * @param streamWriter javax.xml.stream.XMLStreamWriter
    * @param props HashMap
    */
   @Override
   @SuppressWarnings("unchecked")
   public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter,HashMap props) throws XMLStreamException {
        try{
            Marshaller marshaller = getMarshaller();
            Iterator<Map.Entry<Object, Object>> itr = props.entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry<Object, Object> entry = itr.next();
                marshaller.setProperty((String)entry.getKey(), entry.getValue());
            }
            
            //writeTo(streamWriter);
            marshaller.marshal(signature,streamWriter);
        }catch(JAXBException jbe){
            //logging
            throw new XMLStreamException(jbe);
        }
    }
    
    
    public byte[] canonicalize(final String algorithm, final List<com.sun.xml.wss.impl.c14n.AttributeNS> namespaceDecls) {
        if(!isCanonicalized()){
            canonicalizeSignature();
        }
        return cs;
    }
    
    public boolean isCanonicalized() {
        return isCanonicalized;
    }
    
    private Marshaller getMarshaller() throws JAXBException{
        if(marshaller == null){
            marshaller = JAXBUtil.createMarshaller(soapVersion);
        }
        return marshaller;
    }
    
    private void canonicalizeSignature() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    @Override
    public void writeTo(OutputStream os) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    /**
     * finds whether this  security header element refers to the element with given id
     * @param id String
     * @return boolean
     */
    @Override
    public boolean refersToSecHdrWithId(final String id) {

        String refId = "#" +
                id;
        KeyInfo ki = signature.getKeyInfo();
        if(ki != null){
            List list = ki.getContent();
            if(list.size() >0 ){
                JAXBElement je = (JAXBElement) list.get(0);
                Object data = je.getValue();
                
                if(data instanceof SecurityHeaderElement){
                    if(((SecurityHeaderElement)data).refersToSecHdrWithId(id)){
                        return true;
                    }
                }
            }
        }
        List refList = signature.getSignedInfo().getReferences();
        for(int i=0;i< refList.size();i++){
            com.sun.xml.ws.security.opt.crypto.dsig.Reference ref = (com.sun.xml.ws.security.opt.crypto.dsig.Reference)refList.get(i);
            if(ref.getURI().equals(refId)){
                return true;
            }
        }
        return false;
    }
    /**
     * signs the data using the  signContext
     */
    public void sign()throws XMLStreamException{
        try{
            signature.sign(signContext);
        }catch(MarshalException | XMLSignatureException me){
            throw new XMLStreamException(me);
        }
    }
}
