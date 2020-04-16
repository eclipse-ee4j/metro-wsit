/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: BinarySecurityToken.java,v 1.2 2010-10-21 15:37:11 snajper Exp $
 */

package com.sun.xml.wss.core;

import org.w3c.dom.Document;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.soap.SOAPElement;

import org.apache.xml.security.exceptions.Base64DecodingException;
import com.sun.xml.wss.impl.misc.Base64;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.MessageConstants;

import com.sun.xml.wss.impl.XMLUtil;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.SecurityTokenException;

import com.sun.xml.wss.impl.misc.SecurityHeaderBlockImpl;

/**
 * A wsse:BinarySecurityToken.  
 *
 * @author Manveen Kaur
 * @author Edwin Goei
 */
public class BinarySecurityToken extends SecurityHeaderBlockImpl 
                                 implements SecurityToken {

    /**
     * Valid values are:
     *     #X509v3
     *     #X509PKIPathv1
     *     #PKCS7
     */
    protected String valueType = null;
    
    /** Default encoding */
    protected String encodingType = MessageConstants.BASE64_ENCODING_NS;
    
    protected String wsuId = null;
    
    protected String encodedText = null;

    protected Document soapDoc = null;

    protected static final Logger log =
        Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    BinarySecurityToken(
        Document document,
        String wsuId,                    
        String valueType) 
        throws SecurityTokenException {

        this.soapDoc = document;
        this.wsuId = wsuId;
        setValueType(valueType);
        
        // BSP:R3029 :EncodingType MUST always be specified.
        setEncodingType(encodingType);
    }

    BinarySecurityToken(SOAPElement binTokenSoapElement)
        throws SecurityTokenException {
        this(binTokenSoapElement, false);
    }
    
    BinarySecurityToken(SOAPElement binTokenSoapElement, boolean isBSP)
        throws SecurityTokenException {

        setSOAPElement(binTokenSoapElement);
        this.soapDoc = getOwnerDocument();

        setTextValue(XMLUtil.getFullTextFromChildren(this));
       
        String wsuId = getAttributeNS(MessageConstants.WSU_NS, "Id");
        if (!"".equals(wsuId))
            setId(wsuId);
        
        String valueType = getAttribute("ValueType");

        // BSP:3031: ValueType MUST always be specified
        if (isBSP && valueType.length()<1) {
            log.log(Level.SEVERE, "BSP3031.ValueType.NotPresent");
            throw new SecurityTokenException("Any wsse:BinarySecurityToken in a SECURE_ENVELOPE MUST have an ValueType attribute.");
        }
         
        if (!"".equals(valueType)) {        
            setValueType(valueType);
        }
        
        if (isBSP) {
            String encoding = getAttribute("EncodingType");

            // BSP:R3029: encodingType MUST be specified.                
            if (encodingType.length()<1) {
                log.log(Level.SEVERE, "BSP3029.EncodingType.NotPresent");
                throw new SecurityTokenException("Any wsse:BinarySecurityToken in a SECURE_ENVELOPE MUST have an EncodingType attribute.");
            }

            if (!encodingType.equalsIgnoreCase(MessageConstants.BASE64_ENCODING_NS))
            {
                log.log(Level.SEVERE, "BSP3030.EncodingType.Invalid");
                throw new SecurityTokenException("EncodingType attribute value in wsse:BinarySecurityToken is invalid.");            
            }
            
            if (!"".equals(encoding)) {
                setEncodingType(encoding);
            }                
        }        
    }

    public String getValueType() {
        return this.valueType;
    }
    
    protected void setValueType(String valueType) {
        if (!(MessageConstants.X509v3_NS.equals(valueType)||MessageConstants.X509v1_NS.equals(valueType))) { 
            log.log(Level.SEVERE,"WSS0342.valtype.invalid");
            throw new RuntimeException("Unsupported value type: " + valueType);
        }
        this.valueType = valueType;
    }
    
    public String getEncodingType() {
        return this.encodingType;
    }
    
    protected void setEncodingType(String encodingType) {
        
        if (!MessageConstants.BASE64_ENCODING_NS.equals(encodingType)) {
            log.log(Level.SEVERE,"WSS0316.enctype.invalid");
            throw new RuntimeException("Encoding type invalid");
        }
        this.encodingType = encodingType;
    }

    public String getId() {
        return this.wsuId;
    }
    
    protected void setId(String wsuId) {
        this.wsuId = wsuId;
    }
        
    /** returns the decoded value of the text node.*/
    public byte[] getRawValue() throws SecurityTokenException {
        try {
            return Base64.decode(encodedText);
        } catch (Base64DecodingException bde) {
            log.log(Level.SEVERE, "WSS0344.error.decoding.bst");  
            throw new SecurityTokenException(bde);
        }
    }
    
    protected void setRawValue(byte[] rawText) {
        this.encodedText = Base64.encode(rawText);
    }
    
    /**
     * get the actual value of the text node. This will typically be encoded.
     * It is the onus of the filter to decode this before operation upon it.
     */
    public String getTextValue() throws XWSSecurityException {
        return encodedText;
    }
    
    /**
     * set the value of the text node. It is assumed that the 
     * filter would have already encoded the value appropriately.
     */
    protected void setTextValue(String encodedText) {
        this.encodedText = encodedText;
    }
        
    public SOAPElement getAsSoapElement() throws SecurityTokenException {
           
        if (null != delegateElement)
            return delegateElement; 
        try {
            setSOAPElement(
                (SOAPElement) soapDoc.createElementNS(
                    MessageConstants.WSSE_NS,
                    MessageConstants.WSSE_PREFIX + ":BinarySecurityToken"));
            addNamespaceDeclaration(
                MessageConstants.WSSE_PREFIX,
                MessageConstants.WSSE_NS);
            
            if (null != valueType)
                setAttributeNS(null, "ValueType", valueType);

            if (encodingType != null) {
               setAttributeNS(null, "EncodingType", encodingType);
            }
            
            if (wsuId != null) {
                setWsuIdAttr(this, wsuId);
            }
            
            addTextNode(getTextValue());
            
        } catch (Exception e) {            
            log.log(Level.SEVERE,"WSS0343.error.creating.bst", e.getMessage());            
            throw new SecurityTokenException(
                "There was an error in creating the BinarySecurityToken "  +
                e.getMessage());
        }
        return delegateElement;        
    }
}
