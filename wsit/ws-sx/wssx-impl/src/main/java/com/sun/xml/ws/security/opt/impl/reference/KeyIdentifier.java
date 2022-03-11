/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * KeyIdentifier.java
 *
 * Created on August 7, 2006, 1:48 PM
 */

package com.sun.xml.ws.security.opt.impl.reference;

import com.sun.istack.NotNull;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.wss.XWSSecurityException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.ws.security.secext10.KeyIdentifierType;
import com.sun.xml.ws.security.secext10.ObjectFactory;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.wss.impl.misc.Base64;

import java.util.Map;
import java.io.OutputStream;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.wss.core.reference.X509SubjectKeyIdentifier;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class KeyIdentifier extends KeyIdentifierType
        implements com.sun.xml.ws.security.opt.api.reference.KeyIdentifier,
        SecurityHeaderElement, SecurityElementWriter {

    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;

    /** Creates a new instance of KeyIdentifier */
    public KeyIdentifier(SOAPVersion sv) {
        this.soapVersion = sv;
    }

    /**
     *
     * @return the valueType attribute for KeyIdentifier
     */
    @Override
    public String getValueType() {
        return super.getValueType();
    }

    /**
     *
     * @param valueType the valueType attribute for KeyIdentifier
     */
    @Override
    public void setValueType(final String valueType) {
        super.setValueType(valueType);
    }

    /**
     *
     * @return the encodingType attribute
     */
    @Override
    public String getEncodingType() {
        return super.getEncodingType();
    }

    /**
     *
     * @param value the encodingType attribute
     */
    @Override
    public void setEncodingType(final String value) {
        super.setEncodingType(value);
    }

    /**
     *
     * @return the referenced value by this key identifier
     */
    @Override
    public String getReferenceValue() {
        return super.getValue();
    }

    /**
     *
     * @param referenceValue the referenced value by this keyIdentifier
     */
    @Override
    public void setReferenceValue(final String referenceValue) {
        super.setValue(referenceValue);
    }

    /**
     *
     * @return the reference type used
     */
    @Override
    public String getType() {
        return MessageConstants.KEY_INDETIFIER_TYPE;
    }

    /**
     *
     * @return id attribute
     */
    @Override
    public String getId() {
        QName qname = new QName(MessageConstants.WSU_NS, "Id", MessageConstants.WSU_PREFIX);
        Map<QName, String> otherAttributes = this.getOtherAttributes();
        return otherAttributes.get(qname);
    }

    /**
     *
     */
    @Override
    public void setId(String id) {
        QName qname = new QName(MessageConstants.WSU_NS, "Id", MessageConstants.WSU_PREFIX);
        Map<QName, String> otherAttributes = this.getOtherAttributes();
        otherAttributes.put(qname, id);
    }

    /**
     *
     * @return namespace uri of Keyidentifier.
     */
    @Override
    public String getNamespaceURI() {
        return MessageConstants.WSSE_NS;
    }

    /**
     * Gets the local name of this header element.
     *
     * @return
     *      this string must be interned.
     */
    @Override
    public String getLocalPart() {
        return "KeyIdentifier".intern();
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

    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        XMLStreamBufferResult xbr = new XMLStreamBufferResult();
        JAXBElement<KeyIdentifierType> keyIdentifierElem = new ObjectFactory().createKeyIdentifier(this);
        try{
            getMarshaller().marshal(keyIdentifierElem, xbr);

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
    @Override
    public void writeTo(XMLStreamWriter streamWriter) throws XMLStreamException {
        JAXBElement<KeyIdentifierType> keyIdentifierElem = new ObjectFactory().createKeyIdentifier(this);
        try {
            // If writing to Zephyr, get output stream and use JAXB UTF-8 writer
            if (streamWriter instanceof Map) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    getMarshaller().marshal(keyIdentifierElem, os);
                    return;
                }
            }

            getMarshaller().marshal(keyIdentifierElem,streamWriter);
        } catch (JAXBException e) {
            throw new XMLStreamException(e);
        }
    }

    /**
     *
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

    private Marshaller getMarshaller() throws JAXBException{
        return JAXBUtil.createMarshaller(soapVersion);
    }

    /**
     *
     */
    @Override
    public void writeTo(OutputStream os) {
    }

    public void updateReferenceValue(byte[] kerberosToken) throws XWSSecurityException{
        if(getValueType() == MessageConstants.KERBEROS_v5_APREQ_IDENTIFIER){
            try {
                setReferenceValue(Base64.encode(MessageDigest.getInstance("SHA-1").digest(kerberosToken)));
            } catch (NoSuchAlgorithmException ex) {
                throw new XWSSecurityException("Digest algorithm SHA-1 not found");
            }
        } else{
            throw new XWSSecurityException(getValueType() + " ValueType not supported for kerberos tokens");
        }
    }

    public void updateReferenceValue(X509Certificate cert) throws XWSSecurityException{
        if(getValueType() == MessageConstants.ThumbPrintIdentifier_NS){
            try {
                setReferenceValue(Base64.encode(MessageDigest.getInstance("SHA-1").digest(cert.getEncoded())));
            } catch ( NoSuchAlgorithmException ex ) {
                throw new XWSSecurityException("Digest algorithm SHA-1 not found");
            } catch ( CertificateEncodingException ex) {
                throw new XWSSecurityException("Error while getting certificate's raw content");
            }
        }else if(getValueType() ==MessageConstants.X509SubjectKeyIdentifier_NS) {
            byte[] keyId = X509SubjectKeyIdentifier.getSubjectKeyIdentifier(cert);
            if (keyId == null) {
                return;
            }
            setReferenceValue(Base64.encode(keyId));
        }
    }

    /**
     *
     */
    @Override
    public boolean refersToSecHdrWithId(String id) {
        String valueType =this.getValueType();
        if(MessageConstants.WSSE_SAML_KEY_IDENTIFIER_VALUE_TYPE.equals(valueType) ||
                MessageConstants.WSSE_SAML_v2_0_KEY_IDENTIFIER_VALUE_TYPE.equals(valueType)){
            return this.getValue().equals(id);
        }
        return false;
    }
}
