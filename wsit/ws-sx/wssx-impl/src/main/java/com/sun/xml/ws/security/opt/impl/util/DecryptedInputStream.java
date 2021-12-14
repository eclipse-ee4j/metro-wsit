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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class DecryptedInputStream extends FilterInputStream{
    
    private static final int SKIP_BUFFER_SIZE = 2048;
    // skipBuffer is initialized in skip(long), if needed.
    private static byte[] skipBuffer;
    
    private StringBuilder startElement = new StringBuilder("<StartElement");
    private static final String endElement = "</StartElement>";
    private InputStream startIS = null;
    private InputStream endIS = new ByteArrayInputStream(endElement.getBytes());
    
    /** Creates a new instance of DecryptedInputStream */
    public DecryptedInputStream(InputStream is, HashMap<String,String> parentNS) {
        super(is);
        Set<Map.Entry<String, String>> set = parentNS.entrySet(); 
        Iterator<Map.Entry<String, String>> iter = set.iterator();
        while(iter.hasNext()){
           Map.Entry<String, String> entry = iter.next();
           if(!"".equals(entry.getKey())){
               startElement.append(" xmlns:").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
           } else{
               startElement.append(" xmlns=\"").append(entry.getValue()).append("\"");
           }
        }
        startElement.append(" >");
        String startElem = startElement.toString();
        startIS = new ByteArrayInputStream(startElem.getBytes());
    }
    
    @Override
    public int read() throws IOException{
        int readVal = startIS.read();
        if(readVal != -1){
            return readVal;
        }
        readVal = in.read();
        if(readVal != -1){
            return readVal;
        }
        return endIS.read();
    }
    
    @Override
    public int read(byte [] b) throws IOException{
        return read(b,0,b.length-1);
    }
    
    @Override
    public int read(byte[] b , int off, int len) throws IOException{
        if (b == null) {
	    throw new NullPointerException();
	} else if ((off < 0) || (off > b.length) || (len < 0) ||
		   ((off + len) > b.length) || ((off + len) < 0)) {
	    throw new IndexOutOfBoundsException();
	} else if (len == 0) {
	    return 0;
	}
        int readVal = read();
        if(readVal == -1){
            return -1;
        }
        b[off] = (byte)readVal;
        int i = 1;
        for(; i < len; i++){
            readVal = read();
            if(readVal == -1){
                break;
            }
            if(b != null){
                b[off+i] = (byte)readVal;
            }
        }
        return i;
    }
    
    @Override
    public long skip(long n) throws IOException {
        long remaining = n;
	int nr;
	if (skipBuffer == null)
	    skipBuffer = new byte[SKIP_BUFFER_SIZE];

	byte[] localSkipBuffer = skipBuffer;
        
        if (n <= 0) {
	    return 0;
	}

	while (remaining > 0) {
	    nr = read(localSkipBuffer, 0,
		      (int) Math.min(SKIP_BUFFER_SIZE, remaining));
	    if (nr < 0) {
		break;
	    }
	    remaining -= nr;
	}
	
	return n - remaining;
    }
    
    @Override
    public boolean markSupported() {
	return false;
    }
    
    @Override
    public synchronized void reset() throws IOException {
	throw new IOException("mark/reset not supported");
    }
    
    @Override
    public void close() throws IOException{
        startIS.close();
        in.close();
        endIS.close();
    }
    
}
