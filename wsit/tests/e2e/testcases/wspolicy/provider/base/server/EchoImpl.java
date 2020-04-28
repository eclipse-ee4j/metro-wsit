/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wspolicy.provider.base.server;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import jakarta.xml.ws.Provider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.ServiceMode;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceProvider;

@ServiceMode(value=Service.Mode.PAYLOAD)
@WebServiceProvider(wsdlLocation="WEB-INF/wsdl/EchoService.wsdl")
public class EchoImpl implements Provider<Source> {

    private static final String XSLT = "<?xml version=\"1.0\"?>"
                                     + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
                                     + "  <xsl:output method=\"text\" omit-xml-declaration=\"yes\"/>"
                                     + "  <xsl:for-each select=\"echo\">"
                                     + "    <xsl:value-of select=\"arg0\"/>"
                                     + "  </xsl:for-each>"
                                     + "</xsl:stylesheet>";
    private final Transformer transformer;

    public EchoImpl() {
        try {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(XSLT)));
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(EchoImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new WebServiceException(ex);
        }
    }
    
    public Source invoke(Source request) {
        try {
            final StringWriter writer = new StringWriter();
            final Result output = new StreamResult(writer);
            transformer.transform(request, output);
            final String input = writer.toString();
            final String result = echo(input);
            final String body = "<ns:echoResponse xmlns:ns=\"http://server.wsdl.provider.wspolicy/\">"
                              + "  <return>" + result + "</return>"
                              + "</ns:echoResponse>";
            final Source response = new StreamSource(new ByteArrayInputStream(body.getBytes()));
            return response;
        } catch (TransformerException ex) {
            Logger.getLogger(EchoImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new WebServiceException(ex);
        }
    }

    private String echo(String yodel) {
        final StringBuffer holladrio = new StringBuffer();
        final int l = yodel.length();
        for (int i = 0; i <  l; i++) {
            holladrio.append(yodel.substring(i, l));
        }
        return holladrio.toString();
    }

}
