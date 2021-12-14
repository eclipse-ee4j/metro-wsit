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
 * $Id: SecurityTokenReference.java,v 1.2 2010-10-21 15:37:11 snajper Exp $
 */

package com.sun.xml.wss.core;

import com.sun.xml.wss.core.reference.X509ThumbPrintIdentifier;
import com.sun.xml.wss.core.reference.EncryptedKeySHA1Identifier;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.impl.XMLUtil;
import com.sun.xml.wss.XWSSecurityException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.xml.wss.impl.misc.SecurityHeaderBlockImpl;
import com.sun.xml.wss.core.reference.DirectReference;
import com.sun.xml.wss.core.reference.KeyIdentifier;
import com.sun.xml.wss.core.reference.SamlKeyIdentifier;
import com.sun.xml.wss.core.reference.X509IssuerSerial;
import com.sun.xml.wss.core.reference.X509SubjectKeyIdentifier;



/**
 * @author Vishal Mahajan
 */
public class SecurityTokenReference extends SecurityHeaderBlockImpl implements com.sun.xml.ws.security.SecurityTokenReference {
        //implements com.sun.xml.ws.security.SecurityTokenReference {
    
    protected static final Logger log =
            Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    /**
     * Assumes that there is only one reference child element.
     */
    private ReferenceElement refElement;
    private Element samlAuthorityBinding;
    
    private static final String authorityBinding =
            "AuthorityBinding".intern();
    
    /**
     * Creates an "empty" SecurityTokenReference element
     */
    public SecurityTokenReference() throws XWSSecurityException {
        try {
            setSOAPElement(
                    getSoapFactory().createElement(
                    "SecurityTokenReference",
                    "wsse",
                    MessageConstants.WSSE_NS));
            addNamespaceDeclaration(
                    MessageConstants.WSSE_PREFIX,
                    MessageConstants.WSSE_NS);
        } catch (SOAPException e) {
            log.log(Level.SEVERE, "WSS0377.error.creating.str", e.getMessage());
            throw new XWSSecurityException(e);
        }
    }
    
    /**
     * Creates an "empty" SecurityTokenReference element whose owner document
     * is doc
     */
    public SecurityTokenReference(Document doc) throws XWSSecurityException {
        try {
            setSOAPElement(
                    (SOAPElement) doc.createElementNS(
                    MessageConstants.WSSE_NS,
                    MessageConstants.WSSE_SECURITY_TOKEN_REFERENCE_QNAME));
            addNamespaceDeclaration(
                    MessageConstants.WSSE_PREFIX,
                    MessageConstants.WSSE_NS);
        } catch (Exception e) {
            log.log(Level.SEVERE, "WSS0378.error.creating.str", e.getMessage());
            throw new XWSSecurityException(e);
        }
    }
    
    /**
     * Takes a SOAPElement which has the required structure of a
     * SecurityTokenReference (including the reference element).
     */
    public SecurityTokenReference(SOAPElement element, boolean isBSP)
    throws XWSSecurityException {
        
        super(element);
        
        if (!(element.getLocalName().equals("SecurityTokenReference") &&
                XMLUtil.inWsseNS(element))) {
            log.log(Level.SEVERE, "WSS0379.error.creating.str", element.getTagName());
            throw new XWSSecurityException("Invalid tokenRef passed");
        }
        
        isBSP(isBSP);
        
        Iterator eachChild = getChildElements();
        if (!eachChild.hasNext()) {
            throw new XWSSecurityException("Error: A SECURITY_TOKEN_REFERENCE with No child elements encountered");
        }

        jakarta.xml.soap.Node node = null;
        
        // reference mechanisms found in the STR
        int refMechanismFound = 0;
        
        while (eachChild.hasNext()) {
            
            if (isBSP && refMechanismFound >1) {
                throw new XWSSecurityException("Violation of BSP R3061: "
                        + " A SECURITY_TOKEN_REFERENCE MUST have exactly one child element");
            }
            
            node = (jakarta.xml.soap.Node) eachChild.next();
            
            if (node == null) {
                log.log(Level.SEVERE, "WSS0379.error.creating.str");
                throw new XWSSecurityException(
                        "Passed tokenReference does not contain a refElement");
            }
            
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }                     
            
            if (authorityBinding == node.getLocalName() || authorityBinding.equals(node.getLocalName())) {
                try {
                    if (MessageConstants.debug) {
                        log.log(Level.FINEST, "Unmarshall authorityBinding");
                    }
                    samlAuthorityBinding = (Element)node;
                } catch (Exception e) {
                    throw new XWSSecurityException(e);
                }
            } else {
                refElement = getReferenceElementfromSoapElement((SOAPElement) node, isBSP);
                refMechanismFound = refMechanismFound + 1;
            }
        }
    }
    
    public SecurityTokenReference(SOAPElement element)
    throws XWSSecurityException {
        this(element, false);
    }
    
    public ReferenceElement getReference() {
        return refElement;
    }
    
    public void setSamlAuthorityBinding(Element binding, Document doc)
    throws XWSSecurityException {
        if (samlAuthorityBinding != null) {
            throw new XWSSecurityException(
                    " SAML AuthorityBinding element is already present");
        }
        try {
            addTextNode("\n");
            Element temp = (Element)doc.getOwnerDocument().importNode(binding, true);
            addChildElement((SOAPElement)temp);
            //(SOAPElement)binding.toElement(doc, MessageConstants.SAML_v1_0_NUMBER));
            addTextNode("\n");
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }
        samlAuthorityBinding = binding;
    }
    
    public Element getSamlAuthorityBinding() {
        return samlAuthorityBinding;
    }
    
    
    public void setReference(ReferenceElement referenceElement)
    throws XWSSecurityException {
        
        if (refElement != null) {
            log.log(Level.SEVERE, "WSS0380.error.setting.reference");
            throw new XWSSecurityException(
                    "Reference element is already present");
        }
        
        try {
            addTextNode("\n");
            addChildElement(referenceElement.getAsSoapElement());
            addTextNode("\n");
        } catch (SOAPException e) {
            log.log(Level.SEVERE, "WSS0381.error.setting.reference");
            throw new XWSSecurityException(e);
        }
        
        refElement = referenceElement;
    }
    
    public void setWsuId(String wsuId) {
        setAttributeNS(
                MessageConstants.NAMESPACES_NS,
                "xmlns:" + MessageConstants.WSU_PREFIX,
                MessageConstants.WSU_NS);
        setAttributeNS(
                MessageConstants.WSU_NS,
                MessageConstants.WSU_ID_QNAME,
                wsuId);
    }
    
    /*
     * set the WSS 1.1 Token type for SecurityTokenRerference
     */
    public void setTokenType(String tokenType){
        setAttributeNS(
                MessageConstants.NAMESPACES_NS,
                "xmlns:" + MessageConstants.WSSE11_PREFIX,
                MessageConstants.WSSE11_NS);
        setAttributeNS(
                MessageConstants.WSSE11_NS,
                MessageConstants.WSSE11_TOKEN_TYPE,
                tokenType);
    }
    
    /*
     * get the WSS 1.1 Token type for SecurityTokenRerference
     */
    public String getTokenType(){
        return getAttributeNS(
                MessageConstants.WSSE11_NS,
                "TokenType");
    }
    
    public static SecurityHeaderBlock fromSoapElement(SOAPElement element)
    throws XWSSecurityException {
        return SecurityHeaderBlockImpl.fromSoapElement(
                element, SecurityTokenReference.class);
    }
    
    /**
     * Creates an appropriate instance of ReferenceElement depending on the
     * qualified name of the SOAPElement.
     */
    private ReferenceElement getReferenceElementfromSoapElement(
            SOAPElement element, boolean isBSP)
            throws XWSSecurityException {
        
        String name = element.getLocalName();
        if (name.equals("KeyIdentifier"))
            return getKeyIdentifier(element, isBSP);
        else if (name.equals("Reference"))
            return new DirectReference(element, isBSP);
        else if (name.equals("X509Data"))
            return new X509IssuerSerial(element);
        else if (isBSP && name.equals("KeyName")) {
            throw new XWSSecurityException("Violation of BSP R3027:" +
                    " A SECURITY_TOKEN_REFERENCE MUST NOT use a Key Name to reference a SECURITY_TOKEN." +
                    " KeyName is not supported");
        } else {
            log.log(Level.SEVERE,"WSS0335.unsupported.referencetype");
            XWSSecurityException xwsse =
                    new XWSSecurityException(
                    element.getTagName() +
                    " key reference type is not supported");
            throw SecurableSoapMessage.newSOAPFaultException(
                    MessageConstants.WSSE_UNSUPPORTED_SECURITY_TOKEN,
                    xwsse.getMessage(),
                    xwsse);
        }
    }
    
    private KeyIdentifier getKeyIdentifier(SOAPElement element, boolean isBSP)
    throws XWSSecurityException {
        
        String keyIdValueType = element.getAttribute("ValueType");
        if (isBSP && (keyIdValueType.length() < 1)) {
            throw new XWSSecurityException("Voilation of BSP R3054 " +
                    ": A wsse:KeyIdentifier element in a SECURITY_TOKEN_REFERENCE MUST specify a ValueType attribute");
        }
        
        String keyIdEncodingType = element.getAttribute("EncodingType");
        if (isBSP && (keyIdEncodingType.length() < 1)) {
            throw new XWSSecurityException("Voilation of BSP R3070 " +
                    ": A wsse:KeyIdentifier element in a SECURITY_TOKEN_REFERENCE MUST specify an EncodingType attribute. ");
        }
        
        if (isBSP && !(keyIdEncodingType.equals(MessageConstants.BASE64_ENCODING_NS))) {
            throw new XWSSecurityException("Voilation of BSP R3071 " +
                    ": An EncodingType attribute on a wsse:KeyIdentifier element in a SECURITY_TOKEN_REFERENCE MUST have a value of http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
        }
        
        if (keyIdValueType.equals(MessageConstants.WSSE_SAML_KEY_IDENTIFIER_VALUE_TYPE) || 
                keyIdValueType.equals(MessageConstants.WSSE_SAML_v2_0_KEY_IDENTIFIER_VALUE_TYPE)) {
            return new SamlKeyIdentifier(element);
        } else if ((keyIdValueType.equals(MessageConstants.X509SubjectKeyIdentifier_NS)) ||
                (keyIdValueType.equals(MessageConstants.X509v3SubjectKeyIdentifier_NS))){
            return new X509SubjectKeyIdentifier(element);
        } else if ((keyIdValueType.equals(MessageConstants.ThumbPrintIdentifier_NS))) {
            //TODO: hardcoding as per the current spec status
            return new X509ThumbPrintIdentifier(element);
        } else if ((keyIdValueType.equals(MessageConstants.EncryptedKeyIdentifier_NS))) {
            //TODO: hardcoding as per the current spec status
            return new EncryptedKeySHA1Identifier(element);
        } else {
            log.log(Level.SEVERE,"WSS0334.unsupported.keyidentifier");
            throw new XWSSecurityException("Unsupported KeyIdentifier Reference Type encountered");
        }
    }

    @Override
    public List getAny() {
        //TODO: Implement this method
        return null;
    }

    @Override
    public void setId(String value) {
        setWsuId(value);
    }

    @Override
    public String getType() {
        //TODO: Implement this method
        return null;
    }
   
    @Override
    public Object getTokenValue() {
        //TODO: Implement this method
        try {
            return this.getAsSoapElement();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    } 
    
}

