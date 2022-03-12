/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.incoming;

import com.sun.xml.ws.security.opt.api.NamespaceContextInfo;
import com.sun.xml.ws.security.opt.api.PolicyBuilder;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.TokenValidator;
import com.sun.xml.wss.impl.FilterProcessingContext;
import com.sun.xml.wss.impl.misc.DefaultSecurityEnvironmentImpl;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import java.util.HashMap;
import java.io.OutputStream;

import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferException;
import com.sun.xml.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.stream.buffer.stax.StreamReaderBufferCreator;
import javax.xml.stream.XMLInputFactory;

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.impl.SecurableSoapMessage;



import com.sun.xml.ws.security.opt.impl.util.StreamUtil;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.jvnet.staxex.XMLStreamReaderEx;
import org.jvnet.staxex.Base64Data;
import com.sun.xml.wss.impl.misc.Base64;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.Base64DecodingException;
import java.util.logging.Level;
import com.sun.xml.wss.logging.impl.opt.LogStringsMessages;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class X509BinarySecurityToken implements com.sun.xml.ws.security.opt.api.keyinfo.X509BinarySecurityToken,
        SecurityHeaderElement,PolicyBuilder,TokenValidator,NamespaceContextInfo,
        SecurityElementWriter{

    private String valueType = null;
    private String encodingType = null;
    private String id = "";
    private XMLStreamBuffer mark = null;
    private String namespaceURI = null;
    private String localPart = null;
    //private BSTProcessor filter = new BSTProcessor();
    private AuthenticationTokenPolicy.X509CertificateBinding x509Policy = null;
    private HashMap<String,String> nsDecls;

    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_DOMAIN,
            LogDomainConstants.IMPL_OPT_DOMAIN_BUNDLE);

    private byte [] bstValue = null;
    private X509Certificate cert = null;
    @SuppressWarnings("unchecked")
    public X509BinarySecurityToken(XMLStreamReader reader, StreamReaderBufferCreator creator,HashMap nsDecl, XMLInputFactory  staxIF)
    throws XMLStreamException,XMLStreamBufferException{
        localPart = reader.getLocalName();
        namespaceURI = reader.getNamespaceURI();
        id = reader.getAttributeValue(MessageConstants.WSU_NS,"Id");
        valueType = reader.getAttributeValue(null,MessageConstants.WSE_VALUE_TYPE);
        encodingType = reader.getAttributeValue(null,"EncodingType");
        mark = new XMLStreamBufferMark(nsDecl,creator);
        creator.createElementFragment(reader,true);
        x509Policy = new AuthenticationTokenPolicy.X509CertificateBinding();
        x509Policy.setUUID(id);
        x509Policy.setValueType(valueType);
        x509Policy.setEncodingType(encodingType);
        this.nsDecls = nsDecl;
        XMLStreamReader bstReader = mark.readAsXMLStreamReader();
    bstReader.next();
        digestBST(bstReader);
    }

    @Override
    public String getValueType() {
        return valueType;
    }

    @Override
    public String getEncodingType() {
        return encodingType;
    }

    @Override
    public byte[] getTokenValue() {
        return bstValue;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean refersToSecHdrWithId(final String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setId(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNamespaceURI() {
        return namespaceURI;
    }

    @Override
    public String getLocalPart() {
        return localPart;
    }

    @Override
    public javax.xml.stream.XMLStreamReader readHeader() throws javax.xml.stream.XMLStreamException {
        return mark.readAsXMLStreamReader();
    }

    @Override
    public void writeTo(OutputStream os) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws javax.xml.stream.XMLStreamException {
        mark.writeToXMLStreamWriter(streamWriter);
    }

    @Override
    public WSSPolicy getPolicy() {
        return x509Policy;

    }

    @Override
    public void validate(ProcessingContext context) throws com.sun.xml.wss.XWSSecurityException {
        X509Certificate cert = getCertificate();
//        if (context.getSecurityEnvironment().isSelfCertificate(cert)) {
//            //nothing to do if this is service certificate
//            return;
//        }

        if(!context.getSecurityEnvironment().validateCertificate(cert, context.getExtraneousProperties())){
            //TODO: MISSING-LOG
            throw SecurableSoapMessage.newSOAPFaultException(MessageConstants.WSSE_INVALID_SECURITY_TOKEN,
                    "Certificate validation failed", null);
        }
        context.getSecurityEnvironment().updateOtherPartySubject(
                DefaultSecurityEnvironmentImpl.getSubject((FilterProcessingContext) context), cert);
    }

    @Override
    public HashMap<String, String> getInscopeNSContext() {
        return nsDecls;
    }


    @Override
    public X509Certificate getCertificate(){
        return cert;
    }

    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter, HashMap props) {
        throw new UnsupportedOperationException();
    }

    private void digestBST(XMLStreamReader reader) throws XMLStreamException{
        if(reader.getEventType() == XMLStreamReader.START_ELEMENT){
            reader.next();
        }
        if(reader.getEventType() == XMLStreamReader.CHARACTERS){
            if(reader instanceof XMLStreamReaderEx){
                try{
                    CharSequence data = ((XMLStreamReaderEx)reader).getPCDATA();
                    if(data instanceof Base64Data){
                        Base64Data binaryData = (Base64Data)data;
                        //bstValue = binaryData.getExact();
                        buildCertificate(binaryData.getInputStream());
                        return;
                    }
                }catch(XMLStreamException ex){
                    logger.log(Level.SEVERE, LogStringsMessages.WSS_1603_ERROR_READING_STREAM(ex));
                    throw new XWSSecurityRuntimeException(LogStringsMessages.WSS_1603_ERROR_READING_STREAM(ex));
                }catch(IOException ex){
                    logger.log(Level.SEVERE, LogStringsMessages.WSS_1603_ERROR_READING_STREAM(ex));
                    throw new XWSSecurityRuntimeException(LogStringsMessages.WSS_1603_ERROR_READING_STREAM(ex));
                }
            }

            try {
                bstValue = Base64.decode(StreamUtil.getCV(reader));
                buildCertificate(new ByteArrayInputStream(bstValue));

            } catch (Base64DecodingException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1604_ERROR_DECODING_BASE_64_DATA(ex));
                throw new XWSSecurityRuntimeException(LogStringsMessages.WSS_1604_ERROR_DECODING_BASE_64_DATA(ex));
            } catch (XMLStreamException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1604_ERROR_DECODING_BASE_64_DATA(ex));
                throw new XWSSecurityRuntimeException(LogStringsMessages.WSS_1604_ERROR_DECODING_BASE_64_DATA(ex));
            }
        }else{
             logger.log(Level.SEVERE, LogStringsMessages.WSS_1603_ERROR_READING_STREAM(null));
             throw new XWSSecurityRuntimeException(LogStringsMessages.WSS_1603_ERROR_READING_STREAM(null));
        }

        if(reader.getEventType() != XMLStreamConstants.END_ELEMENT){
            reader.next();
        }        //else it is end of BST.
    }


    private void buildCertificate(InputStream certValue){
        try {
            CertificateFactory certFact;
            certFact = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) certFact.generateCertificate(certValue);
        } catch (CertificateException ex) {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1605_ERROR_GENERATING_CERTIFICATE(ex));
            throw new XWSSecurityRuntimeException(LogStringsMessages.WSS_1605_ERROR_GENERATING_CERTIFICATE(ex));
        }
    }
}
