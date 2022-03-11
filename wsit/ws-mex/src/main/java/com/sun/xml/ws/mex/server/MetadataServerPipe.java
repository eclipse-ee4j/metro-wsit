/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.mex.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.ws.mex.MetadataConstants;
import jakarta.xml.ws.WebServiceException;

import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.mex.MessagesMessages;

/**
 * This pipe handles any mex requests that come through. If a
 * message comes through that has no headers or does not have
 * a mex action in the header, then the pipe ignores the message
 * and passes it on to the next pipe. Otherwise, it responds
 * to a mex Get request and returns a fault for a GetMetadata
 * request (these optional requests are not supported).
 *
 * TODO: Remove the createANSFault() method after the next
 * jax-ws integration. See the method for more details.
 *
 * @author WS Development Team
 */
public class MetadataServerPipe extends AbstractFilterTubeImpl {

    private final WSDLRetriever wsdlRetriever;
    private final SOAPVersion soapVersion;
    
    private static final Logger logger =
        Logger.getLogger(MetadataServerPipe.class.getName());
    
    public MetadataServerPipe(WSEndpoint endpoint, Pipe next) {
        super(PipeAdapter.adapt(next));
        wsdlRetriever = new WSDLRetriever(endpoint);
        soapVersion = endpoint.getBinding().getSOAPVersion();
    }

    protected MetadataServerPipe(MetadataServerPipe that, TubeCloner cloner) {
        super(that, cloner);
        soapVersion = that.soapVersion;
        wsdlRetriever = that.wsdlRetriever;
    }

    @Override
    public MetadataServerPipe copy(TubeCloner cloner) {
        return new MetadataServerPipe(this, cloner);
    }

    /**
     * Method returns immediately if there are no headers
     * in the message to check. If there are, the pipe checks
     * W3C and then MEMBER addressing for an action header.
     * If there is an action header, and if it is a mex Get
     * request, then ask addressing again for the address and
     * process the request.
     */
    @Override
    public NextAction processRequest(final Packet request) {
        if (request.getMessage()==null || !request.getMessage().hasHeaders()) {
            return super.processRequest(request);
        }
        
        // try w3c version of ws-a first, then member submission version
        final MessageHeaders headers = request.getMessage().getHeaders();
        String action = AddressingUtils.getAction(headers, AddressingVersion.W3C, soapVersion);
        AddressingVersion adVersion = AddressingVersion.W3C;
        if (action == null) {
            action = AddressingUtils.getAction(headers, AddressingVersion.MEMBER, soapVersion);
            adVersion = AddressingVersion.MEMBER;
        }
        
        if (action != null) {
            if (action.equals(MetadataConstants.GET_REQUEST)) {
                final String toAddress = AddressingUtils.getTo(headers, adVersion, soapVersion);
                return doReturnWith(processGetRequest(request, toAddress, adVersion));
            } else if (action.equals(MetadataConstants.GET_MDATA_REQUEST)) {
                final Message faultMessage = Messages.create(MetadataConstants.GET_MDATA_REQUEST,
                    adVersion, soapVersion);
                return doReturnWith(request.createServerResponse(
                    faultMessage, adVersion, soapVersion,
                    adVersion.getDefaultFaultAction()));
            }
        }
        return super.processRequest(request);
    }

    /*
     * This method creates an xml stream buffer, writes the response to
     * it, and uses it to create a response message.
     */
    private Packet processGetRequest(final Packet request,
        final String address, final AddressingVersion adVersion) {
        
        try {
            final MutableXMLStreamBuffer buffer = new MutableXMLStreamBuffer();
            final XMLStreamWriter writer = buffer.createFromXMLStreamWriter();

            writeStartEnvelope(writer, adVersion);
            wsdlRetriever.addDocuments(writer, request, address);
            //writer.writeEndDocument();
            writeEndEnvelope(writer);
            writer.flush();

            final Message responseMessage = Messages.create(buffer);
            final Packet response = request.createServerResponse(
                responseMessage, adVersion, soapVersion, MetadataConstants.GET_RESPONSE);
            return response;
        } catch (XMLStreamException streamE) {
            final String exceptionMessage =
                MessagesMessages.MEX_0001_RESPONSE_WRITING_FAILURE(address);
            logger.log(Level.SEVERE, exceptionMessage, streamE);
            throw new WebServiceException(exceptionMessage, streamE);
        }
    }

    private void writeStartEnvelope(final XMLStreamWriter writer,
        final AddressingVersion adVersion) throws XMLStreamException {

        final String soapPrefix = "soapenv";

        writer.writeStartDocument();
        writer.writeStartElement(soapPrefix, "Envelope", soapVersion.nsUri);

        // todo: this line should go away after bug fix - 6418039
        writer.writeNamespace(soapPrefix, soapVersion.nsUri);

        writer.writeNamespace(MetadataConstants.WSA_PREFIX, adVersion.nsUri);
        writer.writeNamespace(MetadataConstants.MEX_PREFIX, MetadataConstants.MEX_NAMESPACE);

        writer.writeStartElement(soapPrefix, "Body", soapVersion.nsUri);
        writer.writeStartElement(MetadataConstants.MEX_PREFIX, "Metadata", MetadataConstants.MEX_NAMESPACE);
    }
    
    private void writeEndEnvelope(final XMLStreamWriter writer)
        throws XMLStreamException {
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
    }
}
