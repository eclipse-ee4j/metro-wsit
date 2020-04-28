/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wsrm.v1_0.persistent.session.server;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import java.util.Hashtable;


@WebService(endpointInterface="wsrm.v1_0.persistent.session.server.RMDemo")
@jakarta.xml.ws.BindingType(jakarta.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class RMDemoImpl {


    /* JAX-WS initializes context for each request */
    @Resource
    private WebServiceContext context;

    /* Get Sesssion using well-known key in MessageContext */
    private Hashtable getSession() {
        return (Hashtable)context.getMessageContext()
                .get("com.sun.xml.ws.session");
    }

    /* Get String associated with SessionID for current request */

    private String getSessionData() {
	Hashtable sess = getSession();
        String ret = (String)sess.get("request_record");
        return ret != null ? ret : "";

    }

    /* Store String associated with SessionID for current request */
    private void setSessionData(String data) {
        Hashtable session = getSession();
        session.put("request_record", data);
    }

    /* RMDemo Methods */

    @WebMethod
    public void addString(String s ) {
        /* append string to session data */
        setSessionData(getSessionData() + " " + s);
    }



    @WebMethod
    public String getResult() {
        /* return session data */
        return getSessionData();
    }

}

