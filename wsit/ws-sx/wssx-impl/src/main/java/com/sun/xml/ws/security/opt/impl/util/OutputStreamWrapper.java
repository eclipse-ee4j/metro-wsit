/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class OutputStreamWrapper extends OutputStream {

    private OutputStream os = null;

    public OutputStreamWrapper(OutputStream stream) {
        this.os = stream;
    }

    @Override
    public void write(int value) throws IOException {
        this.os.write(value);
    }

    @Override
    public void close() {
        //no-op
    }

    @Override
    public void flush() throws IOException {
        this.os.flush();
    }

    @Override
    public void write(byte[] value) throws IOException {
        this.os.write(value);
    }

    @Override
    public void write(byte[] value, int off, int len) throws IOException {
        this.os.write(value, off, len);
    }
}
