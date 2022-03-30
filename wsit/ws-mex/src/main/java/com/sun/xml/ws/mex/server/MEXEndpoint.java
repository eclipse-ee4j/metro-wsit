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

import com.sun.istack.NotNull;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.server.BoundEndpoint;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.message.ProblemActionHeader;
import com.sun.xml.ws.mex.MetadataConstants;
import com.sun.xml.ws.mex.MessagesMessages;
import com.sun.xml.ws.transport.http.servlet.ServletModule;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import jakarta.xml.soap.Detail;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFault;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.Provider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.ServiceMode;
import jakarta.xml.ws.WebServiceProvider;
import jakarta.xml.ws.soap.Addressing;


@ServiceMode(value=Service.Mode.MESSAGE)
@WebServiceProvider
@Addressing(required=true)
public class MEXEndpoint implements Provider<Message> {

    @Resource
    protected WebServiceContext wsContext;

    private static final Logger logger =
        Logger.getLogger(MEXEndpoint.class.getName());

    @Override
    public Message invoke(Message requestMsg) {
        if (requestMsg == null || !requestMsg.hasHeaders()) {
            // TODO: Better error message
            throw new WebServiceException("Malformed MEX Request");
        }

        @SuppressWarnings({"unchecked"})
        WSEndpoint<?> wsEndpoint = (WSEndpoint<?>) wsContext.getMessageContext().get(JAXWSProperties.WSENDPOINT);
        SOAPVersion soapVersion = wsEndpoint.getBinding().getSOAPVersion();

        // try w3c version of ws-a first, then member submission version
        final MessageHeaders headers = requestMsg.getHeaders();

        String action = AddressingUtils.getAction(headers, AddressingVersion.W3C, soapVersion);
        AddressingVersion wsaVersion = AddressingVersion.W3C;
        if (action == null) {
            action = AddressingUtils.getAction(headers, AddressingVersion.MEMBER, soapVersion);
            wsaVersion = AddressingVersion.MEMBER;
        }

        if (action == null) {
            // TODO: Better error message
            throw new WebServiceException("No wsa:Action specified");
        }
        else if (action.equals(MetadataConstants.GET_REQUEST)) {
            final String toAddress = AddressingUtils.getTo(headers, wsaVersion, soapVersion);
            return processGetRequest(requestMsg, toAddress, wsaVersion, soapVersion);
        }
        else if (action.equals(MetadataConstants.GET_MDATA_REQUEST)) {
            String faultText = MessagesMessages.MEX_0017_GET_METADATA_NOT_IMPLEMENTED(MetadataConstants.GET_MDATA_REQUEST, MetadataConstants.GET_REQUEST);
            logger.warning(faultText);
            final Message faultMessage = createFaultMessage(faultText, MetadataConstants.GET_MDATA_REQUEST,
                wsaVersion, soapVersion);
            wsContext.getMessageContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, wsaVersion.getDefaultFaultAction());
            return faultMessage;
        }
        // If here, either action is unsupported
        // TODO: Better error message
        throw new UnsupportedOperationException(action);
    }

    /*
     * This method creates an xml stream buffer, writes the response to
     * it, and uses it to create a response message.
     */
    private Message processGetRequest(final Message request,
        String address, final AddressingVersion wsaVersion,
        final SOAPVersion soapVersion) {

        try {
            WSEndpoint<?> ownerEndpoint = findEndpoint();

            // If the owner endpoint has been found, then
            // get its metadata and write it to the response message
            if (ownerEndpoint != null) {
                final MutableXMLStreamBuffer buffer = new MutableXMLStreamBuffer();
                final XMLStreamWriter writer = buffer.createFromXMLStreamWriter();

                address = this.getAddressFromMexAddress(address, soapVersion);
                writeStartEnvelope(writer, wsaVersion, soapVersion);
                WSDLRetriever wsdlRetriever = new WSDLRetriever(ownerEndpoint);
                wsdlRetriever.addDocuments(writer, null, address);
                writeEndEnvelope(writer);
                writer.flush();
                final Message responseMessage = Messages.create(buffer);

                MessageHeaders headers = responseMessage.getHeaders();
                //headers.add(Headers.create(new QName(wsaVersion.nsUri, "To"), "http://www.w3.org/2005/08/addressing/anonymous"));
                headers.add(Headers.create(new QName(wsaVersion.nsUri, "Action"), MetadataConstants.GET_RESPONSE));
                //headers.add(Headers.create(new QName(wsaVersion.nsUri, "MessageID"), "uuid:" + UUID.randomUUID().toString()));
                //headers.add(Headers.create(new QName(wsaVersion.nsUri, "RelatedTo"), request.getHeaders().getMessageID(wsaVersion, soapVersion)));

                //wsContext.getMessageContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, GET_RESPONSE);
                return responseMessage;
            }

            // If we get here there was no metadata for the owner endpoint
            WebServiceException exception = new WebServiceException(MessagesMessages.MEX_0016_NO_METADATA());
            final Message faultMessage = Messages.create(exception, soapVersion);
            wsContext.getMessageContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, wsaVersion.getDefaultFaultAction());
            return faultMessage;
        } catch (XMLStreamException streamE) {
            final String exceptionMessage =
               MessagesMessages.MEX_0001_RESPONSE_WRITING_FAILURE(address);
            logger.log(Level.SEVERE, exceptionMessage, streamE);
            throw new WebServiceException(exceptionMessage, streamE);
        }
    }

    /**
     * Find the endpoint that this MEX endpoint is serving.
     *
     * This method is searching for an endpoint that has the same address as the MEX endpoint
     * with the suffix "/mex" removed. If the MEX endpoint has an HTTPS address,
     * it will first look for an endpoint on HTTP and then HTTPS.
     *
     * @return The endpoint that owns the actual service or null.
     */
    @SuppressWarnings({"unchecked"})
    private WSEndpoint<?> findEndpoint() {

        WSEndpoint<?> wsEndpoint = (WSEndpoint<?>) wsContext.getMessageContext().get(JAXWSProperties.WSENDPOINT);
        HttpServletRequest servletRequest = (HttpServletRequest)wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        if (servletRequest == null) {
            // TODO: better error message
            throw new WebServiceException("MEX: no ServletRequest can be found");
        }

        // Derive the address of the owner endpoint.
        // e.g. http://localhost/foo/mex --> http://localhost/foo
        WSEndpoint<?> ownerEndpoint = null;
        ServletModule module = wsEndpoint.getContainer().getSPI(ServletModule.class);
        String baseAddress = module.getContextPath(servletRequest);
        String ownerEndpointAddress = null;
        List<BoundEndpoint> boundEndpoints = module.getBoundEndpoints();
        for (BoundEndpoint endpoint : boundEndpoints) {
            if (endpoint.getEndpoint().equalsProxiedInstance(wsEndpoint)) {
                ownerEndpointAddress = endpoint.getAddress(baseAddress).toString();
                break;
            }
        }
        if (ownerEndpointAddress != null) {
            ownerEndpointAddress = getAddressFromMexAddress(ownerEndpointAddress, wsEndpoint.getBinding().getSOAPVersion());

            boundEndpoints = module.getBoundEndpoints();
            for (BoundEndpoint endpoint : boundEndpoints) {
                //compare ownerEndpointAddress with this endpoints address
                //   if matches, set ownerEndpoint to the corresponding WSEndpoint
                String endpointAddress = endpoint.getAddress(baseAddress).toString();
                if (endpointAddress.equals(ownerEndpointAddress)) {
                    ownerEndpoint = endpoint.getEndpoint();
                    break;
                }
            }
        }

        return ownerEndpoint;
    }

    private void writeStartEnvelope(final XMLStreamWriter writer,
        final AddressingVersion wsaVersion, final SOAPVersion soapVersion)
        throws XMLStreamException {

        final String soapPrefix = "soapenv";

        writer.writeStartDocument();
        writer.writeStartElement(soapPrefix, "Envelope", soapVersion.nsUri);

        // todo: this line should go away after bug fix - 6418039
        writer.writeNamespace(soapPrefix, soapVersion.nsUri);

        writer.writeNamespace(MetadataConstants.WSA_PREFIX, wsaVersion.nsUri);
        writer.writeNamespace(MetadataConstants.MEX_PREFIX, MetadataConstants.MEX_NAMESPACE);

        writer.writeStartElement(soapPrefix, "Body", soapVersion.nsUri);
        writer.writeStartElement(MetadataConstants.MEX_PREFIX, "Metadata", MetadataConstants.MEX_NAMESPACE);
    }

    private Message createFaultMessage(@NotNull final String faultText, @NotNull final String unsupportedAction,
            @NotNull final AddressingVersion av, @NotNull final SOAPVersion sv) {
        final QName subcode = av.actionNotSupportedTag;
        Message faultMessage;
        SOAPFault fault;
        try {
            if (sv == SOAPVersion.SOAP_12) {
                fault = SOAPVersion.SOAP_12.getSOAPFactory().createFault();
                fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
                fault.appendFaultSubcode(subcode);
                Detail detail = fault.addDetail();
                SOAPElement se = detail.addChildElement(av.problemActionTag);
                se = se.addChildElement(av.actionTag);
                se.addTextNode(unsupportedAction);
            } else {
                fault = SOAPVersion.SOAP_11.getSOAPFactory().createFault();
                fault.setFaultCode(subcode);
            }
            fault.setFaultString(faultText);

            faultMessage = SOAPFaultBuilder.createSOAPFaultMessage(sv, fault);
            if (sv == SOAPVersion.SOAP_11) {
                faultMessage.getHeaders().add(new ProblemActionHeader(unsupportedAction, av));
            }
        } catch (SOAPException e) {
            throw new WebServiceException(e);
        }

        return faultMessage;
    }

    private String getAddressFromMexAddress(String mexAddress, SOAPVersion soapVersion){
        if (mexAddress.endsWith("mex")){
            return mexAddress.substring(0, mexAddress.length()-"/mex".length());
        }

        if (soapVersion.equals(SOAPVersion.SOAP_11)){
            return mexAddress.substring(0, mexAddress.length()-"/mex/soap11".length());
        } else if (soapVersion.equals(SOAPVersion.SOAP_12)){
            return mexAddress.substring(0, mexAddress.length()-"/mex/soap12".length());
        }

        return null;
    }


   private void writeEndEnvelope(final XMLStreamWriter writer)
        throws XMLStreamException {
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
    }
}
