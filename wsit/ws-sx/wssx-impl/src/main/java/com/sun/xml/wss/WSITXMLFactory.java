/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss;

import com.sun.xml.wss.impl.MessageConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * @author Nithya Subramanian
 * Class to invoke secure XML Factory methods.
 * TODO: Refactor this code to istack-commons,
 * Replica of XMLFactory in JAXB internal
 */
public class WSITXMLFactory {


    public static final boolean DISABLE_SECURE_PROCESSING =
            Boolean.parseBoolean(System.getProperty(MessageConstants.DISABLE_XML_SECURITY));

    private static boolean xmlFeatureValue(boolean runtimeSetting) {
        return !(DISABLE_SECURE_PROCESSING || (!DISABLE_SECURE_PROCESSING && runtimeSetting));
    }

    /**
     * Returns properly configured (e.g. security features) schema factory
     * - namespaceAware == true
     * - securityProcessing == is set based on security processing property, default is true
     */
    public static final SchemaFactory createSchemaFactory(final String language, boolean disableSecureProcessing) throws IllegalArgumentException {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(language);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, xmlFeatureValue(disableSecureProcessing));
            return factory;
        } catch (SAXNotRecognizedException ex) {
            Logger.getLogger(WSITXMLFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException( ex);
        } catch (SAXNotSupportedException ex) {
            Logger.getLogger(WSITXMLFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException( ex);
        }
    }

    /**
     * Returns properly configured (e.g. security features) parser factory
     * - namespaceAware == true
     * - securityProcessing == is set based on security processing property, default is true
     */
    public static final SAXParserFactory createParserFactory(boolean disableSecureProcessing) throws IllegalArgumentException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, xmlFeatureValue(disableSecureProcessing));
            return factory;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(WSITXMLFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException( ex);
        } catch (SAXNotRecognizedException ex) {
            Logger.getLogger(WSITXMLFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException( ex);
        } catch (SAXNotSupportedException ex) {
            Logger.getLogger(WSITXMLFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException( ex);
        }
    }

    /**
     * Returns properly configured (e.g. security features) factory
     * - securityProcessing == is set based on security processing property, default is true
     */
    public static final XPathFactory createXPathFactory(boolean disableSecureProcessing) throws IllegalArgumentException {
        try {
            XPathFactory factory = XPathFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, xmlFeatureValue(disableSecureProcessing));
            return factory;
        } catch (XPathFactoryConfigurationException ex) {
            Logger.getLogger(WSITXMLFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException( ex);
        }
    }

    /**
     * Returns properly configured (e.g. security features) factory
     * - securityProcessing == is set based on security processing property, default is true
     */
    public static final TransformerFactory createTransformerFactory(boolean disableSecureProcessing) throws IllegalArgumentException {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, xmlFeatureValue(disableSecureProcessing));
            return factory;
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(WSITXMLFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException( ex);
        }
    }

    /**
     * Returns properly configured (e.g. security features) factory
     * - namespaceAware == true
     * - securityProcessing == is set based on security processing property, default is true
     */
    public static final DocumentBuilderFactory createDocumentBuilderFactory(boolean disableSecureProcessing) throws IllegalStateException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, xmlFeatureValue(disableSecureProcessing));
            return factory;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(WSITXMLFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException( ex);
        }
    }



}
