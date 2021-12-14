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
 * TeeOutputStream.java
 *
 * Created on September 15, 2006, 3:09 PM
 */

package com.sun.xml.ws.security.opt.impl.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class TeeOutputStream extends OutputStream{
    
    OutputStream tee = null;
    OutputStream out = null;
    
    /** Creates a new instance of TeeOutputStream */
    public TeeOutputStream(OutputStream chainedStream, OutputStream teeStream) {
        out = chainedStream;
        tee = teeStream;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        tee.write(b);
    }
    
    @Override
    public void close() throws IOException{
        flush();
        out.close();
        tee.close();
    }
    
    @Override
    public void flush() throws IOException {
        out.flush();
        tee.flush();
    }
}
