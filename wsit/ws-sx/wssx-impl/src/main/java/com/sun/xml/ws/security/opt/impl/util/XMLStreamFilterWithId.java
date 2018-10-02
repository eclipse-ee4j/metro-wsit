/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;

import com.sun.xml.wss.impl.c14n.BaseCanonicalizer;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;

import com.sun.xml.wss.impl.MessageConstants;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class XMLStreamFilterWithId  extends XMLStreamFilter{
    
    String id = null;
    boolean wroteId = false;
    
    /** Creates a new instance of XMLStreamWriterWithId */
    public XMLStreamFilterWithId(XMLStreamWriter writer, NamespaceContextEx nce ,String id) throws XMLStreamException {
        super(writer, nce);
        this.id = id;
    }
    
    public void setDefaultNamespace(String string) throws XMLStreamException {
        writer.setDefaultNamespace(string);
    }
    
    public void writeEndElement() throws XMLStreamException {
        if(!wroteId && count == 1){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
            wroteId = true;
        }
        writer.writeEndElement();
    }
    
    public void writeStartElement(String string) throws XMLStreamException {
        if(!wroteId && count == 1){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
            wroteId = true;
        }
        if(!seenFirstElement){
            seenFirstElement = true;
        }
        writer.writeStartElement(string);
        /*if(count == 0){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
        }*/
        count++;
    }
    
    public void writeAttribute(String string, String string0) throws XMLStreamException {
        writer.writeAttribute(string,string0);
    }
    
    public void writeNamespace(String string, String string0) throws XMLStreamException {
        writer.writeNamespace(string,string0);
    }
    
    public void writeStartElement(String string, String string0) throws XMLStreamException {
        if(!wroteId && count == 1){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
            wroteId = true;
        }
        if(!seenFirstElement){
            seenFirstElement = true;
        }
        writer.writeStartElement(string,string0);
        /*if(count == 0){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
        }*/
        count++;
        
    }
    
    public void writeAttribute(String string, String string0, String string1) throws XMLStreamException {
        writer.writeAttribute(string,string0,string1);
    }
    
    public void writeStartElement(String string, String string0, String string1) throws XMLStreamException {
        if(!wroteId && count == 1){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
            wroteId = true;
        }
        if(!seenFirstElement){
            seenFirstElement = true;
        }
        
        writer.writeStartElement(string,string0,string1);
        /*if(count == 0){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
        }*/
        count++;
        
    }
    
    public void writeAttribute(String string, String string0, String string1, String string2) throws XMLStreamException {
        writer.writeAttribute(string,string0,string1,string2);
    }
    
    public void writeCharacters(char[] c, int index, int len) throws XMLStreamException {
        if(!wroteId && count == 1){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
            wroteId = true;
        }
        writer.writeCharacters(c,index,len);
    }
    
    public void writeCharacters(String string) throws XMLStreamException {
        if(!wroteId && count == 1){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
            wroteId = true;
        }
        writer.writeCharacters(string);
    }
    
    public void writeEmptyElement(String string) throws XMLStreamException {
        if(count == 0){
            writer.setNamespaceContext(nsContext);
        }
        if(!wroteId && count == 1){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
            wroteId = true;
        }
        writer.writeEmptyElement(string);
        
    }
    
    public void writeEmptyElement(String string, String string0, String string1) throws XMLStreamException {
        if(count == 0){
            writer.setNamespaceContext(nsContext);
        }
        if(!wroteId && count == 1){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
            wroteId = true;
        }
        writer.writeEmptyElement(string,string0,string1);
    }
    
    public void writeEmptyElement(String string, String string0) throws XMLStreamException {
        if(count == 0){
            writer.setNamespaceContext(nsContext);
        }
        if(!wroteId && count == 1){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
            wroteId = true;
        }
        writer.writeEmptyElement(string,string0);
    }
    
    public void writeProcessingInstruction(String string, String string0) throws XMLStreamException {
        if(!wroteId && count == 1){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
            wroteId = true;
        }
        writer.writeProcessingInstruction(string,string0);
    }
    
    public void writeProcessingInstruction(String string) throws XMLStreamException {
        if(!wroteId && count == 1){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
            wroteId = true;
        }
        writer.writeProcessingInstruction(string);
    }
    
    public void writeCData(String string) throws XMLStreamException {
        if(!wroteId && count == 1){
            writer.writeAttribute(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS,
                    "Id", id);
            if(writer instanceof BaseCanonicalizer){
                writer.setNamespaceContext(nsContext);
            }
            wroteId = true;
        }
        writer.writeCData(string);
    }
}
