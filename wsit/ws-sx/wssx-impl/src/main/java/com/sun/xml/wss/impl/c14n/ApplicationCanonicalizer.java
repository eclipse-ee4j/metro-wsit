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
 * $Id: ApplicationCanonicalizer.java,v 1.2 2010-10-21 15:37:18 snajper Exp $
 * $Revision: 1.2 $
 * $Date: 2010-10-21 15:37:18 $
 */

package com.sun.xml.wss.impl.c14n;

import com.sun.xml.wss.XWSSecurityException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.logging.Level;

/**
 * Canoncializer for an Application Media Type.
 * This should work for all application/** MIME types.
 *
 * @author  XWS-Security Team
 */
public class ApplicationCanonicalizer extends Canonicalizer {

    public ApplicationCanonicalizer() {}
    
    public ApplicationCanonicalizer(String charset) {
        super(charset);
    }
    
    @Override
    public byte[] canonicalize(byte[] input) {
        return input;
    }
    
    @Override
    public InputStream canonicalize(InputStream input, OutputStream outputStream)
    throws javax.xml.crypto.dsig.TransformException {
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
            return null;
        }catch(Exception ex){
            log.log(Level.SEVERE, "WSS1000.error.canonicalizing", 
                    new Object[] {ex.getMessage()});
            throw new javax.xml.crypto.dsig.TransformException(ex.getMessage());
        }
    }
}
