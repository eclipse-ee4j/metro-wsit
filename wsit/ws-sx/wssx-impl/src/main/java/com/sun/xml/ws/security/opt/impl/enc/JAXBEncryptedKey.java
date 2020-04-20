/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.enc;

import com.sun.xml.security.core.xenc.CVAdapter;
import com.sun.xml.security.core.xenc.EncryptedKeyType;
import com.sun.xml.security.core.xenc.ReferenceList;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.security.opt.api.EncryptedKey;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo;
import com.sun.xml.wss.logging.LogDomainConstants;
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
import jakarta.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import javax.crypto.Cipher;
import jakarta.xml.bind.JAXBException;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.security.core.xenc.ReferenceType;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.wss.logging.impl.opt.crypto.LogStringsMessages;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class JAXBEncryptedKey implements EncryptedKey,
        SecurityHeaderElement, SecurityElementWriter {
    
    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_CRYPTO_DOMAIN,
            LogDomainConstants.IMPL_OPT_CRYPTO_DOMAIN_BUNDLE);
    
    private EncryptedKeyType ekt = null;
    //private Data data = null;
    private Key dataEnckey = null;
    private Key dkEK = null;
    CryptoProcessor dep = null;
    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    
    /** Creates a new instance of JAXBEncryptedKey */
    public JAXBEncryptedKey(EncryptedKeyType ekt,Key kk,Key dk,SOAPVersion soapVersion) throws XWSSecurityException{
        this.ekt = ekt;
        this.dkEK = kk;
        this.dataEnckey = dk;
        this.soapVersion = soapVersion;
        dep = new CryptoProcessor(Cipher.WRAP_MODE, ekt.getEncryptionMethod().getAlgorithm(),dataEnckey,dkEK);
        
    }
    
    public void encrypt() {
    }
    
    public void decrypt() {
    }
    
    public String getId() {
        return ekt.getId();
    }
    
    public void setId(String id) {
        ekt.setId(id);
    }
    
    public String getNamespaceURI() {
        return "http://www.w3.org/2001/04/xmlenc#";
    }
    
    
    public String getLocalPart() {
        return "EncryptedKey";
    }
    
    
    public String getAttribute( String nsUri, String localName) {
        throw new UnsupportedOperationException();
    }
    
    
    public String getAttribute( QName name) {
        throw new UnsupportedOperationException();
    }
    
    public javax.xml.stream.XMLStreamReader readHeader() throws javax.xml.stream.XMLStreamException {
        throw new UnsupportedOperationException();
    }
    
    public <T> T readAsJAXB(Unmarshaller unmarshaller) throws jakarta.xml.bind.JAXBException {
        throw new UnsupportedOperationException();
    }
    
    public <T> T readAsJAXB(com.sun.xml.bind.api.Bridge<T> bridge, com.sun.xml.bind.api.BridgeContext context) throws jakarta.xml.bind.JAXBException {
        throw new UnsupportedOperationException();
    }
    
    public <T> T readAsJAXB(com.sun.xml.bind.api.Bridge<T> bridge) throws jakarta.xml.bind.JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * writes the jaxb encrypted key to to an XMLStreamWriter
     * @param streamWriter javax.xml.stream.XMLStreamWriter
     * @throws javax.xml.stream.XMLStreamException
     */
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws javax.xml.stream.XMLStreamException {
        Marshaller writer;
        try {
            if (streamWriter instanceof Map) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    writeTo(os);
                    return;
                }
            }
            writer = getMarshaller();
            JAXBElement ed = getEK(writer);
            writer.marshal(ed,streamWriter);
        } catch (jakarta.xml.bind.JAXBException ex) {
            logger.log(Level.SEVERE,LogStringsMessages.WSS_1921_ERROR_WRITING_ENCRYPTEDKEY(ex.getMessage()), ex);
        } catch (com.sun.xml.wss.XWSSecurityException ex) {
            logger.log(Level.SEVERE,LogStringsMessages.WSS_1921_ERROR_WRITING_ENCRYPTEDKEY(ex.getMessage()), ex);
        }
    }
    
    /**
     * writes the jaxb encrypted key to to an XMLStreamWriter
     * @param os OutputStream
     */
    public void writeTo(OutputStream os)  {
        Marshaller writer;
        try {
            writer = getMarshaller();
            
            JAXBElement ed = getEK(writer);
            writer.marshal(ed,os);
        } catch (jakarta.xml.bind.JAXBException ex) {
            logger.log(Level.SEVERE,LogStringsMessages.WSS_1921_ERROR_WRITING_ENCRYPTEDKEY(ex.getMessage()), ex);
        } catch (com.sun.xml.wss.XWSSecurityException ex) {
            logger.log(Level.SEVERE,LogStringsMessages.WSS_1921_ERROR_WRITING_ENCRYPTEDKEY(ex.getMessage()), ex);
        }
    }
    
    private JAXBElement getEK(Marshaller writer) throws JAXBException, XWSSecurityException{
        
        CVAdapter adapter = new CVAdapter(dep);
        writer.setAdapter(CVAdapter.class,adapter);
        com.sun.xml.security.core.xenc.ObjectFactory obj = new com.sun.xml.security.core.xenc.ObjectFactory();
        return obj.createEncryptedKey(ekt);
    }
    
    public void writeTo(jakarta.xml.soap.SOAPMessage saaj) throws jakarta.xml.soap.SOAPException {
        throw new UnsupportedOperationException();
    }
    
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        throw new UnsupportedOperationException();
    }
    
    public byte[] canonicalize(String algorithm, List<com.sun.xml.wss.impl.c14n.AttributeNS> namespaceDecls) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isCanonicalized() {
        throw new UnsupportedOperationException();
    }
    
    private Marshaller getMarshaller() throws JAXBException{
        return JAXBUtil.createMarshaller(soapVersion);
    }
    
    public ReferenceList getReferenceList() {
        return ekt.getReferenceList();
    }
    
    public boolean hasReferenceList() {
        return (ekt.getReferenceList() != null);
    }
    /**
     * finds whether the this security header element refers to the element with given id
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean refersToSecHdrWithId(String id) {
        KeyInfo ki = (KeyInfo) this.ekt.getKeyInfo();
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
        List list = null;
        if(getReferenceList() != null){
            list = getReferenceList().getDataReferenceOrKeyReference();
        }
        if(list == null){
            return false;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("#");
        sb.append(id);
        String idref = sb.toString();
        for(int i=0;i< list.size();i++){
            JAXBElement<ReferenceType> rt =(JAXBElement<ReferenceType> )list.get(i);
            ReferenceType ref = (ReferenceType) rt.getValue();
            if(ref.getURI().equals(idref)){
                return true;
            }
        }
        return false;
    }
    
    public void setReferenceList(ReferenceList list) {
        ekt.setReferenceList(list);
    }
    
    public Key getKey() {
        return dataEnckey;
    }
    
    public byte[] getCipherValue(){
        return dep.getCipherValueOfEK();
    }
    
    /**
     * writes the jaxb encrypted key to to an XMLStreamWriter
     * @param streamWriter javax.xml.stream.XMLStreamWriter
     * @param props HashMap
     * @throws XMLStreamException
     */
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
            logger.log(Level.SEVERE,LogStringsMessages.WSS_1921_ERROR_WRITING_ENCRYPTEDKEY(jbe.getMessage()), jbe);
            throw new XMLStreamException(jbe);
        }
    }

    @Override
    public boolean isFeatureSupported(String feature) {
        return false;
    }
}
