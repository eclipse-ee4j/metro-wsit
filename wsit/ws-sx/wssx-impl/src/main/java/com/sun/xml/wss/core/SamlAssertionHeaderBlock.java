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
 * $Id: SamlAssertionHeaderBlock.java,v 1.2 2010-10-21 15:37:11 snajper Exp $
 */

package com.sun.xml.wss.core;

import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import jakarta.xml.soap.SOAPElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.xml.wss.impl.misc.SecurityHeaderBlockImpl;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.XMLUtil;
import com.sun.xml.wss.XWSSecurityException;


/**
 * The schema definition for a SAML <code>Assertion</code> is as follows:
 * <pre>{@code
 * <xmp>
 * <element name="Assertion" type="saml:AssertionType"/>
 * <complexType name="AssertionType">
 *     <sequence>
 *         <element ref="saml:Conditions" minOccurs="0"/>
 *         <element ref="saml:Advice" minOccurs="0"/>
 *         <choice maxOccurs="unbounded">
 *             <element ref="saml:Statement"/>
 *             <element ref="saml:SubjectStatement"/>
 *             <element ref="saml:AuthenticationStatement"/>
 *             <element ref="saml:AuthorizationDecisionStatement"/>
 *             <element ref="saml:AttributeStatement"/>
 *         </choice>
 *         <element ref="ds:Signature" minOccurs="0"/>
 *     </sequence>
 *     <attribute name="MajorVersion" type="integer" use="required"/>
 *     <attribute name="MinorVersion" type="integer" use="required"/>
 *     <attribute name="AssertionID" type="saml:IDType" use="required"/>
 *     <attribute name="Issuer" type="string" use="required"/>
 *     <attribute name="IssueInstant" type="dateTime" use="required"/>
 * </complexType>
 * </xmp>
 * }</pre>
 *
 * @author Axl Mattheus
 */
public class SamlAssertionHeaderBlock extends SecurityHeaderBlockImpl implements SecurityToken {
    private static Logger log =
    Logger.getLogger(
    LogDomainConstants.WSS_API_DOMAIN,
    LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    
    /**
     *
     * @param element
     * @return
     * @throws XWSSecurityException
     */
    public static SecurityHeaderBlock fromSoapElement(SOAPElement element)
    throws XWSSecurityException {
        return SecurityHeaderBlockImpl.fromSoapElement(
        element, SamlAssertionHeaderBlock.class);
    }
    
    private Document contextDocument_ = null;
    private Element delegateAssertion_ = null;
    
    
    /**
     * Constructs code&gt;SamlAssertionHeaderBlock&lt;/code&gt; from an existing SAML
     * &lt;code&gt;Assertion&lt;/code&gt;.
     *
     * @param assertion
     * @throws XWSSecurityException
     */
    public SamlAssertionHeaderBlock(Element assertion, Document doc) throws  XWSSecurityException {
        if (null != assertion) {
            delegateAssertion_ = assertion;
            contextDocument_ = doc;
        } else {
            throw new XWSSecurityException("Assertion may not be null.");
        }
    }
    
    /**
     * Constructs a SAML &lt;code&gt;Assertion&lt;/code&gt; header block from an existing
     * &lt;code&gt;SOAPElement&lt;/code&gt;.
     *
     * @param element an existing SAML assertion element.
     * @throws XWSSecurityException when the element is not a valid template
     *         for a SAML &lt;code&gt;Assertion&lt;/code&gt;.
     */
    public SamlAssertionHeaderBlock(SOAPElement element)
    throws XWSSecurityException {
        contextDocument_ = element.getOwnerDocument();
        
        delegateAssertion_ = element;
        
        
        setSOAPElement(element);
    }
    
    /* (non-Javadoc)
     * @see com.sun.xml.wss.SecurityHeaderBlock#getAsSoapElement()
     */
    public SOAPElement getAsSoapElement() throws XWSSecurityException {
        
        
        // uncomment after making SamlAssertionHeaderBlock like others (using a dirty flag).
        if (delegateElement != null) {
            return delegateElement;
        }
        
        if (null == contextDocument_) {
            try {
                contextDocument_ = XMLUtil.newDocument();
            } catch (ParserConfigurationException e) {
                throw new XWSSecurityException(e);
            }
        }
        
        try {
            SOAPElement se = (SOAPElement)contextDocument_.importNode(delegateAssertion_, true);
            setSOAPElement(se);
            
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }
        
        return super.getAsSoapElement();
    }
    
    
    /**
     * @return
     */
    public Document getContextDocument() {
        return contextDocument_;
    }
    
    
    /**
     * @return
     */
    public Element getDelegateAssertion() {
        return delegateAssertion_;
    }

    /**
     * Set the signature for the Request.
     *
     * @param elem &lt;code&gt;ds:Signature&lt;/code&gt; element.
     * @return A boolean value: true if the operation succeeds; false otherwise.
     */
    /*public boolean setSignature(Element elem) {
        try {
            JAXBContext jc =
                JAXBContext.newInstance("com.sun.xml.wss.saml.internal");
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            delegateAssertion_.setSignature((SignatureType)u.unmarshal(elem));
            return true;
        } catch ( Exception ex) {
            return false;
        }
    }*/

    
}
