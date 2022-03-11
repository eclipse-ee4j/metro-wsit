/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;

import com.sun.xml.wss.impl.MessageConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.XMLStreamReaderEx;
import org.jvnet.staxex.XMLStreamWriterEx;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class StreamUtil {

    /** Creates a new instance of StreamUtil */
    public StreamUtil() {
    }

    public static boolean moveToNextElement(XMLStreamReader reader) throws XMLStreamException{
        if(reader.hasNext()){
            reader.next();
            while(reader.getEventType() != XMLStreamReader.START_ELEMENT){
                if(reader.hasNext()){
                    reader.next();
                }else{
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }

    public static boolean moveToNextStartOREndElement(XMLStreamReader reader) throws XMLStreamException{
        if(reader.hasNext()){
            reader.next();
            while(move(reader)){
                if(reader.hasNext()){
                    reader.next();
                }else{
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }

    public static boolean moveToNextStartOREndElement(XMLStreamReader reader,XMLStreamWriter writer ) throws XMLStreamException{
        if(writer == null){
            return moveToNextStartOREndElement(reader);
        }
        if(reader.hasNext()){
            reader.next();
            writeCurrentEvent(reader,writer);
            while(move(reader)){
                if(reader.hasNext()){
                    reader.next();
                    writeCurrentEvent(reader,writer);
                }else{
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }


    public static boolean isStartElement(XMLStreamReader reader){
        if(reader.getEventType() == XMLStreamReader.START_ELEMENT){
            return true;
        }
        return false;
    }


    public static boolean _break(XMLStreamReader reader,String localName,String uri) {
        if(reader.getEventType() == XMLStreamReader.END_ELEMENT){
            if(reader.getLocalName() == localName &&
                    (reader.getNamespaceURI() == uri || reader.getNamespaceURI() == MessageConstants.WSSC_13NS)){
                return true;
            }
        }
        return false;
    }


    private static boolean move(XMLStreamReader reader) {
        if(reader.getEventType() == XMLStreamReader.START_ELEMENT ||
                reader.getEventType() == XMLStreamReader.END_ELEMENT){
            return false;
        }
        return true;
    }


    public static void writeStartElement(XMLStreamReader reader,XMLStreamWriter writer) throws XMLStreamException{
        String pref = reader.getPrefix();
        if (pref == null) {
            pref = "";
        }
        writer.writeStartElement(pref, reader.getLocalName(), reader.getNamespaceURI());

        int nsCount = reader.getNamespaceCount();

        for(int i=0;i< nsCount ;i++){
            String prefix = reader.getNamespacePrefix(i);
            if(prefix == null)prefix ="";
            writer.writeNamespace(prefix,reader.getNamespaceURI(i));
        }
        int atCount = reader.getAttributeCount();
        for(int i=0;i< atCount ;i++){
            if(reader.getAttributePrefix(i) == "" || reader.getAttributePrefix(i) == null){
                writer.writeAttribute(reader.getAttributeLocalName(i),reader.getAttributeValue(i));
            }else{
                writer.writeAttribute(reader.getAttributePrefix(i),reader.getAttributeNamespace(i),reader.getAttributeLocalName(i),reader.getAttributeValue(i));
            }
        }

    }

    public static void writeCurrentEvent(XMLStreamReader reader , XMLStreamWriter writer) throws XMLStreamException{
        int event = reader.getEventType();
        switch(event){

            case XMLStreamReader.CDATA:{
                writer.writeCData(reader.getText());
                break;
            }
            case XMLStreamReader.CHARACTERS:{
                //writer.writeCharacters(reader.getTextCharacters(),reader.getTextStart(),reader.getTextLength());
                char[] buf = new char[2048];
                int actual= 0;
                int sourceStart = 0;
                do {
                    actual = reader.getTextCharacters(sourceStart, buf, 0, 2048);
                    if (actual > 0) {
                        writer.writeCharacters(buf, 0, actual);
                        sourceStart += actual;
                    }
                }while (actual == 2048) ;


                break;
            }
            case XMLStreamReader.COMMENT:{
                writer.writeComment(reader.getText());
                break;
            }
            case XMLStreamReader.DTD:{
                break;
            }
            case XMLStreamReader.END_DOCUMENT:{
                break;
            }
            case XMLStreamReader.END_ELEMENT:{
                writer.writeEndElement();
                break;
            }
            case XMLStreamReader.ENTITY_DECLARATION:{
                break;
            }
            case XMLStreamReader.ENTITY_REFERENCE:{
                break;
            }
            case XMLStreamReader.NAMESPACE:{
                break;
            }
            case XMLStreamReader.NOTATION_DECLARATION:{
                break;
            }
            case XMLStreamReader.PROCESSING_INSTRUCTION:{
                break;
            }
            case XMLStreamReader.SPACE:{
                writer.writeCharacters(reader.getText());
                break;
            }
            case XMLStreamReader.START_DOCUMENT:{

                break;
            }
            case XMLStreamReader.START_ELEMENT:{
                writeStartElement(reader,writer);
                break;
            }
        }
    }


    public static void writeCurrentEvent(XMLStreamReaderEx reader , XMLStreamWriterEx writer) throws XMLStreamException{
        int event = reader.getEventType();
        switch(event){

            case XMLStreamReader.CDATA:{
                writer.writeCData(reader.getText());
                break;
            }
            case XMLStreamReader.CHARACTERS:{
                writer.writeCharacters(reader.getTextCharacters(),reader.getTextStart(),reader.getTextLength());
                break;
            }
            case XMLStreamReader.COMMENT:{
                writer.writeComment(reader.getText());
                break;
            }
            case XMLStreamReader.DTD:{
                break;
            }
            case XMLStreamReader.END_DOCUMENT:{
                break;
            }
            case XMLStreamReader.END_ELEMENT:{
                writer.writeEndElement();
                break;
            }
            case XMLStreamReader.ENTITY_DECLARATION:{
                break;
            }
            case XMLStreamReader.ENTITY_REFERENCE:{
                break;
            }
            case XMLStreamReader.NAMESPACE:{
                break;
            }
            case XMLStreamReader.NOTATION_DECLARATION:{
                break;
            }
            case XMLStreamReader.PROCESSING_INSTRUCTION:{
                break;
            }
            case XMLStreamReader.SPACE:{
                writer.writeCharacters(reader.getText());
                break;
            }
            case XMLStreamReader.START_DOCUMENT:{

                break;
            }
            case XMLStreamReader.START_ELEMENT:{
                writeStartElement(reader,writer);
                break;
            }
        }
    }

    public static String getWsuId(XMLStreamReader reader){
        return reader.getAttributeValue(MessageConstants.WSU_NS,"Id");
    }

    public static String getId(XMLStreamReader reader){
        return reader.getAttributeValue(null,"Id");
    }

    public static String getCV(XMLStreamReader reader) throws  XMLStreamException{
        StringBuilder content = new StringBuilder();
        int eventType = reader.getEventType();
        while(eventType != XMLStreamReader.END_ELEMENT ) {
            if(eventType == XMLStreamReader.CHARACTERS
                    || eventType == XMLStreamReader.CDATA
                    || eventType == XMLStreamReader.SPACE
                    || eventType == XMLStreamReader.ENTITY_REFERENCE) {
                content.append(reader.getText());
            } else if(eventType == XMLStreamReader.PROCESSING_INSTRUCTION
                    || eventType == XMLStreamReader.COMMENT) {
                // skipping
            }
            eventType = reader.next();
        }
        return content.toString();
    }

    public static String getCV(XMLStreamReaderEx reader) throws  XMLStreamException{
        StringBuilder sb = new StringBuilder();
        while(reader.getEventType() == reader.CHARACTERS && reader.getEventType() != reader.END_ELEMENT){
            CharSequence charSeq = reader.getPCDATA();
            for(int i=0;i<charSeq.length();i++){
                sb.append(charSeq.charAt(i));
            }
            reader.next();
        }
        return sb.toString();
    }

    public static String convertDigestAlgorithm(String algo){
        if(MessageConstants.SHA1_DIGEST.equals(algo)){
            return MessageConstants.SHA_1;
        }
        if(MessageConstants.SHA256.equals(algo)){
            return MessageConstants.SHA_256;
        }

        if(MessageConstants.SHA512.equals(algo)){
            return MessageConstants.SHA_512;
        }

        return MessageConstants.SHA_1;
    }

}
