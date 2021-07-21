/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright 1995-2005 The Apache Software Foundation
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

/*
 * $Id: SignerOutputStream.java,v 1.2 2010-10-21 15:36:08 snajper Exp $
 */
package com.sun.xml.ws.security.opt.crypto.dsig.internal;

import java.io.ByteArrayOutputStream;
import java.security.Signature;
import java.security.SignatureException;

/**
 * Derived from Apache sources and changed to use java.security.Signature 
 * objects as input instead of org.apache.xml.security.algorithms.SignatureAlgorithm
 * objects.
 *
 * @author raul
 * @author Sean Mullan
 */
public class SignerOutputStream extends ByteArrayOutputStream {
    private final Signature sig;

    public SignerOutputStream(Signature sig) {
        this.sig=sig;       
    }

    public void write(byte[] arg0)  {
	super.write(arg0, 0, arg0.length);
        try {
	    sig.update(arg0);
	} catch (SignatureException e) {
            throw new RuntimeException(""+e);
	}
    }
    
    public void write(int arg0) {
	super.write(arg0);
        try {
            sig.update((byte)arg0);
        } catch (SignatureException e) {
            throw new RuntimeException(""+e);
        }
    }
    
    public void write(byte[] arg0, int arg1, int arg2) {
	super.write(arg0, arg1, arg2);
        try {
            sig.update(arg0,arg1,arg2);
        } catch (SignatureException e) {
            throw new RuntimeException(""+e);
        }
    }
}
