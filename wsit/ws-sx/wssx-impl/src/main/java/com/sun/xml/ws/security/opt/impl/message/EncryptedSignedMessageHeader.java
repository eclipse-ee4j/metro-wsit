/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.message;

import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.dsig.SignedMessageHeader;
import com.sun.xml.wss.impl.c14n.AttributeNS;
import com.sun.xml.wss.impl.c14n.StAXAttr;
import java.util.HashMap;
import java.util.Vector;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class EncryptedSignedMessageHeader extends SignedMessageHeader{

    private SecurityHeaderElement encHeader = null;
    private boolean parsed = false;
    private String localName;
    private String uri;
    private String prefix;
    private Vector attrList = new Vector();
    private Vector attrNSList = new Vector();

    private MutableXMLStreamBuffer buffer = null;

    /** Creates a new instance of EncryptedSignedMessageHeader */
    public EncryptedSignedMessageHeader(SignedMessageHeader hdr, SecurityHeaderElement she) {
        super(hdr);
        encHeader = she;
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
            super.writeTo(writer);
        }
        return buffer.readAsXMLStreamReader();
    }

    /**
     * Write the header to an XMLStreamWriter
     */
    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws javax.xml.stream.XMLStreamException {
        if(!parsed){
            parse();
        }
        writeStartElement(streamWriter);
        ((SecurityElementWriter)encHeader).writeTo(streamWriter);
        writeEndElement(streamWriter);
    }

    /**
     * Write the header to an XMLStreamWriter
     */
    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter, final HashMap props) throws javax.xml.stream.XMLStreamException {
        if(!parsed){
            parse();
        }
        writeStartElement(streamWriter);
        ((SecurityElementWriter)encHeader).writeTo(streamWriter, props);
        writeEndElement(streamWriter);

    }
    @SuppressWarnings("unchecked")
    protected void parse()throws XMLStreamException{
        XMLStreamReader reader = readHeader();
        parsed = true;
        boolean stop = false;
        while(reader.hasNext()){
            int eventType = reader.next();
            if(stop){
                return;
            }
            switch(eventType){
                case XMLStreamConstants.START_ELEMENT :{
                    localName = reader.getLocalName();
                    uri = reader.getNamespaceURI();
                    prefix = reader.getPrefix();
                    if(prefix == null)
                        prefix = "";
                    int count = reader.getAttributeCount();
                    for(int i=0;i<count ;i++){
                        String localName = reader.getAttributeLocalName(i);
                        String uri = reader.getAttributeNamespace(i);
                        String prefix = reader.getAttributePrefix(i);
                        if(prefix == null)
                            prefix = "";
                        final String value = reader.getAttributeValue(i);
                        StAXAttr attr = new StAXAttr();
                        attr.setLocalName(localName);
                        attr.setValue(value);
                        attr.setPrefix(prefix);
                        attr.setUri(uri);
                        attrList.add(attr);
                    }

                    count = 0;
                    count = reader.getNamespaceCount();
                    for(int i=0;i<count ;i++){
                        String prefix = reader.getNamespacePrefix(i);
                        if(prefix == null)
                            prefix = "";
                        String uri = reader.getNamespaceURI(i);
                        AttributeNS attrNS = new AttributeNS();
                        attrNS.setPrefix(prefix);
                        attrNS.setUri(uri);
                        attrNSList.add(attrNS);
                    }
                    stop = true;
                    break;
                }
                case XMLStreamConstants.END_ELEMENT :{
                    stop = true;
                    break;
                }
            }

        }
    }

    private void writeEndElement(XMLStreamWriter xsw) throws XMLStreamException{
        xsw.writeEndElement();
    }

    private void writeStartElement(XMLStreamWriter xsw) throws XMLStreamException{
        xsw.writeStartElement(prefix,localName,uri);
        for(int i=0;i<attrNSList.size();i++){
            AttributeNS attrNs = (AttributeNS)attrNSList.get(i);
            xsw.writeNamespace(attrNs.getPrefix(),attrNs.getUri());
        }
        for(int i=0;i<attrList.size();i++){
            StAXAttr attr = (StAXAttr) attrList.get(i);
            xsw.writeAttribute(attr.getPrefix(),attr.getUri(),attr.getLocalName(),attr.getValue());
        }
    }
}
