/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Ashutosh.Shahi@Sun.com
 */
public class CheckedInputStream extends FilterInputStream{
    
    int read;
    boolean isEmpty = false;
    boolean xmlDecl = false;
    byte[] tmpBytes = new byte[4];
    ByteArrayInputStream tmpIs = null;
    
    /** Creates a new instance of CheckedCipherInputStream */
    public CheckedInputStream(InputStream cin) throws IOException {
        super(cin);
        read = cin.read();
        if(read == -1){
            isEmpty = true;
        }else{
            cin.read(tmpBytes, 0, 4);
            tmpIs = new ByteArrayInputStream(tmpBytes);
        }
    }
    
    @Override
    public int read() throws IOException{
        if(read != -1){
            int tmp = read;
            read = -1;
            
            if(tmp == '<' && "?xml".equals(new String(tmpBytes))){
                xmlDecl = true;
                int c = super.read();
                while(c != '>'){
                    //do nothing
                    c = super.read();
                }
            }
            
            if(!xmlDecl){
                return tmp;
            }
        }
        
        if(!xmlDecl){
            int c = tmpIs.read();
            if(c != -1){
                return c;
            }
        }
        
        return super.read();
    }
    
    @Override
    public int read(byte [] b) throws IOException{
        return read(b,0,b.length);
    }
    
    @Override
    public int read(byte[] b , int off, int len) throws IOException{
        if(read != -1){
            
            if(read == '<' && "?xml".equals(new String(tmpBytes))){
                xmlDecl = true;
                int c = super.read();
                while(c != '>'){
                    //do nothing
                    c = super.read();
                }
            }
            
            int i = 0;
            b[off + i] = (byte) read;
            i++;
            len--;
            read = -1;
            
            if(!xmlDecl){          
                
                int c = tmpIs.read();
                while(c != -1 && len > 0){
                    b[off + i] = (byte)c;
                    i++;
                    c = tmpIs.read();
                    len--;
                }              
                
            }
            int rb = 0;
            if(len > 0){
                rb = super.read(b,off+i,len);
            }
            
            return rb+i;
        }
        return super.read(b,off,len);
    }
    @Override
    public long skip(long n) throws IOException {
        if(read != -1){
            read = -1;
            return super.skip(n-1) + 1;
        }
        return super.skip(n);
    }
    
    public boolean isEmpty(){
        return isEmpty;
    }
}
