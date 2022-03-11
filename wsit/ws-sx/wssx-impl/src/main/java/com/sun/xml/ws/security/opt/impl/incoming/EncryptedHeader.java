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



import com.sun.xml.ws.security.opt.api.SecurityElementWriter;

import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;

import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;

import com.sun.xml.wss.XWSSecurityException;

import com.sun.xml.wss.impl.MessageConstants;

import com.sun.xml.wss.impl.policy.mls.WSSPolicy;



import java.io.InputStream;

import java.io.OutputStream;

import java.security.Key;

import java.util.HashMap;



import javax.xml.stream.XMLStreamException;

import javax.xml.stream.XMLStreamReader;

import javax.xml.stream.XMLStreamWriter;



/**

 *

 * @author Ashutosh.Shahi@sun.com

 */

public class EncryptedHeader implements SecurityHeaderElement, SecurityElementWriter  {



    private JAXBFilterProcessingContext pc = null;

    private String id = "";

    private String namespaceURI = "";

    private String localName = "";

    private EncryptedData ed = null;

    private HashMap<String, String> parentNS = null;



    /** Creates a new instance of EncryptedHeader */

    public EncryptedHeader(XMLStreamReader reader,JAXBFilterProcessingContext pc, HashMap<String, String> parentNS) throws XMLStreamException, XWSSecurityException {

        this.pc = pc;

        this.parentNS = parentNS;

        process(reader);

    }



    public EncryptedData getEncryptedData(){

        return ed;

    }



    public String getEncryptionAlgorithm(){

        return ed.getEncryptionAlgorithm();

    }



    public Key getKey(){

        return ed.getKey();

    }



    public InputStream getCipherInputStream() throws XWSSecurityException{

        return ed.getCipherInputStream();

    }



    public InputStream getCipherInputStream(Key key) throws XWSSecurityException{

        return ed.getCipherInputStream(key);

    }



    public XMLStreamReader getDecryptedData() throws XMLStreamException, XWSSecurityException{

        return ed.getDecryptedData();

    }



    public XMLStreamReader getDecryptedData(Key key) throws XMLStreamException, XWSSecurityException{

        return ed.getDecryptedData(key);

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

        return localName;

    }



    @Override
    public XMLStreamReader readHeader() {

        throw new UnsupportedOperationException();

    }



    @Override
    public void writeTo(XMLStreamWriter streamWriter) {

        throw new UnsupportedOperationException();

    }



    @Override
    public void writeTo(XMLStreamWriter streamWriter, HashMap props) {

        throw new UnsupportedOperationException();

    }



    @Override
    public void writeTo(OutputStream os) {

        throw new UnsupportedOperationException();

    }



    private void process(XMLStreamReader reader) throws XMLStreamException, XWSSecurityException{

        id = reader.getAttributeValue(MessageConstants.WSU_NS,"Id");

        namespaceURI = reader.getNamespaceURI();

        localName = reader.getLocalName();



        while(reader.hasNext()){

            reader.next();

            if(reader.getEventType() == XMLStreamReader.START_ELEMENT){

                if(MessageConstants.ENCRYPTED_DATA_LNAME.equals(reader.getLocalName()) && MessageConstants.XENC_NS.equals(reader.getNamespaceURI())){

                    ed = new EncryptedData(reader, pc, parentNS);

                }

            }



            if(reader.getEventType() == XMLStreamReader.END_ELEMENT){

                if(MessageConstants.ENCRYPTED_HEADER_LNAME.equals(reader.getLocalName()) && MessageConstants.WSSE11_NS.equals(reader.getNamespaceURI())){

                    break;

                }

            }

        }

    }



    public WSSPolicy getInferredKB(){

        return ed.getInferredKB();

    }



}

