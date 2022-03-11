/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.policy.parser;

import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.SDDocumentSource;
import com.sun.xml.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.ws.api.wsdl.parser.XMLEntityResolver.Parser;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.ws.streaming.TidyXMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.SAXException;

/**
 * A collection of utility methods to load resources from the classpath.
 *
 * @author Fabian Ritzmann
 */
public class PolicyResourceLoader {

    /**
     * Parse the given URL and return the resulting WSDLModel.
     *
     * Note that this method uses the PolicyResolverFactory.DEFAULT_POLICY_RESOLVER
     * instead of the PolicyResolver that is injected at runtime.
     *
     * @param resourceUrl URL for a valid WSDL document.
     * @param isClient True if client-side parser, false otherwise.
     * @return The WSDLModel that corresponds to the given WSDL document.
     * @throws IOException If resourceUrl could not be opened.
     * @throws XMLStreamException If document could not be read.
     * @throws SAXException If document could not be parsed.
     */
    public static WSDLModel getWsdlModel(URL resourceUrl, boolean isClient)
            throws IOException, XMLStreamException, SAXException {
        final SDDocumentSource doc = SDDocumentSource.create(resourceUrl);
        final Parser parser = new Parser(doc);
        final WSDLModel model = WSDLModel.WSDLParser.parse(parser,
                                                           new PolicyEntityResolver(),
                                                           isClient,
                                                           Container.NONE,
                                                           PolicyResolverFactory.DEFAULT_POLICY_RESOLVER
        );
        return model;
    }


    /**
     * Assumes that a given XML entity holds a valid URL and returns an
     * XMLEntityResolver.Parser for that URL. An XMLEntityResolver.Parser is
     * essentially a wrapper around an XMLStreamReader.
     *
     * @author Jakub Podlesak (jakub.podlesak at sun.com)
     * @author Fabian Ritzmann
     */
    private static class PolicyEntityResolver implements XMLEntityResolver {

        private static final Logger LOGGER = Logger.getLogger(PolicyEntityResolver.class);
        private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

        /**
         * Assumes that a given XML entity holds a valid URL and returns an
         * XMLEntityResolver.Parser for that URL.
         *
         * @param publicId The public ID of the entity. This parameter is ignored.
         * @param systemId The system ID of the entity. Must be a valid URL.
         * @return A parser (i.e. an XMLStreamReader) for the systemId URL.
         * @throws XMLStreamException If the XMLStreamReader could not be created
         * @throws IOException If the URL was invalid or a connection to the URL
         * failed
         * @see javax.xml.stream.XMLStreamReader
         * @see com.sun.xml.ws.api.wsdl.parser.XMLEntityResolver.Parser
         */
        @Override
        public Parser resolveEntity(final String publicId, final String systemId)
            throws XMLStreamException, IOException {

            LOGGER.entering(publicId, systemId);
            Parser parser = null;

            try {
                // TODO: think about using alg from http://www.w3.org/International/O-URL-code.html
                final URL systemUrl = new URL(PolicyUtils.Rfc2396.unquote(systemId));
                final InputStream input = systemUrl.openStream();
                final XMLStreamReader reader = new TidyXMLStreamReader(xmlInputFactory.createXMLStreamReader(systemId, input), input);

                parser = new Parser(systemUrl, reader);
                return parser;
            } finally {
                LOGGER.exiting(parser);
            }
        }
    }
}
