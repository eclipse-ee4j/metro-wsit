/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.encoding;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;

import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class LazyStreamCodec implements StreamSOAPCodec{

    private StreamSOAPCodec  codec = null;
    /** Creates a new instance of SecurityStream11Codec */
    public LazyStreamCodec(StreamSOAPCodec codec) {
        this.codec = codec;
    }

    @Override
    public Message decode(XMLStreamReader reader) {
        return new com.sun.xml.ws.security.message.stream.LazyStreamBasedMessage(reader,codec);
    }

    public  @NotNull@Override
 Message decode(@NotNull XMLStreamReader reader, AttachmentSet att){
        return new com.sun.xml.ws.security.message.stream.LazyStreamBasedMessage(reader,codec, att);
    }

    @Override
    public String getMimeType() {
        return codec.getMimeType();
    }

    @Override
    public ContentType getStaticContentType(Packet packet) {
        return codec.getStaticContentType(packet);
    }

    @Override
    public ContentType encode(Packet packet, OutputStream outputStream) throws IOException {
        return codec.encode(packet,outputStream);
    }

    @Override
    public ContentType encode(Packet packet, WritableByteChannel writableByteChannel) {
        return codec.encode(packet,writableByteChannel);
    }

    @Override
    public Codec copy() {
        return this;
    }

    @Override
    public void decode(InputStream inputStream, String string, Packet packet) {
        XMLStreamReader reader = XMLStreamReaderFactory.create(null, inputStream,true);
        packet.setMessage(decode(reader));
    }

    @Override
    public void decode(ReadableByteChannel readableByteChannel, String string, Packet packet) {
        throw new UnsupportedOperationException();
    }

}
