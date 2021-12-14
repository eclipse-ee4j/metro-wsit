/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.enc;

import com.sun.xml.security.core.xenc.CVAdapter;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.security.opt.api.SecurityElement;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.crypto.SSEData;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.secext11.EncryptedHeaderType;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.c14n.AttributeNS;
import com.sun.xml.wss.impl.c14n.StAXEXC14nCanonicalizerImpl;
import com.sun.xml.wss.logging.LogDomainConstants;
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
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.crypto.Data;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo;
import javax.crypto.Cipher;
import com.sun.xml.wss.logging.impl.opt.crypto.LogStringsMessages;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class EncryptedHeader
        implements SecurityHeaderElement, SecurityElementWriter{
    
    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_CRYPTO_DOMAIN,
            LogDomainConstants.IMPL_OPT_CRYPTO_DOMAIN_BUNDLE);
    
    private EncryptedHeaderType eht = null;
    private boolean isCanonicalized = false;
    //private ObjectFactory objFac = new ObjectFactory();
    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    private Data data = null;
    private Key key = null;
    private CryptoProcessor dep = null;
    
    /** Creates a new instance of EncryptedHeader */
    public EncryptedHeader(EncryptedHeaderType eht, Data data,Key key,SOAPVersion soapVersion) {
        this.eht = eht;
        this.key  = key;
        this.data = data;
        this.soapVersion = soapVersion;
    }
    
    @Override
    public boolean refersToSecHdrWithId(String id) {
        KeyInfo ki = (KeyInfo) eht.getEncryptedData().getKeyInfo();
        if(ki != null){
            List list = ki.getContent();
            if(list.size() >0 ){
                Object data = list.get(0);
                if(data instanceof SecurityHeaderElement){
                    return ((SecurityHeaderElement)data).refersToSecHdrWithId(id);
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
    
    @Override
    public String getId() {
        return eht.getId();
    }
    
    @Override
    public void setId(String id) {
        eht.setId(id);
    }
    
    @Override
    public String getNamespaceURI() {
        return MessageConstants.WSSE11_NS;
    }
    
    @Override
    public String getLocalPart() {
        return MessageConstants.ENCRYPTED_HEADER_LNAME;
    }
    
    @Override
    public XMLStreamReader readHeader() {
        throw new UnsupportedOperationException();
    }
    
    public byte[] canonicalize(String algorithm, List<AttributeNS> namespaceDecls) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isCanonicalized() {
        return isCanonicalized;
    }
    /**
     * writes the encrypted header to an XMLStreamWriter
     * @param streamWriter XMLStreamWriter
     */
    @Override
    public void writeTo(XMLStreamWriter streamWriter) throws XMLStreamException {
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
                dep = new CryptoProcessor(Cipher.ENCRYPT_MODE, eht.getEncryptedData().getEncryptionMethod().getAlgorithm(), data, key);
                
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
            
            com.sun.xml.ws.security.secext11.ObjectFactory obj = new com.sun.xml.ws.security.secext11.ObjectFactory();
            JAXBElement eh = obj.createEncryptedHeader(eht);
            writer.marshal(eh,streamWriter);
        } catch (jakarta.xml.bind.JAXBException ex) {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1916_ERROR_WRITING_ECRYPTEDHEADER(ex.getMessage()), ex);
        }
    }
    /**
     * writes the encrypted header to an XMLStreamWriter
     * @param streamWriter XMLStreamWriter
     * @param props  HashMap
     */
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
        }catch(JAXBException jbe){
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1916_ERROR_WRITING_ECRYPTEDHEADER(jbe.getMessage()), jbe);
            throw new XMLStreamException(jbe);
        }
    }
    /**
     * writes the encrypted header to an OutputStream
     * @param os OutputStream
     */
    @Override
    public void writeTo(OutputStream os) {
        try {
            Marshaller writer = getMarshaller();
            CryptoProcessor dep;
            
            dep = new CryptoProcessor(Cipher.ENCRYPT_MODE, eht.getEncryptedData().getEncryptionMethod().getAlgorithm(), data, key);
            CVAdapter adapter = new CVAdapter(dep);
            writer.setAdapter(CVAdapter.class,adapter);
            com.sun.xml.ws.security.secext11.ObjectFactory obj = new com.sun.xml.ws.security.secext11.ObjectFactory();
            JAXBElement eh = obj.createEncryptedHeader(eht);
            writer.marshal(eh,os);
        } catch (jakarta.xml.bind.JAXBException ex) {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1916_ERROR_WRITING_ECRYPTEDHEADER(ex.getMessage()), ex);
        }
    }
    
    private Marshaller getMarshaller() throws JAXBException{
        return JAXBUtil.createMarshaller(soapVersion);
    }
    
}
