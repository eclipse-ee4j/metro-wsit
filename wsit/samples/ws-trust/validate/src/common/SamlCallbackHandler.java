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

import com.sun.xml.ws.api.security.trust.client.IssuedTokenManager;
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.GenericToken;
import com.sun.xml.ws.security.trust.impl.client.DefaultSTSIssuedTokenConfiguration;
import com.sun.xml.wss.XWSSConstants;
import java.io.*;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.sun.xml.wss.impl.callback.*;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author  TOSHIBA USER
 */
public  class SamlCallbackHandler implements CallbackHandler {

    public SamlCallbackHandler() {

    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i=0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof SAMLCallback) {
        SAMLCallback samlCallback = (SAMLCallback)callbacks[i];
                Map runtimeProp = samlCallback.getRuntimeProperties();
                Element samlAssertion = getSAMLAssertionFromSTS();
        samlCallback.setAssertionElement(samlAssertion);
            }
    }
    }

    private Element getSAMLAssertionFromSTS() {
        String stsEndpoint = "http://localhost:8080/jaxws-fs-sts/sts";
        String stsMexAddress = "http://localhost:8080/jaxws-fs-sts/sts/mex";
        DefaultSTSIssuedTokenConfiguration config = new DefaultSTSIssuedTokenConfiguration(
                    stsEndpoint, stsMexAddress);

        config.setKeyType("http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey");

        try{
            IssuedTokenManager manager = IssuedTokenManager.getInstance();

            String appliesTo = "http://localhost:8080/jaxws-fs/simple";
            IssuedTokenContext ctx = manager.createIssuedTokenContext(config, appliesTo);
            manager.getIssuedToken(ctx);
            Token issuedToken = ctx.getSecurityToken();

            return (Element)issuedToken.getTokenValue();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
