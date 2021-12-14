/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.keyinfo;

import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.secconv.impl.bindings.ObjectFactory;
import com.sun.xml.ws.security.secconv.impl.bindings.SecurityContextTokenType;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.impl.c14n.AttributeNS;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.wss.WSITXMLFactory;

/**
 * SecurityContextToken Implementation
 * @author Manveen Kaur manveen.kaur@sun.com
 * @author K.Venugopal@sun.com
 */
public class SecurityContextToken extends SecurityContextTokenType implements SecurityHeaderElement, SecurityElementWriter, com.sun.xml.ws.security.SecurityContextToken {
    
    public final String SECURITY_CONTEXT_TOKEN = "SecurityContextToken";
    
    private String instance = null;
    private URI identifier = null;
    private List extElements = null;
    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    
    public SecurityContextToken(URI identifier, String instance, String wsuId, SOAPVersion sv) {
        if (identifier != null) {
            setIdentifier(identifier);
        }
        if (instance != null) {
            setInstance(instance);
        }
        
        if (wsuId != null){
            setWsuId(wsuId);
        }
        this.soapVersion = sv;
    }
    
    // useful for converting from JAXB to our owm impl class
    @SuppressWarnings("unchecked")
    public SecurityContextToken(SecurityContextTokenType sTokenType, SOAPVersion sv){
        List<Object> list = sTokenType.getAny();
        for (int i = 0; i < list.size(); i++) {
            Object object = list.get(i);
            if(object instanceof JAXBElement){
                JAXBElement obj = (JAXBElement)object;
                
                String local = obj.getName().getLocalPart();
                if (local.equalsIgnoreCase("Instance")) {
                    setInstance((String)obj.getValue());
                } else if (local.equalsIgnoreCase("Identifier")){
                    try {
                        setIdentifier(new URI((String)obj.getValue()));
                    }catch (URISyntaxException ex){
                        throw new RuntimeException(ex);
                    }
                }
            }else{
                getAny().add(object);
                if(extElements == null){
                    extElements = new ArrayList();
                    extElements.add(object);
                }
            }
        }
        
        setWsuId(sTokenType.getId());
        this.soapVersion = sv;
    }
    
    @Override
    public URI getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(URI identifier) {
        this.identifier = identifier;
        JAXBElement<String> iElement =
                  (new ObjectFactory()).createIdentifier(identifier.toString());
        getAny().add(iElement);
    }
    
    @Override
    public String getInstance() {
        return instance;
    }
    
    public void setInstance(String instance) {
        this.instance = instance;
        JAXBElement<String> iElement =
                  (new ObjectFactory()).createInstance(instance);
        getAny().add(iElement);
    }
    
    public void setWsuId(String wsuId){
        setId(wsuId);
        
    }
    
    @Override
    public String getWsuId(){
        return getId();
    }
    
    @Override
    public String getType() {
        return SECURITY_CONTEXT_TOKEN;
    }
    
    @Override
    public Object getTokenValue() {
        try {
            DocumentBuilderFactory dbf = WSITXMLFactory.createDocumentBuilderFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            
            jakarta.xml.bind.Marshaller marshaller = WSTrustElementFactory.getContext().createMarshaller();
            JAXBElement<SecurityContextTokenType> tElement =  (new ObjectFactory()).createSecurityContextToken(this);
            marshaller.marshal(tElement, doc);
            return doc.getDocumentElement();
            
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    @Override
    public List getExtElements() {
        return extElements;
    }
    
    @Override
    public String getNamespaceURI() {
        return "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    }
    
    @Override
    public String getLocalPart() {
        return "SecurityContextToken";
    }
    
    public String getAttribute(String nsUri, String localName) {
        throw new UnsupportedOperationException();
    }
    
    public String getAttribute(QName name) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public javax.xml.stream.XMLStreamReader readHeader() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void writeTo(OutputStream os) {
        try {
            JAXBElement<SecurityContextTokenType> sct =
                      new com.sun.xml.ws.security.secconv.impl.bindings.ObjectFactory().createSecurityContextToken(this);
            Marshaller writer = getMarshaller();
            writer.marshal(sct, os);
        } catch (jakarta.xml.bind.JAXBException ex) {
            throw new XWSSecurityRuntimeException(ex);
        }
    }
    /**
     * writes the SecurityContextToken to the XMLStreamWriter
     */
    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws javax.xml.stream.XMLStreamException {
        JAXBElement<SecurityContextTokenType> sct =
                  new com.sun.xml.ws.security.secconv.impl.bindings.ObjectFactory().createSecurityContextToken(this);
        try {
            // If writing to Zephyr, get output stream and use JAXB UTF-8 writer
            Marshaller writer = getMarshaller();
            if (streamWriter instanceof Map) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    
                    writer.marshal(sct, os);
                    return;
                }
            }
            writer.marshal(sct, streamWriter);
        } catch (JAXBException e) {
            throw new XMLStreamException(e);
        }
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
    public boolean refersToSecHdrWithId(String id) {
        return false;
    }
    /**
     * writes the SecurityContextToken to the XMLStreamWriter
     */
    @Override
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
