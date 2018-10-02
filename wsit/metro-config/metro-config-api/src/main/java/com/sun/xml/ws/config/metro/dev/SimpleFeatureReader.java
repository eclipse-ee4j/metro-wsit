/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.config.metro.dev;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.config.metro.util.ParserUtil;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

/**
 * Parse a feature with no further attributes than "enabled" and return it as
 * a WebServiceFeature instance.
 *
 * @author Fabian Ritzmann
 */
public abstract class SimpleFeatureReader<T extends WebServiceFeature> implements FeatureReader {

    private static final Logger LOGGER = Logger.getLogger(SimpleFeatureReader.class);

    public T parse(final XMLEventReader reader) throws WebServiceException {
        try {
            final StartElement element = reader.nextEvent().asStartElement();
            boolean attributeEnabled = true;
            final QName elementName = element.getName();
            final Iterator iterator = element.getAttributes();
            while (iterator.hasNext()) {
                final Attribute nextAttribute = (Attribute) iterator.next();
                final QName attributeName = nextAttribute.getName();
                if (ENABLED_ATTRIBUTE_NAME.equals(attributeName)) {
                    attributeEnabled = ParserUtil.parseBooleanValue(nextAttribute.getValue());
                }
                else {
                    // TODO logging message
                    throw LOGGER.logSevereException(new WebServiceException("Unexpected attribute, was " + nextAttribute));
                }
            }

            loop:
            while (reader.hasNext()) {
                try {
                    final XMLEvent event = reader.nextEvent();
                    switch (event.getEventType()) {
                        case XMLStreamConstants.COMMENT:
                            break; // skipping the comments and start document events
                        case XMLStreamConstants.CHARACTERS:
                            if (event.asCharacters().isWhiteSpace()) {
                                break;
                            }
                            else {
                                // TODO: logging message
                                throw LOGGER.logSevereException(new WebServiceException("No character data allowed, was " + event.asCharacters()));
                            }
                        case XMLStreamConstants.END_ELEMENT:
                            final EndElement endElement = event.asEndElement();
                            if (!elementName.equals(endElement.getName())) {
                                // TODO logging message
                                throw LOGGER.logSevereException(new WebServiceException("Expected end element"));
                            }
                            break loop;
                        default:
                            throw LOGGER.logSevereException(new WebServiceException("Unexpected event, was " + event));
                    }
                } catch (XMLStreamException e) {
                    // TODO logging message
                    throw LOGGER.logSevereException(new WebServiceException("Failed to unmarshal XML document", e));
                }
            }
            return createFeature(attributeEnabled);
        } catch (XMLStreamException e) {
            // TODO logging message
            throw LOGGER.logSevereException(new WebServiceException("Failed to unmarshal XML document", e));
        }
    }

    /**
     * Instantiate the proper feature class.
     */
    protected abstract T createFeature(boolean enabled) throws WebServiceException;

}
