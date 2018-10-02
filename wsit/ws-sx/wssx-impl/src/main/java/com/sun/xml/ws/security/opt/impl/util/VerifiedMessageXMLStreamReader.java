/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public class VerifiedMessageXMLStreamReader implements XMLStreamReader{
    
    private XMLStreamReader reader = null;
    private NamespaceContext _nsCtx = null;
    
    /** Creates a new instance of FilteredXMLStreamReader */
    public VerifiedMessageXMLStreamReader(XMLStreamReader reader, Map<String,String> bodyENVNS ) {
        this.reader = reader;
        this._nsCtx = new InternalNamespaceContext(reader.getNamespaceContext(), bodyENVNS);
    }

    public int getAttributeCount() {
        return reader.getAttributeCount();
    }

    public int getEventType() {
        return reader.getEventType();
    }

    public int getNamespaceCount() {
        return reader.getNamespaceCount();
    }

    public int getTextLength() {
        return reader.getTextLength();
    }

    public int getTextStart() {
        return reader.getTextStart();
    }

    public int next() throws XMLStreamException { 
        return reader.next();
    }

    public int nextTag() throws XMLStreamException {
        return reader.nextTag();
    }

    public void close() throws XMLStreamException {
        reader.close();
    }

    public boolean hasName() {
        return reader.hasName();
    }

    public boolean hasNext() throws XMLStreamException {
        return reader.hasNext();
    }

    public boolean hasText() {
        return reader.hasText();
    }

    public boolean isCharacters() {
        return reader.isCharacters();
    }

    public boolean isEndElement() {
        return reader.isEndElement();
    }

    public boolean isStandalone() {
        return reader.isStandalone();
    }

    public boolean isStartElement() {
        return reader.isStartElement();
    }

    public boolean isWhiteSpace() {
        return reader.isWhiteSpace();
    }

    public boolean standaloneSet() {
        return reader.standaloneSet();
    }

    public char[] getTextCharacters() {
        return reader.getTextCharacters();
    }

    public boolean isAttributeSpecified(int i) {
        return reader.isAttributeSpecified(i);
    }

    public int getTextCharacters(int i, char[] c, int i0, int i1) throws XMLStreamException {
        return reader.getTextCharacters(i, c, i0, i1);
    }

    public String getCharacterEncodingScheme() {
        return reader.getCharacterEncodingScheme();
    }

    public String getElementText() throws XMLStreamException {
        return reader.getElementText();
    }

    public String getEncoding() {
        return reader.getEncoding();
    }

    public String getLocalName() {
        return reader.getLocalName();
    }

    public String getNamespaceURI() {
        return reader.getNamespaceURI();
    }

    public String getPIData() {
        return reader.getPIData();
    }

    public String getPITarget() {
        return reader.getPITarget();
    }

    public String getPrefix() {
        return reader.getPrefix();
    }

    public String getText() {
        return reader.getText();
    }

    public String getVersion() {
        return reader.getVersion();
    }

    public String getAttributeLocalName(int i) {
        return reader.getAttributeLocalName(i);
    }

    public String getAttributeNamespace(int i) {
        return reader.getAttributeNamespace(i);
    }

    public String getAttributePrefix(int i) {
        return reader.getAttributePrefix(i);
    }

    public String getAttributeType(int i) {
        return reader.getAttributeType(i);
    }

    public String getAttributeValue(int i) {
        return reader.getAttributeValue(i);
    }

    public String getNamespacePrefix(int i) {
        return reader.getNamespacePrefix(i);
    }

    public String getNamespaceURI(int i) {
        return reader.getNamespaceURI(i);
    }

    public NamespaceContext getNamespaceContext() {
        return _nsCtx;
    }

    public QName getName() {
        return reader.getName();
    }

    public QName getAttributeName(int i) {
        return reader.getAttributeName(i);
    }

    public Location getLocation() {
        return reader.getLocation();
    }

    public Object getProperty(String string) throws IllegalArgumentException {
        return reader.getProperty(string);
    }

    public void require(int i, String string, String string0) throws XMLStreamException {
        reader.require(i, string, string0);
    }

    public String getNamespaceURI(String string) {
        return reader.getNamespaceURI(string);
    }

    public String getAttributeValue(String string, String string0) {
        return reader.getAttributeValue(string, string0);
    }
    private static final class InternalNamespaceContext implements NamespaceContext {
        
        private NamespaceContext parent;

        private Map<String, String> bodyEnvNs;
        
        public InternalNamespaceContext(NamespaceContext parent, Map<String,String> bodyEnvNs) {
            this.parent = parent;
            this.bodyEnvNs = bodyEnvNs;
        }
        
        public String getNamespaceURI(String prefix) {
            String nsUri = parent.getNamespaceURI(prefix);
            if(nsUri == null || nsUri == XMLConstants.NULL_NS_URI){
                nsUri = bodyEnvNs.get(prefix);
            }
            return nsUri;
        }

        public String getPrefix(String namespaceURI) {
            if (namespaceURI == null) {
                return null;
            }
            String prefix = parent.getPrefix(namespaceURI);
            if (prefix == null) {
                Iterator it = this.bodyEnvNs.keySet().iterator();
                while (it.hasNext()) {
                    String nextKey = (String)it.next();
                    if (namespaceURI.equals(this.bodyEnvNs.get(nextKey))) {
                        return nextKey;
                    }
                }
            }
            return prefix;
        }

        public Iterator getPrefixes(String namespaceURI) {
            return  new InternalIterator(parent.getPrefixes(namespaceURI), this.bodyEnvNs, namespaceURI);
        }
        
    }
    private static final class InternalIterator implements Iterator {
        ArrayList<String> arr = new ArrayList<String>();
        Iterator internal = null;
        public InternalIterator(Iterator parent, Map<String, String> bodyEnvNs, String namespaceURI) {
            while (parent.hasNext()) {
                arr.add((String)parent.next());
            }
            if (namespaceURI != null) {
                Iterator<Map.Entry<String, String>> it = bodyEnvNs.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    if (namespaceURI.equals(entry.getValue())) {
                        arr.add(entry.getKey());
                    }
                }
            }
            internal = arr.iterator();
        }
        
        public boolean hasNext() {
            return internal.hasNext();
        }
        
        public Object next() {
            return internal.next();
        }
        
        public void remove() {
            throw new UnsupportedOperationException("Remove Not Supported");
        }
    }
}

