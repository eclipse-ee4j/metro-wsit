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
 * $Id: CanonicalizerFactory.java,v 1.2 2010-10-21 15:37:19 snajper Exp $
 * $Revision: 1.2 $
 * $Date: 2010-10-21 15:37:19 $
 */

package com.sun.xml.wss.impl.c14n;

import java.util.HashMap;
import java.nio.charset.Charset;
import jakarta.mail.internet.ContentType;
import com.sun.xml.wss.XWSSecurityException;

import com.sun.xml.wss.swa.MimeConstants;
/**
 *
 * @author  XWS-Security Team
 */
public class CanonicalizerFactory {

    static MimeHeaderCanonicalizer _mhCanonicalizer = null;

    static HashMap<String, Object> _canonicalizers = new HashMap<>(10);
   
    public static final Canonicalizer getCanonicalizer(String mimeType) throws Exception {
        ContentType contentType = new ContentType(mimeType);        
        String baseMimeType = contentType.getBaseType();
        
        if (baseMimeType.equalsIgnoreCase(MimeConstants.TEXT_PLAIN_TYPE)) {
            ensureRegisteredCharset(contentType);
        }

        // use primaryMimeType as the key.
        // i.e. text canonicalizer will apply to text/* etc.
        String primaryMimeType = contentType.getPrimaryType();
        Canonicalizer _canonicalizer = 
                           (Canonicalizer)_canonicalizers.get(primaryMimeType);
        
        if (_canonicalizer == null) {
            _canonicalizer = newCanonicalizer(primaryMimeType);
        }

        // defaults to US-ASCII
        String charset = contentType.getParameter("charset"); 
        if (charset != null) _canonicalizer.setCharset(charset);
        
        return _canonicalizer;
    }

    /*
     * Primary MimeType is the key. 
     * ImageCanonicalizer is sufficient for all image/** MIME types and so on.
     *
     * Finer grained processing as per section 4.1.4 RFC2046  has not been 
     * incorporated yet. I don't think so much processing is required at this time.
     *
     */ 
    public static final Canonicalizer newCanonicalizer(String primaryMimeType) {
        Canonicalizer canonicalizer = null; 
        
        if (primaryMimeType.equalsIgnoreCase("text"))
            canonicalizer = new TextPlainCanonicalizer();
        else
        if (primaryMimeType.equalsIgnoreCase("image"))
            canonicalizer = new ImageCanonicalizer();
        else
        if (primaryMimeType.equalsIgnoreCase("application"))
            canonicalizer = new ApplicationCanonicalizer();
        else
            ; // log n throw exception

        _canonicalizers.put(primaryMimeType, canonicalizer);

        return canonicalizer;
    }

    public static final MimeHeaderCanonicalizer getMimeHeaderCanonicalizer(String charset) {
        if (_mhCanonicalizer == null)
            _mhCanonicalizer = new MimeHeaderCanonicalizer();
        _mhCanonicalizer.setCharset(charset); 
        return _mhCanonicalizer;
    }

    public static void registerCanonicalizer(String baseMimeType,
                                             Canonicalizer implementingClass) {
         _canonicalizers.put(baseMimeType, implementingClass);
    }
    
    public static void registerCanonicalizer(String baseMimeType,
                                             String implementingClass) throws XWSSecurityException {
         try {
             Class _class = Class.forName(implementingClass);
             Canonicalizer canonicalizer = (Canonicalizer)_class.newInstance();            
             _canonicalizers.put(baseMimeType, canonicalizer);
         } catch (Exception e) {
             // log
             throw new XWSSecurityException(e);
         } 
    }

    /*
     * Ensure that the charset is a registered charset - see RFC 2633.
     * This assumes the complete content-type string will be input as the parameter.
     * (including charset param value).
     *
     * Section 4.1.2.  Charset Parameter [RFC 2046]
     * Example of charset parameter as per the RFC definition is -
     * Content-type: text/plain; charset=iso-8859-1
     */
    public static boolean ensureRegisteredCharset(ContentType contentType) {
        String charsetName = contentType.getParameter("charset");
        if (charsetName != null) {            
            return Charset.forName(charsetName).isRegistered();
        }
        return true;
    }
}
