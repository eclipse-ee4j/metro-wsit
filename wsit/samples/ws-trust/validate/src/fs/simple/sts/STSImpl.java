/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package simple.sts;

import com.sun.xml.wss.XWSSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.ws.Provider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.Service.Mode;
import jakarta.xml.ws.ServiceMode;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.WebServiceProvider;

import com.sun.xml.ws.security.trust.sts.BaseSTSImpl;

import com.sun.xml.wss.SubjectAccessor;
import jakarta.annotation.Resource;

import javax.security.auth.Subject;
import javax.xml.transform.Source;
import jakarta.xml.ws.handler.MessageContext;

@ServiceMode(value=Service.Mode.PAYLOAD)
@WebServiceProvider(wsdlLocation="WEB-INF/wsdl/sts.wsdl")
public class STSImpl extends BaseSTSImpl implements Provider<Source>{
    @Resource
    protected WebServiceContext context;

    protected MessageContext getMessageContext() {
        MessageContext msgCtx = context.getMessageContext();
        return msgCtx;
    }
}
