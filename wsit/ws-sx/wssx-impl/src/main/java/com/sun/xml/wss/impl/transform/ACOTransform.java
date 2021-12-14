/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * ACOTransform.java
 *
 * Created on March 15, 2005, 8:25 PM
 */

package com.sun.xml.wss.impl.transform;

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.dsig.TransformService;
import com.sun.xml.wss.impl.c14n.Canonicalizer;
import com.sun.xml.wss.impl.c14n.CanonicalizerFactory;
import com.sun.xml.wss.impl.dsig.AttachmentData;
import com.sun.xml.wss.logging.impl.dsig.LogStringsMessages;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.crypto.OctetStreamData;
import jakarta.xml.soap.AttachmentPart;

/**
 *
 * @author  K.venugopal@sun.com
 */
public class ACOTransform extends TransformService {
    private static Logger logger = Logger.getLogger(LogDomainConstants.IMPL_SIGNATURE_DOMAIN,
            LogDomainConstants.IMPL_SIGNATURE_DOMAIN_BUNDLE);
    
    private static final String implementedTransformURI = MessageConstants.ATTACHMENT_CONTENT_ONLY_TRANSFORM_URI;
    
    /** Creates a new instance of ACOTransform */
    public ACOTransform() {
    }
    
    @Override
    public java.security.spec.AlgorithmParameterSpec getParameterSpec() {
        return null;
        
        //Revisit.
    }
    
    @Override
    public void init(javax.xml.crypto.dsig.spec.TransformParameterSpec transformParameterSpec) {
    }
    
    @Override
    public void init(javax.xml.crypto.XMLStructure xMLStructure, javax.xml.crypto.XMLCryptoContext xMLCryptoContext) {
    }
    
    @Override
    public void marshalParams(javax.xml.crypto.XMLStructure xMLStructure, javax.xml.crypto.XMLCryptoContext xMLCryptoContext) throws javax.xml.crypto.MarshalException {
        //no-op
    }
    
    
    private  javax.xml.crypto.Data canonicalize(OctetStreamData data) {
        throw new UnsupportedOperationException();
        //Revisit ::
        /*try{
            //Raised issue that passing of input to attachment complete
            //transform  to be standardised.
            String contentType = data.getMimeType();
            InputStream ioStream = data.getOctetStream();
            Canonicalizer canonicalizer = CanonicalizerFactory.getCanonicalizer(contentType);
            InputStream canonicalizedStream =  canonicalizer.canonicalize(ioStream);
            return new OctetStreamData(canonicalizedStream);
        }catch(Exception ex){
            ex.printStackTrace();
        }*/
        
    }
    
    private javax.xml.crypto.Data canonicalize(AttachmentData data,OutputStream outputStream) throws javax.xml.crypto.dsig.TransformException{
        try{
            AttachmentPart attachment = data.getAttachmentPart();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            attachment.getDataHandler().writeTo(os);
            OutputStream byteStream = null;
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(os.toByteArray());
            if(outputStream == null){
                byteStream = new ByteArrayOutputStream();
            }else{
                byteStream = outputStream;
            }
            Canonicalizer canonicalizer =  CanonicalizerFactory.getCanonicalizer(attachment.getContentType());
            InputStream is = canonicalizer.canonicalize(byteInputStream,byteStream);
            if(is!= null) return new OctetStreamData(is);
            
            return null;
        }catch(javax.xml.crypto.dsig.TransformException te){
            logger.log(Level.SEVERE,LogStringsMessages.WSS_1318_AC_TRANSFORM_ERROR(),te);
            throw te;
        }catch(Exception ex){
            logger.log(Level.SEVERE,LogStringsMessages.WSS_1318_AC_TRANSFORM_ERROR(),ex);
            throw new javax.xml.crypto.dsig.TransformException(ex.getMessage());
        }
    }
    
    @Override
    public boolean isFeatureSupported(String str) {
        return false;
    }
    
    @Override
    public javax.xml.crypto.Data transform(javax.xml.crypto.Data data, javax.xml.crypto.XMLCryptoContext xMLCryptoContext) throws javax.xml.crypto.dsig.TransformException {
        if(data instanceof OctetStreamData){
            return canonicalize((OctetStreamData)data);
        }else if(data instanceof AttachmentData){
            ByteArrayOutputStream os = null;
            return canonicalize((AttachmentData)data,os);
        }
        return null;
    }
    
    @Override
    public javax.xml.crypto.Data transform(javax.xml.crypto.Data data, javax.xml.crypto.XMLCryptoContext xMLCryptoContext, java.io.OutputStream outputStream) throws javax.xml.crypto.dsig.TransformException {
        if(data instanceof AttachmentData){
            return canonicalize((AttachmentData)data,outputStream);
        }
        return null;
    }
    
    
}
