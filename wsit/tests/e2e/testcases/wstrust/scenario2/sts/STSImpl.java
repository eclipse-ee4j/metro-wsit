/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * STSImpl.java
 *
 *
 */


package wstrust.scenario2.sts;

import com.sun.xml.ws.security.trust.sts.BaseSTSImpl;
import jakarta.annotation.Resource;

import jakarta.xml.ws.Provider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.Service.Mode;
import jakarta.xml.ws.ServiceMode;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.WebServiceProvider;

import javax.xml.transform.Source;
import jakarta.xml.ws.handler.MessageContext;

@ServiceMode(value=Service.Mode.PAYLOAD)
@WebServiceProvider(wsdlLocation="WEB-INF/wsdl/sts.wsdl")
@jakarta.xml.ws.BindingType(jakarta.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class STSImpl extends BaseSTSImpl implements Provider<Source>{
    @Resource
    protected WebServiceContext context;
    
    public Source invoke(Source rstElement){
        return super.invoke(rstElement);
    }
    
    protected MessageContext getMessageContext() {        
        MessageContext msgCtx = context.getMessageContext(); 
        return msgCtx;
    }
    
}
