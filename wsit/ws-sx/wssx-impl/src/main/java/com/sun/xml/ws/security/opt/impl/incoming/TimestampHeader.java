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
import com.sun.xml.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.ws.security.opt.api.NamespaceContextInfo;
import com.sun.xml.ws.security.opt.api.PolicyBuilder;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.api.TokenValidator;
import com.sun.xml.ws.security.opt.api.tokens.Timestamp;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.opt.impl.incoming.processor.TimestampProcessor;
import com.sun.xml.ws.security.opt.impl.util.XMLStreamReaderFactory;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.c14n.AttributeNS;
import com.sun.xml.wss.impl.policy.mls.TimestampPolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */

public class TimestampHeader implements Timestamp, SecurityHeaderElement, TokenValidator, PolicyBuilder, NamespaceContextInfo, SecurityElementWriter{
    
    private String localPart = null;
    private String namespaceURI = null;
    private String id = "";
    
    private XMLStreamBuffer mark = null;
    private TimestampProcessor filter =null;
    
    private TimestampPolicy tsPolicy = null;
    
    private HashMap<String,String> nsDecls;
    
    /** Creates a new instance of TimestampHeader */
    @SuppressWarnings("unchecked")
    public TimestampHeader(XMLStreamReader reader, StreamReaderBufferCreator creator,
              HashMap nsDecls,JAXBFilterProcessingContext ctx) throws XMLStreamException, XMLStreamBufferException {
        localPart = reader.getLocalName();
        namespaceURI = reader.getNamespaceURI();
        id = reader.getAttributeValue(MessageConstants.WSU_NS,"Id");
        this.filter =  new TimestampProcessor(ctx);
        mark = new XMLStreamBufferMark(nsDecls,creator);
        XMLStreamReader tsReader = XMLStreamReaderFactory.createFilteredXMLStreamReader(reader,filter) ;
        creator.createElementFragment(tsReader,true);
        
        tsPolicy = new TimestampPolicy();
        tsPolicy.setUUID(id);
        tsPolicy.setCreationTime(filter.getCreated());
        tsPolicy.setExpirationTime(filter.getExpires());
        
        this.nsDecls = nsDecls;
    }
    
    @Override
    public void validate(ProcessingContext context) throws XWSSecurityException {
        context.getSecurityEnvironment().validateTimestamp(context.getExtraneousProperties(), filter.getCreated(),
                  filter.getExpires(), tsPolicy.getMaxClockSkew(), tsPolicy.getTimestampFreshness());
    }
    
    @Override
    public WSSPolicy getPolicy() {
        return tsPolicy;
    }
    
    @Override
    public void setCreated(String created) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setExpires(String expires) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getCreatedValue() {
        return filter.getCreated();
    }
    
    @Override
    public String getExpiresValue() {
        return filter.getExpires();
    }
    
    @Override
    public boolean refersToSecHdrWithId(String id) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void setId(String id) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getNamespaceURI() {
        return namespaceURI;
    }
    
    @Override
    public String getLocalPart() {
        return localPart;
    }
    
    public String getAttribute(String nsUri, String localName) {
        throw new UnsupportedOperationException();
    }
    
    public String getAttribute(QName name) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        return mark.readAsXMLStreamReader();
    }
    
    @Override
    public void writeTo(OutputStream os) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void writeTo(XMLStreamWriter streamWriter) throws XMLStreamException {
        mark.writeToXMLStreamWriter(streamWriter);
    }
    
    public byte[] canonicalize(String algorithm, List<AttributeNS> namespaceDecls) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isCanonicalized() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public HashMap<String, String> getInscopeNSContext() {
        return nsDecls;
    }
    
    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter, HashMap props) {
        throw new UnsupportedOperationException();
    }
    
}
