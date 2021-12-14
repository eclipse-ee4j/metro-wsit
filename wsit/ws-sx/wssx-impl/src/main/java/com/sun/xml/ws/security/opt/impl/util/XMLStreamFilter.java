/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class XMLStreamFilter implements XMLStreamWriter{
    
    protected XMLStreamWriter writer = null;
    protected NamespaceContextEx nsContext = null;
    protected boolean seenFirstElement = false;
    protected int count = 0;
    /** Creates a new instance of XMLStreamFilter */
    public XMLStreamFilter(XMLStreamWriter writer,NamespaceContextEx nce) throws XMLStreamException {
        this.writer = writer;
        nsContext = nce;
        if(nsContext == null){
            throw new XMLStreamException("NamespaceContext cannot be null");
        }
    }
    
    
    @Override
    public NamespaceContext getNamespaceContext() {
        if(count == 0)
            return nsContext;
        else
            return writer.getNamespaceContext();
    }
    
    @Override
    public void close() throws XMLStreamException {
        writer.close();
    }
    
    @Override
    public void flush() throws XMLStreamException {
        writer.flush();
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        writer.writeEndDocument();
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        if(count ==0){
            return;
        }
        --count;
        writer.writeEndElement();
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        writer.writeStartDocument();
    }
    
    @Override
    public void writeCharacters(char[] c, int index, int len) throws XMLStreamException {
        writer.writeCharacters(c,index,len);
    }
    
    @Override
    public void setDefaultNamespace(String string) throws XMLStreamException {
        if(count == 0){
            nsContext.add("",string);
            return;
        }
        writer.writeCharacters(string);
    }
    
    @Override
    public void writeCData(String string) throws XMLStreamException {
        writer.writeCData(string);
    }
    
    @Override
    public void writeCharacters(String string) throws XMLStreamException {
        writer.writeCharacters(string);
    }
    
    @Override
    public void writeComment(String string) throws XMLStreamException {
        writer.writeComment(string);
    }
    
    @Override
    public void writeDTD(String string) throws XMLStreamException {
        writer.writeDTD(string);
    }
    
    @Override
    public void writeDefaultNamespace(String string) throws XMLStreamException {
        writer.writeDefaultNamespace(string);
    }
    
    @Override
    public void writeEmptyElement(String string) throws XMLStreamException {
        if(count == 0){
            writer.setNamespaceContext(nsContext);
        }
        writer.writeEmptyElement(string);
        
    }
    
    @Override
    public void writeEntityRef(String string) throws XMLStreamException {
        writer.writeEntityRef(string);
    }
    
    @Override
    public void writeProcessingInstruction(String string) throws XMLStreamException {
        writer.writeProcessingInstruction(string);
    }
    
    @Override
    public void writeStartDocument(String string) throws XMLStreamException {
        writer.writeStartDocument(string);
    }
    
    @Override
    public void writeStartElement(String string) throws XMLStreamException {
        if(!seenFirstElement){
            seenFirstElement = true;
            return;
        }
        count++;
        if(count == 1){
            writer.setNamespaceContext(nsContext);
        }
        writer.writeStartElement(string);
    }
    
    @Override
    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        writer.setNamespaceContext(namespaceContext);
    }
    
    @Override
    public Object getProperty(String value) throws IllegalArgumentException {
        if("com.ctc.wstx.outputUnderlyingStream".equals(value) || 
                "http://java.sun.com/xml/stream/properties/outputstream".equals(value)){
            return null;
        }
        return writer.getProperty(value);
    }
    
    
    @Override
    public String getPrefix(String string) throws XMLStreamException {
        return writer.getPrefix(string);
    }
    
    @Override
    public void setPrefix(String string, String string0) throws XMLStreamException {
        writer.setPrefix(string,string0);
    }
    
    @Override
    public void writeAttribute(String string, String string0) throws XMLStreamException {
        if(count == 0){
            return;
        }
        writer.writeAttribute(string,string0);
    }
    
    @Override
    public void writeEmptyElement(String string, String string0) throws XMLStreamException {
        if(count == 0){
            writer.setNamespaceContext(nsContext);
        }
        writer.writeEmptyElement(string,string0);
    }
    
    @Override
    public void writeNamespace(String string, String string0) throws XMLStreamException {
        if(count == 0){
            nsContext.add(string,string0);
            return;
        }
        writer.writeNamespace(string,string0);
    }
    
    @Override
    public void writeProcessingInstruction(String string, String string0) throws XMLStreamException {
        writer.writeProcessingInstruction(string,string0);
    }
    
    @Override
    public void writeStartDocument(String string, String string0) throws XMLStreamException {
        writer.writeStartDocument(string,string0);
    }
    
    @Override
    public void writeStartElement(String string, String string0) throws XMLStreamException {
        if(!seenFirstElement){
            seenFirstElement = true;
            return;
        }
        count++;
        if(count == 1){
            writer.setNamespaceContext(nsContext);
        }
        writer.writeStartElement(string,string0);
    }
    
    @Override
    public void writeAttribute(String string, String string0, String string1) throws XMLStreamException {
        if(count == 0){
            return;
        }
        writer.writeAttribute(string,string0,string1);
    }
    
    @Override
    public void writeEmptyElement(String string, String string0, String string1) throws XMLStreamException {
        if(count == 0){
            writer.setNamespaceContext(nsContext);
        }
        writer.writeEmptyElement(string,string0,string1);
    }
    
    @Override
    public void writeStartElement(String string, String string0, String string1) throws XMLStreamException {
        if(!seenFirstElement){
            seenFirstElement = true;
            return;
        }
        count++;
        if(count == 1){
            writer.setNamespaceContext(nsContext);
        }
        writer.writeStartElement(string,string0,string1);
    }
    
    @Override
    public void writeAttribute(String string, String string0, String string1, String string2) throws XMLStreamException {
        if(count == 0){
            return;
        }
        writer.writeAttribute(string,string0,string1,string2);
    }
    
    
}
