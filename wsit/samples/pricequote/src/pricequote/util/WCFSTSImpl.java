/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 $Id: WCFSTSImpl.java,v 1.7 2010-10-21 14:28:53 snajper Exp $
*/

package pricequote.util;

import jakarta.xml.ws.ServiceMode;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceProvider;
import jakarta.xml.ws.Provider;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import javax.xml.transform.Source;
import jakarta.annotation.Resource;

import com.sun.xml.ws.security.trust.sts.BaseSTSImpl;

@ServiceMode(value= Service.Mode.PAYLOAD)
@WebServiceProvider(wsdlLocation="WEB-INF/wsdl/sts.wsdl")
public class WCFSTSImpl extends BaseSTSImpl implements Provider<Source> {
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
