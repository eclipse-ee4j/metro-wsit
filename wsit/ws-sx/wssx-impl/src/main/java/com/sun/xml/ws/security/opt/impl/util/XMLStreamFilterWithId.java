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

    @Override
    public void setDefaultNamespace(String string) throws XMLStreamException {
        writer.setDefaultNamespace(string);
    }

    @Override
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

    @Override
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

    @Override
    public void writeAttribute(String string, String string0) throws XMLStreamException {
        writer.writeAttribute(string,string0);
    }

    @Override
    public void writeNamespace(String string, String string0) throws XMLStreamException {
        writer.writeNamespace(string,string0);
    }

    @Override
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

    @Override
    public void writeAttribute(String string, String string0, String string1) throws XMLStreamException {
        writer.writeAttribute(string,string0,string1);
    }

    @Override
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

    @Override
    public void writeAttribute(String string, String string0, String string1, String string2) throws XMLStreamException {
        writer.writeAttribute(string,string0,string1,string2);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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
