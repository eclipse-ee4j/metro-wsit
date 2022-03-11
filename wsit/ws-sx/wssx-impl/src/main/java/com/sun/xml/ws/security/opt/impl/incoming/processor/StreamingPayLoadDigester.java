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

import com.sun.xml.ws.security.opt.crypto.dsig.internal.DigesterOutputStream;
import com.sun.xml.ws.security.opt.impl.util.StreamUtil;
import com.sun.xml.wss.impl.c14n.StAXEXC14nCanonicalizerImpl;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jakarta.xml.ws.WebServiceException;
import com.sun.xml.wss.logging.impl.opt.signature.LogStringsMessages;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class StreamingPayLoadDigester implements StreamFilter{

    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN,
            LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN_BUNDLE);

    private XMLStreamReader reader = null;
    private Reference ref = null;
    private StAXEXC14nCanonicalizerImpl canonicalizer = null;
    private int index = 0;
    private boolean payLoad = false;
    private boolean digestDone = false;
    /** Creates a new instance of StreamingPayLoadDigester */
    public StreamingPayLoadDigester(Reference ref,XMLStreamReader reader,StAXEXC14nCanonicalizerImpl canonicalizer,boolean payLoad) {
        this.ref = ref;
        this.reader = reader;
        this.canonicalizer = canonicalizer;
        this.payLoad = payLoad;
    }
    /**
     * calculates the digest of the payload in a streaming fashion
     * @param xMLStreamReader XMLStreamReader
     */
    @Override
    public boolean accept(XMLStreamReader xMLStreamReader) {
        try {
            if(!digestDone){
                StreamUtil.writeCurrentEvent(xMLStreamReader,canonicalizer);
                if(reader.getEventType() == XMLStreamReader.START_ELEMENT){
                    index++;
                }else if(reader.getEventType() == XMLStreamReader.END_ELEMENT ){
                    index --;
                    //|| (reader.getLocalName() == SOAP_BODY_LNAME && (reader.getNamespaceURI() == SOAP_1_1_NS || reader.getNamespaceURI() == SOAP_1_2_NS ))
                    if( index == 0 ){
                        assert (ref != null);
                        byte [] originalDigest = ref.getDigestValue();
                        if(logger.isLoggable(Level.FINEST)){
                            logger.log(Level.FINEST, LogStringsMessages.WSS_1763_ACTUAL_DEGEST_VALUE(new String(originalDigest)));
                        }
                        canonicalizer.writeEndDocument();
                        digestDone = true;
                        if(canonicalizer.getOutputStream() instanceof DigesterOutputStream){
                            byte [] calculatedDigest = ((DigesterOutputStream)canonicalizer.getOutputStream()).getDigestValue();
                            if(logger.isLoggable(Level.FINEST)){
                                logger.log(Level.FINEST,LogStringsMessages.WSS_1762_CALCULATED_DIGEST_VALUE(new String(calculatedDigest)));
                            }
                            if (!Arrays.equals(originalDigest, calculatedDigest)) {
                                XMLSignatureException xe = new XMLSignatureException(LogStringsMessages.WSS_1717_ERROR_PAYLOAD_VERIFICATION());
                                logger.log(Level.SEVERE, LogStringsMessages.WSS_1717_ERROR_PAYLOAD_VERIFICATION(),xe);
                                throw new WebServiceException(xe);
                            }else{
                                if(logger.isLoggable(Level.FINEST)){
                                    if(!payLoad){
                                        logger.log(Level.FINEST,"Digest verification of Body was successful");
                                    }else{
                                        logger.log(Level.FINEST,"Digest verification of PayLoad was successful");
                                    }
                                }
                            }
                        }else if(canonicalizer.getOutputStream() instanceof ByteArrayOutputStream){
                            byte[] canonicalizedData = ((ByteArrayOutputStream)canonicalizer.getOutputStream()).toByteArray();
                            byte [] calculatedDigest = null;
                            MessageDigest  md = null;
                            String algo= null;
                            try {
                                algo = StreamUtil.convertDigestAlgorithm(ref.getDigestMethod().getAlgorithm());
                                md = MessageDigest.getInstance(algo);

                            } catch (NoSuchAlgorithmException nsae) {
                                logger.log(Level.SEVERE, LogStringsMessages.WSS_1705_INVALID_DIGEST_ALGORITHM(algo),nsae);
                                throw new WebServiceException(nsae);
                            }
                            calculatedDigest = md.digest(canonicalizedData);
                            if(logger.isLoggable(Level.FINEST)){
                                logger.log(Level.FINEST,LogStringsMessages.WSS_1762_CALCULATED_DIGEST_VALUE(new String(calculatedDigest)));
                                logger.log(Level.FINEST, LogStringsMessages.WSS_1764_CANONICALIZED_PAYLOAD_VALUE(new String(canonicalizedData)));
                            }
                            if (!Arrays.equals(originalDigest, calculatedDigest)) {
                                XMLSignatureException xe = new XMLSignatureException(LogStringsMessages.WSS_1717_ERROR_PAYLOAD_VERIFICATION());
                                logger.log(Level.SEVERE, LogStringsMessages.WSS_1717_ERROR_PAYLOAD_VERIFICATION(),xe);
                                throw new WebServiceException(xe);
                            }else{
                                if(logger.isLoggable(Level.FINEST)){
                                    if(!payLoad){
                                        logger.log(Level.FINEST,"Digest verification of Body was successful");
                                    }else{
                                        logger.log(Level.FINEST,"Digest verification of PayLoad was successful");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (XMLStreamException ex) {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1717_ERROR_PAYLOAD_VERIFICATION(),ex);
            throw new WebServiceException(ex);
        }
        return true;
    }
}
