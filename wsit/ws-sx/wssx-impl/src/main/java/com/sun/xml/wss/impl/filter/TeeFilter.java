/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: TeeFilter.java,v 1.2 2010-10-21 15:37:29 snajper Exp $
 */

package com.sun.xml.wss.impl.filter;

import com.sun.xml.wss.WSITXMLFactory;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import jakarta.xml.soap.SOAPMessage;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.logging.LogStringsMessages;
import java.util.logging.Logger;

/**
 * Copies the SOAP message into an OutputStream using an optional stylesheet
 * to format the message.  The original message is not modified.  This is
 * analogous to the "tee" unix command.
 *
 * @author Edwin Goei
 */
public class TeeFilter {
    // TODO Fix the stylesheet to pretty print a SOAP Message
    private static Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    private static final String prettyPrintStylesheet =
    "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd'\n"
    + "  version='1.0'>\n"
    + "  <xsl:output method='xml' indent='yes'/>\n"
    + "  <xsl:strip-space elements='*'/>\n"
    + "  <xsl:template match='/'>\n"
    + "    <xsl:apply-templates/>\n"
    + "  </xsl:template>\n"
    + "  <xsl:template match='node() | @*'>\n"
    + "    <xsl:choose>\n"
    + "      <xsl:when test='contains(name(current()), \"wsse:Password\")'>\n"
    + "        <wsse:Password Type='{@Type}'>****</wsse:Password>\n"
    + "      </xsl:when>\n"
    + "      <xsl:otherwise>\n"
    + "        <xsl:copy>\n"
    + "          <xsl:apply-templates select='node() | @*'/>\n"
    + "        </xsl:copy>\n"
    + "      </xsl:otherwise>\n"
    + "    </xsl:choose>\n"
    + "  </xsl:template>\n"
    + "</xsl:stylesheet>\n";

    /** OutputStream for output. */
    private OutputStream out;

    /** Represents a stylesheet */
    private Templates templates;


    /**
     * Copy and optionally format a message
     *
     * @param out destination OutputStream
     * @param stylesheet XSLT stylesheet for format or if null, then does
     *     not format
     */
    public TeeFilter(OutputStream out, Source stylesheet)
    throws XWSSecurityException {
        init(out, stylesheet);
    }

    /**
     * Copy and optionally pretty print a message
     *
     * @param out destination OutputStream
     * @param prettyPrint true means to use built-in pretty print stylesheet
     */
    public TeeFilter(OutputStream out, boolean prettyPrint)
    throws XWSSecurityException {
        if (prettyPrint) {
            init(out, getPrettyPrintStylesheet());
        } else {
            init(out, null);
        }
    }

    /**
     * Saves a copy of message to Outputstream out
     *
     */
    public TeeFilter(OutputStream out) throws XWSSecurityException {
        init(out, null);
    }

    /**
     * A no-op
     *
     */
    public TeeFilter() throws XWSSecurityException {
        init(null, null);
    }

    private void init(OutputStream out, Source stylesheet)
    throws XWSSecurityException {
        this.out = out;

        if (stylesheet == null) {
            templates = null;
        } else {
            TransformerFactory tf = WSITXMLFactory.createTransformerFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
            //new com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl();
            try {
                templates = tf.newTemplates(stylesheet);
            } catch (TransformerConfigurationException e) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0147_DIAG_CAUSE_1(),
                new Object[] {e.getMessage()});
                throw new XWSSecurityException("Unable to use stylesheet", e);
            }
        }
    }

    private Source getPrettyPrintStylesheet() {
        //        if (true) {
        //            if (defaultStylesheetSource == null) {
        //                byte[] xsltBytes = defaultStylesheet.getBytes();
        //                ByteArrayInputStream bais = new ByteArrayInputStream(xsltBytes);
        //                defaultStylesheetSource = new StreamSource(bais);
        //            }
        //            return defaultStylesheetSource;
        //        } else {
        byte[] xsltBytes = prettyPrintStylesheet.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(xsltBytes);
        return new StreamSource(bais);
        //        }
    }

    /**
     * Invokes the MessageFilter on the SOAPMessage sm.  A
     * XWSSecurityException is thrown if the operation did not succeed.
     *
     * @param secureMessage SOAPMessage to perform the operation on
     *
     * @throws com.sun.xml.wss.XWSSecurityException if the operation did not
     *    succeed
     */
    public void process(SOAPMessage secureMessage) throws XWSSecurityException {
        if (out == null) {
            return;
        }

        Transformer transformer;
        try {
            if (secureMessage.countAttachments() > 0) {
                secureMessage.writeTo(out);
            } else {
                if (templates == null) {
                    // Use identity transform
                    transformer = WSITXMLFactory.createTransformerFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING).newTransformer();
                    //new com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl()
                    //.newTransformer();
                } else {
                    // Use supplied stylesheet via Templates object
                    transformer = templates.newTransformer();
                }
                Source msgSource = secureMessage.getSOAPPart().getContent();
                transformer.transform(msgSource, new StreamResult(out));
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0148_UNABLETO_PROCESS_SOAPMESSAGE(new Object[] {ex.getMessage()}));
            throw new XWSSecurityException("Unable to process SOAPMessage", ex);
        }
    }
}
