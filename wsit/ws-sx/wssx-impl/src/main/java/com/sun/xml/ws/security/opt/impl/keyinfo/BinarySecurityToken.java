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
 * BinarySecurityToken.java
 *
 * Created on August 2, 2006, 10:36 AM
 */

package com.sun.xml.ws.security.opt.impl.keyinfo;

import com.sun.istack.NotNull;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.secext10.BinarySecurityTokenType;
import com.sun.xml.wss.impl.MessageConstants;

import java.io.OutputStream;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import com.sun.xml.ws.security.secext10.ObjectFactory;
import com.sun.xml.wss.logging.impl.crypto.LogStringsMessages;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class BinarySecurityToken implements com.sun.xml.ws.security.opt.api.keyinfo.BinarySecurityToken,SecurityHeaderElement, SecurityElementWriter {

    private BinarySecurityTokenType bst = null;

    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    /** Creates a new instance of BinarySecurityToken */
    public BinarySecurityToken(BinarySecurityTokenType token,SOAPVersion sv) {
        this.bst = token;
        this.soapVersion = sv;
    }

    @Override
    public String getValueType() {
        return bst.getValueType();
    }

    @Override
    public String getEncodingType() {
        return bst.getEncodingType();
    }

    @Override
    public String getId() {
        return bst.getId();
    }

    @Override
    public void setId(String id) {
        bst.setId(id);
    }

    @Override
    @NotNull
    public String getNamespaceURI() {
        return MessageConstants.WSSE_NS;
    }

    @Override
    @NotNull
    public String getLocalPart() {
        return MessageConstants.WSSE_BINARY_SECURITY_TOKEN_LNAME;
    }
    /**
     * marshalls the BST element into the XMLStreamBuffer
     * @return XMLStreamReader
     */
    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        XMLStreamBufferResult xbr = new XMLStreamBufferResult();
        JAXBElement<BinarySecurityTokenType> bstElem =
                new ObjectFactory().createBinarySecurityToken(bst);
        try{
            getMarshaller().marshal(bstElem, xbr);
        }catch(JAXBException je){
            //log here
            throw new XMLStreamException(je);
        }
        return xbr.getXMLStreamBuffer().readAsXMLStreamReader();
    }

    public <T> T readAsJAXB(Unmarshaller unmarshaller) {
        throw new UnsupportedOperationException();
    }

    /**
     *  writes the binary security token to the XMLStreamWriter
     * @param streamWriter XMLStreamWriter
     */
    @Override
    public void writeTo(XMLStreamWriter streamWriter) throws XMLStreamException {
        JAXBElement<BinarySecurityTokenType> bstElem =
                new ObjectFactory().createBinarySecurityToken(bst);
        try {
            // If writing to Zephyr, get output stream and use JAXB UTF-8 writer
            if (streamWriter instanceof Map) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    Marshaller writer = getMarshaller();

                    writer.marshal(bstElem, os);
                    return;
                }
            }
            getMarshaller().marshal(bstElem, streamWriter);
        } catch (JAXBException e) {
            //log here also
            throw new XMLStreamException(e);
        }
    }

    public void writeTo(SOAPMessage saaj) throws SOAPException {
        NodeList nl = saaj.getSOAPHeader().getElementsByTagNameNS(MessageConstants.WSSE_NS,MessageConstants.WSSE_SECURITY_LNAME);
        try {
            Marshaller writer = getMarshaller();

            writer.marshal(bst,nl.item(0));
        } catch (JAXBException ex) {
            throw new SOAPException(ex);
        }
    }

    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) {
        throw new UnsupportedOperationException();
    }
   /**
    * returns base64 decoded value of the binary securt token value
    * @return byte[]
    */
    @Override
    public byte[] getTokenValue() {
        try {
            return Base64.getMimeDecoder().decode(bst.getValue());
        } catch (IllegalArgumentException ex) {
            LogDomainConstants.CRYPTO_IMPL_LOGGER.log(Level.SEVERE,LogStringsMessages.WSS_1243_BST_DECODING_ERROR(),ex);
            return null;
        }
    }

    private Marshaller getMarshaller() throws JAXBException{
        return JAXBUtil.createMarshaller(soapVersion);
    }

    @Override
    public void writeTo(OutputStream os) {
    }

    @Override
    public boolean refersToSecHdrWithId(String id) {
        return false;
    }

    public X509Certificate getCertificate() {
        return null;
    }
    /**
     * writes the binary security token to the XMLStreamWriter
     * @param streamWriter javax.xml.stream.XMLStreamWriter
     * @param props HashMap
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
