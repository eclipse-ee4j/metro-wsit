/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.saml.assertion.saml11.jaxb20;

//import com.sun.xml.wss.impl.dsig.DSigResolver;
import com.sun.xml.wss.core.SecurityTokenReference;
import com.sun.xml.wss.impl.dsig.WSSPolicyConsumerImpl;
import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.saml.util.SAMLUtil;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.AssertionType;
import java.util.GregorianCalendar;
import jakarta.xml.bind.JAXBElement;

import javax.xml.datatype.DatatypeFactory;

import java.lang.reflect.Constructor;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.XMLUtil;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.core.reference.X509SubjectKeyIdentifier;
import com.sun.xml.wss.impl.misc.Base64;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.AttributeStatementType;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.AuthenticationStatementType;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.AuthorizationDecisionStatementType;
import com.sun.xml.wss.saml.util.SAMLJAXBUtil;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import jakarta.xml.bind.JAXBContext;
import java.util.Set;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.ParserConfigurationException;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;
//import com.sun.xml.security.core.dsig.SignatureType;
import org.w3c.dom.NodeList;


/**
 * This object stands for <code>Assertion</code> element. An Assertion is a package
 * of information that supplies one or more <code>Statement</code> made by an
 * issuer. There are three kinds of assertions Au
 * {@code
 * [java] <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * [java] <Conditions NotBefore="2005-08-16T13:21:50.503+05:30" NotOnOrAfter="2005-08-16T15:21:50.504+05:30" xmlns="urn:oasis:names:tc:SAML:1.0:assertion"/>
 * [java] <Subject xmlns="urn:oasis:names:tc:SAML:1.0:assertion">
 * [java]     <NameIdentifier Format="urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName">CN=SAML User,OU=SU,O=SAML
 * User,L=Los Angeles,ST=CA,C=US</NameIdentifier>
 * [java]     <SubjectConfirmation>
 * [java]         <ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:sender-vouches</ConfirmationMethod>
 * [java]     </SubjectConfirmation>
 * [java] </Subject>
 * [java] <Attribute AttributeName="attribute1" AttributeNamespace="urn:com:sun:xml:wss:attribute" xmlns="urn:oasis:names:tc:SAML:1.0:assertion">
 * [java]     <AttributeValue>ATTRIBUTE1</AttributeValue>
 * [java] </Attribute>
 * }
 * thentication, Authorization
 * Decision and Attribute assertion.
 */
public class Assertion  extends com.sun.xml.wss.saml.internal.saml11.jaxb20.AssertionType implements com.sun.xml.wss.saml.Assertion {

    private Element signedAssertion = null;
    private String version = null;
    private String canonicalizationMethod = CanonicalizationMethod.EXCLUSIVE;
    //private Element processedAssertionElement = null;
    private List<Object> statementList = null;
     private JAXBContext jc;
    /**
     * XML Information Set REC
     * all namespace attributes (including those named xmlns,
     * whose [prefix] property has no value) have a namespace URI of http://www.w3.org/2000/xmlns/
     */
    public final static String XMLNS_URI = "http://www.w3.org/2000/xmlns/".intern();

    public Assertion(AssertionType assertion) {
        this.setAdvice(assertion.getAdvice());
        this.setAssertionID(assertion.getAssertionID());
        this.setConditions(assertion.getConditions());
        this.setIssueInstant(assertion.getIssueInstant());
        this.setIssuer(assertion.getIssuer());
        this.setMajorVersion(assertion.getMajorVersion());
        this.setMinorVersion(assertion.getMinorVersion());
        this.setSignature(assertion.getSignature());
        this.setStatement(assertion.getStatementOrSubjectStatementOrAuthenticationStatement());
    }

    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    @Override
    public String getVersion(){
        return this.version;
    }

    @Override
    public void setVersion(String version){
        this.version = version;
    }

    @Override
    public String getID(){
        return null;
    }

    @Override
    public String getSamlIssuer(){
        return getIssuer();
    }

    @Override
    public String getIssueInstance(){
        if(this.issueInstant != null){
            return this.issueInstant.toString();
        }
        return null;
    }

    @Override
    public Conditions getConditions(){
        return new Conditions(super.getConditions());
    }

    @Override
    public Advice getAdvice(){
        return new Advice(super.getAdvice());
    }

    @Override
    public Subject getSubject(){
        throw new UnsupportedOperationException("Direct call of getSubject() method on SAML1.1 assertion is not supported."+
                "So, first get the Statements of the SAML assertion and then call the getSubject() on each statement");
    }

    /**
     * sign the saml assertion (Enveloped Signature)
     * @param pubKey PublicKey to be used for Signature verification
     * @param privKey PrivateKey to be used for Signature calculation
     */

    @Override
    public Element sign(PublicKey pubKey, PrivateKey privKey) throws SAMLException {



        //Check if the signature is already calculated
        if ( signedAssertion != null) {
            return signedAssertion;
        }

        //Calculate the enveloped signature
        try {

            XMLSignatureFactory fac = WSSPolicyConsumerImpl.getInstance().getSignatureFactory();
            return sign(fac.newDigestMethod(DigestMethod.SHA1,null),SignatureMethod.RSA_SHA1, pubKey,privKey);

        } catch (Exception ex) {
            // log here
            throw new SAMLException(ex);
        }
    }

    @Override
    public Element sign(X509Certificate cert, PrivateKey privKey, boolean alwaysIncludeCert) throws SAMLException {
        //Check if the signature is already calculated
        if ( signedAssertion != null) {
            return signedAssertion;
        }

        //Calculate the enveloped signature
        try {

            XMLSignatureFactory fac = WSSPolicyConsumerImpl.getInstance().getSignatureFactory();
            return sign(fac.newDigestMethod(DigestMethod.SHA1,null),SignatureMethod.RSA_SHA1, cert,privKey, alwaysIncludeCert);

        } catch (Exception ex) {
            // log here
            throw new SAMLException(ex);
        }
    }

    @Override
    public Element sign(X509Certificate cert, PrivateKey privKey, boolean alwaysIncludeCert, String sigAlgorithm, String canonicalizationAlgorithm) throws SAMLException {
        //Check if the signature is already calculated
        if ( signedAssertion != null) {
            return signedAssertion;
        }

        if(sigAlgorithm == null){
            sigAlgorithm = SignatureMethod.RSA_SHA1;
        }
        if(canonicalizationAlgorithm != null){
            this.canonicalizationMethod = canonicalizationAlgorithm;
        }
        //Calculate the enveloped signature
        try {

            XMLSignatureFactory fac = WSSPolicyConsumerImpl.getInstance().getSignatureFactory();
            return sign(fac.newDigestMethod(DigestMethod.SHA1,null), sigAlgorithm, cert,privKey, alwaysIncludeCert);

        } catch (Exception ex) {
            // log here
            throw new SAMLException(ex);
        }
    }

     @Override
     public Element sign(X509Certificate cert, PrivateKey privKey) throws SAMLException {
        //Check if the signature is already calculated
        if ( signedAssertion != null) {
            return signedAssertion;
        }

        //Calculate the enveloped signature
        try {

            XMLSignatureFactory fac = WSSPolicyConsumerImpl.getInstance ().getSignatureFactory ();
            return sign (fac.newDigestMethod (DigestMethod.SHA1,null),SignatureMethod.RSA_SHA1, cert,privKey);

        } catch (Exception ex) {
            // log here
            throw new SAMLException (ex);
        }
    }

    /**
     * sign the saml assertion (Enveloped Signature)
     * @param digestMethod DigestMethod to be used
     * @param signatureMethod SignatureMethod to be used.
     * @param pubKey PublicKey to be used for Signature verification
     * @param privKey PrivateKey to be used for Signature calculation
     */
    @Override
    @SuppressWarnings("unchecked")
    public Element sign(DigestMethod digestMethod, String signatureMethod,PublicKey pubKey, PrivateKey privKey) throws SAMLException {



        //Check if the signature is already calculated
        if ( signedAssertion != null) {
            return signedAssertion;
            //return;
        }

        //Calculate the enveloped signature
        try {
            XMLSignatureFactory fac = WSSPolicyConsumerImpl.getInstance().getSignatureFactory();
            ArrayList transformList = new ArrayList();

            Transform tr1 = fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
            Transform tr2 = fac.newTransform(CanonicalizationMethod.EXCLUSIVE, (TransformParameterSpec) null);
            transformList.add(tr1);
            transformList.add(tr2);

            String uri = "#" + this.getAssertionID();
            Reference ref = fac.newReference(uri,digestMethod,transformList, null, null);

            // Create the SignedInfo
            SignedInfo si = fac.newSignedInfo
                    (fac.newCanonicalizationMethod
                    (CanonicalizationMethod.EXCLUSIVE,
                    (C14NMethodParameterSpec) null),
                    fac.newSignatureMethod(signatureMethod, null),
                    Collections.singletonList(ref));

            // Create a KeyValue containing the DSA PublicKey that was generated
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            KeyValue kv = kif.newKeyValue(pubKey);

            // Create a KeyInfo and add the KeyValue to it
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

            // Instantiate the document to be signed
            Document doc =  XMLUtil.newDocument();


            //Document document;

            //Element assertionElement = this.toElement(doc);
            Element assertionElement = this.toElement(doc);
            //try {
            //    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //    DocumentBuilder builder = factory.newDocumentBuilder();
            //    document = builder.newDocument();
            //} catch (Exception ex) {
            //    throw new XWSSecurityException("Unable to create Document : " + ex.getMessage());
            //}

            //document.appendChild(assertionElement);
            //doc.appendChild(assertionElement);



            // Create a DOMSignContext and specify the DSA PrivateKey and
            // location of the resulting XMLSignature's parent element



            DOMSignContext dsc = new DOMSignContext(privKey, assertionElement);
            HashMap map = new HashMap();
            map.put(this.getAssertionID(),assertionElement);

            dsc.setURIDereferencer(new DSigResolver(map,assertionElement));
            XMLSignature signature = fac.newXMLSignature(si, ki);
            dsc.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", "ds");

            // Marshal, generate (and sign) the enveloped signature
            signature.sign(dsc);

            signedAssertion = assertionElement;
            return assertionElement;
        } catch (Exception ex) {
            // log here
            throw new SAMLException(ex);
        }
        //return signedAssertion;
    }
    @Override
    @SuppressWarnings("unchecked")
    public Element sign(DigestMethod digestMethod, String signatureMethod, X509Certificate cert, PrivateKey privKey, boolean alwaysIncludeCert) throws SAMLException {
        //Check if the signature is already calculated
        if (signedAssertion != null) {
            return signedAssertion;
            //return;
        }

        //Calculate the enveloped signature
        try {
            XMLSignatureFactory fac = WSSPolicyConsumerImpl.getInstance().getSignatureFactory();
            ArrayList transformList = new ArrayList();

            Transform tr1 = fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
            Transform tr2 = fac.newTransform(canonicalizationMethod, (TransformParameterSpec) null);
            transformList.add(tr1);
            transformList.add(tr2);

            String uri = "#" + this.getAssertionID();
            Reference ref = fac.newReference(uri, digestMethod, transformList, null, null);

            // Create the SignedInfo
            SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(canonicalizationMethod,
                    (C14NMethodParameterSpec) null),
                    fac.newSignatureMethod(signatureMethod, null),
                    Collections.singletonList(ref));

            // Instantiate the document to be signed
            Document doc = MessageFactory.newInstance().createMessage().getSOAPPart();
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            KeyInfo ki = null;

            if (!alwaysIncludeCert) {
                byte[] skid = X509SubjectKeyIdentifier.getSubjectKeyIdentifier(cert);
                if (skid != null) {
                    X509SubjectKeyIdentifier keyIdentifier = new X509SubjectKeyIdentifier(doc);
                    keyIdentifier.setCertificate(cert);
                    keyIdentifier.setReferenceValue(Base64.encode(skid));
                    SecurityTokenReference str = new SecurityTokenReference();
                    str.setReference(keyIdentifier);
                    DOMStructure domKeyInfo = new DOMStructure(str.getAsSoapElement());
                    ki = kif.newKeyInfo(Collections.singletonList(domKeyInfo));
                }
            }

            if (ki == null) {
                X509Data x509Data = kif.newX509Data(Collections.singletonList(cert));
                ki = kif.newKeyInfo(Collections.singletonList(x509Data));
            }
            /* KeyIdentifier kid = new KeyIdentifierImpl(MessageConstants.X509SubjectKeyIdentifier_NS, MessageConstants.MessageConstants.BASE64_ENCODING_NS);
            kid.setValue(Base64.encode(X509SubjectKeyIdentifier.getSubjectKeyIdentifier(cert)));
            SecurityTokenReference str = new SecurityTokenReferenceImpl(kid);*/

            Element assertionElement = this.toElement(doc);
            //try {
            //    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //    DocumentBuilder builder = factory.newDocumentBuilder();
            //    document = builder.newDocument();
            //} catch (Exception ex) {
            //    throw new XWSSecurityException("Unable to create Document : " + ex.getMessage());
            //}
            //document.appendChild(assertionElement);
            //doc.appendChild(assertionElement);



            // Create a DOMSignContext and specify the DSA PrivateKey and
            // location of the resulting XMLSignature's parent element
            DOMSignContext dsc = new DOMSignContext(privKey, assertionElement);
            HashMap map = new HashMap();
            map.put(this.getAssertionID(), assertionElement);

            dsc.setURIDereferencer(new DSigResolver(map, assertionElement));
            XMLSignature signature = fac.newXMLSignature(si, ki);
            dsc.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", "ds");

            // Marshal, generate (and sign) the enveloped signature
            signature.sign(dsc);

            signedAssertion = assertionElement;
            return assertionElement;
        } catch (XWSSecurityException | InvalidAlgorithmParameterException | XMLSignatureException | SOAPException | NoSuchAlgorithmException | MarshalException ex) {
            throw new SAMLException(ex);
        }
    }

    @Override
    public Element sign(DigestMethod digestMethod, String signatureMethod, X509Certificate cert, PrivateKey privKey) throws SAMLException {
        return sign(digestMethod, signatureMethod, cert, privKey, false);
    }


    @Override
    public Element toElement(Node doc) throws XWSSecurityException {
        if ( signedAssertion == null) {

            signedAssertion = SAMLUtil.toElement(doc, this,jc);
            if ( System.getProperty("com.sun.xml.wss.saml.binding.jaxb") == null) {
                signedAssertion.setAttributeNS(XMLNS_URI, "xmlns:xs", MessageConstants.XSD_NS);
            }
        }

        return signedAssertion;
    }

    public boolean isSigned() {
        return signature != null;
    }

    /**
     * This constructor is used to build <code>Assertion</code> object from a
     * block of existing XML that has already been built into a DOM.
     *
     * @param element A <code>org.w3c.dom.Element</code> representing
     *        DOM tree for <code>Assertion</code> object
     * @exception SAMLException if it could not process the Element properly,
     *            implying that there is an error in the sender or in the
     *            element definition.
     */
    public static Assertion fromElement(org.w3c.dom.Element element)
    throws SAMLException {
        try {
            JAXBContext jc = SAMLJAXBUtil.getJAXBContext();

            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            Object el = u.unmarshal(element);
            //return new Assertion((AssertionType)u.unmarshal(element));
            return new Assertion((AssertionType)((JAXBElement)el).getValue());
        } catch ( Exception ex) {
            // log here
            throw new SAMLException(ex);
        }
    }
    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getStatements() {
        if(statementList == null){
            statementList = new ArrayList();
        }else{
            return statementList;
        }
        List list = super.getStatementOrSubjectStatementOrAuthenticationStatement();
        Iterator ite = list.iterator();

        while (ite.hasNext()) {
            Object object = ite.next();
            if (object instanceof AttributeStatementType) {
                AttributeStatement attStmt = new AttributeStatement((AttributeStatementType) object);
                statementList.add(attStmt);
            } else if (object instanceof AuthenticationStatementType) {
                AuthenticationStatement authStmt = new AuthenticationStatement((AuthenticationStatementType) object);
                statementList.add(authStmt);
            } else if (object instanceof AuthorizationDecisionStatementType) {
                AuthorizationDecisionStatement authDesStmt = new AuthorizationDecisionStatement((AuthorizationDecisionStatementType) object);
                statementList.add(authDesStmt);
            }else if((object instanceof AttributeStatement) ||
                  (object instanceof AuthenticationStatement) ||
                     (object instanceof AuthorizationDecisionStatement)){
                statementList = list;
                return statementList;
            }
        }
        return statementList;
    }

    @SuppressWarnings("unchecked")
    private void setStatement(List statement) {
        this.statementOrSubjectStatementOrAuthenticationStatement = statement;
    }

    @Override
    public String getType(){
        return MessageConstants.SAML_v1_1_NS;
    }

    @Override
    public Object getTokenValue(){
       try {
            Document doc = XMLUtil.newDocument();
            return this.toElement(doc);
        } catch (ParserConfigurationException | XWSSecurityException ex) {
        }
        return null;
    }

    /**
     * This constructor is used to populate the data members: the
     * <code>assertionID</code>, the issuer, time when assertion issued,
     * the conditions when creating a new assertion , <code>Advice</code>
     * applicable to this <code>Assertion</code> and a set of
     * <code>Statement</code>(s) in the assertion.
     *
     * @param assertionID <code>AssertionID</code> object contained within this
     *        <code>Assertion</code> if null its generated internally.
     * @param issuer The issuer of this assertion.
     * @param issueInstant Time instant of the issue. It has type
     *        <code>dateTime</code> which is built in to the W3C XML Schema
     *        Types specification. if null, current time is used.
     * @param conditions <code>Conditions</code> under which the this
     *        <code>Assertion</code> is valid.
     * @param advice <code>Advice</code> applicable for this
     *        <code>Assertion</code>.
     * @param statements List of <code>Statement</code> objects within this
     *         <code>Assertion</code>. It could be of type
     *         <code>AuthenticationStatement</code>,
     *         <code>AuthorizationDecisionStatement</code> and
     *         <code>AttributeStatement</code>. Each Assertion can have
     *         multiple type of statements in it.
     * @exception SAMLException if there is an error in processing input.
     */
    public Assertion(
            String assertionID, java.lang.String issuer, GregorianCalendar issueInstant,
            Conditions conditions, Advice advice, List statements)
            throws SAMLException {
        if ( assertionID != null)
            setAssertionID(assertionID);

        if ( issuer != null)
            setIssuer(issuer);

        if ( issueInstant != null) {
            try {
                DatatypeFactory factory = DatatypeFactory.newInstance();
                setIssueInstant(factory.newXMLGregorianCalendar(issueInstant));
            } catch (Exception e) {
                //ignore
            }
        }


        if ( conditions != null)
            setConditions(conditions);

        if ( advice != null)
            setAdvice(advice);

        if ( statements != null)
            setStatement(statements);

         setMajorVersion(BigInteger.ONE);
         setMinorVersion(BigInteger.ONE);

    }
    /**
     * This constructor is used to populate the data members: the
     * <code>assertionID</code>, the issuer, time when assertion issued,
     * the conditions when creating a new assertion , <code>Advice</code>
     * applicable to this <code>Assertion</code> ,a set of
     * <code>Statement</code>(s) and a JAXBContext for  the assertion.
     *
     * @param assertionID <code>AssertionID</code> object contained within this
     *        <code>Assertion</code> if null its generated internally.
     * @param issuer The issuer of this assertion.
     * @param issueInstant Time instant of the issue. It has type
     *        <code>dateTime</code> which is built in to the W3C XML Schema
     *        Types specification. if null, current time is used.
     * @param conditions <code>Conditions</code> under which the this
     *        <code>Assertion</code> is valid.
     * @param advice <code>Advice</code> applicable for this
     *        <code>Assertion</code>.
     * @param statements List of <code>Statement</code> objects within this
     *         <code>Assertion</code>. It could be of type
     *         <code>AuthenticationStatement</code>,
     *         <code>AuthorizationDecisionStatement</code> and
     *         <code>AttributeStatement</code>. Each Assertion can have
     *         multiple type of statements in it.
     * @param jcc JAXBContext to be used for marshaling and unmarshalling the asertions.
     * @exception SAMLException if there is an error in processing input.
     */
    public Assertion(
            String assertionID, java.lang.String issuer, GregorianCalendar issueInstant,
            Conditions conditions, Advice advice, List statements,JAXBContext jcc)
            throws SAMLException {
        this(assertionID,  issuer,  issueInstant,
             conditions,  advice,  statements);
         jc=jcc;
    }


    private static class DSigResolver implements URIDereferencer{
        //TODO : Convert DSigResolver to singleton class.
        Element elem = null;
        Map map = null;
        Class _nodeSetClass = null;
        String optNSClassName = "org.apache.jcp.xml.dsig.internal.dom.DOMSubTreeData";
        Constructor _constructor = null;
        Boolean  _false = Boolean.FALSE;
        DSigResolver(Map map,Element elem){
            this.elem = elem;
            this.map = map;
            init();
        }
       @SuppressWarnings("unchecked")
        void init(){
            try{
                _nodeSetClass = Class.forName(optNSClassName);
                _constructor = _nodeSetClass.getConstructor(Node.class,boolean.class);
            }catch(LinkageError le){
                // logger.log (Level.FINE,"Not able load JSR 105 RI specific NodeSetData class ",le);
            }catch(ClassNotFoundException cne){
                // logger.log (Level.FINE,"Not able load JSR 105 RI specific NodeSetData class ",cne);
            }catch(NoSuchMethodException ne){

            }
        }
        @Override
        public Data dereference(URIReference uriRef, XMLCryptoContext context) throws URIReferenceException {
            try{
                String uri = null;
                uri = uriRef.getURI();
                return dereferenceURI(uri,context);
            }catch(Exception ex){
                // log here
                throw new URIReferenceException(ex);
            }
        }
        Data dereferenceURI(String uri, XMLCryptoContext context) throws URIReferenceException{
            if(uri.charAt(0) == '#'){
                uri =  uri.substring(1);
                Element el = elem.getOwnerDocument().getElementById(uri);
                if(el == null){
                    el = (Element)map.get(uri);
                }

                if(_constructor != null){
                    try{
                        return (Data)_constructor.newInstance(new Object[] {el,_false});
                    }catch(Exception ex){
                        // TODO: igonore this ?
                        ex.printStackTrace();
                    }
                }else{
                    final HashSet nodeSet = new HashSet();
                    toNodeSet(el,nodeSet);
                    return new NodeSetData(){
                        @Override
                        public Iterator iterator(){
                            return nodeSet.iterator();
                        }
                    };
                }

            }

            return null;
            //throw new URIReferenceException("Resource "+uri+" was not found");
        }
        @SuppressWarnings("unchecked")
        void toNodeSet(final Node rootNode,final Set result){
            switch (rootNode.getNodeType()) {
                case Node.ELEMENT_NODE:
                    result.add(rootNode);
                    Element el=(Element)rootNode;
                    if (el.hasAttributes()) {
                        NamedNodeMap nl = rootNode.getAttributes();
                        for (int i=0;i<nl.getLength();i++) {
                            result.add(nl.item(i));
                        }
                    }
                    //no return keep working
                case Node.DOCUMENT_NODE:
                    for (Node r=rootNode.getFirstChild();r!=null;r=r.getNextSibling()){
                        if (r.getNodeType()==Node.TEXT_NODE) {
                            result.add(r);
                            while ((r!=null) && (r.getNodeType()==Node.TEXT_NODE)) {
                                r=r.getNextSibling();
                            }
                            if (r==null)
                                return;
                        }
                        toNodeSet(r,result);
                    }
                    return;
                case Node.COMMENT_NODE:
                    return;
                case Node.DOCUMENT_TYPE_NODE:
                    return;
                default:
                    result.add(rootNode);
            }
        }

    }
    @Override
    @SuppressWarnings("unchecked")
    public boolean verifySignature(PublicKey pubKey) throws SAMLException {
        try {
            Document doc = XMLUtil.newDocument();
            Element samlAssertion = this.toElement(doc);
            HashMap map = new HashMap();
            map.put(this.getAssertionID(), samlAssertion);
            NodeList nl = samlAssertion.getElementsByTagNameNS(MessageConstants.DSIG_NS, "Signature");
            //verify the signature inside the SAML assertion
            if (nl.getLength() == 0) {
                throw new SAMLException("Unsigned SAML Assertion encountered while verifying the SAML signature");
            }
            Element signElement = (Element) nl.item(0);
            DOMValidateContext validationContext = new DOMValidateContext(pubKey, signElement);
            XMLSignatureFactory signatureFactory = WSSPolicyConsumerImpl.getInstance().getSignatureFactory();
            // unmarshal the XMLSignature
            XMLSignature xmlSignature = signatureFactory.unmarshalXMLSignature(validationContext);
            validationContext.setURIDereferencer(new DSigResolver(map, samlAssertion));
            return xmlSignature.validate(validationContext);
        } catch (Exception ex) {
            throw new SAMLException(ex);
        }
    }
}
