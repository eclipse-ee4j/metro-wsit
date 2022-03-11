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
import com.sun.xml.stream.buffer.stax.StreamWriterBufferCreator;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.message.source.PayloadSourceMessage;
import com.sun.xml.ws.message.stream.StreamMessage;
import com.sun.xml.ws.security.opt.api.SecurityElement;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx;
import com.sun.xml.ws.security.opt.impl.util.XMLStreamFilterWithId;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.c14n.StAXEXC14nCanonicalizerImpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class SOAPBody{
    private static final String BODY = "Body";
    private static final String BODY_PREFIX = "S";

    private Message  message;
    private SOAPVersion soapVersion ;
    ///private byte [] byteStream;
    private SecurityElement bodyContent;
    private String wsuId;
    private String contentId;
    private MutableXMLStreamBuffer buffer = null;
    private List attributeValuePrefixes = null;

    public SOAPBody(Message message ) {
        this.message = message;
        this.soapVersion  = SOAPVersion.SOAP_11;
    }

    /**
     *
     * Creates a new instance of SOAPBody
     *
     */

    public SOAPBody(Message message,SOAPVersion soapVersion ) {
        this.message = message;
        this.soapVersion  = soapVersion;
    }

    public SOAPBody(byte[]  payLoad,SOAPVersion soapVersion ) {
        //byteStream = payLoad;
        this.soapVersion  = soapVersion;
    }

    public SOAPBody(SecurityElement se,SOAPVersion soapVersion ) {
        bodyContent = se;
        this.soapVersion  = soapVersion;
    }

    public SOAPVersion getSOAPVersion(){
        return soapVersion;
    }

    public String getId(){
        return wsuId;
    }

    public void setId(String id){
        wsuId = id;
    }

    public String getBodyContentId(){
        if(contentId != null)
            return contentId;
        else if(bodyContent != null)
            return bodyContent.getId();
        return null;
    }

    public void setBodyContentId(String id){
        this.contentId = id;
    }
    @SuppressWarnings("unchecked")
    public void writePayload(XMLStreamWriter writer)throws XMLStreamException{
        if(this.message != null){
            if(getBodyContentId() == null)
                this.message.writePayloadTo(writer);
            else{
                boolean isSOAP12 = (this.soapVersion == SOAPVersion.SOAP_12) ? true : false;
                XMLStreamFilterWithId xmlStreamFilterWithId = new XMLStreamFilterWithId(writer, new NamespaceContextEx(isSOAP12),getBodyContentId());
                this.message.writePayloadTo(xmlStreamFilterWithId);
            }
        }else if(bodyContent != null){
            ((SecurityElementWriter)bodyContent).writeTo(writer);
        }else if(buffer != null){
            if(writer instanceof StAXEXC14nCanonicalizerImpl){
                if(attributeValuePrefixes != null && !attributeValuePrefixes.isEmpty()){
                    List prefixList = ((StAXEXC14nCanonicalizerImpl)writer).getInclusivePrefixList();
                    if(prefixList == null){
                        prefixList = new ArrayList();
                    }
                    prefixList.addAll(attributeValuePrefixes);
                    // remove duplicates by going through a HashSet
                    HashSet set = new HashSet(prefixList);
                    prefixList = new ArrayList(set);
                    ((StAXEXC14nCanonicalizerImpl)writer).setInclusivePrefixList(prefixList);
                }
            }
            buffer.writeToXMLStreamWriter(writer, true);
        }else{
            throw new UnsupportedOperationException();
            //TODO
        }
    }

    public void writeTo(XMLStreamWriter writer) throws XMLStreamException{
        writer.writeStartElement(BODY_PREFIX,BODY,this.soapVersion.nsUri);
        if(wsuId != null){
            writer.writeAttribute("wsu",MessageConstants.WSU_NS,"Id",wsuId);
        }
        writePayload(writer);
        writer.writeEndElement();
        //writer.flush();
    }

    public String getPayloadNamespaceURI(){
        if(message != null){
            return message.getPayloadNamespaceURI();
        }
        if(bodyContent != null){
            return bodyContent.getNamespaceURI();
        }
        return null;
    }

    public String getPayloadLocalPart(){
        if(message != null){
            return message.getPayloadLocalPart();
        }
        if(bodyContent != null){
            return bodyContent.getLocalPart();
        }
        return null;
    }

    public XMLStreamReader read() throws XMLStreamException{
        if(message != null){
            return message.readPayload();
        }else if(bodyContent != null){
            return bodyContent.readHeader();
        }
        throw new XMLStreamException("Invalid SOAPBody");
    }

    public void cachePayLoad() throws XMLStreamException {
        if(message != null){
            if(message instanceof StreamMessage ||  message instanceof PayloadSourceMessage ||
                    message instanceof com.sun.xml.ws.message.jaxb.JAXBMessage){
                if(buffer == null){
                    buffer = new MutableXMLStreamBuffer();
                    StreamWriterBufferCreator creator = new StreamWriterBufferCreator(buffer);
                    // check for attribute value prefixes
                    creator.setCheckAttributeValue(true);
                    this.writePayload(creator);
                    attributeValuePrefixes = creator.getAttributeValuePrefixes();
                    this.message = null;
                }
            }
        }
    }

    public List getAttributeValuePrefixes(){
        return attributeValuePrefixes;
    }
}

