/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.incoming;

import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferException;
import com.sun.xml.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.message.AbstractHeaderImpl;
import com.sun.xml.ws.security.opt.api.NamespaceContextInfo;
import com.sun.xml.ws.security.opt.impl.util.StreamUtil;
import com.sun.xml.ws.security.opt.impl.util.XMLStreamReaderFactory;
import com.sun.istack.FinalArrayList;
import com.sun.xml.ws.message.Util;
import com.sun.xml.ws.security.opt.api.SecuredHeader;
import com.sun.xml.wss.WSITXMLFactory;
import com.sun.xml.wss.impl.MessageConstants;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import jakarta.xml.bind.Unmarshaller;

import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import jakarta.xml.bind.JAXBException;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class GenericSecuredHeader extends AbstractHeaderImpl implements SecuredHeader, NamespaceContextInfo {

    private static final String SOAP_1_1_MUST_UNDERSTAND = "mustUnderstand";
    private static final String SOAP_1_2_MUST_UNDERSTAND = SOAP_1_1_MUST_UNDERSTAND;
    private static final String SOAP_1_1_ROLE = "actor";
    private static final String SOAP_1_2_ROLE = "role";
    private static final String SOAP_1_2_RELAY = "relay";
    private XMLStreamBuffer completeHeader;
    //private XMLStreamBuffer headerContent;
    private boolean isMustUnderstand;
    private SOAPVersion soapVersion = null;
    //private boolean hasId = true;
    private Vector idValues = new Vector(2);
    private HashMap<String, String> shNSDecls = new HashMap<>();
    private HashMap<String, String> nsDecls = null;
    //private QName headerName = null;
    // never null. role or actor value
    private String role;
    private boolean isRelay;
    private String localName;
    private String namespaceURI = "";
    private String id = "";
    private final FinalArrayList<Attribute> attributes;
    private boolean hasED = false;

    @SuppressWarnings("unchecked")
    public GenericSecuredHeader(XMLStreamReader reader, SOAPVersion soapVersion, StreamReaderBufferCreator creator, HashMap nsDecl, XMLInputFactory staxIF, boolean encHeaderContent) throws XMLStreamBufferException, XMLStreamException {

        this.shNSDecls = nsDecl;
        this.soapVersion = soapVersion;
        if (reader.getNamespaceURI() != null) {
            namespaceURI = reader.getNamespaceURI();
        }
        localName = reader.getLocalName();
        attributes = processHeaderAttributes(reader);
        completeHeader = new XMLStreamBufferMark(this.nsDecls, creator);
        creator.createElementFragment(XMLStreamReaderFactory.createFilteredXMLStreamReader(reader, new IDProcessor()), true);
        nsDecls.putAll(shNSDecls);
        if (this.id.length() > 0) {
            idValues.add(id);
        }

        if (encHeaderContent) {
            checkEncryptedData();
        }
    }

    public boolean hasEncData() {
        return hasED;
    }

    @SuppressWarnings("unchecked")
    private void checkEncryptedData() throws XMLStreamException {
        XMLStreamReader reader = readHeader();
        while (StreamUtil.moveToNextElement(reader)) {
            if (MessageConstants.ENCRYPTED_DATA_LNAME.equals(reader.getLocalName()) &&
                    MessageConstants.XENC_NS.equals(reader.getNamespaceURI())) {
                hasED = true;
                String encId = reader.getAttributeValue(null, "Id");
                if (encId != null && encId.length() > 0) {
                    idValues.add(encId);
                }
                break;
            }
        }
    }

    private com.sun.istack.FinalArrayList<Attribute> processHeaderAttributes(XMLStreamReader reader) {
        if (soapVersion == SOAPVersion.SOAP_11) {
            return process11Header(reader);
        } else {
            return process12Header(reader);
        }
    }

    private com.sun.istack.FinalArrayList<Attribute> process12Header(XMLStreamReader reader) {
        FinalArrayList<Attribute> atts = null;
        role = soapVersion.implicitRole;
        nsDecls = new HashMap<>();
        //headerName = reader.getName();
        nsDecls = new HashMap<>();
        if (reader.getNamespaceCount() > 0) {
            for (int i = 0; i < reader.getNamespaceCount(); i++) {
                nsDecls.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
            }
        }

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            final String lName = reader.getAttributeLocalName(i);
            final String nsURI = reader.getAttributeNamespace(i);
            final String value = reader.getAttributeValue(i);
            if (MessageConstants.WSU_NS.equals(nsURI) && "Id".intern().equals(lName)) {
                //hasId = true;
                id = value;

            } else if (nsURI == null && "Id".intern().equals(lName)) {
                //hasId = true;
                id = value;
            }
            //handle other Id's eg SAML
            if (SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE.equals(nsURI)) {
                if (SOAP_1_1_MUST_UNDERSTAND.equals(lName)) {
                    isMustUnderstand = Util.parseBool(value);
                } else if (SOAP_1_1_ROLE.equals(lName)) {
                    if (value != null && value.length() > 0) {
                        role = value;
                    }
                }
            }

            if (atts == null) {
                atts = new FinalArrayList<>();
            }
            atts.add(new Attribute(nsURI, lName, value));
        }
        return atts;
    }

    private FinalArrayList<Attribute> process11Header(XMLStreamReader reader) {
        FinalArrayList<Attribute> atts = null;

        role = soapVersion.implicitRole;
        //headerName = reader.getName();
        nsDecls = new HashMap<>();
        if (reader.getNamespaceCount() > 0) {

            for (int j = 0; j < reader.getNamespaceCount(); j++) {
                nsDecls.put(reader.getNamespacePrefix(j), reader.getNamespaceURI(j));
            }
        }

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            final String lName = reader.getAttributeLocalName(i);
            final String nsURI = reader.getAttributeNamespace(i);
            final String value = reader.getAttributeValue(i);

            if (MessageConstants.WSU_NS.equals(nsURI) && "Id".intern().equals(lName)) {
                //hasId = true;
                id = value;
            } else if (nsURI == null && "Id".intern().equals(lName)) {
                //hasId = true;
                id = value;
            }

            if (SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE.equals(nsURI)) {
                if (SOAP_1_2_MUST_UNDERSTAND.equals(lName)) {
                    isMustUnderstand = Util.parseBool(value);
                } else if (SOAP_1_2_ROLE.equals(lName)) {
                    if (value != null && value.length() > 0) {
                        role = value;
                    }
                } else if (SOAP_1_2_RELAY.equals(lName)) {
                    isRelay = Util.parseBool(value);
                }
            }

            if (atts == null) {
                atts = new FinalArrayList<>();
            }
            atts.add(new Attribute(nsURI, lName, value));
        }

        return atts;
    }

    @Override
    public boolean hasID(String id) {
        return idValues.contains(id);
    }

    @Override
    public final boolean isIgnorable(SOAPVersion soapVersion, Set<String> roles) {
        // check mustUnderstand
        if (!isMustUnderstand) {
            return true;
        }

        // now role
        return !roles.contains(role);
    }

    @Override
    public String getRole(SOAPVersion soapVersion) {
        assert role != null;
        return role;
    }

    @Override
    public boolean isRelay() {
        return isRelay;
    }

    @Override
    public String getNamespaceURI() {
        return namespaceURI;
    }

    @Override
    public String getLocalPart() {
        return localName;
    }

    @Override
    public String getAttribute(String nsUri, String localName) {
        if (attributes != null) {
            for (int i = attributes.size() - 1; i >= 0; i--) {
                Attribute a = attributes.get(i);
                if (a.localName.equals(localName) && a.nsUri.equals(nsUri)) {
                    return a.value;
                }
            }
        }
        return null;
    }

    /**
     * Reads the header as a {@link XMLStreamReader}
     */
    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        return completeHeader.readAsXMLStreamReader();
    }

    @Override
    public void writeTo(XMLStreamWriter w) throws XMLStreamException {
        try {
            // TODO what about in-scope namespaces
            completeHeader.writeToXMLStreamWriter(w);
        } catch (Exception e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        try {
            // TODO what about in-scope namespaces
            // Not very efficient consider implementing a stream buffer
            // processor that produces a DOM node from the buffer.
            TransformerFactory tf = WSITXMLFactory.createTransformerFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
            Transformer t = tf.newTransformer();
            XMLStreamBufferSource source = new XMLStreamBufferSource(completeHeader);
            DOMResult result = new DOMResult();
            t.transform(source, result);
            Node d = result.getNode();
            if (d.getNodeType() == Node.DOCUMENT_NODE) {
                d = d.getFirstChild();
            }
            SOAPHeader header = saaj.getSOAPHeader();
            Node node = header.getOwnerDocument().importNode(d, true);
            header.appendChild(node);
        } catch (Exception e) {
            throw new SOAPException(e);
        }
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        completeHeader.writeTo(contentHandler);
    }

    @Override
    public String getStringContent() {
        try {
            XMLStreamReader xsr = readHeader();
            xsr.nextTag();
            return xsr.getElementText();
        } catch (XMLStreamException e) {
            return null;
        }
    }

    private static String fixNull(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readAsJAXB(Unmarshaller um) throws jakarta.xml.bind.JAXBException {
        try {
            return (T) um.unmarshal(completeHeader.readAsXMLStreamReader());
        } catch (Exception e) {
            throw new JAXBException(e);
        }
    }

    @Override
    public <T> T readAsJAXB(org.glassfish.jaxb.runtime.api.Bridge<T> bridge) throws jakarta.xml.bind.JAXBException {
        try {
            return bridge.unmarshal(completeHeader.readAsXMLStreamReader());
        } catch (Exception e) {
            throw new JAXBException(e);
        }
    }

    @Override
    public <T> T readAsJAXB(com.sun.xml.ws.spi.db.XMLBridge<T> bridge) throws jakarta.xml.bind.JAXBException {
        try {
            return bridge.unmarshal(completeHeader.readAsXMLStreamReader(), null);
        } catch (Exception e) {
            throw new JAXBException(e);
        }
    }

    @Override
    public HashMap<String, String> getInscopeNSContext() {
        return nsDecls;
    }

    protected static final class Attribute {

        /**
         * Can be empty but never null.
         */
        final String nsUri;
        final String localName;
        final String value;

        public Attribute(String nsUri, String localName, String value) {
            this.nsUri = fixNull(nsUri);
            this.localName = localName;
            this.value = value;
        }
    }

    class IDProcessor implements StreamFilter {

        boolean elementRead = false;

        @Override
        @SuppressWarnings("unchecked")
        public boolean accept(XMLStreamReader reader) {
            if (reader.getEventType() == XMLStreamReader.END_ELEMENT) {
                if (reader.getLocalName().equals(localName) && reader.getNamespaceURI().equals(namespaceURI)) {
                    elementRead = true;
                }
            }
            if (!elementRead && reader.getEventType() == XMLStreamReader.START_ELEMENT) {
                String id = reader.getAttributeValue(MessageConstants.WSU_NS, "Id");
                if (id != null && id.length() > 0) {
                    idValues.add(id);
                }
            }

            return true;
        }
    }
}
