/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: DumpFilter.java,v 1.2 2010-10-21 15:37:28 snajper Exp $
 */

package com.sun.xml.wss.impl.filter;

import com.sun.xml.wss.logging.LogDomainConstants;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.logging.impl.filter.LogStringsMessages;
import java.util.logging.Logger;

/**
 * Dump a SOAP message for debugging.
 *
 */
public class DumpFilter  {
    
    private static  Level DEFAULT_LOG_LEVEL = Level.INFO;
    
    private static final String lineSeparator = System.getProperty("line.separator");

    private static Logger log =  Logger.getLogger(LogDomainConstants.IMPL_FILTER_DOMAIN,
        LogDomainConstants.IMPL_FILTER_DOMAIN_BUNDLE);
    /**
     * dumps the soap messages and throws XWSSecurityException if it is unable to dump.
     * @param context ProcessingContext
     */
    public static void process(ProcessingContext context)
        throws XWSSecurityException {

        OutputStream dest;
        ByteArrayOutputStream baos = null;
        // Collect output in a byte[]
        baos = new ByteArrayOutputStream();
        dest = baos;
        
        String label = "Sending Message";
        
        if (context.isInboundMessage()) {
            label = "Received Message";
        }
        
        String msg1 = "==== " + label + " Start ====" + lineSeparator;
        
        try {
            TeeFilter teeFilter;
            teeFilter = new TeeFilter(dest, true);
            teeFilter.process(context.getSOAPMessage());
        } catch (Exception e) {
            log.log(
            Level.SEVERE,
            LogStringsMessages.WSS_1411_UNABLETO_DUMP_SOAPMESSAGE(new Object[] { e.getMessage()}));
            throw new XWSSecurityException("Unable to dump SOAPMessage");
        }
        
        String msg2 = "==== " + label + " End  ====" + lineSeparator;

        byte[] bytes = baos.toByteArray();       
        String logMsg = msg1 + new String(bytes) + msg2;
        log.log(DEFAULT_LOG_LEVEL, logMsg);
    }
    
}
