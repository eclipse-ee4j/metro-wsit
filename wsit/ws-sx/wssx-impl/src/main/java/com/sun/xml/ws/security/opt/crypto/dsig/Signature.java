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
 * Signature.java 
 * 
 * Created on January 24, 2006, 3:59 PM 
 */ 

package com.sun.xml.ws.security.opt.crypto.dsig;

import com.sun.xml.ws.security.opt.impl.util.JAXBUtil; 
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo; 
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.security.InvalidKeyException; 
import java.security.Key; 
import java.security.SignatureException; 
import java.util.Arrays; 
import java.util.List; 
import java.util.logging.Logger;
import java.util.logging.Level;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javax.xml.crypto.KeySelector; 
import javax.xml.crypto.KeySelectorException; 
import javax.xml.crypto.KeySelectorResult; 
import javax.xml.crypto.MarshalException; 
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.XMLSignContext; 
import javax.xml.crypto.dsig.XMLSignatureException; 
import javax.xml.crypto.dsig.XMLValidateContext;  
import org.jvnet.staxex.XMLStreamReaderEx; 
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec; 
import javax.xml.crypto.dsig.spec.HMACParameterSpec; 
import com.sun.xml.wss.logging.impl.opt.signature.LogStringsMessages;

/** 
 * 
 * @author Abhijit Das 
 * @author K.Venugopal@sun.com 
 */



@XmlRootElement(name="Signature",namespace = "http://www.w3.org/2000/09/xmldsig#") 
public class Signature extends com.sun.xml.security.core.dsig.SignatureType implements javax.xml.crypto.dsig.XMLSignature { 
    @XmlTransient private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN,
            LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN_BUNDLE);

    @XmlTransient private XMLStreamReaderEx _streamSI = null; 
    @XmlTransient private String type = null; 
    @XmlTransient private List<XMLObject> objects = null; 
    @XmlTransient private SignatureProcessor _sp; 
    @XmlTransient private Key verificationKey = null; 
    @XmlTransient private byte []  signedInfoBytes = null;

    /** 
     * Creates a new instance of Signature 
     */

    public Signature() { 
    }

    public void setSignedInfo(XMLStreamReaderEx streamReader) { 
        this._streamSI = streamReader; 
    }

    
    public void setSignedInfo(byte [] si){ 
        this.signedInfoBytes =  si; 
    }

    public void setVerificationKey(Key key){ 
        this.verificationKey = key; 
    } 
    

    public Key getVerificationKey(){ 
        return verificationKey;

    }

    @Override
    public boolean validate(XMLValidateContext xMLValidateContext) throws XMLSignatureException {

        SignatureMethod sm; 
        if (xMLValidateContext == null) { 
            throw new NullPointerException("validateContext cannot be null"); 
        } 
        

        //List allReferences = new ArrayList(signedInfo.getReferences());

        
        SignedInfo si = getSignedInfo(); 
        List refList = si.getReferences(); 
        for (int i = 0, size = refList.size(); i < size; i++) { 
            Reference ref = (Reference) refList.get(i); 
            byte[] originalDigest = ref.getDigestValue(); 
            ref.digest(xMLValidateContext); 
            byte[] calculatedDigest = ref.getDigestValue(); 
            if ( ! Arrays.equals(originalDigest, calculatedDigest) ) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1713_SIGNATURE_VERIFICATION_EXCEPTION("Signature digest values mismatch"));
                throw new XMLSignatureException(LogStringsMessages.WSS_1713_SIGNATURE_VERIFICATION_EXCEPTION("Signature digest values mismatch"));

            } 
        } 
        si.setSignedInfo(_streamSI);

        // si.setCanonicalizedSI(signedInfoBytes);
        

        KeySelectorResult keySelectoResult = null; 
        try { 
            sm = si.getSignatureMethod(); 
            if(verificationKey == null){ 
                keySelectoResult = xMLValidateContext.getKeySelector().select(getKeyInfo(),KeySelector.Purpose.VERIFY,sm,xMLValidateContext); 
                verificationKey = keySelectoResult.getKey(); 
            } 
            if (verificationKey == null) { 
                throw new XMLSignatureException("The KeySelector"+ xMLValidateContext.getKeySelector()+ " did not " + 
                          "find the key used for signature verification"); 
            }

            if(_sp == null){ 
                _sp = new SignatureProcessor(); 
            }

            try {
                String signatureAlgo = sm.getAlgorithm();
                String algo = getRSASignatureAlgorithm(signatureAlgo);
                if (algo != null) {
                    return _sp.verifyRSASignature(verificationKey, si, getSignatureValue().getValue(), algo);
                } else if (signatureAlgo.equals(SignatureMethod.DSA_SHA1)) {
                    return _sp.verifyDSASignature(verificationKey, si, getSignatureValue().getValue());
                } else if (signatureAlgo.equals(SignatureMethod.HMAC_SHA1)) {
                    SignatureMethodParameterSpec params = (SignatureMethodParameterSpec)sm.getParameterSpec();

                    int outputLength = -1;

                    if (params != null) { 
                        if (!(params instanceof HMACParameterSpec)) { 
                            throw new XMLSignatureException ("SignatureMethodParameterSpec must be of type HMACParameterSpec");

                        } 
                        outputLength = ((HMACParameterSpec) params).getOutputLength(); 
                    } 
                    return _sp.verifyHMACSignature(verificationKey,si,getSignatureValue().getValue(), outputLength); 
                } else { 
                    throw new XMLSignatureException("Unsupported signature algorithm found"); 
                } 
            } catch (InvalidKeyException ex) { 
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1713_SIGNATURE_VERIFICATION_EXCEPTION(ex));
                throw new XMLSignatureException(LogStringsMessages.WSS_1713_SIGNATURE_VERIFICATION_EXCEPTION(ex)); 
            } catch (SignatureException ex) { 
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1713_SIGNATURE_VERIFICATION_EXCEPTION("Signature digest values mismatch"));
                throw new XMLSignatureException(LogStringsMessages.WSS_1713_SIGNATURE_VERIFICATION_EXCEPTION(ex)); 
            } 
        } catch (KeySelectorException kse) { 
            throw new XMLSignatureException("Cannot find verification key", kse); 
        } 
        //return false; 
    } 

    @Override
    public List getObjects() {
        return null; 
    }

    @Override
    public void sign(XMLSignContext xMLSignContext) throws MarshalException, XMLSignatureException {
        SignatureMethod sm; 
        if (xMLSignContext == null) { 
            throw new NullPointerException("signContext cannot be null"); 
        } 
        
        //List allReferences = new ArrayList(signedInfo.getReferences()); 
        SignedInfo si = getSignedInfo(); 
        List refList = si.getReferences(); 
        for (int i = 0, size = refList.size(); i < size; i++) { 
            Reference ref = (Reference) refList.get(i); 
            ref.digest(xMLSignContext); 
        }


        Key signingKey = null; 
        KeySelectorResult keySelectoResult = null; 
        try { 
            sm = si.getSignatureMethod(); 
            keySelectoResult = xMLSignContext.getKeySelector().select(getKeyInfo(),KeySelector.Purpose.SIGN,sm,xMLSignContext); 
            signingKey = keySelectoResult.getKey(); 
            if (signingKey == null) { 
                throw new XMLSignatureException("The KeySelector"+ xMLSignContext.getKeySelector()+ " did not " + 
                          "find the key used for signing"); 
            } 
        } catch (KeySelectorException kse) { 
            throw new XMLSignatureException("Cannot find signing key", kse); 
        }

        if(_sp == null){ 
            try { 
                JAXBContext jc = JAXBUtil.getJAXBContext(); 
                _sp = new SignatureProcessor(); 
                _sp.setJAXBContext(jc); 
                _sp.setCryptoContext(xMLSignContext); 
            } catch (Exception ex) { 
                throw new XMLSignatureException(ex); 
            } 
        }

        String signatureAlgo = sm.getAlgorithm(); 
        //SignatureValue sv=getSignatureValue(); 
        String algo = getRSASignatureAlgorithm(signatureAlgo);
        if(algo != null){
             try {
                com.sun.xml.ws.security.opt.crypto.dsig.SignatureValue sigValue = new com.sun.xml.ws.security.opt.crypto.dsig.SignatureValue();
                sigValue.setValue(_sp.performRSASign(signingKey,signedInfo,algo));
                setSignatureValue(sigValue);
                //((SignatureValueType)getSignatureValue()).setValue(_sp.performRSASign(signingKey,signedInfo));
                //((SignatureValueType)sv).setValue(_sp.performRSASign(signingKey,signedInfo));
            } catch (InvalidKeyException ex) {
                throw new XMLSignatureException(ex);
            }
        } else if(signatureAlgo.equals(SignatureMethod.DSA_SHA1)){
            try { 
                com.sun.xml.ws.security.opt.crypto.dsig.SignatureValue sigValue = new com.sun.xml.ws.security.opt.crypto.dsig.SignatureValue(); 
                sigValue.setValue(_sp.performDSASign(signingKey,signedInfo)); 
                setSignatureValue(sigValue); 
                
                //((SignatureValueType)sv).setValue(_sp.performDSASign(signingKey,signedInfo));

            } catch (InvalidKeyException ex) { 
                throw new XMLSignatureException(ex); 
            }

        } else if ( signatureAlgo.equals(SignatureMethod.HMAC_SHA1)) { 
            SignatureMethodParameterSpec params = (SignatureMethodParameterSpec)sm.getParameterSpec(); 
            int outputLength = -1; 
            if (params != null) { 
                if (!(params instanceof HMACParameterSpec)) { 
                    throw new XMLSignatureException 
                              ("SignatureMethodParameterSpec must be of type HMACParameterSpec"); 
                } 
                outputLength = ((HMACParameterSpec) params).getOutputLength(); 
            }

            try{ 
                com.sun.xml.ws.security.opt.crypto.dsig.SignatureValue sigValue = new com.sun.xml.ws.security.opt.crypto.dsig.SignatureValue(); 
                sigValue.setValue(_sp.performHMACSign(signingKey,signedInfo, outputLength)); 
                setSignatureValue(sigValue); 
            } catch (InvalidKeyException ex) { 
                throw new XMLSignatureException(ex); 
            } 
        } else {
             throw new XMLSignatureException("Unsupported signature algorithm found");
        }
    }

    @Override
    public KeySelectorResult getKeySelectorResult() {
        return null; 
    }

    

    @Override
    public boolean isFeatureSupported(String string) {
        return false; 
    } 
    
    @Override
    public SignatureValue getSignatureValue() {
        return signatureValue;
    }


    @Override
    public SignedInfo getSignedInfo() {
        return this.signedInfo; 
    }


    @Override
    public KeyInfo getKeyInfo() {
        return keyInfo; 
    }

    
    public void setObjects(List<XMLObject> objects) { 
        this.objects = objects; 
    }


    public String getType() { 
        return type; 
    }


    public void setType(String type) { 
        this.type = type; 
    }

    private String getRSASignatureAlgorithm(String signatureAlgo) {
        if (signatureAlgo.equals(SignatureMethod.RSA_SHA1)) {
            return MessageConstants.RSA_SHA1;
        } else if (signatureAlgo.equals(MessageConstants.RSA_SHA256_SIGMETHOD)) {
            return MessageConstants.RSA_SHA256;
        } else if (signatureAlgo.equals(MessageConstants.RSA_SHA384_SIGMETHOD)) {
            return MessageConstants.RSA_SHA384;
        } else if (signatureAlgo.equals(MessageConstants.RSA_SHA512_SIGMETHOD)) {
            return MessageConstants.RSA_SHA512;
        }
        return null;
    }
}

