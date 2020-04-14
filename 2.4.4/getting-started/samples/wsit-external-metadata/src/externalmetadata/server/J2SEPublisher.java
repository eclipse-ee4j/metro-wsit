/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package externalmetadata.server;

//import java.net.InetSocketAddress;
//import java.util.concurrent.Executors;
//import com.sun.net.httpserver.HttpContext;
//import com.sun.net.httpserver.HttpServer;

import javax.xml.ws.Endpoint;

public class J2SEPublisher {
    
    public static void main (String[] args) throws Exception {
        Endpoint.publish (
            "http://localhost:8080/jaxws-fromjava/addnumbers",
            new BlackboxImpl());
    }
    
//    public static void deployMethod2 () throws Exception {
//        Endpoint endpoint = Endpoint.create(
//            new URI (SOAPBinding.SOAP11HTTP_BINDING),
//            new BlackboxImpl ());
//        
//        HttpServer server = HttpServer.create (new InetSocketAddress (8080), 5);
//        server.setExecutor (Executors.newFixedThreadPool (5));
//        HttpContext context = server.createContext (
//            "http",
//            "/jaxws-fromjava/addnumbers");
//        
//        endpoint.publish (context);
//        server.start ();
//    }
    
}
