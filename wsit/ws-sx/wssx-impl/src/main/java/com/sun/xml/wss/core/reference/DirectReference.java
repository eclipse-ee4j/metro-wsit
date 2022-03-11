/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: DirectReference.java,v 1.2 2010-10-21 15:37:14 snajper Exp $
 */

package com.sun.xml.wss.core.reference;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;


import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.XMLUtil;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.core.ReferenceElement;

/**
 * @author Vishal Mahajan
 */
public class DirectReference extends ReferenceElement {

    protected static final Logger log =
        Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /**
     * Creates a DirectReference element.
     */
    public DirectReference() throws XWSSecurityException {
        try {
            setSOAPElement(
                soapFactory.createElement(
                    "Reference",
                    "wsse",
                    MessageConstants.WSSE_NS));
        } catch (SOAPException e) {
            log.log(Level.SEVERE,
                    "WSS0750.soap.exception",
                    new Object[] {"wsse:Reference", e.getMessage()});
            throw new XWSSecurityException(e);
        }
    }

    /**
     * Takes a SOAPElement and checks if it has the right name.
     */
    public DirectReference(SOAPElement element, boolean isBSP) throws XWSSecurityException {
        setSOAPElement(element);
        if (!(element.getLocalName().equals("Reference") &&
              XMLUtil.inWsseNS(element))) {
            log.log(Level.SEVERE,
                    "WSS0751.invalid.direct.reference",
                    "{"+element.getNamespaceURI()+"}"+element.getLocalName());
            throw new XWSSecurityException("Invalid DirectReference passed");
        }

        if (isBSP && (getURI()==null)) {
                throw new XWSSecurityException("Violation of BSP R3062" +
                        ": A wsse:Reference element in a SECURITY_TOKEN_REFERENCE MUST specify a URI attribute");
        }
    }

    public DirectReference(SOAPElement element) throws XWSSecurityException {
        this(element, false);
    }

    /**
     * If this attr is not present, returns null.
     */
    public String getValueType() {
        String valueType = getAttribute("ValueType");
        if (valueType.equals(""))
            return null;
        return valueType;
    }

    public void setValueType(String valueType) {
        setAttribute("ValueType", valueType);
    }

    /**
     * @return URI attr value.
     *         If this attr is not present, returns null.
     */
    public String getURI() {
        String uri = getAttribute("URI");
        if (uri.equals(""))
            return null;
        return uri;
    }

    /**
     * @param uri Value to be assigned to URI attr.
     */
    public void setURI(String uri) {
        setAttribute("URI", uri);
    }

    /**
     * @param uri Value to be assigned to URI attr.
     */
    public void setSCTURI(String uri, String instance) {
        setAttribute("URI", uri);
        //setAttributeNS(MessageConstants.WSSC_NS, MessageConstants.WSSC_PREFIX + ":Instance" , instance);
        setAttribute("ValueType", MessageConstants.SCT_VALUETYPE);
    }
}
