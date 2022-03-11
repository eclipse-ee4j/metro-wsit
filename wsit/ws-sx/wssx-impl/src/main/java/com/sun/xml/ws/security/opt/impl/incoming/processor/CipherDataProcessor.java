/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.incoming.processor;

import org.apache.xml.security.exceptions.Base64DecodingException;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.opt.impl.util.SOAPUtil;
import com.sun.xml.ws.security.opt.impl.util.StreamUtil;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.misc.Base64;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jvnet.staxex.Base64Data;
import org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.wss.logging.impl.opt.crypto.LogStringsMessages;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class CipherDataProcessor {

    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_CRYPTO_DOMAIN,
            LogDomainConstants.IMPL_OPT_CRYPTO_DOMAIN_BUNDLE);

    private static String CIPHER_VALUE = "CipherValue".intern();
    private static String CIPHER_REFERENCE = "CipherReference".intern();
    private static String TRANSFORMS = "Transforms".intern();
    private static String TRANSFORM = "Transform".intern();
    private Base64Data bd = null;
    private byte[] cipherValue;
    private JAXBFilterProcessingContext pc = null;
    boolean hasCipherReference = false;
    String attachmentContentId = null;
    String attachmentContentType = null;
    /** Creates a new instance of CipherDataProcessor */
    public CipherDataProcessor(JAXBFilterProcessingContext pc) {
        this.pc = pc;
    }
    /**
     * processes the cipher data and sets the cipher value
     * @param reader XMLStreamReader
     */
    public void process(XMLStreamReader reader) {
        try {
            if(StreamUtil.moveToNextElement(reader)){
                if(reader.getLocalName() == CIPHER_VALUE){
                    if(reader instanceof XMLStreamReaderEx){
                        reader.next();
                        if(reader.getEventType() == XMLStreamReader.CHARACTERS){
                            CharSequence charSeq = ((XMLStreamReaderEx)reader).getPCDATA();
                            if(charSeq instanceof Base64Data){
                                bd = (Base64Data) charSeq;
                            }else{
                                try {
                                    cipherValue = Base64.decode(StreamUtil.getCV((XMLStreamReaderEx)reader));
                                } catch (Base64DecodingException ex) {
                                    logger.log(Level.SEVERE, LogStringsMessages.WSS_1922_ERROR_DECODING_CIPHERVAL(ex),ex);
                                    throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_FAILED_CHECK, LogStringsMessages.WSS_1922_ERROR_DECODING_CIPHERVAL(ex), ex);
                                }
                            }
                        }
                    }else{

                        try {
                            //cipherValue = Base64.decode(reader.getElementText());
                            cipherValue = Base64.decode(StreamUtil.getCV(reader));
                        } catch (Base64DecodingException ex) {
                            logger.log(Level.SEVERE, LogStringsMessages.WSS_1922_ERROR_DECODING_CIPHERVAL(ex),ex);
                            throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_FAILED_CHECK, LogStringsMessages.WSS_1922_ERROR_DECODING_CIPHERVAL(ex), ex);
                        }

                    }
                    //reader.next();//move to END OF CIPHER VALUE
                    reader.next();//move to END OF CIPHER DATA
                    reader.next();//move to NEXT ELEMENT
                    return;
                } else if(reader.getLocalName() == CIPHER_REFERENCE){
                    hasCipherReference = true;
                    String attachUri = reader.getAttributeValue(null,"URI");
                    if (attachUri.startsWith("cid:")) {
                        attachUri = attachUri.substring("cid:".length());
                    }
                    String algorithm = null;
                    if(StreamUtil.moveToNextElement(reader)){
                        if(reader.getLocalName() == TRANSFORMS){
                            if(StreamUtil.moveToNextElement(reader)){
                                if(reader.getLocalName() == TRANSFORM){
                                    algorithm = reader.getAttributeValue(null,"Algorithm");
                                    reader.next(); // Move to end of Transform
                                }
                            }
                            reader.next(); // Move to end of Transforms
                        }
                    }
                    if(algorithm != null && algorithm.equals(MessageConstants.SWA11_ATTACHMENT_CIPHERTEXT_TRANSFORM)){
                        AttachmentSet attachmentSet = pc.getSecurityContext().getAttachmentSet();
                        Attachment as = attachmentSet.get(attachUri);//sm.getAttachment(attachUri);
                        cipherValue = as.asByteArray();
                        attachmentContentId = as.getContentId();
                        attachmentContentType = as.getContentType();
                        reader.next(); // Move to end of CipherReference
                        reader.next(); // Move to NEXT ELEMENT
                        return;
                    } else {
                        logger.log(Level.SEVERE, LogStringsMessages.WSS_1928_UNRECOGNIZED_CIPHERTEXT_TRANSFORM(algorithm));
                        throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_FAILED_CHECK, LogStringsMessages.WSS_1928_UNRECOGNIZED_CIPHERTEXT_TRANSFORM(algorithm),
                                new XWSSecurityException(LogStringsMessages.WSS_1928_UNRECOGNIZED_CIPHERTEXT_TRANSFORM(algorithm)));
                    }
                }
            }
            reader.next();
        } catch (XMLStreamException ex) {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1923_ERROR_PROCESSING_CIPHERVAL(ex),ex);
            throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_FAILED_CHECK,LogStringsMessages.WSS_1923_ERROR_PROCESSING_CIPHERVAL(ex),ex);
        }
        logger.log(Level.SEVERE, LogStringsMessages.WSS_1923_ERROR_PROCESSING_CIPHERVAL("unexpected element:"+reader.getLocalName()));
        throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_FAILED_CHECK,LogStringsMessages.WSS_1923_ERROR_PROCESSING_CIPHERVAL("unexpected element:"+reader.getLocalName()), null);

    }
    /**
     *
     * @return InputStream
     */
    public InputStream readAsStream() throws XWSSecurityException{

        if(bd != null ){
            try {
                return bd.getInputStream();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1923_ERROR_PROCESSING_CIPHERVAL(ex),ex);
                throw new XWSSecurityException(LogStringsMessages.WSS_1923_ERROR_PROCESSING_CIPHERVAL(ex));
            }
        }
        if(cipherValue != null){
            return new ByteArrayInputStream(cipherValue);
        }
        logger.log(Level.SEVERE, LogStringsMessages.WSS_1924_CIPHERVAL_MISSINGIN_CIPHERDATA());
        throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_FAILED_CHECK,LogStringsMessages.WSS_1924_CIPHERVAL_MISSINGIN_CIPHERDATA(),null);
    }

    /**
     *
     * @return byte[] cipherValue
     */
    public byte[] readAsBytes() {
        if(cipherValue != null){
            return cipherValue;
        }
        if(bd != null ){
            cipherValue = bd.getExact();
            return cipherValue;
        }
        logger.log(Level.SEVERE, LogStringsMessages.WSS_1924_CIPHERVAL_MISSINGIN_CIPHERDATA());
        throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_FAILED_CHECK,LogStringsMessages.WSS_1924_CIPHERVAL_MISSINGIN_CIPHERDATA(),null);
    }

    public boolean hasCipherReference(){
        return hasCipherReference;
    }

    public String getAttachmentContentId(){
        return attachmentContentId;
    }

    public String getAttachmentContentType(){
        return attachmentContentType;
    }
}
