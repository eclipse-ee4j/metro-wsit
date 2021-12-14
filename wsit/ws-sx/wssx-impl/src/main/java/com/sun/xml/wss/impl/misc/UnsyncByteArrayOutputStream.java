/*
 * Copyright (c) 1995, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright 1995-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.xml.wss.impl.misc;

import java.io.ByteArrayOutputStream;

/**
 * A simple Unsynced ByteArryOutputStream
 * @author raul
 *
 */
public class UnsyncByteArrayOutputStream extends ByteArrayOutputStream {
    int size=4*1024;
    //redefines member buf : But this is a class obtained from Apache, see license above
    //Findbugs shows this as a correctness issue.
    byte []buf=null;
    int pos =0;
    
    public UnsyncByteArrayOutputStream() {
        buf =new byte[size];
    }
    
    public UnsyncByteArrayOutputStream(int size) {
        this.size = size;
        buf =new byte[size];
    }
    
    @Override
    public void write(byte[] arg0) {
        int newPos=pos+arg0.length;
        if (newPos>size) {
            expandSize();
        }
        System.arraycopy(arg0,0,buf,pos,arg0.length);
        pos=newPos;
    }
    @Override
    public void write(byte[] arg0, int arg1, int arg2) {
        int newPos=pos+arg2;
        if (newPos>size) {
            expandSize();
        }
        System.arraycopy(arg0,arg1,buf,pos,arg2);
        pos=newPos;
    }
    @Override
    public void write(int arg0) {
        if (pos>=size) {
            expandSize();
        }
        buf[pos++]=(byte)arg0;
    }
    @Override
    public byte[] toByteArray() {
        byte[] result =new byte[pos];
        System.arraycopy(buf,0,result,0,pos);
        return result;
    }
    
    @Override
    public void reset() {
        pos=0;
    }
    
    void expandSize() {
        int newSize=size<<2;
        byte[] newBuf =new byte[newSize];
        System.arraycopy(buf,0,newBuf,0,pos);
        buf=newBuf;
        size=newSize;
    }
    
    public int getLength(){
        return pos;
    }
    
    public byte[] getBytes(){
        return buf;
    }
}
