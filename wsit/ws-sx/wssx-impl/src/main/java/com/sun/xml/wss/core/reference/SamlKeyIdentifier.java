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
 * $Id: SamlKeyIdentifier.java,v 1.2 2010-10-21 15:37:14 snajper Exp $
 */

package com.sun.xml.wss.core.reference;

import jakarta.xml.soap.SOAPElement;

import org.w3c.dom.Document;

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.XWSSecurityException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 */
public class SamlKeyIdentifier extends KeyIdentifier {

    /** Defaults */
    private String valueType = MessageConstants.WSSE_SAML_KEY_IDENTIFIER_VALUE_TYPE;


    /**
     * Creates an "empty" KeyIdentifier element with default encoding type
     * and default value type.
     */
    public SamlKeyIdentifier(Document doc) throws XWSSecurityException {
        super(doc);
        // Set default attributes
        String vType = valueType;
        NodeList nodeList = doc.getElementsByTagName(MessageConstants.SAML_ASSERTION_LNAME);
        if(nodeList.getLength() > 0){
              Node assertion = nodeList.item(0);
              if (assertion.getNamespaceURI() == MessageConstants.SAML_v2_0_NS) {
                  vType = MessageConstants.WSSE_SAML_v2_0_KEY_IDENTIFIER_VALUE_TYPE;
              }

        }
        // old behavior left for BackwardCompatibility reasons
        setAttribute("ValueType", vType);
    }

    public SamlKeyIdentifier(SOAPElement element) throws XWSSecurityException {
        super(element);
    }

}
