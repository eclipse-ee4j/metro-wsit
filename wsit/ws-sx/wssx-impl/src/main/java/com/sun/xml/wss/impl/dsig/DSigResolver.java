/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.dsig;


import com.sun.xml.wss.core.reference.X509ThumbPrintIdentifier;
import com.sun.xml.wss.impl.FilterProcessingContext;
import com.sun.xml.wss.core.reference.X509SubjectKeyIdentifier;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.impl.XMLUtil;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.core.ReferenceElement;
import com.sun.xml.wss.core.SecurityToken;
import com.sun.xml.wss.core.SecurityTokenReference;
import com.sun.xml.wss.core.X509SecurityToken;
import com.sun.xml.wss.core.reference.DirectReference;
import com.sun.xml.wss.core.reference.KeyIdentifier;
import com.sun.xml.wss.core.reference.X509IssuerSerial;
import com.sun.xml.wss.logging.impl.dsig.LogStringsMessages;
import com.sun.xml.wss.saml.AssertionUtil;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import jakarta.xml.soap.AttachmentPart;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sun.xml.wss.saml.Assertion;
import com.sun.xml.wss.saml.util.SAMLUtil;
import java.util.logging.Level;


/**
 * Implementation of JSR 105 URIDereference interface.
 * @author <U><B>k.venugopal@sun.com</B></U>
 */
public class DSigResolver implements URIDereferencer{

    private static volatile DSigResolver resolver = null;
    private static Logger logger = Logger.getLogger (LogDomainConstants.IMPL_SIGNATURE_DOMAIN,
            LogDomainConstants.IMPL_SIGNATURE_DOMAIN_BUNDLE);

    private String optNSClassName = "org.apache.jcp.xml.dsig.internal.dom.DOMSubTreeData";
    private Class _nodeSetClass = null;
    private Constructor _constructor = null;
    private Boolean  _false = Boolean.FALSE;

    /** Creates a new instance of DSigResolver */
    @SuppressWarnings("unchecked")
    private DSigResolver () {
        try{
            _nodeSetClass = Class.forName (optNSClassName);
            _constructor = _nodeSetClass.getConstructor (Node.class,boolean.class);
        }catch(LinkageError | ClassNotFoundException le){
            logger.log (Level.FINE,"Not able load JSR 105 RI specific NodeSetData class ",le);
        } catch(NoSuchMethodException ne){

        }
    }

    /**
     *
     * @return URI Dereferencer instance.
     */
    public static URIDereferencer getInstance (){
        if(resolver == null){
            init ();
        }
        return resolver;
    }

    private static void init (){
        if(resolver == null){
            synchronized(DSigResolver.class){
                if(resolver == null){
                    resolver = new DSigResolver ();
                }
            }
        }
    }


    /**
     * resolve the URI of type "cid:" , "attachmentRef:", "http:", "#xyz".
     */
    @Override
    public Data dereference (URIReference uriRef, XMLCryptoContext context) throws URIReferenceException {
        String uri = null;

        try{
            if(uriRef instanceof DOMURIReference ){
                DOMURIReference domRef = (DOMURIReference) uriRef;
                Node node = domRef.getHere ();
                if(node.getNodeType () == Node.ATTRIBUTE_NODE){
                    uri = uriRef.getURI ();
                    return dereferenceURI (uri,context);
                }else if(node.getNodeType () == Node.ELEMENT_NODE ){
                    if("SecurityTokenReference".equals (node.getLocalName ())){
                        return derefSecurityTokenReference (node,context);
                    }
                }
            }else {
                uri = uriRef.getURI ();
                if(MessageConstants.debug){
                    logger.log (Level.FINEST, "URI "+ uri);
                }
                return dereferenceURI (uri,context);
            }
        }catch(XWSSecurityException ex){
            if(logger.getLevel () == Level.FINEST){
                logger.log (Level.FINEST,"Error occurred while resolving"+uri,ex);
            }
            throw new URIReferenceException (ex.getMessage ());
        }
        return null;
    }


    Data dereferenceURI (String uri, XMLCryptoContext context) throws URIReferenceException, XWSSecurityException{
        FilterProcessingContext filterContext =(FilterProcessingContext) context.get (MessageConstants.WSS_PROCESSING_CONTEXT);
        SecurableSoapMessage secureMsg = filterContext.getSecurableSoapMessage ();
        if(uri == null || uri.equals ("")){
            SOAPMessage msg = filterContext.getSOAPMessage ();
            Document doc = msg.getSOAPPart ();
            if(_constructor == null){
                return convertToData (doc,true);
            }else{
                try{
                    return (Data)_constructor.newInstance (new Object[] {doc,_false});
                }catch(Exception ex){
                    //throw new XWSSecurityException(ex);
                }
                return convertToData (doc,true);
            }
        }else if(uri.charAt (0) == '#'){
            return dereferenceFragment (SecurableSoapMessage.getIdFromFragmentRef(uri),context);
        }else if(uri.startsWith ("cid:") || uri.startsWith ("attachmentRef:")){
            return dereferenceAttachments (uri,context);
        }else if(uri.startsWith ("http")){
            //throw new UnsupportedOperationException("Not yet supported ");
            return dereferenceExternalResource (uri,context);
        }else {
            return dereferenceFragment (uri,context);
        }
        //throw new URIReferenceException("Resource "+uri+" was not found");
    }

    Data dereferenceExternalResource (final String uri,XMLCryptoContext context) throws URIReferenceException {

        URIDereferencer resolver = WSSPolicyConsumerImpl.getInstance ().getDefaultResolver ();
        URIReference uriRef = null;
        FilterProcessingContext filterContext =(FilterProcessingContext) context.get (MessageConstants.WSS_PROCESSING_CONTEXT);
        SecurableSoapMessage secureMsg = filterContext.getSecurableSoapMessage ();
        final Attr uriAttr = secureMsg.getSOAPMessage ().getSOAPPart ().createAttribute ("uri");
        uriAttr.setNodeValue (uri);
        uriRef = new DOMURIReference (){

            @Override
            public String getURI (){
                return uri;
            }

            @Override
            public String getType (){
                return null;
            }
            @Override
            public Node getHere (){
                return uriAttr;
            }
        };
        try{
            Data data = resolver.dereference (uriRef, context);

            if(MessageConstants.debug){
                if(data instanceof NodeSetData){
                    logger.log (Level.FINE,"Node set Data");
                }else if(data instanceof OctetStreamData){
                    logger.log (Level.FINE,"Octet Data");
                    try{
                        InputStream is = ((OctetStreamData)data).getOctetStream ();
                        int len = is.available ();
                        byte [] bb = new byte[len];
                        is.read (bb);
                        logger.log (Level.FINE,"Data: "+new String (bb));
                    }catch(Exception ex){
                        logger.log (Level.FINE,"ERROR",ex);
                    }
                }
            }
            return data;
        }catch(URIReferenceException ue){
            logger.log (Level.SEVERE,LogStringsMessages.WSS_1325_DSIG_EXTERNALTARGET(ue));
            throw ue;
        }

    }


    Data dereferenceAttachments (String uri, XMLCryptoContext context) throws URIReferenceException, XWSSecurityException {
        boolean sunAttachmentTransformProvider = true;
        FilterProcessingContext filterContext =(FilterProcessingContext)
        context.get (MessageConstants.WSS_PROCESSING_CONTEXT);

        SecurableSoapMessage secureMsg = filterContext.getSecurableSoapMessage ();
        AttachmentPart attachment = secureMsg.getAttachmentPart (uri);
        if(attachment == null){
            throw new URIReferenceException ("Attachment Resource with Identifier  "+uri+" was not found");
        }
        if(sunAttachmentTransformProvider){
            AttachmentData attachData = new AttachmentData ();
            attachData.setAttachmentPart (attachment);
            return attachData;
        }else{
            //Attachments need to be serialized and returned as OctectStreamData
            //attachment.getDataHandler();
            // new OctectStreamData();
            throw new UnsupportedOperationException ("Not yet supported ");
        }
        //        throw new URIReferenceException("Attachment Resource with Identifier  "+uri+" was not found");
    }

    Data dereferenceFragment (String uri, XMLCryptoContext context) throws URIReferenceException, XWSSecurityException {
        FilterProcessingContext filterContext =(FilterProcessingContext) context.get(MessageConstants.WSS_PROCESSING_CONTEXT);
        HashMap elementCache = filterContext.getElementCache ();
        if(elementCache.size () > 0){
            Object obj = elementCache.get (uri);
            if(obj != null ){
                if(_constructor == null){
                    return convertToData ((Element)obj,true);
                }else{
                    try{
                        return (Data)_constructor.newInstance (new Object[] {obj,_false});
                    }catch(Exception ex){
                        //throw new XWSSecurityException(ex);
                    }
                }
                return convertToData ((Element)obj,true);
            }
        }
        SecurableSoapMessage secureMsg = filterContext.getSecurableSoapMessage ();
        Element element = secureMsg.getElementById (uri);
        if(element == null){
            throw new URIReferenceException ("Resource with fragment Identifier  "+uri+" was not found");
            //log;
        }
        if(_constructor == null){
            return convertToData (element,true);
        }else{
            try{
                return (Data)_constructor.newInstance (new Object[] {element,_false});
            }catch(Exception ex){
                //throw new XWSSecurityException(ex);
            }
        }
        return convertToData (element,true);
    }

    Data convertToData (final Node node,boolean xpathNodeSet){
        final HashSet nodeSet = new HashSet ();
        if(xpathNodeSet){
            toNodeSet (node,nodeSet);
            return new NodeSetData (){
                @Override
                public Iterator iterator (){
                    return nodeSet.iterator ();
                }
            };
        }else{
            return new NodeSetData (){
                @Override
                public Iterator iterator (){
                    return Collections.singletonList (node).iterator ();

                }
            };
        }
    }
    @SuppressWarnings("unchecked")
    void toNodeSet (final Node rootNode,final Set result){
        //handle EKSHA1 under DKT
        if (rootNode == null) return;
        switch (rootNode.getNodeType ()) {
            case Node.ELEMENT_NODE:
                result.add (rootNode);
                Element el=(Element)rootNode;
                if (el.hasAttributes ()) {
                    NamedNodeMap nl = rootNode.getAttributes ();
                    for (int i=0;i<nl.getLength ();i++) {
                        result.add (nl.item (i));
                    }
                }
                //no return keep working
            case Node.DOCUMENT_NODE:
                for (Node r=rootNode.getFirstChild ();r!=null;r=r.getNextSibling ()){
                    if (r.getNodeType ()==Node.TEXT_NODE) {
                        result.add (r);
                        while ((r!=null) && (r.getNodeType ()==Node.TEXT_NODE)) {
                            r=r.getNextSibling ();
                        }
                        if (r==null)
                            return;
                    }
                    toNodeSet (r,result);
                }
                return;
            case Node.COMMENT_NODE:
                return;
            case Node.DOCUMENT_TYPE_NODE:
                return;
            default:
                result.add (rootNode);
        }
    }
    @SuppressWarnings("unchecked")
    private Data derefSecurityTokenReference (Node element, XMLCryptoContext context)
    throws XWSSecurityException,URIReferenceException {
        /*
          Four Cases:
             (1). Direct Reference
             (2). KeyIdentifier
                  * X509 SubjectKeyIdentifier
                  * SAML Assertion ID
             (3). Embedded Reference
             (4). X509 Issuer Serial
         */
        SecurityToken secToken = null;
        FilterProcessingContext filterContext = (FilterProcessingContext)context.get (MessageConstants.WSS_PROCESSING_CONTEXT);
        SecurableSoapMessage secureMessage = filterContext.getSecurableSoapMessage ();
        Document soapDocument = secureMessage.getSOAPPart ();
        SOAPElement soapElem =  XMLUtil.convertToSoapElement (soapDocument, (Element) element);
        SecurityTokenReference tokenRef = new SecurityTokenReference (soapElem);
        ReferenceElement refElement =  tokenRef.getReference ();
        HashMap tokenCache = filterContext.getTokenCache ();
        Element tokenElement = null;
        Element newElement = null;

        if (refElement instanceof DirectReference) {
            // isXMLToken = true;
            /* Use the URI value to locate the BST */
            String uri = ((DirectReference) refElement).getURI ();
            String tokenId = uri.substring (1);
            secToken = (SecurityToken)tokenCache.get (tokenId);
            if(secToken == null){
                tokenElement = secureMessage.getElementById (tokenId);
                if(tokenElement == null){
                    throw new URIReferenceException ("Could not locate token with following ID"+tokenId);
                }

            } else {
                tokenElement = secToken.getAsSoapElement();
            }
            newElement = (Element)element.getOwnerDocument ().importNode (tokenElement, true);

        } else if (refElement instanceof KeyIdentifier) {
            String valueType = ((KeyIdentifier) refElement).getValueType ();
            String keyId = ((KeyIdentifier) refElement).getReferenceValue ();
            if (MessageConstants.X509SubjectKeyIdentifier_NS.
                    equals (valueType) ||
                    MessageConstants.X509v3SubjectKeyIdentifier_NS.
                    equals (valueType)) {
                /* Use the Subject Key Identifier to locate BST */
                //  isXMLToken = false;
                X509Certificate cert = null;

                Object token = tokenCache.get (keyId);
                if(token instanceof X509SubjectKeyIdentifier ){
                    if(token != null){
                        cert = ((X509SubjectKeyIdentifier)token).getCertificate ();
                    }
                }

                if(cert == null){
                    cert = filterContext.getSecurityEnvironment ().getCertificate (
                            filterContext.getExtraneousProperties (), XMLUtil.getDecodedBase64EncodedData (keyId));
                }
                secToken = new X509SecurityToken (soapDocument, cert);
                tokenElement = secToken.getAsSoapElement ();
                newElement = tokenElement;
                //(Element)element.getOwnerDocument().importNode(tokenElement, true);
                try {
                    // EncodingType should not be set -
                    // As specified by WSS spec
                    newElement.removeAttribute ("EncodingType");
                } catch (DOMException de) {
                    logger.log(Level.SEVERE,com.sun.xml.wss.logging.LogStringsMessages.WSS_0607_STR_TRANSFORM_EXCEPTION());
                    throw new XWSSecurityRuntimeException (de.getMessage (), de);
                }
            } else if (MessageConstants.ThumbPrintIdentifier_NS.equals (valueType)) {
                X509Certificate cert = null;

                Object token = tokenCache.get (keyId);
                if(token instanceof X509ThumbPrintIdentifier ){
                    if(token != null){
                        cert = ((X509ThumbPrintIdentifier)token).getCertificate ();
                    }
                }

                if(cert == null){
                    cert = filterContext.getSecurityEnvironment ().getCertificate (
                            filterContext.getExtraneousProperties (), XMLUtil.getDecodedBase64EncodedData (keyId), MessageConstants.THUMB_PRINT_TYPE);
                }
                secToken = new X509SecurityToken (soapDocument, cert);
                tokenElement = secToken.getAsSoapElement ();
                newElement = tokenElement;
                //(Element)element.getOwnerDocument().importNode(tokenElement, true);
                try {
                    // EncodingType should not be set -
                    // As specified by WSS spec
                    newElement.removeAttribute ("EncodingType");
                } catch (DOMException de) {
                    logger.log(Level.SEVERE,com.sun.xml.wss.logging.LogStringsMessages.WSS_0607_STR_TRANSFORM_EXCEPTION());
                    throw new XWSSecurityRuntimeException (de.getMessage (), de);
                }
            }else if(MessageConstants.EncryptedKeyIdentifier_NS.equals (valueType)){
                // do something here
                newElement = null;
            } else if (MessageConstants.WSSE_SAML_KEY_IDENTIFIER_VALUE_TYPE.equals (valueType) ||
                     MessageConstants.WSSE_SAML_v2_0_KEY_IDENTIFIER_VALUE_TYPE.equals (valueType)) {

                //TODO : should we first try locating from the cache
                if (tokenRef.getSamlAuthorityBinding () != null) {
                    tokenElement = filterContext.getSecurityEnvironment ().
                            locateSAMLAssertion (
                            filterContext.getExtraneousProperties(), tokenRef.getSamlAuthorityBinding (), keyId, secureMessage.getSOAPPart ());
                } else {
                    tokenElement = SAMLUtil.locateSamlAssertion (keyId,secureMessage.getSOAPPart ());
                }
                newElement = (Element)element.getOwnerDocument ().importNode (tokenElement, true);

                Assertion assertion = null;
                try {
                    assertion = AssertionUtil.fromElement(tokenElement);
                } catch (Exception e) {
                    throw new XWSSecurityException (e);
                }
                tokenCache.put (keyId, assertion);

            } else {
                try {
                    tokenElement = resolveSAMLToken (tokenRef, keyId, filterContext);
                } catch (Exception e) {
                    // ignore
                }
                if (tokenElement != null) {
                    newElement = (Element)element.getOwnerDocument ().importNode (tokenElement, true);
                } else {
                    //TODO : there can be a X509 KeyIdentifier without ValueType
                    //    log.log(Level.SEVERE, "WSS0334.unsupported.keyidentifier");
                    XWSSecurityException xwsse =
                            new XWSSecurityException (
                            "WSS_DSIG0008:unsupported KeyIdentifier Reference Type "
                            + valueType);
                    throw SecurableSoapMessage.newSOAPFaultException (
                            MessageConstants.WSSE_INVALID_SECURITY_TOKEN,
                            xwsse.getMessage (),
                            xwsse);
                }
            }

        } else if (refElement instanceof X509IssuerSerial) {
            //       isXMLToken = false;
            BigInteger serialNumber =
                    ((X509IssuerSerial) refElement).getSerialNumber ();
            String issuerName = ((X509IssuerSerial) refElement).getIssuerName ();
            X509Certificate cert =  null;
            Object token = tokenCache.get (issuerName+serialNumber);
            if(token instanceof X509IssuerSerial){
                cert = ((X509IssuerSerial)token).getCertificate ();
            }

            if(cert == null){
                cert = filterContext.getSecurityEnvironment ().getCertificate (
                        filterContext.getExtraneousProperties (),serialNumber, issuerName);
            }
            secToken = new X509SecurityToken (soapDocument, cert);
            tokenElement = secToken.getAsSoapElement ();
            newElement = tokenElement;
            //(Element)element.getOwnerDocument().importNode(tokenElement, true);
            try {
                // EncodingType should not be set - As specified by WSS spec
                newElement.removeAttribute ("EncodingType");
            } catch (DOMException de) {
                logger.log(Level.SEVERE,com.sun.xml.wss.logging.LogStringsMessages.WSS_0607_STR_TRANSFORM_EXCEPTION());
                throw new XWSSecurityException (de.getMessage (), de);
            }
        } else {
            logger.log(Level.SEVERE,com.sun.xml.wss.logging.LogStringsMessages.WSS_0608_DIAG_CAUSE_1());
            throw new XWSSecurityException (
                    "Cannot handle reference mechanism: " +
                    refElement.getTagName ());
        }
        Attr attr = newElement.getOwnerDocument ().createAttributeNS (MessageConstants.NAMESPACES_NS, "xmlns");
        attr.setValue ("");
        if (newElement != null) {
            newElement.setAttributeNodeNS (attr);
        }
        return convertToData (newElement,false);
    }
    @SuppressWarnings("unchecked")
    private static Element resolveSAMLToken (SecurityTokenReference tokenRef, String assertionId,
            FilterProcessingContext context)throws XWSSecurityException {

        Assertion ret = (Assertion)context.getTokenCache().get(assertionId);
        if (ret != null) {
            try {
                return SAMLUtil.toElement(context.getSecurableSoapMessage().getSOAPPart(), ret,null);
            } catch (Exception e) {
                throw new XWSSecurityException (e);
            }
        }

        Element tokenElement = null;
        if (tokenRef.getSamlAuthorityBinding () != null) {
            tokenElement = context.getSecurityEnvironment ().
                    locateSAMLAssertion (
                    context.getExtraneousProperties(),
                    tokenRef.getSamlAuthorityBinding (),
                    assertionId,
                    context.getSOAPMessage ().getSOAPPart ());
        } else {
            tokenElement = SAMLUtil.locateSamlAssertion (
                    assertionId, context.getSOAPMessage ().getSOAPPart ());
        }

        try {
            ret = AssertionUtil.fromElement(tokenElement);
        } catch (Exception e) {
            throw new XWSSecurityException (e);
        }
        context.getTokenCache ().put (assertionId, ret);

        return tokenElement;
    }

}
