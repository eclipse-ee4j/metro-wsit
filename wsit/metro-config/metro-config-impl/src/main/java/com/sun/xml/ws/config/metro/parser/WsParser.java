/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.config.metro.parser;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.config.metro.parser.jsr109.WebserviceDescriptionType;
import com.sun.xml.ws.config.metro.parser.jsr109.WebservicesType;

import java.util.List;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import jakarta.xml.ws.WebServiceException;

/**
 * Parse webservices.xml.
 *
 * @author Fabian Ritzmann
 */
class WsParser {

    private static final Logger LOGGER = Logger.getLogger(WsParser.class);

    private static JAXBContext context;

    public WsParser() throws WebServiceException {
        try {
            // We don't need to care about race conditions here, in the worst case
            // the context gets initialized several times. We don't instantiate context
            // in a static block because we would lose the exception message.
            if (context == null) {
                context = JAXBContext.newInstance("com.sun.xml.ws.config.metro.parser.jsr109");
            }
        } catch (JAXBException e) {
            // TODO logging message
            throw LOGGER.logSevereException(new WebServiceException("Failed to initialize", e));
        }
    }

    public List<WebserviceDescriptionType> parse(final XMLStreamReader reader) throws WebServiceException {
        try {
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final JAXBElement<WebservicesType> elements = unmarshaller.unmarshal(reader, WebservicesType.class);
            final WebservicesType root = elements.getValue();
            final List<WebserviceDescriptionType> descriptions = root.getWebserviceDescription();
            return descriptions;
        } catch (JAXBException e) {
            // TODO logging message
            throw LOGGER.logSevereException(new WebServiceException("Failed to unmarshal webservices.xml", e));
        }
    }

}
