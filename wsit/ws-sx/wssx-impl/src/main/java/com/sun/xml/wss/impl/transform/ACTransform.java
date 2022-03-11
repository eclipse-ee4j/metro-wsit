/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * ACTranform.java
 *
 * Created on March 16, 2005, 2:14 PM
 */

package com.sun.xml.wss.impl.transform;

import com.sun.xml.wss.impl.misc.UnsyncByteArrayOutputStream;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.dsig.TransformService;

import com.sun.xml.wss.impl.c14n.Canonicalizer;
import com.sun.xml.wss.impl.c14n.CanonicalizerFactory;
import com.sun.xml.wss.impl.c14n.MimeHeaderCanonicalizer;
import com.sun.xml.wss.impl.dsig.AttachmentData;
import com.sun.xml.wss.logging.impl.dsig.LogStringsMessages;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import jakarta.xml.soap.AttachmentPart;

/**
 *
 * @author  K.Venugopal@sun.com
 */
public class ACTransform extends TransformService {
    private static Logger logger = Logger.getLogger(LogDomainConstants.IMPL_SIGNATURE_DOMAIN,
            LogDomainConstants.IMPL_SIGNATURE_DOMAIN_BUNDLE);
    /** Creates a new instance of ACTranform */
    public ACTransform() {
    }

    @Override
    public void init(javax.xml.crypto.dsig.spec.TransformParameterSpec transformParameterSpec) {
    }

    @Override
    public void init(javax.xml.crypto.XMLStructure xMLStructure, javax.xml.crypto.XMLCryptoContext xMLCryptoContext) {
    }

    @Override
    public java.security.spec.AlgorithmParameterSpec getParameterSpec() {
        return null;
    }


    @Override
    public void marshalParams(javax.xml.crypto.XMLStructure xMLStructure, javax.xml.crypto.XMLCryptoContext xMLCryptoContext) throws javax.xml.crypto.MarshalException {
    }

    /*
    private Data canonicalize(OctetStreamData octetData,javax.xml.crypto.XMLCryptoContext xMLCryptoContext) throws Exception {
       Vector mimeHeaders = (Vector)xMLCryptoContext.getProperty(MessageConstants.ATTACHMENT_MIME_HEADERS);
        InputStream os = octetData.getOctetStream();
        //Revisit ::
        // rf. RFC822
        MimeHeaderCanonicalizer mHCanonicalizer = CanonicalizerFactory.getMimeHeaderCanonicalizer("US-ASCII");
        byte[] outputHeaderBytes = mHCanonicalizer._canonicalize(mimeHeaders);
        Canonicalizer canonicalizer =  CanonicalizerFactory.getCanonicalizer(octetData.getMimeType());
        InputStream is = canonicalizer.canonicalize(os);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write(outputHeaderBytes);
        int len=0;
        byte [] data= null;
        try{
            len = is.read(data);
        } catch (IOException e) {
            // log me
            throw new XWSSecurityException(e);
        }

        while(len > 0){
            try {
                byteStream.write(data);
                len = is.read(data);
            } catch (IOException e) {
                // log me
                throw new XWSSecurityException(e);
            }
        }
        return new OctetStreamData(new ByteArrayInputStream(byteStream.toByteArray()));
    }
     */

    private Data canonicalize(AttachmentData attachmentData,OutputStream outputStream) throws javax.xml.crypto.dsig.TransformException  {
        try{
            AttachmentPart attachment = attachmentData.getAttachmentPart();
            Iterator mimeHeaders = attachment.getAllMimeHeaders();
            //Revisit ::
            // rf. RFC822
            MimeHeaderCanonicalizer mHCanonicalizer = CanonicalizerFactory.getMimeHeaderCanonicalizer("US-ASCII");
            byte[] outputHeaderBytes = mHCanonicalizer._canonicalize(mimeHeaders);
            OutputStream byteStream = new UnsyncByteArrayOutputStream();
            attachment.getDataHandler().writeTo(byteStream);
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(((ByteArrayOutputStream)byteStream).toByteArray());
            byteStream.close();
            if(outputStream == null){
                byteStream = new ByteArrayOutputStream();
            }else{
                byteStream = outputStream;
            }
            byteStream.write(outputHeaderBytes);
            Canonicalizer canonicalizer =  CanonicalizerFactory.getCanonicalizer(attachment.getContentType());
            InputStream is = canonicalizer.canonicalize(byteInputStream,byteStream);
            if(is != null)  return new OctetStreamData(is);
            return null;
        }catch(javax.xml.crypto.dsig.TransformException te){
            logger.log(Level.SEVERE,LogStringsMessages.WSS_1319_ACO_TRANSFORM_ERROR(),te);
            throw te;
        }catch(Exception ex){
            logger.log(Level.SEVERE,LogStringsMessages.WSS_1319_ACO_TRANSFORM_ERROR(),ex);
            throw new javax.xml.crypto.dsig.TransformException(ex.getMessage());
        }
    }

    @Override
    public boolean isFeatureSupported(String str) {
        return false;
    }

    @Override
    public javax.xml.crypto.Data transform(javax.xml.crypto.Data data, javax.xml.crypto.XMLCryptoContext xMLCryptoContext) throws javax.xml.crypto.dsig.TransformException {
        if(data instanceof AttachmentData){
            try{
                return  canonicalize((AttachmentData)data, null);
            }catch(javax.xml.crypto.dsig.TransformException tex) {
                logger.log(Level.SEVERE,LogStringsMessages.WSS_1319_ACO_TRANSFORM_ERROR(),tex);
                throw tex;
            }catch(Exception ex){
                logger.log(Level.SEVERE,LogStringsMessages.WSS_1319_ACO_TRANSFORM_ERROR(),ex);
                throw new RuntimeException(ex);
            }
        }else{
            //TODO::
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public javax.xml.crypto.Data transform(javax.xml.crypto.Data data, javax.xml.crypto.XMLCryptoContext xMLCryptoContext, java.io.OutputStream outputStream) throws javax.xml.crypto.dsig.TransformException {
        if(data instanceof AttachmentData){
            return  canonicalize((AttachmentData)data, outputStream);
        }else{
            //TODO::
            throw new UnsupportedOperationException();
        }
    }



}
