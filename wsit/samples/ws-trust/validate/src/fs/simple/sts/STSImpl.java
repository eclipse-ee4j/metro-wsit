/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;

import com.sun.xml.ws.security.trust.sts.BaseSTSImpl;

import com.sun.xml.wss.SubjectAccessor;
import javax.annotation.Resource;

import javax.security.auth.Subject;
import javax.xml.transform.Source;
import javax.xml.ws.handler.MessageContext;

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
