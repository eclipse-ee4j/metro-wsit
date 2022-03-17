/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.incoming;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.istack.XMLStreamReaderToContentHandler;
import org.glassfish.jaxb.runtime.api.Bridge;
import com.sun.xml.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.encoding.TagInfoset;
import com.sun.xml.ws.message.AbstractMessageImpl;
import com.sun.xml.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.util.xml.DummyLocation;
import com.sun.xml.ws.util.xml.StAXSource;
import java.security.PrivilegedActionException;
import java.util.Map;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import jakarta.xml.ws.WebServiceException;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.ws.message.stream.StreamMessage;
import com.sun.xml.ws.security.message.stream.LazyStreamBasedMessage;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.wss.logging.impl.opt.LogStringsMessages;
import com.sun.xml.ws.security.opt.impl.util.VerifiedMessageXMLStreamReader;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter;

/**
 * {@link Message} implementation backed by {@link XMLStreamReader}.
 *
 * TODO: we need another message class that keeps {@link XMLStreamReader} that points
 * at the start of the envelope element.
 */
public final class VerifiedStreamMessage extends AbstractMessageImpl {

    /**
     * The reader will be positioned at
     * the first child of the SOAP body
     */
    private
    @NotNull
    XMLStreamReader reader;
    private MutableXMLStreamBuffer buffer = null;
    // lazily created
    private
    @Nullable
    MessageHeaders headers;
    private final String payloadLocalName;
    private final String payloadNamespaceURI;
    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_DOMAIN,
            LogDomainConstants.IMPL_OPT_DOMAIN_BUNDLE);
    private Map<String, String> bodyEnvNs;
    /**
     * infoset about the SOAP envelope, header, and body.
     *
     * <p>
     * If the creator of this object didn't care about those,
     * we use stock values.
     */
    private /*almost final*/
    @NotNull
    TagInfoset envelopeTag,  headerTag,  bodyTag;

    /**
     * Creates a {@link StreamMessage} from a {@link XMLStreamReader}
     * that points at the start element of the payload, and headers.
     *
     * <p>
     * This method creaets a {@link Message} from a payload.
     *
     * @param headers
     *      if null, it means no headers. if non-null,
     *      it will be owned by this message.
     * @param reader
     *      points at the start element/document of the payload (or the end element of the &lt;s:Body&gt;
     *      if there's no payload)
     */
    public VerifiedStreamMessage(
            @Nullable MessageHeaders headers,
            @NotNull AttachmentSet attachmentSet,
            @NotNull XMLStreamReader reader,
            @NotNull SOAPVersion soapVersion,  Map<String, String> bodyEnvNs) {
        super(soapVersion);
        this.headers = headers;
        this.attachmentSet = attachmentSet;
        this.reader = reader;
        this.bodyEnvNs = bodyEnvNs;

        if (reader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
            XMLStreamReaderUtil.nextElementContent(reader);
        }

        //if the reader is pointing to the end element </soapenv:Body> then its empty message
        // or no payload
        if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
            String body = reader.getLocalName();
            String nsUri = reader.getNamespaceURI();
            assert body != null;
            assert nsUri != null;
            //if its not soapenv:Body then throw exception, we received malformed stream
            if (body.equals("Body") && nsUri.equals(soapVersion.nsUri)) {
                this.payloadLocalName = null;
                this.payloadNamespaceURI = null;
            } else { //TODO: i18n and also we should be throwing better message that this
                throw new WebServiceException("Malformed stream: {" + nsUri + "}" + body);
            }
        } else {
            this.payloadLocalName = reader.getLocalName();
            this.payloadNamespaceURI = reader.getNamespaceURI();
        }

        // use the default infoset representation for headers
        int base = soapVersion.ordinal() * 3;
        this.envelopeTag = DEFAULT_TAGS[base];
        this.headerTag = DEFAULT_TAGS[base + 1];
        this.bodyTag = DEFAULT_TAGS[base + 2];
    }

    /**
     * Creates a {@link StreamMessage} from a {@link XMLStreamReader}
     * and the complete infoset of the SOAP envelope.
     *
     * <p>
     * See  #StreamMessage(MessageHeaders, AttachmentSet, XMLStreamReader, SOAPVersion) for
     * the description of the basic parameters.
     *
     * @param headerTag
     *      Null if the message didn't have a header tag.
     *
     */
    public VerifiedStreamMessage(
            @NotNull TagInfoset envelopeTag,
            @Nullable TagInfoset headerTag,
            @NotNull AttachmentSet attachmentSet,
            @Nullable MessageHeaders headers,
            @NotNull TagInfoset bodyTag,
            @NotNull XMLStreamReader reader,
            @NotNull SOAPVersion soapVersion,  Map<String, String> bodyEnvNs) {
        this(headers, attachmentSet, reader, soapVersion, bodyEnvNs);
        assert envelopeTag != null && bodyTag != null;
        this.envelopeTag = envelopeTag;
        this.headerTag = headerTag != null ? headerTag : new TagInfoset(envelopeTag.nsUri, "Header", envelopeTag.prefix, EMPTY_ATTS);
        this.bodyTag = bodyTag;
    }

    @Override
    public boolean hasHeaders() {
        // FIXME: RJE -- remove cast when MessageHeaders supports hasHeaders
        return headers != null && !((HeaderList)headers).isEmpty();
    }

    @Override
    public HeaderList getHeaders() {
        if (headers == null) {
            headers = new HeaderList(soapVersion);
        }
        // FIXME: RJE -- remove cast once getHeaders returns MessageContext
        return (HeaderList) headers;
    }

    @Override
    public
    @NotNull
    AttachmentSet getAttachments() {
        return attachmentSet;
    }

    @Override
    public String getPayloadLocalPart() {
        return payloadLocalName;
    }

    @Override
    public String getPayloadNamespaceURI() {
        return payloadNamespaceURI;
    }

    @Override
    public boolean hasPayload() {
        return payloadLocalName != null;
    }

    @Override
    public Source readPayloadAsSource() {
        cacheMessage();
        if (hasPayload()) {
            assert unconsumed();
            return new StAXSource(reader, true);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readPayloadAsJAXB(final Unmarshaller unmarshaller) {
        try {
            cacheMessage();
            if (!hasPayload()) {
                return null;
            }
            assert unconsumed();
            // TODO: How can the unmarshaller process this as a fragment?
            final Object ret = AccessController.doPrivileged(new PrivilegedExceptionAction() {
                @Override
                public Object run() throws Exception {
                    if (hasAttachments()) {
                        unmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(getAttachments()));
                    }
                    try {
                        if (XMLStreamConstants.END_DOCUMENT == reader.getEventType() && buffer != null) {
                            try {
                                reader = buffer.readAsXMLStreamReader();
                                reader = new VerifiedMessageXMLStreamReader(reader, bodyEnvNs);
                                reader.next();
                            } catch (XMLStreamException ex) {
                                logger.log(Level.SEVERE, LogStringsMessages.WSS_1612_ERROR_READING_BUFFER(), ex);
                                throw new com.sun.xml.wss.impl.XWSSecurityRuntimeException(ex);
                            }
                        }
                        return unmarshaller.unmarshal(reader);
                    } finally {
                        unmarshaller.setAttachmentUnmarshaller(null);
                        XMLStreamReaderUtil.close(reader);
                        XMLStreamReaderFactory.recycle(reader);
                    }
                }
            });
            return (T)ret;
        } catch (PrivilegedActionException ex) {
            throw new XWSSecurityRuntimeException(ex);
        }
    }

    @Override
    public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
        cacheMessage();
        if (!hasPayload()) {
            return null;
        }
        assert unconsumed();
        T r = bridge.unmarshal(reader,
                hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null);
        XMLStreamReaderUtil.close(reader);
        XMLStreamReaderFactory.recycle(reader);
        return r;
    }

    @Override
    public <T> T readPayloadAsJAXB(com.sun.xml.ws.spi.db.XMLBridge<T> bridge) throws JAXBException {
        cacheMessage();
        if (!hasPayload()) {
            return null;
        }
        assert unconsumed();
        T r = bridge.unmarshal(reader,
                hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null);
        XMLStreamReaderUtil.close(reader);
        XMLStreamReaderFactory.recycle(reader);
        return r;
    }

    @Override
    public void consume() {
        assert unconsumed();
        XMLStreamReaderFactory.recycle(reader);
    }

    @Override
    public XMLStreamReader readPayload() {
        cacheMessage();
        // TODO: What about access at and beyond </soap:Body>
        assert unconsumed();
        return this.reader;
    }

    @Override
    public void writePayloadTo(XMLStreamWriter writer) throws XMLStreamException {
        if (payloadLocalName == null) {
            return;
        } // no body
        assert unconsumed();
        XMLStreamReaderToXMLStreamWriter conv = new XMLStreamReaderToXMLStreamWriter();
        while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
            String name = reader.getLocalName();
            String nsUri = reader.getNamespaceURI();

            //after previous conv.bridge() call the cursor will be at
            //END_ELEMENT. Check if its not soapenv:Body then move to next
            // ELEMENT
            if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                if (!name.equals("Body") || !nsUri.equals(soapVersion.nsUri)) {
                    XMLStreamReaderUtil.nextElementContent(reader);
                    if (reader.getEventType() == XMLStreamConstants.END_DOCUMENT) {
                        break;
                    }
                    name = reader.getLocalName();
                    nsUri = reader.getNamespaceURI();
                }
            }
            if (name.equals("Body") && nsUri.equals(soapVersion.nsUri) || (reader.getEventType() == XMLStreamConstants.END_DOCUMENT)) {
                break;
            }
            conv.bridge(reader, writer);
        }
        reader.close();
        XMLStreamReaderFactory.recycle(reader);
    }

    @Override
    public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
        writeEnvelope(sw);
    }

    /**
     * This method should be called when the StreamMessage is created with a payload
     */
    private void writeEnvelope(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartDocument();
        envelopeTag.writeStart(writer);

        //write headers
        // FIXME: RJE -- remove cast
        HeaderList hl = getHeaders();
        if (hl.size() > 0) {
            headerTag.writeStart(writer);
            for (Header h : hl) {
                h.writeTo(writer);
            }
            writer.writeEndElement();
        }
        bodyTag.writeStart(writer);
        if (hasPayload()) {
            writePayloadTo(writer);
        }
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
    }

    @Override
    public void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
        assert unconsumed();
        try {
            if (payloadLocalName == null) {
                return;
            } // no body

            XMLStreamReaderToContentHandler conv =
                    new XMLStreamReaderToContentHandler(reader, contentHandler, true, fragment);

            while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
                String name = reader.getLocalName();
                String nsUri = reader.getNamespaceURI();

                //after previous conv.bridge() call the cursor will be at
                //END_ELEMENT. Check if its not soapenv:Body then move to next
                // ELEMENT
                if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                    if (!name.equals("Body") || !nsUri.equals(soapVersion.nsUri)) {
                        XMLStreamReaderUtil.nextElementContent(reader);
                        if (reader.getEventType() == XMLStreamConstants.END_DOCUMENT) {
                            break;
                        }
                        name = reader.getLocalName();
                        nsUri = reader.getNamespaceURI();
                    }
                }
                if (name.equals("Body") && nsUri.equals(soapVersion.nsUri) || (reader.getEventType() == XMLStreamConstants.END_DOCUMENT)) {
                    break;
                }

                conv.bridge();
            }
            reader.close();
            XMLStreamReaderFactory.recycle(reader);
        } catch (XMLStreamException e) {
            Location loc = e.getLocation();
            if (loc == null) {
                loc = DummyLocation.INSTANCE;
            }

            SAXParseException x = new SAXParseException(
                    e.getMessage(), loc.getPublicId(), loc.getSystemId(), loc.getLineNumber(), loc.getColumnNumber(), e);
            errorHandler.error(x);
        }
    }

    @Override
    public Message copy() {
        try {
            // copy the payload
            XMLStreamReader clone;
            XMLStreamReader clonedReader;

            if (hasPayload()) {
                assert unconsumed();
                consumedAt = null; // but we don't want to mark it as consumed
                MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();

                //the boolean value tells the first body part is written.
                //based on this we do the right thing
                StreamReaderBufferCreator c = new StreamReaderBufferCreator(xsb);
                while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
                    String name = reader.getLocalName();
                    String nsUri = reader.getNamespaceURI();
                    if (name.equals("Body") && nsUri.equals(soapVersion.nsUri) || (reader.getEventType() == XMLStreamConstants.END_DOCUMENT)) {
                        break;
                    }
                    c.create(reader);
                }
                XMLStreamReaderFactory.recycle(reader);

                reader = xsb.readAsXMLStreamReader();
                reader = new VerifiedMessageXMLStreamReader(reader, bodyEnvNs);
                clone = xsb.readAsXMLStreamReader();
                clonedReader = new VerifiedMessageXMLStreamReader(clone, bodyEnvNs);
                // advance to the start tag of the first element
                proceedToRootElement(reader);
                proceedToRootElement(clonedReader);
            } else {
                // it's tempting to use EmptyMessageImpl, but it doesn't presere the infoset
                // of <envelope>,<header>, and <body>, so we need to stick to StreamMessage.
                clone = reader;
                clonedReader = reader;
            }

            // FIXME: RJE -- remove cast once HeaderList.copy() can handle MessageContext
            return new VerifiedStreamMessage(envelopeTag, headerTag, attachmentSet, HeaderList.copy((HeaderList)headers), bodyTag, clone, soapVersion, this.bodyEnvNs);
        } catch (XMLStreamException e) {
            throw new WebServiceException("Failed to copy a message", e);
        }
    }

    private void proceedToRootElement(XMLStreamReader xsr) throws XMLStreamException {
        assert xsr.getEventType() == XMLStreamConstants.START_DOCUMENT;
        xsr.nextTag();
        assert xsr.getEventType() == XMLStreamConstants.START_ELEMENT;
    }

    @Override
    public SOAPMessage readAsSOAPMessage() throws SOAPException {
    cacheMessage();
    return super.readAsSOAPMessage();
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        contentHandler.setDocumentLocator(NULL_LOCATOR);
        contentHandler.startDocument();
        envelopeTag.writeStart(contentHandler);
        headerTag.writeStart(contentHandler);
        if (hasHeaders()) {
            // FIXME: remove cast
            HeaderList headerList = getHeaders();
            int len = headerList.size();
            for (int i = 0; i < len; i++) {
                // shouldn't JDK be smart enough to use array-style indexing for this foreach!?
                headerList.get(i).writeTo(contentHandler, errorHandler);
            }
        }
        headerTag.writeEnd(contentHandler);
        bodyTag.writeStart(contentHandler);
        writePayloadTo(contentHandler, errorHandler, true);
        bodyTag.writeEnd(contentHandler);
        envelopeTag.writeEnd(contentHandler);

    }

    /**
     * Used for an assertion. Returns true when the message is unconsumed,
     * or otherwise throw an exception.
     *
     * <p>
     * Calling this method also marks the stream as 'consumed'
     */
    private boolean unconsumed() {
        if (payloadLocalName == null) {
            return true;
        }    // no payload. can be consumed multiple times.

        if (reader.getEventType() != XMLStreamReader.START_ELEMENT) {
            System.out.append("Event Type=").append(String.valueOf(reader.getEventType())).append(" name=").append(reader.getLocalName());
            System.out.append("START " + XMLStreamReader.START_ELEMENT);
            System.out.append("END =" + XMLStreamReader.END_ELEMENT);
            AssertionError error = new AssertionError("StreamMessage has been already consumed. See the nested exception for where it's consumed");
            error.initCause(consumedAt);
            throw error;
        }
        consumedAt = new Exception().fillInStackTrace();
        return true;
    }
    /**
     * Used only for debugging. This records where the message was consumed.
     */
    private Throwable consumedAt;
    /**
     * Default s:Envelope, s:Header, and s:Body tag infoset definitions.
     *
     * We need 3 for SOAP 1.1, 3 for SOAP 1.2.
     */
    private static final TagInfoset[] DEFAULT_TAGS;

    static {
        DEFAULT_TAGS = new TagInfoset[6];
        create(SOAPVersion.SOAP_11);
        create(SOAPVersion.SOAP_12);
    }

    private static void create(SOAPVersion v) {
        int base = v.ordinal() * 3;
        DEFAULT_TAGS[base] = new TagInfoset(v.nsUri, "Envelope", "S", EMPTY_ATTS, "S", v.nsUri);
        DEFAULT_TAGS[base + 1] = new TagInfoset(v.nsUri, "Header", "S", EMPTY_ATTS);
        DEFAULT_TAGS[base + 2] = new TagInfoset(v.nsUri, "Body", "S", EMPTY_ATTS);
    }

    private void cacheMessage() {
        //TODO 1081: need to verify this
        if (LazyStreamBasedMessage.mtomLargeData()) {
            return;
        }
        if (buffer == null) {
            try {
                buffer = new com.sun.xml.stream.buffer.MutableXMLStreamBuffer();
                buffer.createFromXMLStreamReader(reader);
            } catch (javax.xml.stream.XMLStreamException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1611_PROBLEM_CACHING(), ex);
                throw new com.sun.xml.wss.impl.XWSSecurityRuntimeException(ex);
            }

            try {
                reader = buffer.readAsXMLStreamReader();
                reader = new VerifiedMessageXMLStreamReader(reader, bodyEnvNs);
                reader.next();
            } catch (XMLStreamException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1612_ERROR_READING_BUFFER(), ex);
                throw new com.sun.xml.wss.impl.XWSSecurityRuntimeException(ex);
            }
        }

        if (reader.getEventType() == XMLStreamReader.END_DOCUMENT) {
            if (buffer != null) {
                try {
                    reader = buffer.readAsXMLStreamReader();
                    reader = new VerifiedMessageXMLStreamReader(reader, bodyEnvNs);
                    reader.next();
                } catch (XMLStreamException ex) {
                    logger.log(Level.SEVERE, LogStringsMessages.WSS_1612_ERROR_READING_BUFFER(), ex);
                    throw new com.sun.xml.wss.impl.XWSSecurityRuntimeException(ex);
                }
            }
        }

    }
}

