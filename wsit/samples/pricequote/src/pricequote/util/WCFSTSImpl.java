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
 $Id: WCFSTSImpl.java,v 1.7 2010-10-21 14:28:53 snajper Exp $
*/

package pricequote.util;

import javax.xml.ws.ServiceMode;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.transform.Source;
import javax.annotation.Resource;

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
