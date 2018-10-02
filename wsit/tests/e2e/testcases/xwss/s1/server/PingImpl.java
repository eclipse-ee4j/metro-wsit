/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package xwss.s1.server;


@javax.jws.WebService (endpointInterface="xwss.s1.server.IPingService")
@javax.xml.ws.BindingType(value="http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
public class PingImpl implements IPingService {
    
   public String ping(String message) {
        System.out.println("The message is here : " + message);
        return message;
    }                    
}
