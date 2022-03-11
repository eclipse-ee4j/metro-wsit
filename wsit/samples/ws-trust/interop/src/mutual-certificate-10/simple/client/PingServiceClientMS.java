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

import simple.client.SymmetricFederatedService;
import simple.client.IPingService;
import org.xmlsoap.ping.Ping;

public class PingServiceClientMS {

    public static void main (String[] args) {

            SymmetricFederatedService service = new SymmetricFederatedService();
            IPingService stub = service.getScenario2IssuedTokenMutualCertificate10();

            // use static stubs to override endpoint property of WSDL
            String serviceURL = System.getProperty("service.url");

            System.out.println("Service URL=" + serviceURL);

            ((BindingProvider)stub).getRequestContext().
                put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL);

            stub.ping(new Holder("1"), new Holder("sun"), new Holder("Passed!"));

    }

}
