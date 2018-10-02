/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: Canonicalizer.java,v 1.2 2010-10-21 15:37:19 snajper Exp $
 * $Revision: 1.2 $
 * $Date: 2010-10-21 15:37:19 $
 */

package com.sun.xml.wss.impl.c14n;

import com.sun.xml.wss.swa.MimeConstants;

import com.sun.xml.wss.XWSSecurityException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.logging.Logger;

import com.sun.xml.wss.logging.LogDomainConstants;

/**
 * Interface for defining MIME Content Canonicalizer.
 * (Section 4.3.2) "MIME Content Canonicalization"; SwA
 *
 * @author  XWS-Security Team
 */
public abstract class Canonicalizer {
    String _charset = MimeConstants.US_ASCII;

    protected static final Logger log =  Logger.getLogger( 
            LogDomainConstants.IMPL_CANON_DOMAIN,
            LogDomainConstants.IMPL_CANON_DOMAIN_BUNDLE);

    public Canonicalizer() {}
    
    Canonicalizer(String charset) {
        this._charset = charset;
    }
    
    /*
     * Main method that performs the actual Canonicalization.
     */
    public abstract byte[] canonicalize(byte[] input) throws XWSSecurityException;
    public abstract InputStream canonicalize(InputStream input,OutputStream outputStream) throws javax.xml.crypto.dsig.TransformException ;
    
    void setCharset(String charset) {
        this._charset = charset;
    }
    
    public String getCharset() {
        return _charset;
    }
}
