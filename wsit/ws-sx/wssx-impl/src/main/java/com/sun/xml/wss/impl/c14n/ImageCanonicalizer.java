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
 * $Id: ImageCanonicalizer.java,v 1.2 2010-10-21 15:37:19 snajper Exp $
 * $Revision: 1.2 $
 * $Date: 2010-10-21 15:37:19 $
 */

package com.sun.xml.wss.impl.c14n;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * Section 4.2 on Image Media types in RFC 2046
 * http://www.rfc-editor.org/rfc/rfc2046.txt
 * does not specify any rules for image canonicalization.
 *
 * So assuming that this binary data need not be canonicalized.
 *
 * @author  XWS-Security Team
 */
public class ImageCanonicalizer extends Canonicalizer {
    
    public ImageCanonicalizer() {}
    
    public ImageCanonicalizer(String charset) {
        super(charset);
    }
    
    /*
     * RFC 3851 says - http://www.rfc-archive.org/getrfc.php?rfc=3851
     * Other than text types, most types
     * have only one representation regardless of computing platform or
     * environment which can be considered their canonical representation.
     *
     * So right now we are just serializing the attachment for gif data types.
     *
     */
    @Override
    public byte[] canonicalize(byte[] input) {
        return input;
    }
    
    @Override
    public InputStream canonicalize(InputStream input, OutputStream outputStream)
    throws javax.xml.crypto.dsig.TransformException  {
        try{
            if(outputStream == null){
                return input;
            }else{
                byte [] data = new byte[128];
                while(true){
                    int len = input.read(data);
                    if(len <= 0)
                        break;
                    outputStream.write(data,0,len);
                }
            }
        }catch(Exception ex){
            log.log(Level.SEVERE, "WSS1001.error.canonicalizing.image", 
                    new Object[] {ex.getMessage()});
            throw new javax.xml.crypto.dsig.TransformException(ex.getMessage());
        }
        return null;
    }
}
