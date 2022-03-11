/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package simple.client;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import java.io.FileInputStream;
import jakarta.xml.ws.Holder;

import simple.client.PingService;
import simple.client.IPingService;


import org.xmlsoap.ping.Ping;

public class PingServiceClient {
    public static void main (String[] args) {
      try {
            PingService service = new PingService();
            IPingService stub = service.getPingPort();
            //IPingService stub = service.getIPingService();

            // use static stubs to override endpoint property of WSDL
            String serviceHost = System.getProperty("endpoint.host");
            String servicePort = System.getProperty("endpoint.port");
            String serviceURLFragment = System.getProperty("service.url");
            String serviceURL =
               "https://" + serviceHost + ":" + servicePort + serviceURLFragment;

            System.out.println("Service URL=" + serviceURL);

            ((BindingProvider)stub).getRequestContext().
                put(jakarta.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL);

             ((BindingProvider)stub).getRequestContext().
                put(com.sun.xml.wss.XWSSConstants.USERNAME_PROPERTY, "alice");
            ((BindingProvider)stub).getRequestContext().
                put(com.sun.xml.wss.XWSSConstants.PASSWORD_PROPERTY, "alice");

            stub.ping(new Holder("1"), new Holder("sun"), new Holder("Passed!"));

            // Ping again
            stub.ping(new Holder("1"), new Holder("sun"), new Holder("Passed again!"));


             // Ping the third time
            stub.ping(new Holder("1"), new Holder("sun"), new Holder("Passed again again!"));


        } catch (Exception ex) {
            System.out.println ("Caught Exception: " + ex.getMessage() );
            ex.printStackTrace();
        }
    }
}
