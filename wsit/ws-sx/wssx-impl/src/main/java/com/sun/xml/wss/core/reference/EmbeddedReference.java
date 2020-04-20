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
 * $Id: EmbeddedReference.java,v 1.2 2010-10-21 15:37:14 snajper Exp $
 */

package com.sun.xml.wss.core.reference;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.core.ReferenceElement;
import com.sun.xml.wss.impl.XMLUtil;
import com.sun.xml.wss.XWSSecurityException;

/**
 * @author Vishal Mahajan
 */
public class EmbeddedReference extends ReferenceElement {

    protected static final Logger log =
        Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    private SOAPElement embeddedElement;

    /**
     * Creates an "empty" EmbeddedReference element.
     */
    public EmbeddedReference() throws XWSSecurityException {
        try {
            setSOAPElement(
                soapFactory.createElement(
                    "Embedded",
                    "wsse",
                    MessageConstants.WSSE_NS));
        } catch (SOAPException e) {
            log.log(Level.SEVERE,
                    "WSS0750.soap.exception",
                    new Object[] {"wsse:Embedded", e.getMessage()});
            throw new XWSSecurityException(e);
        }
    }

    /**
     * Takes a SOAPElement and checks if it has the right name and structure.
     */
    public EmbeddedReference(SOAPElement element) throws XWSSecurityException {

        setSOAPElement(element);

        if (!(element.getLocalName().equals("Embedded") &&
              XMLUtil.inWsseNS(element))) {
            log.log(Level.SEVERE,
                    "WSS0752.invalid.embedded.reference");
            throw new XWSSecurityException("Invalid EmbeddedReference passed");
        }

        Iterator eachChild = getChildElements();
        Node node = null;
        while (!(node instanceof SOAPElement) && eachChild.hasNext()) {
            node = (Node) eachChild.next();
        }
        if ((node != null) && (node.getNodeType() == Node.ELEMENT_NODE)) {
            embeddedElement  = (SOAPElement) node;
        } else {
            log.log(Level.SEVERE,
                    "WSS0753.missing.embedded.token");
            throw new XWSSecurityException(
                "Passed EmbeddedReference does not contain an embedded element");
        }
    }

    /**
     * Assumes that there is a single embedded element.
     *
     * @return If no child element is present, returns null
     */
    public SOAPElement getEmbeddedSoapElement() {
        return embeddedElement;
    }

    public void setEmbeddedSoapElement(SOAPElement element)
        throws XWSSecurityException {

        if (embeddedElement != null) {
            log.log(Level.SEVERE,
                    "WSS0754.token.already.set");
            throw new XWSSecurityException(
                "Embedded element is already present");
        }
 
        embeddedElement = element;
        
        try {
            addChildElement(embeddedElement);
        } catch (SOAPException e) {
            log.log(Level.SEVERE,
                    "WSS0755.soap.exception",
                    e.getMessage()); 
            throw new XWSSecurityException(e);
        }
    }

    /**
     * If this attr is not present, returns null.
     */
    public String getWsuId() {
        String wsuId = getAttribute("wsu:Id");
        if (wsuId.equals(""))
            return null;
        return wsuId;
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

} 
