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
import com.sun.xml.wss.impl.misc.Base64;
import java.io.IOException;
import java.io.OutputStream;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class Base64OutputStream extends java.io.OutputStream{
    
    private OutputStream os;
    
    /** Creates a new instance of Base64OutputStream */
    public Base64OutputStream(OutputStream os) {
        this.os = os;
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
        Base64.encodeToStream(b,off,len,os);
    }
    
    public void write(byte[] b) throws IOException{
        //Base64.encodeToStream(b,0,b.length,os);
        String data = Base64.encode(b);
        os.write(data.getBytes());
    }
    
    public void write(int b) throws IOException {
        os.write(b);
    }
    
}
