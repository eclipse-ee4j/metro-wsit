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

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.api.reference.DirectReference;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.secconv.impl.bindings.DerivedKeyTokenType;
import com.sun.xml.ws.security.secext10.KeyIdentifierType;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class DerivedKey implements  com.sun.xml.ws.security.opt.api.keyinfo.DerivedKeyToken,
        SecurityHeaderElement, SecurityElementWriter{
    
    private DerivedKeyTokenType derivedKey = null;
    private com.sun.xml.ws.security.secconv.impl.wssx.bindings.DerivedKeyTokenType derivedKey13 = null;
    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    private String refId = "";
    private String spVersion = "";
    /** Creates a new instance of DerivedKey */
    public DerivedKey(DerivedKeyTokenType dkt,SOAPVersion soapVersion, String spVersion) {
        this.derivedKey = dkt;
        this.soapVersion = soapVersion;
        this.spVersion = spVersion;
    }
    
     public DerivedKey(DerivedKeyTokenType dkt,SOAPVersion soapVersion,String refId, String spVersion) {
        this.derivedKey = dkt;
        this.soapVersion = soapVersion;
        this.refId = refId;
        this.spVersion = spVersion;
    }
     
    public DerivedKey(com.sun.xml.ws.security.secconv.impl.wssx.bindings.DerivedKeyTokenType dkt,SOAPVersion soapVersion, String spVersion) {
        this.derivedKey13 = dkt;
        this.soapVersion = soapVersion;
        this.spVersion = spVersion;
    } 
     
    public DerivedKey(com.sun.xml.ws.security.secconv.impl.wssx.bindings.DerivedKeyTokenType dkt,SOAPVersion soapVersion,String refId, String spVersion) {
        this.derivedKey13 = dkt;
        this.soapVersion = soapVersion;
        this.refId = refId;
        this.spVersion = spVersion;
    } 
    
    @Override
    public String getAlgorithm() {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            return derivedKey13.getAlgorithm();
        }else{
            return derivedKey.getAlgorithm();
        }
    }
    
    @Override
    public BigInteger getGeneration() {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            return derivedKey13.getGeneration();    
        }else{
            return derivedKey.getGeneration();
        }
    }
    
    @Override
    public String getId() {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            return derivedKey13.getId();    
        }else{    
            return derivedKey.getId();
        }
    }
    
    @Override
    public String getLabel() {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            return derivedKey13.getLabel();
        }else{    
            return derivedKey.getLabel();
        }
    }
    
    @Override
    public BigInteger getLength() {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            return derivedKey13.getLength();    
        }else{    
            return derivedKey.getLength();
        }
    }
    
    @Override
    public byte[] getNonce() {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            return derivedKey13.getNonce();
        }else{
            return derivedKey.getNonce();
        }
    }
    
    @Override
    public BigInteger getOffset() {
        
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            return derivedKey13.getOffset();
        }else{
            return derivedKey.getOffset();
        }
    }
    
    @Override
    public SecurityTokenReferenceType getSecurityTokenReference() {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            return derivedKey13.getSecurityTokenReference();
        }else{
            return derivedKey.getSecurityTokenReference();
        }
    }
    
    @Override
    public void setAlgorithm(String value) {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            derivedKey13.setAlgorithm(value);
        }else{
            derivedKey.setAlgorithm(value);
        }
    }
    
    @Override
    public void setGeneration(BigInteger value) {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            derivedKey13.setGeneration(value);
        }else{
            derivedKey.setGeneration(value);
        }
    }
    
    @Override
    public void setId(String value) {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            derivedKey13.setId(value);
        }else{
            derivedKey.setId(value);
        }
    }
    
    @Override
    public void setLabel(String value) {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            derivedKey13.setLabel(value);
        }else{
            derivedKey.setLabel(value);
        }
    }
    
    @Override
    public void setLength(BigInteger value) {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            derivedKey13.setLength(value);
        }else{
            derivedKey.setLength(value);
        }
    }
    
    @Override
    public void setNonce(byte[] value) {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            derivedKey13.setNonce(value);
        }else{
            derivedKey.setNonce(value);
        }
    }
    
    @Override
    public void setOffset(BigInteger value) {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            derivedKey13.setOffset(value);
        }else{
            derivedKey.setOffset(value);
        }
    }
    
    @Override
    public void setSecurityTokenReference(SecurityTokenReferenceType value) {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            derivedKey13.setSecurityTokenReference(value);
        }else{
            derivedKey.setSecurityTokenReference(value);
        }
    }
    
    @Override
    public String getNamespaceURI() {
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            return MessageConstants.WSSC_13NS;
        }else{
            return MessageConstants.WSSC_NS;
        }
    }
    
    @Override
    public String getLocalPart() {
        return MessageConstants.DERIVEDKEY_TOKEN_LNAME;
    }    
   
    @Override
    public javax.xml.stream.XMLStreamReader readHeader() {
        throw new UnsupportedOperationException();
    }
    /**
     * creates and writes the derived key token to the output stream
     * @param os OutputStream
     */
    @Override
    public void writeTo(OutputStream os) {
        try {
            JAXBElement<DerivedKeyTokenType> dkt = null;
            JAXBElement<com.sun.xml.ws.security.secconv.impl.wssx.bindings.DerivedKeyTokenType> dkt13 = null;
            Marshaller writer = getMarshaller();
            if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
                dkt13 = new com.sun.xml.ws.security.secconv.impl.wssx.bindings.ObjectFactory().createDerivedKeyToken(derivedKey13);
                writer.marshal(dkt13, os);
            }else{
                dkt = new com.sun.xml.ws.security.secconv.impl.bindings.ObjectFactory().createDerivedKeyToken(derivedKey);
                writer.marshal(dkt, os);
            }
        } catch (jakarta.xml.bind.JAXBException ex) {
            throw new XWSSecurityRuntimeException(ex);
        }
    }
    /**
     * creates and writes the derived key token to the XMLStreamWriter
     */
    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws javax.xml.stream.XMLStreamException {
        JAXBElement<DerivedKeyTokenType> dkt = null;                
        JAXBElement<com.sun.xml.ws.security.secconv.impl.wssx.bindings.DerivedKeyTokenType> dkt13 = null;         
        if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
            dkt13 = new com.sun.xml.ws.security.secconv.impl.wssx.bindings.ObjectFactory().createDerivedKeyToken(derivedKey13);
        }else{
            dkt = new com.sun.xml.ws.security.secconv.impl.bindings.ObjectFactory().createDerivedKeyToken(derivedKey);
        }
        try {
            // If writing to Zephyr, get output stream and use JAXB UTF-8 writer
            Marshaller writer = getMarshaller();
            if (streamWriter instanceof Map) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
                        writer.marshal(dkt13, os);
                    }else{
                        writer.marshal(dkt, os);
                    }
                    return;
                }
            }
            if(spVersion.equals(MessageConstants.SECURITYPOLICY_12_NS)){
                writer.marshal(dkt13, streamWriter);
            }else{
                writer.marshal(dkt, streamWriter);
            }
        } catch (JAXBException e) {
            //log here
            throw new XMLStreamException(e);
        }
    }
    
    
    private Marshaller getMarshaller() throws JAXBException{
        return JAXBUtil.createMarshaller(soapVersion);
    }
    /**
     * checks whether this object refers to the object with the given id
     * @param id String
     * @return boolean
     */
    @Override
    public boolean refersToSecHdrWithId(String id) {
        if(refId != null && refId.length() >0){
            if(refId.equals(id)){
                return true;
            }
        }
        if(this.getSecurityTokenReference() != null){
            SecurityTokenReferenceType ref =  this.getSecurityTokenReference();
            List list = ref.getAny();
            if(list.size() > 0){
                JAXBElement je = (JAXBElement) list.get(0);
                Object obj = je.getValue();
                if(obj instanceof DirectReference ){
                    String sb = "#" +
                            id;
                    return ((DirectReference)obj).getURI().equals(sb);
                }else if(obj instanceof KeyIdentifierType){
                    KeyIdentifierType ki = (KeyIdentifierType)obj;
                    String valueType = ki.getValueType();
                    if(valueType.equals(MessageConstants.WSSE_SAML_KEY_IDENTIFIER_VALUE_TYPE) ||
                            valueType.equals(MessageConstants.WSSE_SAML_v2_0_KEY_IDENTIFIER_VALUE_TYPE)){
                        if(id.equals(ki.getValue())){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    /**
     * writes the derived key to the XMLStreamWriter
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
            //log here
            throw new XMLStreamException(jbe);
        }
    }
    
}
