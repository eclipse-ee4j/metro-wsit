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
import simple.schema.client.Department;

import com.sun.xml.ws.security.trust.WSTrustConstants;
import java.net.URL;

public class FinancialServiceClient {
    public static void main (String[] args) {
        try {
            FinancialService service = new FinancialService();
            IFinancialService stub = service.getIFinancialServicePort();


            // use static stubs to override endpoint property of WSDL
            String serviceHost = System.getProperty("endpoint.host");
            String servicePort = System.getProperty("endpoint.port");
            String serviceURLFragment = System.getProperty("service.url");
            String serviceURL =
                "http://" + serviceHost + ":" + servicePort + serviceURLFragment;

            System.out.println("Service URL=" + serviceURL);

            ((BindingProvider)stub).getRequestContext().
                put(jakarta.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL);

            Department dept = new Department();
            dept.setCompanyName("A");
            dept.setDepartmentName("B");

            String balance = stub.getAccountBalance(dept);

            System.out.println("balance=" + balance);

        } catch (Exception ex) {
            System.out.println ("Caught Exception: " + ex.getMessage() );
            ex.printStackTrace();
        }
    }

}
