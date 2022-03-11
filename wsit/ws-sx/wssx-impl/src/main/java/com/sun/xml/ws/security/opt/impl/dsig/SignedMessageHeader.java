/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.dsig;

import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.api.SignedData;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx;
import com.sun.xml.ws.security.opt.impl.util.XMLStreamFilterWithId;
import java.io.OutputStream;
import java.util.HashMap;
import com.sun.istack.NotNull;

import com.sun.xml.ws.api.message.Header;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * A wrapper over a <CODE>Header</CODE> or a <CODE>SecurityHeaderElement</CODE>
 * @author K.Venugopal@sun.com
 */

public class SignedMessageHeader extends SignedMessagePart
        implements SecurityHeaderElement, SignedData, SecurityElementWriter{

    private Header header = null;
    private SecurityHeaderElement she = null;

    private byte[] digestValue;

    private String id;

    JAXBFilterProcessingContext context = null;
    private MutableXMLStreamBuffer buffer = null;

    /**
     * Creates a new instance of SignedMessageHeader
     * @param header The SOAP Header which is to be signed
     * @param id The id assigned to the SOAP header
     * @param context JAXBFilterProcessingContext
     */
    public SignedMessageHeader(Header header, String id, JAXBFilterProcessingContext context ) {
        this.header = header;
        this.id = id;
        this.context = context;
    }

    /**
     *
     * Sign a <CODE>SecurityHeaderElement</CODE>
     * @param she The SecurityHeaderElement to be signed
     */
    public SignedMessageHeader(SecurityHeaderElement she){
        this.she = she;
    }

    /**
     *
     * @return the id of the SignedMessageHeader
     */
    @Override
    public String getId() {
        if(header != null){
            return id;
        } else{
            return she.getId();
        }
    }

    /**
     * Assign an id to the SignedMessageHeader
     */
    @Override
    public void setId(final String id) {
        if(header != null){
            this.id = id;
        } else {
            she.setId(id);
        }
    }

    /**
     *
     * @return the namespace of the underlying SOAP header or SecurityHeaderElement
     */
    @Override
    @NotNull
    public String getNamespaceURI() {
        if(header != null){
            return header.getNamespaceURI();
        } else {
            return she.getNamespaceURI();
        }
    }

    /**
     *
     * @return The localname of the underlying SOAP Header or SecurityHeaderElement
     */
    @Override
    @NotNull
    public String getLocalPart() {
        if(header != null){
            return header.getLocalPart();
        } else {
            return she.getLocalPart();
        }
    }

    /**
     *
     * @return The header as as XMLStreamReader
     */
    @Override
    public javax.xml.stream.XMLStreamReader readHeader() throws javax.xml.stream.XMLStreamException {
        if(buffer == null){
          buffer = new MutableXMLStreamBuffer();
          XMLStreamWriter writer = buffer.createFromXMLStreamWriter();
          this.writeTo(writer);
        }
        return buffer.readAsXMLStreamReader();
    }

    /**
     * Write the header to the passed outputStream
     */
    @Override
    public void writeTo(OutputStream os) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Write the header to an XMLStreamWriter
     */
    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws javax.xml.stream.XMLStreamException {
        if(header != null){
            XMLStreamFilterWithId xmlStreamFilterWithId = new XMLStreamFilterWithId(streamWriter, (NamespaceContextEx) context.getNamespaceContext(), id);
            header.writeTo(xmlStreamFilterWithId);
        } else{
            ((SecurityElementWriter)she).writeTo(streamWriter);
        }

    }

    /**
     * Write the header to an XMLStreamWriter
     */
    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter, final HashMap props) throws javax.xml.stream.XMLStreamException {
        /*Marshaller marshaller = getMarshaller();
        Iterator<String> itr = props.keySet().iterator();
        while(itr.hasNext()){
            String key = itr.next();
            Object value = props.get(key);
            marshaller.setProperty(key,value);
        }*/
        if(header != null){
            XMLStreamFilterWithId xmlStreamFilterWithId = new XMLStreamFilterWithId(streamWriter, (NamespaceContextEx) context.getNamespaceContext(), id);
            header.writeTo(xmlStreamFilterWithId);
        } else{
            ((SecurityElementWriter)she).writeTo(streamWriter,props);
        }
    }

    @Override
    public void setDigestValue(final byte[] digestValue){
        this.digestValue = digestValue;
    }

    /**
     *
     * @return The DigestValue of this Header
     */
    @Override
    public byte[] getDigestValue() {
        return digestValue;
    }

    /**
     *
     * @param id The id of the SecurityHeaderElement against which to compare
     * @return true if the current SecurityHeaderElement has reference to the
     * SecurityHeaderElement with passed id
     */
    @Override
    public boolean refersToSecHdrWithId(String id) {
        return she.refersToSecHdrWithId(id);
    }

    public Header getSignedHeader(){
        return header;
    }
}
