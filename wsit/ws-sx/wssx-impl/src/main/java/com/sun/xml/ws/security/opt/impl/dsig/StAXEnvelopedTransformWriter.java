/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.dsig;

import com.sun.xml.ws.security.opt.crypto.JAXBData;
import com.sun.xml.ws.security.opt.crypto.StreamWriterData;
import com.sun.xml.ws.security.opt.impl.crypto.OctectStreamData;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.c14n.StAXEXC14nCanonicalizerImpl;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.impl.opt.signature.LogStringsMessages;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.NamespaceContextEx;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class StAXEnvelopedTransformWriter implements XMLStreamWriter,StreamWriterData{
    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN,
            LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN_BUNDLE);
    
    private XMLStreamWriter nextWriter = null;
    private boolean ignore = false;
    private Data data = null;
    private int index = 0;
    private NamespaceContextEx ns = null;
    
    /** Creates a new instance of StAXEnvelopedTransformWriter */
    public StAXEnvelopedTransformWriter(XMLStreamWriter writer,Data data) {
        this.nextWriter = writer;
        this.data = data;
        if(data instanceof JAXBData){
            ns = ((JAXBData)data).getNamespaceContext();
        }else if(data instanceof StreamWriterData){
            ns = ((StreamWriterData)data).getNamespaceContext();
        }
        
    }
    
    public StAXEnvelopedTransformWriter(Data data) {
        this.data = data;
        if(data instanceof JAXBData){
            ns = ((JAXBData)data).getNamespaceContext();
        }else if(data instanceof StreamWriterData){
            ns = ((StreamWriterData)data).getNamespaceContext();
        }
    }
    
    @Override
    public NamespaceContextEx getNamespaceContext() {
        return ns;
    }
    
    @Override
    public void close() throws XMLStreamException {
        nextWriter.close();
    }
    
    @Override
    public void flush() throws XMLStreamException {
        nextWriter.flush();
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        if(index >0){
            int size = index;
            for(int i=0;i<size;i++){
                writeEndElement();
            }
        }
        nextWriter.writeEndDocument();
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        if(index > 0){
            index --;
        }        
        if(!ignore){
            nextWriter.writeEndElement();
        }        
        if(index == 0){
            ignore = false;
        }
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        if(!ignore){
            nextWriter.writeStartDocument();
        }
    }
    
    @Override
    public void writeCharacters(char[] c, int index, int len) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeCharacters(c,index,len);
        }
    }
    
    @Override
    public void setDefaultNamespace(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.setDefaultNamespace(string);
        }
    }
    
    @Override
    public void writeCData(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeCData(string);
        }
    }
    
    @Override
    public void writeCharacters(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeCharacters(string);
        }
    }
    
    @Override
    public void writeComment(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeComment(string);
        }
    }
    
    @Override
    public void writeDTD(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeDTD(string);
        }
    }
    
    @Override
    public void writeDefaultNamespace(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeDefaultNamespace(string);
        }
    }
    
    @Override
    public void writeEmptyElement(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeEmptyElement(string);
        }
    }
    
    @Override
    public void writeEntityRef(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeEntityRef(string);
        }
    }
    
    @Override
    public void writeProcessingInstruction(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeProcessingInstruction(string);
        }
    }
    
    @Override
    public void writeStartDocument(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeStartDocument(string);
        }
    }
    
    @Override
    public void writeStartElement(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeStartElement(string);
        }
    }
    
    @Override
    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        if(!ignore){
            nextWriter.setNamespaceContext(namespaceContext);
        }
    }
    
    @Override
    public Object getProperty(String string) throws IllegalArgumentException {
        return nextWriter.getProperty(string);
    }
    
    @Override
    public String getPrefix(String string) throws XMLStreamException {
        return nextWriter.getPrefix(string);
    }
    
    @Override
    public void setPrefix(String string, String string0) throws XMLStreamException {
        if(!ignore){
            nextWriter.setPrefix(string,string0);
        }
    }
    
    @Override
    public void writeAttribute(String localName, String value)throws XMLStreamException {
        if(!ignore){
            nextWriter.writeAttribute(localName,value);
        }
    }
    
    @Override
    public void writeEmptyElement(String string, String string0) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeEmptyElement(string,string0);
        }
    }
    
    @Override
    public void writeNamespace(String string, String string0) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeNamespace(string,string0);
        }
    }
    
    @Override
    public void writeProcessingInstruction(String string, String string0) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeProcessingInstruction(string,string0);
        }
    }
    
    @Override
    public void writeStartDocument(String string, String string0) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeStartDocument(string,string0);
        }
    }
    
    @Override
    public void writeStartElement(String namespaceURI, String localName)  throws XMLStreamException {
        if(!ignore){
            if(localName == MessageConstants.SIGNATURE_LNAME && namespaceURI == MessageConstants.DSIG_NS){
                if(!((StAXEXC14nCanonicalizerImpl)nextWriter).isParentToParentAdvice()){
                    ignore = true;
                    index ++;
                    return;    
                }                
            }            
            nextWriter.writeStartElement(namespaceURI,localName);
        }else{
            index ++;
        }
    }
    
    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeAttribute(prefix,namespaceURI,localName,value);
        }
    }
    
    @Override
    public void writeEmptyElement(String string, String string0, String string1) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeEmptyElement(string,string0,string1);
        }
    }
    
    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI)  throws XMLStreamException {
        if(!ignore){
            if(localName == MessageConstants.SIGNATURE_LNAME && namespaceURI == MessageConstants.DSIG_NS){
                if(!((StAXEXC14nCanonicalizerImpl)nextWriter).isParentToParentAdvice()){
                    ignore = true;
                    index ++;
                    return;
                }
            }
            nextWriter.writeStartElement(prefix,localName,namespaceURI);
        }else{
            index ++;
        }
    }
    
    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeAttribute(namespaceURI,localName,value);
        }
    }
    /**
     * processes the envelop transform and writes it to the data
     * @param writer XMLStreamWriter
     */
    @Override
    public void write(XMLStreamWriter writer) throws XMLStreamException {
        this.nextWriter = writer;
        if(data instanceof JAXBData){
            try {
                ((JAXBData)data).writeTo(this);
            } catch (XWSSecurityException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1706_ERROR_ENVELOPED_SIGNATURE(),ex);
                throw new XMLStreamException("Error occurred while performing Enveloped Signature");
            }
        }else if(data instanceof StreamWriterData){
            ((StreamWriterData)data).write(this);
        }else if(data instanceof OctectStreamData){
            ((OctectStreamData)data).write(this);
        }
    }
}
