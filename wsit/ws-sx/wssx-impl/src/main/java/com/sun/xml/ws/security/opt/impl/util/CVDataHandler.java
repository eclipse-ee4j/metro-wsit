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
import com.sun.xml.ws.security.opt.impl.enc.CryptoProcessor;
import java.io.IOException;
import java.io.OutputStream;

import jakarta.activation.ActivationDataFlavor;
import jakarta.activation.DataContentHandler;
import jakarta.activation.DataSource;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class CVDataHandler implements DataContentHandler {

    /** Creates a new instance of CVDataHandler */
    public CVDataHandler() {
    }

    @Override
    public Object getContent(DataSource ds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getTransferData(ActivationDataFlavor df, DataSource ds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ActivationDataFlavor[] getTransferDataFlavors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(Object obj, String mimeType, OutputStream os)throws IOException{
        if(obj instanceof CryptoProcessor){
            CryptoProcessor cp = (CryptoProcessor) obj;
            cp.encrypt(os);
        }else{
            throw new UnsupportedOperationException();
        }
    }
}
