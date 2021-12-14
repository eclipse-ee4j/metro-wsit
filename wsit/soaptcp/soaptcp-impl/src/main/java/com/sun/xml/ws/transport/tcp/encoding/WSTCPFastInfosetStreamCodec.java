/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.encoding;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.pipe.Codecs;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.ws.transport.tcp.encoding.WSTCPFastInfosetStreamReaderRecyclable.RecycleAwareListener;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.fastinfoset.FastInfosetStreamSOAPCodec;
import com.sun.xml.ws.message.stream.StreamHeader;
import com.sun.xml.ws.transport.tcp.encoding.configurator.WSTCPCodecConfigurator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import jakarta.xml.ws.WebServiceException;

/**
 * @author Alexey Stashok
 */
public abstract class WSTCPFastInfosetStreamCodec implements Codec {
    private StAXDocumentParser _statefulParser;
    private StAXDocumentSerializer _serializer;
    
    private final StreamSOAPCodec _soapCodec;
    private final boolean _retainState;
    
    protected final ContentType _defaultContentType;
    
    private final RecycleAwareListener _readerRecycleListener;
    
    /* package */ WSTCPFastInfosetStreamCodec(@Nullable StreamSOAPCodec soapCodec, @NotNull SOAPVersion soapVersion,
            @NotNull RecycleAwareListener readerRecycleListener, boolean retainState, String mimeType) {
        _soapCodec = soapCodec != null ? soapCodec : Codecs.createSOAPEnvelopeXmlCodec(soapVersion);
        _readerRecycleListener = readerRecycleListener;
        _retainState = retainState;
        _defaultContentType = new ContentTypeImpl(mimeType);
    }
    
    /* package */ WSTCPFastInfosetStreamCodec(WSTCPFastInfosetStreamCodec that) {
        this._soapCodec = (StreamSOAPCodec) that._soapCodec.copy();
        this._readerRecycleListener = that._readerRecycleListener;
        this._retainState = that._retainState;
        this._defaultContentType = that._defaultContentType;
    }
    
    @Override
    public String getMimeType() {
        return _defaultContentType.getContentType();
    }
    
    @Override
    public ContentType getStaticContentType(Packet packet) {
        return getContentType(packet.soapAction);
    }
    
    @Override
    public ContentType encode(Packet packet, OutputStream out) {
        if (packet.getMessage() != null) {
            final XMLStreamWriter writer = getXMLStreamWriter(out);
            try {
                packet.getMessage().writeTo(writer);
                writer.flush();
            } catch (XMLStreamException e) {
                throw new WebServiceException(e);
            }
        }
        return getContentType(packet.soapAction);
    }
    
    @Override
    public ContentType encode(Packet packet, WritableByteChannel buffer) {
        //TODO: not yet implemented
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void decode(InputStream in, String contentType, Packet response) {
        response.setMessage(
                _soapCodec.decode(getXMLStreamReader(in)));
    }
    
    @Override
    public void decode(ReadableByteChannel in, String contentType, Packet response) {
        throw new UnsupportedOperationException();
    }
    
    protected abstract StreamHeader createHeader(XMLStreamReader reader, XMLStreamBuffer mark);
    
    protected abstract ContentType getContentType(String soapAction);
    
    private XMLStreamWriter getXMLStreamWriter(OutputStream out) {
        if (_serializer != null) {
            _serializer.setOutputStream(out);
            return _serializer;
        } else {
            WSTCPCodecConfigurator configurator = WSTCPCodecConfigurator.INSTANCE;
            StAXDocumentSerializer serializer = configurator.getDocumentSerializerFactory().newInstance();
            serializer.setOutputStream(out);
            
            if (_retainState) {
                SerializerVocabulary vocabulary = configurator.getSerializerVocabularyFactory().newInstance();
                serializer.setVocabulary(vocabulary);
                serializer.setMinAttributeValueSize(
                        configurator.getMinAttributeValueSize());
                serializer.setMaxAttributeValueSize(
                        configurator.getMaxAttributeValueSize());
                serializer.setMinCharacterContentChunkSize(
                        configurator.getMinCharacterContentChunkSize());
                serializer.setMaxCharacterContentChunkSize(
                        configurator.getMaxCharacterContentChunkSize());
                serializer.setAttributeValueMapMemoryLimit(
                        configurator.getAttributeValueMapMemoryLimit());
                serializer.setCharacterContentChunkMapMemoryLimit(
                        configurator.getCharacterContentChunkMapMemoryLimit());
            }
            _serializer = serializer;
            return serializer;
        }
    }
    
    private XMLStreamReader getXMLStreamReader(InputStream in) {
        if (_statefulParser != null) {
            _statefulParser.setInputStream(in);
            return _statefulParser;
        } else {
            WSTCPCodecConfigurator configurator = WSTCPCodecConfigurator.INSTANCE;
            StAXDocumentParser parser = configurator.getDocumentParserFactory().newInstance();
            parser.setInputStream(in);
            if (parser instanceof WSTCPFastInfosetStreamReaderRecyclable) {
                ((WSTCPFastInfosetStreamReaderRecyclable) parser).
                        setListener(_readerRecycleListener);
            }
            
            parser.setStringInterning(true);
            if (_retainState) {
                ParserVocabulary vocabulary = configurator.
                        getParserVocabularyFactory().newInstance();
                parser.setVocabulary(vocabulary);
            }
            _statefulParser = parser;
            return _statefulParser;
        }
    }
    
    /**
     * Creates a new {@link FastInfosetStreamSOAPCodec} instance.
     *
     * @param version the SOAP version of the codec.
     * @return a new {@link WSTCPFastInfosetStreamCodec} instance.
     */
    public static WSTCPFastInfosetStreamCodec create(StreamSOAPCodec soapCodec, 
            SOAPVersion version, RecycleAwareListener readerRecycleListener, boolean retainState) {
        if(version==null)
            // this decoder is for SOAP, not for XML/HTTP
            throw new IllegalArgumentException();
        switch(version) {
            case SOAP_11:
                return new WSTCPFastInfosetStreamSOAP11Codec(soapCodec, readerRecycleListener, retainState);
            case SOAP_12:
                return new WSTCPFastInfosetStreamSOAP12Codec(soapCodec, readerRecycleListener, retainState);
            default:
                throw new AssertionError();
        }
    }
}
