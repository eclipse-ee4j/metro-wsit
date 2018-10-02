/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;



import javax.xml.stream.StreamFilter;
import org.xml.sax.InputSource;
import javax.xml.ws.WebServiceException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/**
 * <p>A factory to create XML and FI parsers.</p>
 *
 * @author Santiago.PericasGeertsen@sun.com
 */
public class XMLStreamReaderFactory {
    
    /**
     * StAX input factory shared by all threads.
     */
    static final XMLInputFactory xmlInputFactory;
    
    /**
     * FI stream reader for each thread.
     */
    static final ThreadLocal fiStreamReader = new ThreadLocal();
    
    /**
     * Zephyr's stream reader for each thread.
     */
    static final ThreadLocal<XMLStreamReader> xmlStreamReader = new ThreadLocal<XMLStreamReader>();
    
    static {
        // Use StAX pluggability layer to get factory instance
        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        
        try {
            // Turn OFF internal factory caching in Zephyr -- not thread safe
            xmlInputFactory.setProperty("reuse-instance", Boolean.FALSE);
        } catch (IllegalArgumentException e) {
            // falls through
        }
    }
    
    // -- XML ------------------------------------------------------------
    
    /**
     * Returns a fresh StAX parser created from an InputSource. Use this
     * method when concurrent instances are needed within a single thread.
     *
     * TODO: Reject DTDs?
     */
    public static XMLStreamReader createFreshXMLStreamReader(InputSource source,
            boolean rejectDTDs) {
        try {
            synchronized (xmlInputFactory) {
                // Char stream available?
                if (source.getCharacterStream() != null) {
                    return xmlInputFactory.createXMLStreamReader(
                            source.getSystemId(), source.getCharacterStream());
                }
                
                // Byte stream available?
                if (source.getByteStream() != null) {
                    return xmlInputFactory.createXMLStreamReader(
                            source.getSystemId(), source.getByteStream());
                }
                
                // Otherwise, open URI
                return xmlInputFactory.createXMLStreamReader(source.getSystemId(),
                        new URL(source.getSystemId()).openStream());
            }
        } catch (Exception e) {
            throw new WebServiceException("stax.cantCreate",e);
        }
    }
    
    /**
     * This factory method would be used for example when caller wants to close the stream.
     */
    public static XMLStreamReader createFreshXMLStreamReader(String systemId, InputStream stream) {
        try {
            synchronized (xmlInputFactory) {
                // Otherwise, open URI
                return xmlInputFactory.createXMLStreamReader(systemId,
                        stream);
            }
        } catch (Exception e) {
            throw new WebServiceException("stax.cantCreate",e);
        }
    }
    
    /**
     * This factory method would be used for example when caller wants to close the stream.
     */
    public static XMLStreamReader createFreshXMLStreamReader(String systemId, Reader reader) {
        try {
            synchronized (xmlInputFactory) {
                // Otherwise, open URI
                return xmlInputFactory.createXMLStreamReader(systemId,
                        reader);
            }
        } catch (Exception e) {
            throw new WebServiceException("stax.cantCreate",e);
        }
    }
    
    
    public static XMLStreamReader createFilteredXMLStreamReader(XMLStreamReader reader,StreamFilter filter){
        try {
            synchronized (xmlInputFactory) {
                // Otherwise, open URI
                return xmlInputFactory.createFilteredReader(reader,filter) ;
            }
        } catch (Exception e) {
            throw new WebServiceException("stax.cantCreate",e);
        }
    }
    
   
    
}
