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
 * CanonicalizationMethod.java
 *
 * Created on January 24, 2006, 2:25 PM
 */

package com.sun.xml.ws.security.opt.crypto.dsig;

import com.sun.xml.security.core.dsig.CanonicalizationMethodType;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;
import java.util.logging.Logger;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javax.xml.crypto.Data;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import com.sun.xml.wss.logging.impl.opt.signature.LogStringsMessages;

/**
 *
 * @author Abhijit Das
 * @author K.Venugopal@sun.com
 */
@XmlRootElement(name="CanonicalizationMethod",namespace = "http://www.w3.org/2000/09/xmldsig#")
public class CanonicalizationMethod extends CanonicalizationMethodType implements javax.xml.crypto.dsig.CanonicalizationMethod {
    @XmlTransient private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN,
    LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN_BUNDLE);
    
    @XmlTransient private Exc14nCanonicalizer _exc14nCanonicalizer = new Exc14nCanonicalizer();
    @XmlTransient private AlgorithmParameterSpec _algSpec = null;
    
    /** Creates a new instance of CanonicalizationMethod */
    public CanonicalizationMethod() {
    }
    
    public void setParameterSpec(AlgorithmParameterSpec algSpec) {
        this._algSpec = algSpec;
    }
    
    @Override
    public AlgorithmParameterSpec getParameterSpec() {
        return _algSpec;
    }
    
    
    @Override
    public boolean isFeatureSupported(String string) {
        //TODO:
        return false;
    }
    
    @Override
    public Data transform(Data data, XMLCryptoContext xMLCryptoContext) throws TransformException {
        if(algorithm == CanonicalizationMethod.EXCLUSIVE){
            _exc14nCanonicalizer.init((TransformParameterSpec) _algSpec);
            _exc14nCanonicalizer.transform(data,xMLCryptoContext);
        }
        return null;
        
    }
    
    @Override
    public Data transform(Data data, XMLCryptoContext xMLCryptoContext, OutputStream outputStream) throws TransformException {
        if(algorithm == CanonicalizationMethod.EXCLUSIVE){
            _exc14nCanonicalizer.transform(data,xMLCryptoContext,outputStream);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public void setContent(List content) {
        this.content = content;
    }
}
