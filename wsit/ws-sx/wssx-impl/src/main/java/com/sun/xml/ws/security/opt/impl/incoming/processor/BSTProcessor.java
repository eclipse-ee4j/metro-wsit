/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.incoming.processor;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import javax.xml.stream.StreamFilter;
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
public class BSTProcessor implements StreamFilter {
    
    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_DOMAIN,
            LogDomainConstants.IMPL_OPT_DOMAIN_BUNDLE);
    
    private byte [] bstValue = null;
    private X509Certificate cert = null;
    /** Creates a new instance of BSTProcessor */
    public BSTProcessor() {
    }
    
    public byte [] getValue(){
        return bstValue;
    }
    
    public X509Certificate getCertificate(){
        return cert;
    }
    /**
     * parse an incomming X509 token
     */
    @Override
    public boolean accept(XMLStreamReader reader){
        if(reader.getEventType() == XMLStreamReader.CHARACTERS){
            if(reader instanceof XMLStreamReaderEx){
                try{
                    CharSequence data = ((XMLStreamReaderEx)reader).getPCDATA();
                    if(data instanceof Base64Data){
                        Base64Data binaryData = (Base64Data)data;
                        //bstValue = binaryData.getExact();
                        buildCertificate(binaryData.getInputStream());
                        return true;
                    }
                }catch(XMLStreamException ex){
                    logger.log(Level.SEVERE, LogStringsMessages.WSS_1603_ERROR_READING_STREAM(ex),ex);
                    throw new XWSSecurityRuntimeException(LogStringsMessages.WSS_1603_ERROR_READING_STREAM(ex));
                }catch(IOException ex){
                    logger.log(Level.SEVERE, LogStringsMessages.WSS_1603_ERROR_READING_STREAM(ex),ex);
                    throw new XWSSecurityRuntimeException(LogStringsMessages.WSS_1603_ERROR_READING_STREAM(ex));
                }
            }
            
            try {
                bstValue = Base64.decode(reader.getText());
                buildCertificate(new ByteArrayInputStream(bstValue));
                
            } catch (Base64DecodingException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1604_ERROR_DECODING_BASE_64_DATA(ex),ex);
                throw new XWSSecurityRuntimeException(LogStringsMessages.WSS_1604_ERROR_DECODING_BASE_64_DATA(ex));
            }
        }
        return true;
    }
    
    /**
     * builds the certificate  from the given cert value
     * @param certValue InputStream
     */
    private void buildCertificate(InputStream certValue){
        try {
            CertificateFactory certFact;
            certFact = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) certFact.generateCertificate(certValue);
        } catch (CertificateException ex) {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1605_ERROR_GENERATING_CERTIFICATE(ex),ex);
            throw new XWSSecurityRuntimeException(LogStringsMessages.WSS_1605_ERROR_GENERATING_CERTIFICATE(ex));
        }
    }
    
}
