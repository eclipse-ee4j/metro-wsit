/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.misc;

import com.sun.xml.wss.XWSSProcessor;
import com.sun.xml.wss.XWSSProcessorFactory;
import java.io.InputStream;

import com.sun.xml.wss.XWSSecurityException;
import javax.security.auth.callback.CallbackHandler;


public  class XWSSProcessorFactory2_0Impl extends XWSSProcessorFactory {

    @Override
    public XWSSProcessor createProcessorForSecurityConfiguration(
        InputStream securityConfiguration,
        CallbackHandler handler) throws XWSSecurityException {
        return new XWSSProcessor2_0Impl(securityConfiguration, handler);
    }


    /*
    public XWSSProcessor createForApplicationSecurityConfiguration(
        InputStream securityConfiguration) throws XWSSecurityException {
        return new XWSSProcessor2_0Impl(securityConfiguration);
    }
    */
}
