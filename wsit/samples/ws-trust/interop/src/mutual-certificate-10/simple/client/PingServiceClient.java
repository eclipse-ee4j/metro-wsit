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
import jakarta.xml.ws.Holder;

import simple.client.PingService;
import simple.client.IPingService;
import simple.schema.client.Ping;

public class PingServiceClient {

    public static void main (String[] args) {

            PingService service = new PingService();
            IPingService stub = service.getCustomBindingIPingService();

            // use static stubs to override endpoint property of WSDL
            String serviceURL = System.getProperty("service.url");

            System.out.println("Service URL=" + serviceURL);

            ((BindingProvider)stub).getRequestContext().
                put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL);

            stub.ping(new Holder("1"), new Holder("sun"), new Holder("Passed!"));

    }

}
