/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * Timestamp.java
 *
 * Created on August 30, 2006, 11:16 PM
 */

package com.sun.xml.ws.security.opt.impl.tokens;

import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.wsu10.AttributedDateTime;
import com.sun.xml.ws.security.wsu10.ObjectFactory;
import com.sun.xml.ws.security.wsu10.TimestampType;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.ws.api.SOAPVersion;
import javax.xml.stream.XMLStreamException;

import com.sun.xml.wss.XWSSecurityException;
import java.util.TimeZone;

/**
 * Representation of Timestamp SecurityHeaderElement
 * @author Ashutosh.Shahi@sun.com
 */
public class Timestamp extends TimestampType
          implements com.sun.xml.ws.security.opt.api.tokens.Timestamp, 
          SecurityHeaderElement, SecurityElementWriter{
    
    private static final TimeZone utc = TimeZone.getTimeZone("UTC");
    private static Calendar utcCalendar = new GregorianCalendar(utc);
    public static final SimpleDateFormat calendarFormatter1 
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final SimpleDateFormat utcCalendarFormatter1
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    static {
        utcCalendarFormatter1.setTimeZone(utc);
    }
    
    private long timeout = 0;
    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    private ObjectFactory objFac = new ObjectFactory();
    
    /**
     * Creates a new instance of Timestamp
     * @param sv the soapVersion for this message
     */
    public Timestamp(SOAPVersion sv) {
        this.soapVersion = sv;
    }
    
    /**
     * 
     * @param created set the creation time on timestamp
     */
    @Override
    public void setCreated(final String created){
        AttributedDateTime timeCreated = objFac.createAttributedDateTime();
        timeCreated.setValue(created);
        setCreated(timeCreated);
    }
    
    /**
     * 
     * @param expires set the expiry time on timestamp
     */
    @Override
    public void setExpires(final String expires){
        AttributedDateTime timeExpires = objFac.createAttributedDateTime();
        timeExpires.setValue(expires);
        setExpires(timeExpires);
    }
    
    /**
     * 
     * @return the creation time value
     */
    @Override
    public String getCreatedValue(){
        String createdValue = null;
        AttributedDateTime created = getCreated();
        if(created != null)
            createdValue = created.getValue();
        return createdValue;
    }
    
    /**
     * 
     * @return the expiry time value
     */
    @Override
    public String getExpiresValue(){
        String expiresValue = null;
        AttributedDateTime expires = getExpires();
        if(expires != null)
            expiresValue = expires.getValue();
        return expiresValue;
    }
    
    /**
     * The timeout is assumed to be in seconds
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    @Override
    public String getNamespaceURI() {
        return MessageConstants.WSU_NS;
    }
    
    @Override
    public String getLocalPart() {
        return MessageConstants.TIMESTAMP_LNAME;
    }
    
    public String getAttribute(String nsUri, String localName) {
        QName qname = new QName(nsUri, localName);
        Map<QName, String> otherAttributes = this.getOtherAttributes();
        return otherAttributes.get(qname);
    }
    
    public String getAttribute(QName name) {
        Map<QName, String> otherAttributes = this.getOtherAttributes();
        return otherAttributes.get(name);
    }
    
    @Override
    public javax.xml.stream.XMLStreamReader readHeader() throws javax.xml.stream.XMLStreamException {
        XMLStreamBufferResult xbr = new XMLStreamBufferResult();
        JAXBElement<TimestampType> tsElem = new ObjectFactory().createTimestamp(this);
        try{
            getMarshaller().marshal(tsElem, xbr);
            
        } catch(JAXBException je){
            throw new XMLStreamException(je);
        }
        return xbr.getXMLStreamBuffer().readAsXMLStreamReader();
    }
    
    /**
     *
     */
    @Override
    public void writeTo(OutputStream os) {
    }
    
    /**
     * Writes out the header.
     *
     * @throws XMLStreamException
     *      if the operation fails for some reason. This leaves the
     *      writer to an undefined state.
     */
    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws javax.xml.stream.XMLStreamException {
        JAXBElement<TimestampType> tsElem = new ObjectFactory().createTimestamp(this);
        try {
            // If writing to Zephyr, get output stream and use JAXB UTF-8 writer
            if (streamWriter instanceof Map) {
                OutputStream os = (OutputStream) ((Map) streamWriter).get("sjsxp-outputstream");
                if (os != null) {
                    streamWriter.writeCharacters("");        // Force completion of open elems
                    getMarshaller().marshal(tsElem, os);
                    return;
                }
            }
            
            getMarshaller().marshal(tsElem,streamWriter);
        } catch (JAXBException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private Marshaller getMarshaller() throws JAXBException{
        return JAXBUtil.createMarshaller(soapVersion);
    }
    
    /*
     * The <wsu:Created> element specifies a timestamp used to
     * indicate the creation time. It is defined as part of the
     * <wsu:Timestamp> definition.
     *
     * Time reference in WSS work should be in terms of
     * dateTime type specified in XML Schema in UTC time(Recommmended)
     */
    public void createDateTime() {
        if (created == null) {
            synchronized (utcCalendar) {
                
                // always send UTC/GMT time
                long currentTime = System.currentTimeMillis();
                utcCalendar.setTimeInMillis(currentTime);
                
                setCreated(utcCalendarFormatter1.format(utcCalendar.getTime()));
                
                utcCalendar.setTimeInMillis(currentTime + timeout);
                setExpires(utcCalendarFormatter1.format(utcCalendar.getTime()));
            }
        }
    }
    
    /**
     *
     */
    @Override
    public boolean refersToSecHdrWithId(String id) {
        return false;
    }

    /**
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter, HashMap props) throws javax.xml.stream.XMLStreamException {
        try{
            Marshaller marshaller = getMarshaller();
            Iterator<Map.Entry<Object, Object>> itr = props.entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry<Object, Object> entry = itr.next();
                marshaller.setProperty((String)entry.getKey(), entry.getValue());
            }
            writeTo(streamWriter);
        }catch(JAXBException jbe){
            throw new XMLStreamException(jbe);
        }
    }
    
}
