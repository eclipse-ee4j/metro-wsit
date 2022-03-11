/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.crypto.dsig;

import org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.ws.security.opt.crypto.JAXBData;
import com.sun.xml.ws.security.opt.crypto.StreamWriterData;
import com.sun.xml.wss.impl.c14n.StAXEXC14nCanonicalizerImpl;
import com.sun.xml.wss.impl.misc.UnsyncByteArrayOutputStream;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import com.sun.xml.ws.security.opt.impl.dsig.ExcC14NParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.wss.logging.impl.opt.signature.LogStringsMessages;
import java.util.logging.Level;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class Exc14nCanonicalizer extends TransformService {

    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN,
            LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN_BUNDLE);


    StAXEXC14nCanonicalizerImpl _canonicalizer = new StAXEXC14nCanonicalizerImpl();
    UnsyncByteArrayOutputStream baos = new UnsyncByteArrayOutputStream();
    TransformParameterSpec _transformParameterSpec;
    /** Creates a new instance of Exc14nCanonicalizer */
    public Exc14nCanonicalizer() {
    }

    @Override
    public void init(TransformParameterSpec transformParameterSpec) {
        _transformParameterSpec = transformParameterSpec;
    }

    @Override
    public void marshalParams(XMLStructure xMLStructure, XMLCryptoContext xMLCryptoContext) throws MarshalException {
    }

    @Override
    public void init(XMLStructure xMLStructure, XMLCryptoContext xMLCryptoContext) {
    }

    @Override
    public AlgorithmParameterSpec getParameterSpec() {
        return _transformParameterSpec;
    }

    @Override
    public Data transform(Data data, XMLCryptoContext xMLCryptoContext) throws TransformException {
        _canonicalizer.setStream(baos);
        _canonicalizer.reset();

        if(data instanceof StreamWriterData ){
            StreamWriterData swd = (StreamWriterData)data;
            NamespaceContextEx nc  = swd.getNamespaceContext();
            Iterator<NamespaceContextEx.Binding> itr = nc.iterator();

            while(itr.hasNext()){
                final NamespaceContextEx.Binding nd = itr.next();
                _canonicalizer.writeNamespace(nd.getPrefix(),nd.getNamespaceURI());
            }
            try {
                ExcC14NParameterSpec spec = (ExcC14NParameterSpec)_transformParameterSpec;
                if(spec != null){
                    _canonicalizer.setInclusivePrefixList(spec.getPrefixList());
                }
                swd.write(_canonicalizer);
                _canonicalizer.flush();
            } catch (XMLStreamException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1759_TRANSFORM_ERROR(ex.getMessage()),ex);
                throw new TransformException(ex);
            }


            return new OctetStreamData(new ByteArrayInputStream(baos.getBytes(),0,baos.getLength()));
        }
        throw new UnsupportedOperationException("Data type"+data+" not yet supported");
    }

    @Override
    public Data transform(Data data, XMLCryptoContext xMLCryptoContext, OutputStream outputStream) throws TransformException {
        _canonicalizer.setStream(outputStream);
        _canonicalizer.reset();

        if(data instanceof StreamWriterData){
            StreamWriterData swd = (StreamWriterData)data;
            NamespaceContextEx nc  = swd.getNamespaceContext();
            Iterator<NamespaceContextEx.Binding> itr = nc.iterator();

            while(itr.hasNext()){
                final NamespaceContextEx.Binding nd = itr.next();
                _canonicalizer.writeNamespace(nd.getPrefix(),nd.getNamespaceURI());
            }
            try {
                ExcC14NParameterSpec spec = (ExcC14NParameterSpec)_transformParameterSpec;
                if(spec != null){
                    _canonicalizer.setInclusivePrefixList(spec.getPrefixList());
                }
                swd.write(_canonicalizer);
                _canonicalizer.flush();
            } catch (XMLStreamException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1759_TRANSFORM_ERROR(ex.getMessage()),ex);
                throw new TransformException(ex);
            }

            return null;
        }else if(data instanceof JAXBData){
            JAXBData jd =(JAXBData)data;
            NamespaceContextEx nc  = jd.getNamespaceContext();
            Iterator<NamespaceContextEx.Binding> itr = nc.iterator();

            while(itr.hasNext()){
                final NamespaceContextEx.Binding nd = itr.next();
                _canonicalizer.writeNamespace(nd.getPrefix(),nd.getNamespaceURI());
            }

            try {
                ExcC14NParameterSpec spec = (ExcC14NParameterSpec)_transformParameterSpec;
                if(spec != null){
                    _canonicalizer.setInclusivePrefixList(spec.getPrefixList());
                }
                jd.writeTo(_canonicalizer);
                _canonicalizer.flush();
            } catch (XWSSecurityException ex) {
                throw new TransformException(ex);
            }

            return null;
        }
        throw new UnsupportedOperationException("Data type "+data+" not yet supported");
    }

    @Override
    public boolean isFeatureSupported(String string) {
        return true;
    }


}
