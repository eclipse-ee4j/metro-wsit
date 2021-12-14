/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.xmlfilter;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.xmlfilter.localization.LocalizationMessages;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class XmlFilteringUtils {
    public static final class AttributeInfo {
        private final QName name;
        private final String value;
        
        AttributeInfo(QName name, String value) {
            this.name = name;
            this.value = value;
        }

        public QName getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
    
    private static final Logger LOGGER = Logger.getLogger(XmlFilteringUtils.class);
   
    /** 
     * Prevents creation of a new instance of XmlFilteringUtils 
     */
    private XmlFilteringUtils() {
        // nothing to initialize
    }
    
    public static String getDefaultNamespaceURI(final XMLStreamWriter writer) {
        return writer.getNamespaceContext().getNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX);
    }
    
    public static QName getElementNameToWrite(final Invocation invocation, final String defaultNamespaceURI) {
        checkInvocationParameter(invocation, XmlStreamWriterMethodType.WRITE_START_ELEMENT);
        
        /*
          void writeStartElement(String localName)
          void writeStartElement(String namespaceURI, String localName)
          void writeStartElement(String prefix, String localName, String namespaceURI)
         */
        final int argumentsCount = invocation.getArgumentsCount();
        final String namespaceURI;
        final String localName;
        
        switch (argumentsCount) {
            case 1:
                namespaceURI = defaultNamespaceURI;
                localName = invocation.getArgument(0).toString();
                break;
            case 2:
                namespaceURI = invocation.getArgument(0).toString();
                localName = invocation.getArgument(1).toString();
                break;
            case 3:
                localName = invocation.getArgument(1).toString();
                namespaceURI = invocation.getArgument(2).toString();
                break;
            default:
                throw LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.XMLF_5003_UNEXPECTED_ARGUMENTS_COUNT(XmlStreamWriterMethodType.WRITE_START_ELEMENT + "(...)", argumentsCount)));
        }
        
        return new QName(namespaceURI, localName);
    }
    
    public static AttributeInfo getAttributeNameToWrite(final Invocation invocation, final String defaultNamespaceURI) {
        checkInvocationParameter(invocation, XmlStreamWriterMethodType.WRITE_ATTRIBUTE);
        
        /*
         * void writeAttribute(String localName, String value)
         * void writeAttribute(String namespaceURI, String localName, String value)
         * void writeAttribute(String prefix, String namespaceURI, String localName, String value)
         */
        final int argumentsCount = invocation.getArgumentsCount();
        String namespaceURI, localName, value;
        
        switch (argumentsCount) {
            case 2:
                namespaceURI = defaultNamespaceURI;
                localName = invocation.getArgument(0).toString();
                value = invocation.getArgument(1).toString();
                break;
            case 3:
                namespaceURI = invocation.getArgument(0).toString();
                localName = invocation.getArgument(1).toString();
                value = invocation.getArgument(2).toString();
                break;
            case 4:
                namespaceURI = invocation.getArgument(1).toString();
                localName = invocation.getArgument(2).toString();
                value = invocation.getArgument(3).toString();
                break;
            default:
                throw LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.XMLF_5003_UNEXPECTED_ARGUMENTS_COUNT(XmlStreamWriterMethodType.WRITE_ATTRIBUTE + "(...)", argumentsCount)));
        }
        
        return new AttributeInfo(new QName(namespaceURI, localName), value);
    }
    
    private static void checkInvocationParameter(final Invocation invocation, final XmlStreamWriterMethodType expectedType) {
        if (invocation == null) {
            throw LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.XMLF_5012_METHOD_PARAMETER_CANNOT_BE_NULL("Invocation parameter")));
        } else {
            if (invocation.getMethodType() != expectedType) {
                throw LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.XMLF_5013_ILLEGAL_INVOCATION_METHOD_TYPE(invocation.getMethodType(), expectedType)));
            }
        }
    }
}
