/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.mex.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.DocumentAddressResolver;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.api.server.ServiceDefinition;
import com.sun.xml.ws.api.server.WSEndpoint;

import com.sun.xml.ws.mex.MessagesMessages;

import static com.sun.xml.ws.mex.MetadataConstants.MEX_NAMESPACE;
import static com.sun.xml.ws.mex.MetadataConstants.MEX_PREFIX;
import static com.sun.xml.ws.mex.MetadataConstants.SCHEMA_DIALECT;
import static com.sun.xml.ws.mex.MetadataConstants.WSDL_DIALECT;

/**
 * This class is used to add the endpoint's metadata to
 * the mex MetadataResponse element.
 *
 * @author WS Development Team
 */
public class WSDLRetriever {

    private final WSEndpoint endpoint;
    
    private static final Logger logger =
        Logger.getLogger(WSDLRetriever.class.getName());
    
    /*
     * This class is used by the SDDocument object in the
     * jax-ws runtime. The agreement we have is to return
     * null to the jax-ws runtime for mex responses. This
     * tells the jax-ws runtime not to add schemaLocation
     * attributes for schema import statements.
     */
    private static final DocumentAddressResolver dar =
        new DocumentAddressResolver() {
        public String getRelativeAddressFor(final SDDocument doc1,
            final SDDocument doc2) {
            
            return null;
        }
    };
    
    public WSDLRetriever(WSEndpoint endpoint) {
        this.endpoint = endpoint;
    }
    
    /*
     * This method is called by the server pipe to write out
     * the wsdl and schema documents to the mex response.
     */
    void addDocuments(final XMLStreamWriter writer, final Packet request,
        final String address) throws XMLStreamException {
        
        final ServiceDefinition sDef = endpoint.getServiceDefinition();
        if (sDef == null) {
            return;
        }
        final Iterator<SDDocument> docs = sDef.iterator();
        while (docs.hasNext()) {
            writeDoc(writer, docs.next(), address);
        }
    }
    
    /*
     * This method writes out each individual document, which
     * must be wrapped in a MetadataSection element within
     * the mex response. It also sets the Dialect attribure
     * of the section so that sections can be classified as
     * wsdl, schema, etc. when parsed.
     */
    private void writeDoc(final XMLStreamWriter writer, final SDDocument doc,
        final String address) throws XMLStreamException {
        
        try {
            writer.writeStartElement(MEX_PREFIX,
                "MetadataSection", MEX_NAMESPACE);
            if (doc.isWSDL()) {
                writer.writeAttribute("Dialect", WSDL_DIALECT);
                writer.writeAttribute("Identifier",
                    ((SDDocument.WSDL) doc).getTargetNamespace());
            } else if(doc.isSchema()) {
                writer.writeAttribute("Dialect", SCHEMA_DIALECT);
                writer.writeAttribute("Identifier",
                    ((SDDocument.Schema) doc).getTargetNamespace());
            }
            doc.writeTo(new MEXAddressResolver(endpoint.getServiceName(), endpoint.getPortName(), address), dar, writer);
            writer.writeEndElement();
        } catch (IOException ioe) {
            // this should be very rare
            String exceptionMessage =
                MessagesMessages.MEX_0015_IOEXCEPTION_WHILE_WRITING_RESPONSE(address);
            logger.log(Level.SEVERE, exceptionMessage, ioe);
            throw new WebServiceException(exceptionMessage, ioe);
        }
    }

}
