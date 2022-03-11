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

import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.ws.security.opt.api.NamespaceContextInfo;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.opt.impl.util.StreamUtil;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class SecurityContextToken implements SecurityHeaderElement, NamespaceContextInfo,
        SecurityElementWriter, com.sun.xml.ws.security.SecurityContextToken {

    private static final String IDENTIFIER = "Identifier".intern();
    private static final String INSTANCE = "Instance".intern();

    private static final int IDENTIFIER_ELEMENT = 1;
    private static final int INSTANCE_ELEMENT = 2;

    private String id = "";
    private String namespaceURI = "";
    private String localName = "";
    private String identifier = null;
    private String instance = null;
    private List extElements = null;
    private JAXBFilterProcessingContext pc;
    private MutableXMLStreamBuffer buffer = null;
    private HashMap<String,String> nsDecls;

    /** Creates a new instance of SecurityContextToken */
    @SuppressWarnings("unchecked")
    public SecurityContextToken(XMLStreamReader reader,JAXBFilterProcessingContext pc,
            HashMap nsDecls) throws XMLStreamException, XWSSecurityException {
        this.pc = pc;
        this.nsDecls = nsDecls;
        id = reader.getAttributeValue(MessageConstants.WSU_NS,"Id");
        namespaceURI = reader.getNamespaceURI();
        localName = reader.getLocalName();
        buffer = new MutableXMLStreamBuffer();
        buffer.createFromXMLStreamReader(reader);
        XMLStreamReader sct =  buffer.readAsXMLStreamReader();
        sct.next();
        process(sct);
    }

    public String getSCId(){
        return identifier;
    }

    @Override
    public URI getIdentifier() {
        try {
            return new URI(identifier);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getInstance() {
        return instance;
    }

    @Override
    public List getExtElements() {
        return extElements;
    }

    @Override
    public boolean refersToSecHdrWithId(final String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        throw new UnsupportedOperationException();
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
    public javax.xml.stream.XMLStreamReader readHeader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(OutputStream os) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws javax.xml.stream.XMLStreamException {
        buffer.writeToXMLStreamWriter(streamWriter);
    }


    @Override
    public HashMap<String, String> getInscopeNSContext() {
        return nsDecls;
    }

    private void process(XMLStreamReader reader) throws XMLStreamException, XWSSecurityException {


        if(StreamUtil.moveToNextElement(reader)){
            int refElement = getEventType(reader);
            while(reader.getEventType() != reader.END_DOCUMENT){
                switch(refElement){
                    case IDENTIFIER_ELEMENT : {
                        identifier = reader.getElementText();
                        break;
                    }
                    case INSTANCE_ELEMENT:{
                        instance = reader.getElementText();
                        break;
                    }
                    // extension elements?
                    default :{
                        throw new XWSSecurityException("Element name "+reader.getName()+" is not recognized under SecurityContextToken");
                    }
                }
                if(StreamUtil.moveToNextStartOREndElement(reader) &&
                        StreamUtil._break(reader, "SecurityContextToken", MessageConstants.WSSC_NS)){

                    break;
                }else{
                    if(reader.getEventType() != XMLStreamReader.START_ELEMENT){
                        StreamUtil.moveToNextElement(reader);
                    }
                }
                refElement = getEventType(reader);
            }

        }
    }

    private int getEventType(javax.xml.stream.XMLStreamReader reader) {
        if(reader.getEventType()== XMLStreamReader.START_ELEMENT){
            if(reader.getLocalName() == IDENTIFIER){
                return IDENTIFIER_ELEMENT;
            }
            if(reader.getLocalName() == INSTANCE){
                return INSTANCE_ELEMENT;
            }
        }
        return -1;
    }

    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter, HashMap props) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getWsuId() {
        return id;
    }

    @Override
    public String getType() {
        return MessageConstants.SECURITY_CONTEXT_TOKEN_NS;
    }

    @Override
    public Object getTokenValue() {
        return this;
    }

}
