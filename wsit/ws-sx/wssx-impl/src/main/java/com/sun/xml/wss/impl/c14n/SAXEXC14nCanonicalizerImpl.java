/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.c14n;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class SAXEXC14nCanonicalizerImpl  implements ContentHandler{

    /** Creates a new instance of SAXEXC14nCanonicalizerImpl */
    public SAXEXC14nCanonicalizerImpl () {
    }

    @Override
    public void setDocumentLocator (Locator locator) {
    }

    @Override
    public void startDocument () {
    }

    @Override
    public void endDocument () {
    }

    @Override
    public void startPrefixMapping (String prefix, String uri) {
    }

    @Override
    public void endPrefixMapping (String prefix) {
    }

    @Override
    public void startElement (String uri, String localName, String qName, Attributes atts) {
    }

    @Override
    public void endElement (String uri, String localName, String qName) {
    }

    @Override
    public void characters (char[] ch, int start, int length) {
    }

    @Override
    public void ignorableWhitespace (char[] ch, int start, int length) {
    }

    @Override
    public void processingInstruction (String target, String data) {
    }

    @Override
    public void skippedEntity (String name) {
    }

}
