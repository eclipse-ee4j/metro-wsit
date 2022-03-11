/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.core;

import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.SecurityTokenException;
import com.sun.xml.wss.impl.XMLUtil;
import com.sun.xml.wss.impl.misc.SecurityHeaderBlockImpl;

import com.sun.xml.ws.security.Token;
import java.util.Iterator;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.apache.xml.security.exceptions.Base64DecodingException;
import com.sun.xml.wss.impl.misc.Base64;

/**
 *
 * @author Abhijit Das
 */
public class DerivedKeyTokenHeaderBlock extends SecurityHeaderBlockImpl implements Token, SecurityToken {
    
    /**
     *
     */
    public static SecurityHeaderBlock fromSoapElement(SOAPElement element)
    throws XWSSecurityException {
        return SecurityHeaderBlockImpl.fromSoapElement(
                element, DerivedKeyTokenHeaderBlock.class);
    }
    
    private Document contextDocument = null;
    private SecurityTokenReference securityTokenRefElement = null;
    private long offset = 0;
    private long length = 32;
    private String nonce = null;
    private long generation = -1;
    private String wsuId = null;
    private String label = null;

    private byte[] decodedNonce = null;
    
    
    public DerivedKeyTokenHeaderBlock(Document contextDocument, SecurityTokenReference securityTokenRefElement, String wsuId) throws XWSSecurityException {
        if (securityTokenRefElement != null ) {
            this.contextDocument = contextDocument;
            this.securityTokenRefElement = securityTokenRefElement;
            this.wsuId = wsuId;
        } else {
            throw new XWSSecurityException("DerivedKeyToken can not be null");
        }
    }
    
    public DerivedKeyTokenHeaderBlock(Document contextDocument,
            SecurityTokenReference securityTokenRefElement,
            String nonce, String wsuId) throws XWSSecurityException {
        
        if (securityTokenRefElement != null ) {
            this.contextDocument = contextDocument;
            this.securityTokenRefElement = securityTokenRefElement;
            this.wsuId = wsuId;
        } else {
            throw new XWSSecurityException("DerivedKeyToken can not be null");
        }
        
        if ( nonce != null ) {
            this.nonce = nonce;
        } else {
            throw new XWSSecurityException("Nonce can not be null");
        }
    }
    
    
    public DerivedKeyTokenHeaderBlock(Document contextDocument,
            SecurityTokenReference securityTokenRefElement,
            String nonce,
            long generation,
            String wsuId) throws XWSSecurityException {
        this(contextDocument, securityTokenRefElement, nonce, wsuId);
        this.generation = generation;
    }
    
    public DerivedKeyTokenHeaderBlock(Document contextDocument,
            SecurityTokenReference securityTokenRefElement,
            String nonce,
            long offset,
            long length, String wsuId ) throws XWSSecurityException {
        this(contextDocument, securityTokenRefElement, nonce, -1, wsuId);
        this.length = length;
        this.offset = offset;
        
    }
    
     public DerivedKeyTokenHeaderBlock(Document contextDocument,
            SecurityTokenReference securityTokenRefElement,
            String nonce,
            long offset,
            long length, String wsuId, String label ) throws XWSSecurityException {
        this(contextDocument, securityTokenRefElement, nonce, -1, wsuId);
        this.length = length;
        this.offset = offset;
        this.label = label;
        
    }
    
    
    public DerivedKeyTokenHeaderBlock(SOAPElement derivedKeyTokenHeaderBlock ) throws XWSSecurityException {
        setSOAPElement(derivedKeyTokenHeaderBlock);
        
        this.contextDocument = getOwnerDocument();
        
        if (!("DerivedKeyToken".equals(getLocalName()) &&
                XMLUtil.inWsscNS(this))) {
            throw new SecurityTokenException(
                    "Expected DerivedKeyToken Element, but Found " + getPrefix() + ":" + getLocalName());
        }
        
        boolean invalidToken = false;
        
        Iterator children = getChildElements();
        
        // Check whether SecurityTokenReference is present inside DerivedKeyToken
        String wsuId = getAttributeNS(MessageConstants.WSU_NS, "Id");
        if (!"".equals(wsuId))
            setId(wsuId);
        
        Node object = null;
        boolean offsetSpecified = false; 
        boolean genSpecified = false; 
        boolean lenSpecified = false; 

        while (children.hasNext()) {
            
            object = (Node)children.next();
            
            if (object.getNodeType() == Node.ELEMENT_NODE) {
                
                SOAPElement element = (SOAPElement) object;
                //TODO: Check for other attributes
                //TODO: Add static final constants for all these string constants below. 
                if ("SecurityTokenReference".equals(element.getLocalName()) &&
                        XMLUtil.inWsseNS(element)) {
                    securityTokenRefElement = new SecurityTokenReference(element);
                } else if ( "Offset".equals(element.getLocalName()) ) {
                    try {
                        offsetSpecified = true;
                        offset = Long.valueOf(element.getValue()).longValue();
                    } catch (NumberFormatException nfe) {
                        throw new XWSSecurityException(nfe);
                    }
                } else if ( "Length".equals(element.getLocalName()) ) {
                    try{
                        lenSpecified = true;
                        length = Long.valueOf(element.getValue()).longValue();
                    } catch (NumberFormatException nfe) {
                        throw new XWSSecurityException(nfe);
                    }
                } else if ( "Nonce".equals(element.getLocalName()) ) {
                    nonce = element.getValue();
                } else if ( "Generation".equals(element.getLocalName())) {
                    try {
                        genSpecified = true;
                        generation = Long.valueOf(element.getValue()).longValue();
                    } catch (NumberFormatException nfe) {
                        throw new XWSSecurityException(nfe);
                    }
                } else if ("Label".equals(element.getLocalName())) {
                    this.label = element.getValue();
                } else {
                    invalidToken = true;
                    break;
                }
            }
        }
 
        if (offsetSpecified && genSpecified) {
            invalidToken = true;
        }
        
        if ( invalidToken) {
            throw new XWSSecurityException("Invalid DerivedKeyToken");
        }
    }
    
    @Override
    public SOAPElement getAsSoapElement() throws XWSSecurityException {
        if ( delegateElement != null )
            return delegateElement;
        
        try {
            setSOAPElement(
                    (SOAPElement) contextDocument.createElementNS(
                    MessageConstants.WSSC_NS,
                    MessageConstants.WSSC_PREFIX + ":DerivedKeyToken"));
            addNamespaceDeclaration(
                    MessageConstants.WSSC_PREFIX,
                    MessageConstants.WSSC_NS);

            if ( securityTokenRefElement == null )  {
                throw new SecurityTokenException("securitytokenreference was not set");
            } else {
                SOAPElement elem = securityTokenRefElement.getAsSoapElement();
                delegateElement.appendChild(elem);
            }
            if (generation == -1) {
                addChildElement("Offset", MessageConstants.WSSC_PREFIX).addTextNode(String.valueOf(offset));
                addChildElement("Length", MessageConstants.WSSC_PREFIX).addTextNode(String.valueOf(length));
            } else {
                addChildElement("Generation", MessageConstants.WSSC_PREFIX).addTextNode(String.valueOf(generation));
                addChildElement("Length", MessageConstants.WSSC_PREFIX).addTextNode(String.valueOf(length));
            }
            if (this.label != null) {
                addChildElement("Label", MessageConstants.WSSC_PREFIX).addTextNode(this.label);
            }
            if ( nonce != null ) {
                addChildElement("Nonce", MessageConstants.WSSC_PREFIX).addTextNode(nonce);
            }
            
            if (wsuId != null) {
                setWsuIdAttr(this, wsuId);
            }
            
        } catch (SOAPException se) {
            throw new SecurityTokenException(
                    "There was an error creating DerivedKey Token " +
                    se.getMessage());
        }
        
        return super.getAsSoapElement();
    }
    
    
    
    
    public Document getContextDocument() {
        return contextDocument;
    }
    
    public byte[] getNonce() {
        if (decodedNonce != null)
            return decodedNonce;
        try {
            decodedNonce = Base64.decode(nonce);
        } catch (Base64DecodingException bde) {
            throw new RuntimeException(bde);
        }
        return decodedNonce;
    }
    
    public long getOffset() {
        return offset;
    }
    
    public long getLength() {
        return length;
    }
    
    public SecurityTokenReference getDerivedKeyElement() {
        return securityTokenRefElement;
    }

    @Override
    public String getType() {
        return MessageConstants.DERIVEDKEY_TOKEN_NS;
    }
                                                                                                                                    
    @Override
    public Object getTokenValue() {
        return this;
    }

    private void setId(String wsuId) {
        this.wsuId = wsuId;
    }
    
    public String getLabel() {
        return this.label;
    }

}
