/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.crypto.dsig;

import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.security.opt.impl.crypto.AttachmentData;
import com.sun.xml.wss.impl.c14n.Canonicalizer;
import com.sun.xml.wss.impl.c14n.CanonicalizerFactory;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class ACOTransform extends TransformService {
    
    /** Creates a new instance of ACOTransform */
    public ACOTransform() {
    }

    @Override
    public void init(TransformParameterSpec params) {

    }

    @Override
    public void marshalParams(XMLStructure parent, XMLCryptoContext context) throws MarshalException {

    }

    @Override
    public void init(XMLStructure parent, XMLCryptoContext context) {

    }

    @Override
    public AlgorithmParameterSpec getParameterSpec() {
        return null;
    }

    @Override
    public Data transform(Data data, XMLCryptoContext context) throws TransformException {
        if (data instanceof AttachmentData) {
            ByteArrayOutputStream os = null;
            return canonicalize((AttachmentData) data, os);
        }
        return null;
    }

    @Override
    public Data transform(Data data, XMLCryptoContext context, OutputStream os) throws TransformException {
        if (data instanceof AttachmentData) {
            return canonicalize((AttachmentData) data, os);
        }
        return null;
    }

    @Override
    public boolean isFeatureSupported(String feature) {
        return false;
    }

    private Data canonicalize(AttachmentData attachmentData, OutputStream os) throws TransformException {
        try{
        Attachment attachment = attachmentData.getAttachment();
        InputStream is = attachment.asInputStream();
        OutputStream byteStream = null;
        if (os == null) {
            byteStream = new ByteArrayOutputStream();
        } else {
            byteStream = os;
        }
        Canonicalizer canonicalizer =  CanonicalizerFactory.getCanonicalizer(attachment.getContentType());
        InputStream resultIs = canonicalizer.canonicalize(is,byteStream);
        if(resultIs!= null) return new OctetStreamData(resultIs);
        return null;
        }catch(Exception ex){
            throw new TransformException(ex.getMessage());
        }
    }
}
