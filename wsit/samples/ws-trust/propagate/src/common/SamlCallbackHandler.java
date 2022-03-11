/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package common;

import java.io.*;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.sun.xml.wss.impl.callback.*;
import java.util.Map;
import org.w3c.dom.Element;

/**
 *
 * @author  Jiandong Guo
 */
public  class SamlCallbackHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i=0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof SAMLCallback) {
        SAMLCallback samlCallback = (SAMLCallback)callbacks[i];
                Map runtimeProp = samlCallback.getRuntimeProperties();
                Element samlAssertion = (Element)runtimeProp.get("userSAMLAssertion");
        samlCallback.setAssertionElement(samlAssertion);
            }
    }
    }
}
