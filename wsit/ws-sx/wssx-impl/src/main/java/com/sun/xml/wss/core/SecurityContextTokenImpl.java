/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * SecurityContextTokenHeaderBlock.java
 *
 * Created on December 15, 2005, 6:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.core;

import com.sun.xml.ws.security.SecurityContextToken;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.SecurityTokenException;
import com.sun.xml.wss.impl.XMLUtil;
import com.sun.xml.wss.impl.misc.SecurityHeaderBlockImpl;

import java.util.Iterator;
import java.util.List;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.net.URI;
import java.util.ArrayList;

/**
 *&lt;wsc:SecurityContextToken wsu:Id="..." ...&gt; 
 *    &lt;wsc:Identifier&gt;...&lt;/wsc:Identifier&gt; 
 *    &lt;wsc:Instance&gt;...&lt;/wsc:Instance&gt; 
 *    ... 
 *&lt;/wsc:SecurityContextToken&gt;
 *
 */
public class SecurityContextTokenImpl extends SecurityHeaderBlockImpl 
    implements SecurityContextToken, SecurityToken {
    
    private String securityContextId = null;
    private String instance = null;
    private List extElements = null;
    
    private String wsuId = null;
    
    /**
     *
     */
    public static SecurityHeaderBlock fromSoapElement(SOAPElement element)
    throws XWSSecurityException {
        return SecurityHeaderBlockImpl.fromSoapElement(
                element, SecurityContextTokenImpl.class);
    }
    
    private Document contextDocument = null;
    
    
    public SecurityContextTokenImpl(
        Document contextDocument, String contextId, String instance, String wsuId, List extElements) {
        securityContextId = contextId;
        this.instance = instance;
        this.wsuId = wsuId;
        this.extElements = extElements;
        this.contextDocument = contextDocument;
    }
    
    @SuppressWarnings("unchecked")
    public SecurityContextTokenImpl(SOAPElement sct) throws XWSSecurityException {
        
        setSOAPElement(sct);
        
        this.contextDocument = getOwnerDocument();
        
        if (!("SecurityContextToken".equals(getLocalName()) &&
                XMLUtil.inWsscNS(this))) {
            throw new SecurityTokenException(
                    "Expected wsc:SecurityContextToken Element, but Found " + getPrefix() + ":" + getLocalName());
        }
        
        String wsuIdVal = getAttributeNS(MessageConstants.WSU_NS, "Id");
        if (!"".equals(wsuIdVal)) {
            this.wsuId = wsuIdVal;
        }
        
        Iterator children = getChildElements();
        Node object;
        
        while (children.hasNext()) {

            object = (Node)children.next();
            if (object.getNodeType() == Node.ELEMENT_NODE) {
                
                SOAPElement element = (SOAPElement) object;
                if ("Identifier".equals(element.getLocalName()) &&
                        XMLUtil.inWsscNS(element)) {
                     securityContextId = element.getFirstChild().getNodeValue();
                } else if ( "Instance".equals(element.getLocalName()) &&
                        XMLUtil.inWsscNS(element)) {
                    this.instance = element.getFirstChild().getNodeValue();
                } else {
                    if (extElements == null) {
                        extElements = new ArrayList();
                    }
                    extElements.add(object);
                }
            }
        }
        
        if (securityContextId == null) {
            throw new XWSSecurityException("Missing Identifier subelement in SecurityContextToken");
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
                    MessageConstants.WSSC_PREFIX + ":SecurityContextToken"));
            /*addNamespaceDeclaration(
                    MessageConstants.WSSE11_PREFIX,
                    MessageConstants.WSS11_SPEC_NS);*/
            addNamespaceDeclaration(
                    MessageConstants.WSSC_PREFIX,
                    MessageConstants.WSSC_NS);
            if (securityContextId == null )  {
                throw new XWSSecurityException("Missing SecurityContextToken Identifier");
            } else {
                addChildElement("Identifier", MessageConstants.WSSC_PREFIX).addTextNode(securityContextId);
            }

            if (this.instance != null) {
                addChildElement("Instance", MessageConstants.WSSC_PREFIX).addTextNode(this.instance);
            }
            
            if (wsuId != null) {
                setWsuIdAttr(this, wsuId);
            }
            
            if (extElements != null) {
                for (int i=0; i<extElements.size(); i++) {
                    Element element = (Element)extElements.get(i);
                    Node newElement = delegateElement.getOwnerDocument().importNode(element,true);
                    appendChild(newElement);
                }
            }
            
        } catch (SOAPException se) {
            throw new SecurityTokenException(
                    "There was an error creating SecurityContextToken " +
                    se.getMessage());
        }
        
        return super.getAsSoapElement();
    }
    
    public Document getContextDocument() {
        return contextDocument;
    }

    @Override
    public String getType() {
        return MessageConstants.SECURITY_CONTEXT_TOKEN_NS;
    }

    @Override
    public Object getTokenValue() {
        return this;
    }
    
    public void setId(String wsuId) {
        this.wsuId = wsuId;
    }
    
    @Override
    public String getWsuId() {
        return this.wsuId;
    }

    // dont use this
    @Override
    public URI getIdentifier() {
        try {
            return new URI(securityContextId);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getSCId() {
        return securityContextId;
    }

    @Override
    public String getInstance() {
        return instance;
    }

    @Override
    public List getExtElements() {
        return extElements;
    }
    
}
