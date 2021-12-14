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
 * JAXBEncryptedData.java
 *
 * Created on August 4, 2006, 2:56 PM
 */

package com.sun.xml.ws.security.opt.impl.enc;

import com.sun.xml.security.core.xenc.CVAdapter;
import com.sun.xml.security.core.xenc.EncryptedDataType;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.security.opt.api.EncryptedData;
import com.sun.xml.ws.security.opt.api.SecurityElement;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.opt.impl.crypto.SSEData;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.c14n.AttributeNS;
import com.sun.xml.wss.impl.c14n.StAXEXC14nCanonicalizerImpl;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.impl.opt.crypto.LogStringsMessages;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.JAXBException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import javax.xml.crypto.Data;
import javax.crypto.Cipher;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author K.Venugopal@sun.com
 */

public class JAXBEncryptedData implements EncryptedData,
        SecurityHeaderElement, SecurityElementWriter {
    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_CRYPTO_DOMAIN,
            LogDomainConstants.IMPL_OPT_CRYPTO_DOMAIN_BUNDLE);
    
    private EncryptedDataType  edt = null;
    private Data data = null;
    private Key key = null;
    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    private CryptoProcessor dep = null;
    /** Creates a new instance of JAXBEncryptedData */
    public JAXBEncryptedData(EncryptedDataType edt,Data data,Key key,SOAPVersion soapVersion) {
        this.edt = edt;
        this.key  = key;
        this.data = data;
        this.soapVersion = soapVersion;
    }
    
    public JAXBEncryptedData(EncryptedDataType edt,Data data,SOAPVersion soapVersion) {
        this.edt = edt;
        this.data = data;
        this.soapVersion = soapVersion;
    }
    
    public String getEncryptedLocalName(){
        if(data instanceof SSEData){
            SecurityElement se = ((SSEData)data).getSecurityElement();
            return se.getLocalPart();
        }
        return "";
    }
    
    public String getEncryptedId(){
        if(data instanceof SSEData){
            SecurityElement se = ((SSEData)data).getSecurityElement();
            return se.getId();
        }
        return "";
    }
    
    @Override
    public void encrypt() {
    }
    
    @Override
    public void decrypt() {
    }
    
    @Override
    public String getId() {
        return edt.getId();
    }
    
    @Override
    public void setId(String id) {
        if(edt.getId() == null || edt.getId().length() ==0){
            edt.setId(id);
        }
    }    
    
    @Override
    public String getNamespaceURI() {
        return MessageConstants.XENC_NS;
    }    
    
    @Override
    public String getLocalPart() {
        return MessageConstants.ENCRYPTED_DATA_LNAME;
    }
    /**
     * writes the jaxb encrypted data to an XMLStreamWriter
     * @param streamWriter javax.xml.stream.XMLStreamWriter
     */
    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws javax.xml.stream.XMLStreamException {
        try {
            
            if (streamWriter instanceof Map && !(dep != null)) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    writeTo(os);
                    return;
                }
            }
            Marshaller writer = getMarshaller();
            
            if(dep == null){
                dep = new CryptoProcessor(Cipher.ENCRYPT_MODE, edt.getEncryptionMethod().getAlgorithm(), data, key);
                
                if(streamWriter instanceof StAXEXC14nCanonicalizerImpl){
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try{
                        dep.encryptData(bos);
                        
                    }catch(IOException ie){
                        logger.log(Level.SEVERE, LogStringsMessages.WSS_1920_ERROR_CALCULATING_CIPHERVALUE(),ie);
                        throw new XMLStreamException("Error occurred while calculating Cipher Value");
                    }
                    dep.setEncryptedDataCV(bos.toByteArray());
                }
            }
            CVAdapter adapter = new CVAdapter(dep);
            writer.setAdapter(CVAdapter.class,adapter);
            
            com.sun.xml.security.core.xenc.ObjectFactory obj = new com.sun.xml.security.core.xenc.ObjectFactory();
            JAXBElement ed = obj.createEncryptedData(edt);
            writer.marshal(ed,streamWriter);
        } catch (jakarta.xml.bind.JAXBException ex) {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1919_ERROR_WRITING_ENCRYPTEDDATA(ex.getMessage()), ex);
        }
    }
    /**
     * writes the jaxb encrypted data to an XMLStreamWriter
     * @param streamWriter javax.xml.stream.XMLStreamWriter
     * @param props HashMap
     */
    @Override
    @SuppressWarnings("unchecked")
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter, HashMap props) throws XMLStreamException {
        try{
            Marshaller marshaller = getMarshaller();
            Iterator<Map.Entry<Object, Object>> itr = props.entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry<Object, Object> entry = itr.next();
                marshaller.setProperty((String)entry.getKey(), entry.getValue());
            }
            writeTo(streamWriter);
        }catch(JAXBException jbe){
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1919_ERROR_WRITING_ENCRYPTEDDATA(jbe.getMessage()), jbe);
            throw new XMLStreamException(jbe);
        }
    }
    /**
     * writes the jaxb encrypted data to an XMLStreamWriter
     * @param os java.io.OutputStream
     */
    @Override
    public void writeTo(java.io.OutputStream os)  {
        try {
            Marshaller writer = getMarshaller();
            CryptoProcessor dep;
            
            dep = new CryptoProcessor(Cipher.ENCRYPT_MODE, edt.getEncryptionMethod().getAlgorithm(), data, key);
            
            CVAdapter adapter = new CVAdapter(dep);
            writer.setAdapter(CVAdapter.class,adapter);
            com.sun.xml.security.core.xenc.ObjectFactory obj = new com.sun.xml.security.core.xenc.ObjectFactory();
            JAXBElement ed = obj.createEncryptedData(edt);
            writer.marshal(ed,os);
        } catch (jakarta.xml.bind.JAXBException ex) {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1919_ERROR_WRITING_ENCRYPTEDDATA(ex.getMessage()), ex);
        }
    }
    
    public void writeTo(jakarta.xml.soap.SOAPMessage saaj) throws jakarta.xml.soap.SOAPException {
        throw new UnsupportedOperationException();
    }
    
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) {
        throw new UnsupportedOperationException();
    }
    
    public byte[] canonicalize(String algorithm, List<AttributeNS> namespaceDecls) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isCanonicalized() {
        return false;
    }
    
    private Marshaller getMarshaller() throws JAXBException{        
        return JAXBUtil.createMarshaller(soapVersion);
    }
    
    @Override
    public javax.xml.stream.XMLStreamReader readHeader() {
        throw new UnsupportedOperationException();
    }
    /**
     * finds whether the this security header element refers to the element with given id
     * @param id String
     * @return boolean
     */
    @Override
    public boolean refersToSecHdrWithId(String id) {
        KeyInfo ki = (KeyInfo) this.edt.getKeyInfo();
        if(ki != null){
            List list = ki.getContent();
            if(list.size() >0 ){
                Object data = ((JAXBElement)list.get(0)).getValue();
                if(data instanceof SecurityHeaderElement){
                   if(((SecurityHeaderElement)data).refersToSecHdrWithId(id)){
                       return true;
                   }
                }
            }
        }        
        if(data instanceof SSEData){
            SecurityElement se = ((SSEData)data).getSecurityElement();
            if(se instanceof SecurityHeaderElement ){
                return ((SecurityHeaderElement)se).refersToSecHdrWithId(id);
            }
        }
        return false;
    }
        
}
