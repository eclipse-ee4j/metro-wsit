/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * Reference.java
 *
 * Created on January 24, 2006, 2:43 PM
 */

package com.sun.xml.ws.security.opt.crypto.dsig;

import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import com.sun.xml.security.core.dsig.TransformsType;
import com.sun.xml.ws.security.opt.crypto.dsig.internal.DigesterOutputStream;
import com.sun.xml.ws.security.opt.impl.util.StreamUtil;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.impl.opt.signature.LogStringsMessages;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;


/**
 *
 * @author Abhijit Das
 * @author K.Venugopal@sun.com
 */
@XmlRootElement(name="Reference",namespace = "http://www.w3.org/2000/09/xmldsig#")
public class Reference extends com.sun.xml.security.core.dsig.ReferenceType implements javax.xml.crypto.dsig.Reference {
    @XmlTransient private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN,
    LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN_BUNDLE);
    @XmlTransient private Data _appliedTransformData;
    //@XmlTransient private boolean _digested = false;
    @XmlTransient private MessageDigest _md;
    
    @XmlTransient private boolean _validated;
    @XmlTransient private boolean _validationStatus;
    @XmlTransient private byte [] _calcDigestValue;
    /** Creates a new instance of Reference */
    public Reference() {
    }
    
    @Override
    public byte[] getCalculatedDigestValue() {
        return _calcDigestValue;
    }
    
    @Override
    public boolean validate(XMLValidateContext xMLValidateContext) throws XMLSignatureException {
        if (xMLValidateContext == null) {
            throw new NullPointerException("validateContext cannot be null");
        }
        if (_validated) {
            return _validationStatus;
        }
        Data data = dereference(xMLValidateContext);
        _calcDigestValue = transform(data, xMLValidateContext);
        
        if(logger.isLoggable(Level.FINEST)){
            logger.log(Level.FINEST,"Calculated digest value is: "+new String(_calcDigestValue));
        }
        
        if(logger.isLoggable(Level.FINEST)){
            logger.log(Level.FINEST," Expected digest value is: "+new String(digestValue));
        }
        
        _validationStatus = Arrays.equals(digestValue, _calcDigestValue);
        _validated = true;
        return _validationStatus;
    }
    
    public void digest(XMLCryptoContext signContext)throws XMLSignatureException {
        if(this.getDigestValue() == null){
            Data data = null;
            if (_appliedTransformData == null) {
                data = dereference(signContext);
            } else {
                data = _appliedTransformData;
            }
            byte [] digest = transform(data, signContext);
            this.setDigestValue(digest);
        }
        // insert digestValue into DigestValue element
        //String encodedDV = Base64.encode(digestValue);
        
    }
    
    
    public DigesterOutputStream getDigestOutputStream() throws XMLSignatureException{
        DigesterOutputStream dos;
        try {
            String algo = StreamUtil.convertDigestAlgorithm(this.getDigestMethod().getAlgorithm());
            if(logger.isLoggable(Level.FINE)){
                logger.log(Level.FINE, "Digest Algorithm is "+ this.getDigestMethod().getAlgorithm());
                logger.log(Level.FINE, "Mapped Digest Algorithm is "+ algo);
            }
            _md = MessageDigest.getInstance(algo);
        } catch (NoSuchAlgorithmException nsae) {
            throw new XMLSignatureException(nsae);
        }
        dos = new DigesterOutputStream(_md);
        return dos;
    }
    
    private byte[] transform(Data dereferencedData,
            XMLCryptoContext context) throws XMLSignatureException {
        
        if (_md == null) {
            try {
                String algo = StreamUtil.convertDigestAlgorithm(this.getDigestMethod().getAlgorithm());
                if(logger.isLoggable(Level.FINE)){
                    logger.log(Level.FINE, "Digest Algorithm is "+ this.getDigestMethod().getAlgorithm());
                    logger.log(Level.FINE, "Mapped Digest Algorithm is "+ algo);
                }
                _md = MessageDigest.getInstance(algo);
                
            } catch (NoSuchAlgorithmException nsae) {
                logger.log(Level.SEVERE,LogStringsMessages.WSS_1760_DIGEST_INIT_ERROR(),nsae);
                throw new XMLSignatureException(nsae);
            }
        }
        _md.reset();
        DigesterOutputStream dos;
        
        //Boolean cache = (Boolean)context.getProperty("javax.xml.crypto.dsig.cacheReference");
        
        dos = new DigesterOutputStream(_md);
        OutputStream os = new UnsyncBufferedOutputStream(dos);
        Data data = dereferencedData;
        if ( transforms != null ) {
            List<Transform> transformList = transforms.getTransform();
            if ( transformList != null ) {
                for (int i = 0, size = transformList.size(); i < size; i++) {
                    Transform transform = transformList.get(i);
                    try {
                        if (i < size - 1) {
                            data = transform.transform(data, context);
                        } else {
                            data = transform.transform(data, context, os);
                        }
                    } catch (TransformException te) {
                        logger.log(Level.SEVERE,LogStringsMessages.WSS_1759_TRANSFORM_ERROR(te.getMessage()),te);
                        throw new XMLSignatureException(te);
                    }
                }
            }
        }
        
        try {
            os.flush();
            dos.flush();
        } catch (IOException ex) {
            logger.log(Level.SEVERE,LogStringsMessages.WSS_1761_TRANSFORM_IO_ERROR(),ex);
            throw new XMLSignatureException(ex);
        }
				
        return dos.getDigestValue();
    }
    
    private Data dereference(XMLCryptoContext context)
            throws XMLSignatureException {
        Data data = null;
        
        // use user-specified URIDereferencer if specified; otherwise use deflt
        URIDereferencer deref = context.getURIDereferencer();
        
        try {
            data = deref.dereference(this, context);
        } catch (URIReferenceException ure) {
            throw new XMLSignatureException(ure);
        }
        return data;
    }
    
    @Override
    public Data getDereferencedData() {
        return _appliedTransformData;
    }
    
    @Override
    public InputStream getDigestInputStream() {
        throw new UnsupportedOperationException("Not supported");
    }
    
    @Override
    public boolean isFeatureSupported(String string) {
        //TODO
        return false;
    }
    
    @Override
    public DigestMethod getDigestMethod() {
        return digestMethod;
        
    }
    
    @Override
    public List getTransforms() {
        return transforms.getTransform();
    }
    
}
