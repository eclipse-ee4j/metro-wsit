/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package client;

import com.sun.xml.ws.transport.http.client.HttpTransportPipe;

/**
 * Client that invokes .NET 3.0 MTOM endpoint using a local wsdl
 */
public class Client {

    public static void main(String[] args) {

        //enble SOAP Message logging
        HttpTransportPipe.dump = true;

        //Create IMtomTest proxy to invoke .NET 3.0 MTOM service
        IMtomTest proxy = new MtomService().getBasicHttpBindingIMtomTest();
        String input="Hello World";
        byte[] response = proxy.echoStringAsBinary(input);
        System.out.println("Sent: "+input+", Received: "+new String(response));        
    }
}
