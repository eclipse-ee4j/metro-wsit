/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wsrm.v1_0.persistent.resendinterval.server;

import jakarta.jws.WebService;

@WebService(endpointInterface = "wsrm.v1_0.persistent.resendinterval.server.IPing")
@jakarta.xml.ws.BindingType(jakarta.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class IPingImpl {

    /**
     * @param String
     */
    public void ping(String s) {
        System.out.println("On server side received " + s);
    }
}
