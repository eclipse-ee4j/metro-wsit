/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.saml.util;


import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.stax.StreamWriterBufferCreator;
import com.sun.xml.wss.WSITXMLFactory;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.dsig.WSSPolicyConsumerImpl;
import com.sun.xml.wss.logging.saml.LogStringsMessages;
import java.text.ParseException;
import java.util.Date;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jakarta.xml.bind.JAXBContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.wss.util.DateUtils;
import java.lang.reflect.Constructor;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.crypto.Data;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import org.w3c.dom.NamedNodeMap;

public class SAMLUtil {
    private static Logger logger = Logger.getLogger(LogDomainConstants.SAML_API_DOMAIN,
            LogDomainConstants.SAML_API_DOMAIN_BUNDLE);      
    
    public static Element locateSamlAssertion(String assertionId,Document soapDocument)
    throws XWSSecurityException {
        
        //System.out.println("\n\n--------SOAP DOCUMENT : " + soapDocument + "--------\n\n");
        
        NodeList nodeList = null;
        
//        try {
          nodeList = soapDocument.getElementsByTagNameNS(MessageConstants.SAML_v1_0_NS, MessageConstants.SAML_ASSERTION_LNAME);
          if((nodeList.item(0)) == null ){
              nodeList = soapDocument.getElementsByTagNameNS(MessageConstants.SAML_v2_0_NS,
                    MessageConstants.SAML_ASSERTION_LNAME);
          }
        
        int nodeListLength = nodeList.getLength();
        if (nodeListLength == 0) {
                logger.log(Level.SEVERE,LogStringsMessages.WSS_001_SAML_ASSERTION_NOT_FOUND(assertionId));
            throw SecurableSoapMessage.newSOAPFaultException(
                    MessageConstants.WSSE_SECURITY_TOKEN_UNAVAILABLE,
                    "Referenced Security Token could not be retrieved",
                    null);
            //throw new XWSSecurityException(
            //"No SAML Assertion found with  AssertionID:" + assertionId );
        }
        
        for (int i=0; i<nodeListLength; i++) {
            Element assertion = (Element) nodeList.item(i);
            String  aId = assertion.getAttribute(MessageConstants.SAML_ASSERTIONID_LNAME);
            String id = assertion.getAttribute(MessageConstants.SAML_ID_LNAME);
            if (aId.equals(assertionId) || id.equals(assertionId)) {
                //return  XMLUtil.convertToSoapElement(soapDocument, assertion);
                return assertion;
            }
        }
            logger.log(Level.SEVERE,LogStringsMessages.WSS_001_SAML_ASSERTION_NOT_FOUND(assertionId));
        throw SecurableSoapMessage.newSOAPFaultException(
                MessageConstants.WSSE_SECURITY_TOKEN_UNAVAILABLE,
                "Referenced Security Token could not be retrieved",
                null);
        //throw new XWSSecurityException("Could not locate SAML assertion with AssertionId:" + assertionId);
    }
    
    public static Element toElement(Node doc, Object element) throws XWSSecurityException{
        return toElement(doc, element, null);
    }

    public static Element toElement(Node doc, Object element,JAXBContext jcc) throws XWSSecurityException{
        
        DOMResult result = null;
        Document document = null;
        //TODO : If DOC is SUPPLIED then this code is not working
        if ( doc != null) {
            
            result = new DOMResult(doc);
        } else {
            
            try {
                DocumentBuilderFactory factory = WSITXMLFactory.createDocumentBuilderFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
                DocumentBuilder builder = factory.newDocumentBuilder();
                document = builder.newDocument();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_002_FAILED_CREATE_DOCUMENT(), ex);
                throw new XWSSecurityException("Unable to create Document : " + ex.getMessage());
            }
            result = new DOMResult(document);
        }
        
        try {
            JAXBContext jc = jcc;
            if (jc == null) {
                if (System.getProperty("com.sun.xml.wss.saml.binding.jaxb") == null) {
                    if (element instanceof com.sun.xml.wss.saml.assertion.saml20.jaxb20.Assertion) {
                        jc = SAML20JAXBUtil.getJAXBContext();
                    } else {
                        jc = SAMLJAXBUtil.getJAXBContext();
                    }
                } else {
                    jc = SAMLJAXBUtil.getJAXBContext();
                }
            }
            
            Marshaller m = jc.createMarshaller();
            
            if (element == null){
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE,"Element is Null in SAMLUtil.toElement()");
                }
            }
            
            m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new WSSNamespacePrefixMapper());
            m.marshal(element, result);            
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE,LogStringsMessages.WSS_003_FAILEDTO_MARSHAL(), ex);
            throw new XWSSecurityException("Not able to Marshal " + element.getClass().getName() + 
                ", got exception: " + ex.getMessage());
        }
        
        if ( doc != null) {
            //return ((Document)doc).getDocumentElement();
            
            
            if (doc.getNodeType() == Node.ELEMENT_NODE) {
                if (doc.getFirstChild().getNamespaceURI().equals(MessageConstants.SAML_v2_0_NS)){
                    Element el = (Element)((Element)doc).getElementsByTagNameNS(MessageConstants.SAML_v2_0_NS, "Assertion").item(0);
                    return el;
                }else{
                    Element el = (Element)((Element)doc).getElementsByTagNameNS(MessageConstants.SAML_v1_0_NS, "Assertion").item(0);
                    return el;
                }
            } else {
                if (doc.getFirstChild().getNamespaceURI().equals(MessageConstants.SAML_v2_0_NS)){
                    Element el = (Element)((Document)doc).getElementsByTagNameNS(MessageConstants.SAML_v2_0_NS,"Assertion").item(0);
                    return el;
                }else{
                    Element el = (Element)((Document)doc).getElementsByTagNameNS(MessageConstants.SAML_v1_0_NS,"Assertion").item(0);
                    return el;
                }
            }
            
        } else {
            if (document.getFirstChild().getNamespaceURI().equals(MessageConstants.SAML_v2_0_NS)){
                Element el = (Element)document.getElementsByTagNameNS(MessageConstants.SAML_v2_0_NS, "Assertion").item(0);
                return el;            
            }else{
                Element el = (Element)document.getElementsByTagNameNS(MessageConstants.SAML_v1_0_NS, "Assertion").item(0);
                return el;            
            }
        }
    }
    
    public static Element createSAMLAssertion(XMLStreamReader reader) throws XWSSecurityException,XMLStreamException{
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();        
        MutableXMLStreamBuffer buffer = new MutableXMLStreamBuffer();
        StreamWriterBufferCreator bCreator = new StreamWriterBufferCreator(buffer);
        Document doc = null;        
        try{                                
            XMLStreamWriter writer = xof.createXMLStreamWriter(baos);
            XMLStreamWriter writer_tmp = (XMLStreamWriter)bCreator;
            while(!(XMLStreamReader.END_DOCUMENT == reader.getEventType())){
                com.sun.xml.ws.security.opt.impl.util.StreamUtil.writeCurrentEvent(reader, writer_tmp);
                reader.next();
            }
            buffer.writeToXMLStreamWriter(writer);
            writer.close();
            try {
                baos.close();
            } catch (IOException ex) {
                throw new XWSSecurityException("Error occurred while trying to convert SAMLAssertion stream into DOM Element", ex);
            }
            DocumentBuilderFactory dbf = WSITXMLFactory.createDocumentBuilderFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();            
            doc = db.parse(new ByteArrayInputStream(baos.toByteArray()));
            return  doc.getDocumentElement();    
        } catch(XMLStreamException xe){
            throw new XMLStreamException("Error occurred while trying to convert SAMLAssertion stream into DOM Element", xe);
        }catch(Exception xe){
            throw new XWSSecurityException("Error occurred while trying to convert SAMLAssertion stream into DOM Element", xe);
        }
    }    
    
    public static boolean validateTimeInConditionsStatement(Element samlAssertion) throws XWSSecurityException {
     
        Date _notBefore=null;
        Date  _notOnOrAfter=null;
        
        NodeList nl = samlAssertion.getElementsByTagNameNS(samlAssertion.getNamespaceURI(), "Conditions");
        Node conditionsElement = null;
        if (nl != null && nl.getLength() > 0) {
            conditionsElement = nl.item(0);
        } else {
            //no conditions stmt
            logger.log(Level.INFO, "No Conditions Element found in SAML Assertion");
            return true;
        }
        Element elt = (Element)conditionsElement;
        String eltName = elt.getLocalName();
        if (eltName == null)  {
            throw new XWSSecurityException("Internal Error: LocalName of Conditions Element found Null") ;
        }
        if (!(eltName.equals("Conditions")))  {
            throw new XWSSecurityException("Internal Error: LocalName of Conditions Element found to be :" + eltName) ;
        }
        
        String dt = elt.getAttribute("NotBefore");
        if ((dt != null) && (!dt.equals("")))  {
            try {
                _notBefore = DateUtils.stringToDate(dt);
            } catch (ParseException pe) {
               throw new XWSSecurityException(pe);
            }
                                                                                                                                                             
        }
        dt = elt.getAttribute("NotOnOrAfter");
        if ((dt != null) && (!dt.equals("")))  {
            try {
                _notOnOrAfter = DateUtils.stringToDate(
                            elt.getAttribute("NotOnOrAfter"));
            } catch (ParseException pe) {
               throw new XWSSecurityException(pe);
            }
        }
        
        long someTime = System.currentTimeMillis();
        
        if (_notBefore == null ) {
            if (_notOnOrAfter == null) {
                return true;
            } else {
                if (someTime < _notOnOrAfter.getTime()) {
                    return true;
                }
            }
        } else if (_notOnOrAfter == null ) {
            if (someTime >= _notBefore.getTime()) {
                return true;
            }
        } else if ((someTime >= _notBefore.getTime()) &&
            (someTime < _notOnOrAfter.getTime()))
        {
            return true;
        }
        return false;
    }

    public static boolean verifySignature(Element samlAssertion, PublicKey pubKey)throws XWSSecurityException {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            String id = samlAssertion.getAttribute("ID");
            if (id == null || id.length() < 1){
                id = samlAssertion.getAttribute("AssertionID");
            }
            map.put(id, samlAssertion);
            NodeList nl = samlAssertion.getElementsByTagNameNS(MessageConstants.DSIG_NS, "Signature");

            //verify the signature inside the SAML assertion
            if (nl.getLength() == 0) {
                throw new XWSSecurityException("Unsigned SAML Assertion encountered while verifying the SAML signature");
            }
            Element signElement = (Element) nl.item(0);
            DOMValidateContext validationContext = new DOMValidateContext(pubKey, signElement);
            XMLSignatureFactory signatureFactory = WSSPolicyConsumerImpl.getInstance().getSignatureFactory();

            // unmarshal the XMLSignature
            XMLSignature xmlSignature = signatureFactory.unmarshalXMLSignature(validationContext);
            validationContext.setURIDereferencer(new DSigResolver(map, samlAssertion));
            boolean coreValidity = xmlSignature.validate(validationContext);
            return coreValidity;
        } catch (Exception ex) {
            throw new XWSSecurityException(ex);
        }
    }

    private static class DSigResolver implements URIDereferencer{
        //TODO : Convert DSigResolver to singleton class.
        Element elem = null;
        Map map = null;
        Class<?> _nodeSetClass = null;
        String optNSClassName = "org.apache.jcp.xml.dsig.internal.dom.DOMSubTreeData";
        Constructor _constructor = null;
        Boolean  _false = Boolean.valueOf(false);
        DSigResolver(Map map,Element elem){
            this.elem = elem;
            this.map = map;
            init();
        }

        void init(){
            try{
                _nodeSetClass = Class.forName(optNSClassName);
                _constructor = _nodeSetClass.getConstructor(new Class [] {org.w3c.dom.Node.class,boolean.class});
            }catch(LinkageError le){
                // logger.log (Level.FINE,"Not able load JSR 105 RI specific NodeSetData class ",le);
            }catch(ClassNotFoundException cne){
                // logger.log (Level.FINE,"Not able load JSR 105 RI specific NodeSetData class ",cne);
            }catch(NoSuchMethodException ne){

            }
        }
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
                uri =  uri.substring(1,uri.length());
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
                    final HashSet<Object> nodeSet = new HashSet<Object>();
                    toNodeSet(el,nodeSet);
                    return new NodeSetData(){
                        public Iterator iterator(){
                            return nodeSet.iterator();
                        }
                    };
                }

            }

            return null;
            //throw new URIReferenceException("Resource "+uri+" was not found");
        }

        void toNodeSet(final Node rootNode,final Set<Object> result){
            switch (rootNode.getNodeType()) {
                case Node.ELEMENT_NODE:
                    result.add(rootNode);
                    Element el=(Element)rootNode;
                    if (el.hasAttributes()) {
                        NamedNodeMap nl = ((Element)rootNode).getAttributes();
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
            return;
        }
    }
}
