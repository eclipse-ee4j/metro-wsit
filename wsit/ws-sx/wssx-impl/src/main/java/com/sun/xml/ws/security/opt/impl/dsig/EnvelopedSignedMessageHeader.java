/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.dsig;

import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.crypto.dsig.Reference;
import com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx;
import com.sun.xml.wss.impl.c14n.AttributeNS;
import com.sun.xml.wss.impl.c14n.StAXEXC14nCanonicalizerImpl;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import com.sun.xml.ws.security.opt.crypto.dsig.internal.DigesterOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;



/**
 *
 * @author K.Venugopal@sun.com
 */
public class EnvelopedSignedMessageHeader implements SecurityHeaderElement,SecurityElementWriter{
    private Reference ref = null;
    private SecurityHeaderElement she = null;
    private StAXEXC14nCanonicalizerImpl stAXC14n = null;    
    private String id = "";
    private NamespaceContextEx nsContext = null;
    /** Creates a new instance of EnvelopedSignedMessageHeader */
    public EnvelopedSignedMessageHeader(SecurityHeaderElement she,Reference ref,JAXBSignatureHeaderElement jse,NamespaceContextEx nsContext) {
        this.she = she;
        this.ref = ref;
        //this.jse = jse;
        this.nsContext = nsContext;
        stAXC14n = new StAXEXC14nCanonicalizerImpl();
    }
    
    public boolean refersToSecHdrWithId(final String id) {
        throw new UnsupportedOperationException();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(final String id) {
    }
    
    public String getNamespaceURI() {
        return she.getNamespaceURI();
    }
    
    public String getLocalPart() {
        return she.getLocalPart();
    }
    
    public XMLStreamReader readHeader() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }
    
    public byte[] canonicalize(final String algorithm, final List<AttributeNS> namespaceDecls) {
        throw new UnsupportedOperationException();
        
    }
    
    public boolean isCanonicalized() {
        throw new UnsupportedOperationException();
    }
    /**
     * writes the enveloped signed message header to an XMLStreamWriter
     * @param streamWriter XMLStreamWriter
     * @throws XMLStreamException
     */
    public void writeTo(XMLStreamWriter streamWriter) throws XMLStreamException{
        if(nsContext == null){
            throw new XMLStreamException("NamespaceContext is null in writeTo method");
        }
        
        Iterator<NamespaceContextEx.Binding> itr = nsContext.iterator();
        stAXC14n.reset();
        while(itr.hasNext()){
            final NamespaceContextEx.Binding nd = itr.next();
            stAXC14n.writeNamespace(nd.getPrefix(),nd.getNamespaceURI());
        }
        DigesterOutputStream dos= null;
        try{
            dos = ref.getDigestOutputStream();
        }catch(XMLSignatureException xse){
            throw new XMLStreamException(xse);
        }
        OutputStream os = new UnsyncBufferedOutputStream(dos);
        stAXC14n.setStream(os);
        //EnvelopedTransformWriter etw = new EnvelopedTransformWriter(streamWriter,stAXC14n,ref,jse,dos);
        ((SecurityElementWriter)she).writeTo(streamWriter);
    }
    /**
     *
     * @param streamWriter XMLStreamWriter
     * @param props HashMap
     * @throws XMLStreamException
     */
    public void writeTo(XMLStreamWriter streamWriter, HashMap props) throws XMLStreamException{
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * 
     * @param os OutputStream
     */
    public void writeTo(OutputStream os){
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
