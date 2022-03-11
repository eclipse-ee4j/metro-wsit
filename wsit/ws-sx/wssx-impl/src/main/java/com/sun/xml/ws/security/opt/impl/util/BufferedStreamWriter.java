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
 * BufferedStreamWriter.java
 *
 * Created on August 7, 2006, 11:39 AM
 */

package com.sun.xml.ws.security.opt.impl.util;

import java.io.IOException;
import javax.crypto.CipherOutputStream;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class BufferedStreamWriter extends java.io.OutputStream {
    int size=4*1024;
    byte []buf=null;
    int pos =0;
    CipherOutputStream cos = null;
    /** Creates a new instance of BufferedStreamWriter */
    public BufferedStreamWriter(CipherOutputStream cos) {
        buf =new byte[this.size];
        this.cos = cos;
    }
    public BufferedStreamWriter(CipherOutputStream cos, int size) {
        buf =new byte[size];
        this.cos = cos;
    }
    @Override
    public void write(byte[] arg0)throws IOException {
        int newPos=pos+arg0.length;
        if (newPos>=size) {
            flush();
            System.arraycopy(arg0,0,buf,0,arg0.length);
            pos = arg0.length;
        }else{
            System.arraycopy(arg0,0,buf,pos,arg0.length);
            pos=newPos;
        }
    }
    @Override
    public void write(byte[] arg0, int arg1, int arg2)throws IOException {
        int newPos=pos+arg2;
        if (newPos>=size) {
            flush();
            System.arraycopy(arg0,arg1,buf,0,arg2);
            pos = arg2;
        }else{
            System.arraycopy(arg0,arg1,buf,pos,arg2);
            pos=newPos;
        }
    }
    @Override
    public void write(int arg0)throws IOException {
        if (pos>=size) {
            flush();
        }
        buf[pos++]=(byte)arg0;
    }

    @Override
    public void flush() throws IOException {
        cos.write(buf,0,pos);
        pos = 0;
    }
}
