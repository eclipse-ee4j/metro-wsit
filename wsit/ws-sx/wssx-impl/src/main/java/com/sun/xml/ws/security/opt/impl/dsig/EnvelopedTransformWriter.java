/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.dsig;


import com.sun.xml.ws.security.opt.crypto.dsig.Reference;
import com.sun.xml.ws.security.opt.crypto.dsig.internal.DigesterOutputStream;
import com.sun.xml.wss.impl.c14n.StAXEXC14nCanonicalizerImpl;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


/**
 *
 * @author K.Venugopal@sun.com
 */
public class EnvelopedTransformWriter implements XMLStreamWriter{
    private StAXEXC14nCanonicalizerImpl stAXC14n = null;
    private JAXBSignatureHeaderElement signature = null;
    private Reference ref = null;
    private DigesterOutputStream dos = null;
    private int index = 0;
    private XMLStreamWriter writer = null;
    /** Creates a new instance of EnvelopedTransform */
    public EnvelopedTransformWriter(XMLStreamWriter writer,StAXEXC14nCanonicalizerImpl stAXC14n,Reference ref ,JAXBSignatureHeaderElement signature,DigesterOutputStream dos) {
        this.stAXC14n = stAXC14n;
        this.writer = writer;
        this.ref = ref;
        this.signature = signature;
        this.dos = dos;
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return writer.getNamespaceContext();
    }

    @Override
    public void close() {
        //writer.close();
    }

    @Override
    public void flush() throws XMLStreamException {
        writer.flush();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        for(int i=0;i< index;i++){
            stAXC14n.writeEndElement();
            writer.writeEndElement();
        }
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        if(index ==0){
            return;
        }
        --index;
        stAXC14n.writeEndElement();
        ref.setDigestValue(dos.getDigestValue());

        signature.sign();

        signature.writeTo(writer);
        writer.writeEndElement();
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        stAXC14n.writeStartDocument();
        writer.writeStartDocument();
    }

    @Override
    public void writeCharacters(char[] c, int index, int len) throws XMLStreamException {
        stAXC14n.writeCharacters(c,index,len);
        writer.writeCharacters(c,index,len);
    }

    @Override
    public void setDefaultNamespace(String string) throws XMLStreamException {
        writer.setDefaultNamespace(string);
        stAXC14n.setDefaultNamespace(string);
    }

    @Override
    public void writeCData(String string) throws XMLStreamException {
        stAXC14n.writeCData(string);
        writer.writeCData(string);
    }

    @Override
    public void writeCharacters(String string) throws XMLStreamException {
        stAXC14n.writeCharacters(string);
        writer.writeCharacters(string);
    }

    @Override
    public void writeComment(String string) throws XMLStreamException {
        stAXC14n.writeComment(string);
        writer.writeComment(string);
    }

    @Override
    public void writeDTD(String string) throws XMLStreamException {
        stAXC14n.writeDTD(string);
        writer.writeDTD(string);
    }

    @Override
    public void writeDefaultNamespace(String string) throws XMLStreamException {
        stAXC14n.writeDefaultNamespace(string);
        writer.writeDefaultNamespace(string);
    }

    @Override
    public void writeEmptyElement(String string) throws XMLStreamException {
        stAXC14n.writeEmptyElement(string);
        writer.writeEmptyElement(string);
    }

    @Override
    public void writeEntityRef(String string) throws XMLStreamException {
        stAXC14n.writeEntityRef(string);
        writer.writeEntityRef(string);
    }

    @Override
    public void writeProcessingInstruction(String string) throws XMLStreamException {
        stAXC14n.writeProcessingInstruction(string);
        writer.writeProcessingInstruction(string);
    }

    @Override
    public void writeStartDocument(String string) throws XMLStreamException {
        stAXC14n.writeStartDocument(string);
        writer.writeStartDocument(string);
    }

    @Override
    public void writeStartElement(String string) throws XMLStreamException {
        index++;
        stAXC14n.writeStartElement(string);
        writer.writeStartElement(string);
    }

    @Override
    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        writer.setNamespaceContext(namespaceContext);
    }

    @Override
    public Object getProperty(String string) throws IllegalArgumentException {
        return writer.getProperty(string);
    }

    @Override
    public String getPrefix(String string) throws XMLStreamException {
        return writer.getPrefix(string);
    }

    @Override
    public void setPrefix(String string, String string0) throws XMLStreamException {
        stAXC14n.setPrefix(string,string0);
        writer.setPrefix(string,string0);
    }

    @Override
    public void writeAttribute(String string, String string0) throws XMLStreamException {
        stAXC14n.writeAttribute(string,string0);
        writer.writeAttribute(string,string0);
    }

    @Override
    public void writeEmptyElement(String string, String string0) throws XMLStreamException {
        stAXC14n.writeEmptyElement(string,string0);
        writer.writeEmptyElement(string,string0);
    }

    @Override
    public void writeNamespace(String string, String string0) throws XMLStreamException {
        stAXC14n.writeNamespace(string,string0);
        writer.writeNamespace(string,string0);
    }

    @Override
    public void writeProcessingInstruction(String string, String string0) throws XMLStreamException {
        stAXC14n.writeProcessingInstruction(string,string0);
        writer.writeProcessingInstruction(string,string0);
    }

    @Override
    public void writeStartDocument(String string, String string0) throws XMLStreamException {
        stAXC14n.writeStartDocument(string,string0);
        writer.writeStartDocument(string,string0);
    }

    @Override
    public void writeStartElement(String string, String string0) throws XMLStreamException {
        index++;
        stAXC14n.writeStartElement(string,string0);
        writer.writeStartElement(string,string0);
    }

    @Override
    public void writeAttribute(String string, String string0, String string1) throws XMLStreamException {
        stAXC14n.writeAttribute(string,string0,string1);
        writer.writeAttribute(string,string0,string1);
    }

    @Override
    public void writeEmptyElement(String string, String string0, String string1) throws XMLStreamException {
        stAXC14n.writeEmptyElement(string,string0,string1);
        writer.writeEmptyElement(string,string0,string1);
    }

    @Override
    public void writeStartElement(String string, String string0, String string1) throws XMLStreamException {
        index++;
        stAXC14n.writeStartElement(string,string0,string1);
        writer.writeStartElement(string,string0,string1);
    }

    @Override
    public void writeAttribute(String string, String string0, String string1, String string2) throws XMLStreamException {
        stAXC14n.writeAttribute(string,string0,string1,string2);
        writer.writeAttribute(string,string0,string1,string2);
    }

}
