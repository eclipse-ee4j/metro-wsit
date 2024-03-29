/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.misc;

import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;

import javax.xml.namespace.QName;

import java.util.Iterator;

public abstract class SOAPElementExtension {

    protected SOAPElementExtension() {}

    public SOAPElement addChildElement(QName qname) throws SOAPException {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }
    public SOAPElement addAttribute(QName qname, String value)
        throws SOAPException {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }
    public String getAttributeValue(QName qname) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }
    public QName createQName(String localName, String prefix)
        throws SOAPException {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    public QName getElementQName() {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    public SOAPElement setElementQName(QName newName) throws SOAPException {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }
    public boolean removeAttribute(QName qname) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }
    public Iterator<Node> getChildElements(QName qname) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

}
