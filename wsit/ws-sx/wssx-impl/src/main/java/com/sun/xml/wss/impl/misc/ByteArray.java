/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.misc;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class ByteArray {
    byte [] iv = null;
    byte [] ed = null;
    int length = 0;
    /** Creates a new instance of ByteArray */
    public ByteArray(byte [] iv, byte [] ed) {
        this.iv = iv;
        this.ed = ed;
        if(this.iv != null){
            length = iv.length;
        }
        if(this.ed != null){
            length = length+ed.length;
        }
    }
    
    public int getLength(){
        return length;
    }
    
    public byte byteAt(int i){
        if(i < 0 || i > length){
            throw new ArrayIndexOutOfBoundsException("Index "+i +" is out of range");
        }
        if(iv != null && i < iv.length){
            return iv[i];
        }else if (iv == null || iv.length == 0){
            return ed[i];
		}else{
            int index = i-iv.length;
            return ed[index];
        }
    }
}
