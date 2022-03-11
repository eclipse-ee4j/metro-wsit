/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.runtime;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.sun.istack.logging.Logger;

import com.sun.xml.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.api.server.SDDocumentFilter;
import com.sun.xml.ws.transport.tcp.wsit.PortAttributeInvocationTransformer;
import com.sun.xml.ws.xmlfilter.EnhancedXmlStreamWriterProxy;
import com.sun.xml.ws.xmlfilter.FilteringInvocationProcessor;
import com.sun.xml.ws.xmlfilter.InvocationProcessor;
import com.sun.xml.ws.xmlfilter.InvocationProcessorFactory;
import com.sun.xml.ws.xmlfilter.MexImportFilteringStateMachine;
import com.sun.xml.ws.xmlfilter.PrivateAttributeFilteringStateMachine;
import com.sun.xml.ws.xmlfilter.PrivateElementFilteringStateMachine;

/**
 * The class provides an implementaion of JAX-WS {@code SDDocumentFilter} interface.
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class WsdlDocumentFilter implements SDDocumentFilter {
    private static final Logger LOGGER = Logger.getLogger(WsdlDocumentFilter.class);

    private static final InvocationProcessorFactory FILTERING_FACTORY = new InvocationProcessorFactory() {
        @Override
        public InvocationProcessor createInvocationProcessor(final XMLStreamWriter writer) throws XMLStreamException {
            return new FilteringInvocationProcessor(
                    writer,
                    new PortAttributeInvocationTransformer(),
                    new MexImportFilteringStateMachine(),
                    new PrivateAttributeFilteringStateMachine(),
                    new PrivateElementFilteringStateMachine(
                        new QName("http://schemas.sun.com/2006/03/wss/server", "KeyStore"),
                        new QName("http://schemas.sun.com/2006/03/wss/server", "TrustStore"),
                        new QName("http://schemas.sun.com/2006/03/wss/server", "CallbackHandlerConfiguration"),
                        new QName("http://schemas.sun.com/2006/03/wss/server", "ValidatorConfiguration"),
                        new QName("http://schemas.sun.com/2006/03/wss/server", "DisablePayloadBuffering"),
                        new QName("http://schemas.sun.com/2006/03/wss/server", "KerberosConfig"),

                        new QName("http://schemas.sun.com/2006/03/wss/client", "KeyStore"),
                        new QName("http://schemas.sun.com/2006/03/wss/client", "TrustStore"),
                        new QName("http://schemas.sun.com/2006/03/wss/client", "CallbackHandlerConfiguration"),
                        new QName("http://schemas.sun.com/2006/03/wss/client", "ValidatorConfiguration"),
                        new QName("http://schemas.sun.com/2006/03/wss/client", "DisablePayloadBuffering"),
                        new QName("http://schemas.sun.com/2006/03/wss/client", "KerberosConfig"),

                        new QName("http://schemas.sun.com/ws/2006/05/sc/server", "SCConfiguration"),

                        new QName("http://schemas.sun.com/ws/2006/05/sc/client", "SCClientConfiguration"),

                        new QName("http://schemas.sun.com/ws/2006/05/trust/server", "STSConfiguration"),

                        new QName("http://schemas.sun.com/ws/2006/05/trust/client", "PreconfiguredSTS"),

                        ManagedServiceAssertion.MANAGED_SERVICE_QNAME,
                        ManagedClientAssertion.MANAGED_CLIENT_QNAME
                    )
                );
        }
    };

    @Override
    public XMLStreamWriter filter(final SDDocument sdDocument, final XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        if (LOGGER.isMethodCallLoggable()) {
            LOGGER.entering(sdDocument, xmlStreamWriter);
        }
        XMLStreamWriter result = null;
        try {
            result = EnhancedXmlStreamWriterProxy.createProxy(xmlStreamWriter, FILTERING_FACTORY);
            return result;
        } finally {
            LOGGER.exiting(result);
        }
    }
}
