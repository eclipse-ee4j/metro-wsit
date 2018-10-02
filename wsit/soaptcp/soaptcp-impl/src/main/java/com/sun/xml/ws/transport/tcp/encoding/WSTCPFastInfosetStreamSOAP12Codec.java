/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.encoding;

import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.message.stream.StreamHeader;
import com.sun.xml.ws.message.stream.StreamHeader12;
import com.sun.xml.ws.transport.tcp.encoding.WSTCPFastInfosetStreamReaderRecyclable.RecycleAwareListener;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.encoding.fastinfoset.FastInfosetMIMETypes;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Alexey Stashok
 */
public class WSTCPFastInfosetStreamSOAP12Codec extends WSTCPFastInfosetStreamCodec{
    /*package*/ WSTCPFastInfosetStreamSOAP12Codec(StreamSOAPCodec soapCodec, 
            RecycleAwareListener readerRecycleListener, boolean retainState) {
        super(soapCodec, SOAPVersion.SOAP_12, readerRecycleListener, retainState,
                (retainState) ? FastInfosetMIMETypes.STATEFUL_SOAP_12 : FastInfosetMIMETypes.SOAP_12);
    }

    private WSTCPFastInfosetStreamSOAP12Codec(WSTCPFastInfosetStreamSOAP12Codec that) {
        super(that);
    }

    @Override
    public Codec copy() {
        return new WSTCPFastInfosetStreamSOAP12Codec(this);
    }

    @Override
    protected final StreamHeader createHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
        return new StreamHeader12(reader, mark);
    }
    
    @Override
    protected ContentType getContentType(String soapAction) {
        if (soapAction == null) {
            return _defaultContentType;
        } else {
            return new ContentTypeImpl(
                    _defaultContentType.getContentType() + ";action=\""+soapAction+"\"");
        }
    }

}
