/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.crypto.jaxb;

import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.opt.crypto.dsig.Signature;
import com.sun.xml.ws.security.opt.crypto.dsig.Transforms;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.DSAKeyValue;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyName;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyValue;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.PGPData;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.RSAKeyValue;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.RetrievalMethod;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.SPKIData;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.X509Data;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.X509IssuerSerial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBContext;

import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

/**
 *
 * @author Abhijit Das
 */
public class JAXBSignatureFactory extends javax.xml.crypto.dsig.XMLSignatureFactory {

    private static JAXBSignatureFactory instance = null;

    /** Creates a new instance of JAXBSignatureFactory */
    private JAXBSignatureFactory() {
    }

    public static JAXBSignatureFactory newInstance() {
        if ( instance == null )
            instance = new JAXBSignatureFactory();

        return instance;
    }

    public JAXBContext getJAXBContext() {
        return JAXBUtil.getJAXBContext();
    }

    /**
     * Creates an XMLSignature and initializes it with the contents of
     * the specified SignedInfo and KeyInfo objects.
     *
     * @param signedInfo - signed info
     * @param keyInfo - key info (may be null)
     *
     * @return XMLSignature
     */
    @Override
    public XMLSignature newXMLSignature(SignedInfo signedInfo, KeyInfo keyInfo) {
        if ( signedInfo == null ) {
            throw new NullPointerException("SignedInfo can not be null");
        }
        Signature signature = new Signature();
        signature.setKeyInfo((com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo) keyInfo);
        signature.setSignedInfo((com.sun.xml.ws.security.opt.crypto.dsig.SignedInfo) signedInfo);
        return signature;
    }

    /**
     * Creates an XMLSignature and initializes it with the specified parameters.
     *
     * @param signedInfo - the Signed Info
     * @param keyInfo - ths key info (may be null)
     * @param objects - a list of XMLObjects (may be null)
     * @param id - the id (may be null)
     * @param type - the type (may be null)
     *
     * @return XMLSignature
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public XMLSignature newXMLSignature(SignedInfo signedInfo, KeyInfo keyInfo, List objects, String id, String type) {
        Signature signature = (Signature) newXMLSignature(signedInfo, keyInfo);
        signature.setId(id);
        signature.setType(type);
        signature.setObjects(objects);
        return signature;
    }

    /**
     *
     * Creates a Reference with the specified URI and digest method.
     *
     * @return Reference
     *
     */
    @Override
    public Reference newReference(String uri, DigestMethod digestMethod) {
        if ( digestMethod == null ) {
            throw new NullPointerException("Digest method can not be null");
        }
        com.sun.xml.ws.security.opt.crypto.dsig.Reference ref = new com.sun.xml.ws.security.opt.crypto.dsig.Reference();
        ref.setURI(uri);
        ref.setDigestMethod((com.sun.xml.ws.security.opt.crypto.dsig.DigestMethod) digestMethod);
        return ref;
    }

    /**
     * Creates a Reference with the specified parameters
     *
     * @return Reference
     */
    @Override
    @SuppressWarnings("unchecked")
    public Reference newReference(String uri, DigestMethod digestMethod, List transforms, String type, String id) {
        com.sun.xml.ws.security.opt.crypto.dsig.Reference ref = (com.sun.xml.ws.security.opt.crypto.dsig.Reference) newReference(uri, digestMethod);
        ref.setType(type);
        ref.setId(id);

        Transforms transfrormList = new Transforms();
        transfrormList.setTransform(transforms);

        ref.setTransforms(transfrormList);
        return ref;
    }

    /**
     *
     * Creates a Reference with the specified parameters
     *
     * @return Reference
     */
    @Override
    public Reference newReference(String uri, DigestMethod digestMethod, List transforms, String type, String id, byte[] digestValue) {
        if ( digestMethod == null ) {
            throw new NullPointerException("DigestMethod can not be null");
        } else if (digestValue == null ) {
            throw new NullPointerException("Digest value can not be null");
        }

        com.sun.xml.ws.security.opt.crypto.dsig.Reference ref = (com.sun.xml.ws.security.opt.crypto.dsig.Reference) newReference(uri, digestMethod, transforms, type, id);
        ref.setDigestValue(digestValue);
        return ref;
    }

    /**
     *
     * Creates a Reference with the specified parameters
     *
     * @return Reference
     */
    @Override
    public Reference newReference(String string, DigestMethod digestMethod, List list, Data data, List list0, String string0, String string1) {
        throw new UnsupportedOperationException("Not yet suported");
    }

    /**
     *
     * Creates a SignedInfo with the specified parameters
     *
     * @return SignedInfo
     */
    @Override
    @SuppressWarnings("unchecked")
    public SignedInfo newSignedInfo(CanonicalizationMethod canonicalizationMethod, SignatureMethod signatureMethod, List references) {

        if ( canonicalizationMethod == null ) {
            throw new NullPointerException("Canonicalization Method can not be null");
        } else if ( signatureMethod == null ) {
            throw new NullPointerException("Signature Method can not be null");
        } else if ( references == null || references.size() == 0 ) {
            throw new NullPointerException("References can not be null");
        }

        com.sun.xml.ws.security.opt.crypto.dsig.SignedInfo signedInfo = new com.sun.xml.ws.security.opt.crypto.dsig.SignedInfo();
        signedInfo.setCanonicalizationMethod((com.sun.xml.ws.security.opt.crypto.dsig.CanonicalizationMethod) canonicalizationMethod);
        signedInfo.setSignatureMethod((com.sun.xml.ws.security.opt.crypto.dsig.SignatureMethod) signatureMethod);
        signedInfo.setReference(references);

        return signedInfo;
    }

    /**
     *
     * Creates a SignedInfo with the specified parameters
     *
     * @return SignedInfo
     */
    @Override
    public SignedInfo newSignedInfo(CanonicalizationMethod canonicalizationMethod, SignatureMethod signatureMethod, List references, String id) {
        com.sun.xml.ws.security.opt.crypto.dsig.SignedInfo signedInfo =
                (com.sun.xml.ws.security.opt.crypto.dsig.SignedInfo) newSignedInfo(canonicalizationMethod, signatureMethod, references);
        signedInfo.setId(id);
        return signedInfo;
    }

    /**
     *
     * Creates a XMLObject with the specified parameters
     *
     * @return XMLObject
     */
    @Override
    public XMLObject newXMLObject(List content, String id, String mime, String encoding) {
        com.sun.xml.ws.security.opt.crypto.dsig.XMLObject xmlObject =
                new com.sun.xml.ws.security.opt.crypto.dsig.XMLObject();
        xmlObject.setEncoding(encoding);
        xmlObject.setMimeType(mime);
        xmlObject.setId(id);
        xmlObject.setContent(content);
        return xmlObject;
    }

    /**
     *
     * Creates a Manifest with the specified parameters
     *
     * @return Manifest
     */
    @Override
    public Manifest newManifest(List list) {
        return null;
    }

    /**
     * Creates a Manifest with the specified parameters
     * @return Manifest
     */
    @Override
    public Manifest newManifest(List list, String string) {
        return null;
    }

    /**
     * Creates a SignatureProperty with the specified parameters
     * @return SignatureProperty
     */
    @Override
    public SignatureProperty newSignatureProperty(List list, String string, String string0) {
        return null;
    }

    /**
     * Creates a SignatureProperties with the specified parameters
     * @return SignatureProperties
     */
    @Override
    public SignatureProperties newSignatureProperties(List list, String string) {
        return null;
    }

    /**
     * Creates a DigestMethod with the specified parameters
     * @return DigestMethod
     */
    @Override
    public DigestMethod newDigestMethod(String algorithm, DigestMethodParameterSpec digestMethodParameterSpec) {
        if ( algorithm == null ) {
            throw new NullPointerException("Digest algorithm can not be null");
        }
        com.sun.xml.ws.security.opt.crypto.dsig.DigestMethod digestMethod =
                new com.sun.xml.ws.security.opt.crypto.dsig.DigestMethod();
        digestMethod.setParameterSpec(digestMethodParameterSpec);
        digestMethod.setAlgorithm(algorithm);
        return digestMethod;
    }

    /**
     * Creates a SignatureMethod with the specified parameters
     * @return SignatureMethod
     */
    @Override
    public SignatureMethod newSignatureMethod(String algorithm, SignatureMethodParameterSpec signatureMethodParameterSpec) {
        if ( algorithm == null ) {
            throw new NullPointerException("Signature Method algorithm can not be null");
        }

        com.sun.xml.ws.security.opt.crypto.dsig.SignatureMethod signatureMethod =
                new com.sun.xml.ws.security.opt.crypto.dsig.SignatureMethod();
        signatureMethod.setAlgorithm(algorithm);
        if ( signatureMethodParameterSpec != null )
            signatureMethod.setParameter(signatureMethodParameterSpec);
        return signatureMethod;
    }

    /**
     * Creates a Transform with the specified parameters
     * @return Transforms
     */
    @Override
    public Transform newTransform(String algorithm, TransformParameterSpec transformParameterSpec) {
        com.sun.xml.ws.security.opt.crypto.dsig.Transform transform =
                new com.sun.xml.ws.security.opt.crypto.dsig.Transform();
        transform.setAlgorithm(algorithm);
        transform.setParameterSpec(transformParameterSpec);
        return transform;
    }

    /**
     * Creates a Transform with the specified parameters
     * @return Transform
     */
    @Override
    @SuppressWarnings("unchecked")
    public Transform newTransform(String algorithm, XMLStructure xMLStructure) {
        if ( algorithm == null ) {
            throw new NullPointerException("Algorithm can not be null");
        }
        com.sun.xml.ws.security.opt.crypto.dsig.Transform transform =
                new com.sun.xml.ws.security.opt.crypto.dsig.Transform();
        transform.setAlgorithm(algorithm);

        List content = new ArrayList();
        content.add(((JAXBStructure)xMLStructure).getJAXBElement());

        transform.setContent(content);
        return transform;
    }

    /**
     * Creates a CanonicalizationMethod with the specified parameters
     * @return CanonicalizationMethod
     */
    @Override
    public CanonicalizationMethod newCanonicalizationMethod(String algorithm, C14NMethodParameterSpec c14NMethodParameterSpec) {
        com.sun.xml.ws.security.opt.crypto.dsig.CanonicalizationMethod canonicalizationMethod =
                new com.sun.xml.ws.security.opt.crypto.dsig.CanonicalizationMethod();
        canonicalizationMethod.setAlgorithm(algorithm);
        canonicalizationMethod.setParameterSpec(c14NMethodParameterSpec);
        return canonicalizationMethod;
    }

    /**
     * Creates a CanonicalizationMethod with the specified parameters
     * @return CanonicalizationMethod
     */
    @Override
    @SuppressWarnings("unchecked")
    public CanonicalizationMethod newCanonicalizationMethod(String algorithm, XMLStructure xMLStructure) {
        com.sun.xml.ws.security.opt.crypto.dsig.CanonicalizationMethod canonicalizationMethod =
                new com.sun.xml.ws.security.opt.crypto.dsig.CanonicalizationMethod();
        canonicalizationMethod.setAlgorithm(algorithm);
        if ( xMLStructure != null ) {
            List content = new ArrayList();
            content.add(xMLStructure);
            canonicalizationMethod.setContent(content);
        }
        return canonicalizationMethod;
    }
    @SuppressWarnings("unchecked")
    public KeyInfo newKeyInfo(List content){
        com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo ki = new com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo();
        ki.setContent(content);
        return ki;
    }
    /**
     * Creates a DSAKeyValue with the specified parameters
     * @return DSAKeyValue
     */
    public DSAKeyValue newDSAKeyValue(
            byte[] p,
            byte[] q,
            byte[] g,
            byte[] y,
            byte[] j,
            byte[] seed,
            byte[] pgenCounter) {

        DSAKeyValue dsaKeyValue = new DSAKeyValue();
        dsaKeyValue.setP(p);
        dsaKeyValue.setQ(q);
        dsaKeyValue.setG(g);
        dsaKeyValue.setY(y);
        dsaKeyValue.setJ(j);
        dsaKeyValue.setSeed(seed);
        dsaKeyValue.setPgenCounter(pgenCounter);

        return dsaKeyValue;
    }

    /**
     * Creates a KeyInfo with the specified parameters
     * @return KeyInfo
     */
    @SuppressWarnings("unchecked")
    public com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo newKeyInfo (String id, List content) {
        com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo keyInfo = new com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo();
        keyInfo.setId(id);
        keyInfo.setContent(content);
        return keyInfo;
    }

    /**
     * Creates a KeyName with the specified parameters
     * @return KeyName
     */
    public KeyName newKeyName(String name) {
        KeyName keyName = new KeyName();
        keyName.setKeyName(name);
        return keyName;
    }

    /**
     * Creates a KeyValue with the specified parameters
     * @return KeyValue
     */
    @SuppressWarnings("unchecked")
    public KeyValue newKeyValue(List content) {
        KeyValue keyValue = new KeyValue();
        keyValue.setContent(content);
        return keyValue;
    }



    /**
     * Creates a PGPData with the specified parameters
     * @return PGPData
     */
    @SuppressWarnings("unchecked")
    public PGPData newPGPData(List content) {
        PGPData pgpData = new PGPData();
        pgpData.setContent(content);
        return pgpData;
    }

    /**
     * Creates a RSAKeyValue with the specified parameters
     * @return RSAKeyValue
     */
    public RSAKeyValue newRSAKeyValue(byte[] modulas, byte[] exponent) {
        RSAKeyValue rsaKeyValue = new RSAKeyValue();
        rsaKeyValue.setExponent(exponent);
        rsaKeyValue.setModulus(modulas);
        return rsaKeyValue;
    }

    /**
     * Creates a RetrievalMethod with the specified parameters
     * @return RetrievalMethod
     */
    public RetrievalMethod newRetrievalMethod(Transforms transforms, String type, String uri) {
        RetrievalMethod rm = new RetrievalMethod();
        rm.setTransforms(transforms);
        rm.setType(type);
        rm.setURI(uri);
        return rm;
    }

    /**
     * Creates a SPKIData with the specified parameters
     * @return SPKIData
     */
    @SuppressWarnings("unchecked")
    public SPKIData newSPKIData(List spkiSexpAndAny) {
        SPKIData spkiData = new SPKIData();
        spkiData.setSpkiSexpAndAny(spkiSexpAndAny);
        return spkiData;
    }

    /**
     * Creates a X509Data with the specified parameters
     * @return X509Data
     */
    @SuppressWarnings("unchecked")
    public X509Data newX509Data(List content) {
        X509Data x509Data = new X509Data();
        x509Data.setX509IssuerSerialOrX509SKIOrX509SubjectName(content);
        return x509Data;
    }

    /**
     * Creates a X509IssuerSerial with the specified parameters
     * @return X509IssuerSerial
     */
    public X509IssuerSerial newX509IssuerSerial(String issuer, BigInteger serialno) {
        X509IssuerSerial x509IssuerSerial = new X509IssuerSerial();
        x509IssuerSerial.setX509IssuerName(issuer);
        x509IssuerSerial.setX509SerialNumber(serialno);
        return x509IssuerSerial;
    }

    /**
     *
     */
    @Override
    public XMLSignature unmarshalXMLSignature(XMLValidateContext xMLValidateContext) throws MarshalException {
        return null;
    }

    /**
     *
     */
    @Override
    public XMLSignature unmarshalXMLSignature(XMLStructure xMLStructure) throws MarshalException {
        return null;
    }

    /**
     *
     */
    @Override
    public boolean isFeatureSupported(String string) {
        return false;
    }

    /**
     *
     */
    @Override
    public URIDereferencer getURIDereferencer() {
        return null;
    }

}
