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
 * $Id: TextPlainCanonicalizer.java,v 1.2 2010-10-21 15:37:19 snajper Exp $
 * $Revision: 1.2 $
 * $Date: 2010-10-21 15:37:19 $
 */

package com.sun.xml.wss.impl.c14n;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

import com.sun.xml.wss.util.CRLFOutputStream;

import com.sun.xml.wss.XWSSecurityException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.logging.Level;

/**
 *
 * Implementation of a text/plain canonicalizer as per rules
 * defined in RFC 2046 (http://www.rfc-editor.org/rfc/rfc2046.txt)
 * Section 4.1.
 *
 * @author  XWS-Security Team
 */
public class TextPlainCanonicalizer extends Canonicalizer {
    
    public TextPlainCanonicalizer() {}
    
    public TextPlainCanonicalizer(String charset) {
        super(charset);
    }
    
    public InputStream canonicalize(InputStream input,OutputStream outputStream)
    throws javax.xml.crypto.dsig.TransformException   {
        
        int len=0;
        byte [] data= null;
        try{
            data = new byte[128];
            len = input.read(data);
        } catch (IOException e) {                        
            log.log(Level.SEVERE, "WSS1002.error.canonicalizing.textplain", 
                    new Object[] {e.getMessage()});
            throw new javax.xml.crypto.dsig.TransformException(e);
        }
        CRLFOutputStream crlfOutStream = null;
        ByteArrayOutputStream bout = null;
        if(outputStream == null){
            bout = new ByteArrayOutputStream();
            crlfOutStream = new CRLFOutputStream(bout);
        }else{
            crlfOutStream = new CRLFOutputStream(outputStream);
        }
        
        while(len > 0){
            try {
                crlfOutStream.write(data,0,len);
                len = input.read(data);
            } catch (IOException e) {
                log.log(Level.SEVERE, "WSS1002.error.canonicalizing.textplain", 
                    new Object[] {e.getMessage()});
                throw new javax.xml.crypto.dsig.TransformException(e);
            }
        }
        
        if(outputStream == null){
            byte [] inputData = bout.toByteArray();
            return new ByteArrayInputStream(inputData);
        }
        return null;
    }
    
    /*
     * Important aspects of "text" media type canonicalization include line
     * ending normalization to <CR><LF>.
     * Section 4.1.1. [RFC 2046]
     */
    public byte[] canonicalize(byte[] inputBytes) throws XWSSecurityException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        CRLFOutputStream crlfOutStream = new CRLFOutputStream(bout);
        try {
            crlfOutStream.write(inputBytes);
        } catch (IOException e) {
            log.log(Level.SEVERE, "WSS1002.error.canonicalizing.textplain", 
                    new Object[] {e.getMessage()});
            throw new XWSSecurityException(e);
        }
        return bout.toByteArray();
    }
    
}
