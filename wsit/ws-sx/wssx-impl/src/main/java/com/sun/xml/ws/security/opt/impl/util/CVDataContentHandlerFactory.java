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
import jakarta.activation.DataContentHandlerFactory;
import jakarta.activation.DataContentHandler;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class CVDataContentHandlerFactory implements DataContentHandlerFactory {

    /** Creates a new instance of CVDataContentHandlerFactory */
    public CVDataContentHandlerFactory() {
    }


    @Override
    public DataContentHandler createDataContentHandler(String mimeType){
        if("application/ciphervalue".equals(mimeType)){
             return new CVDataHandler();
        }
        return null;
    }
}
