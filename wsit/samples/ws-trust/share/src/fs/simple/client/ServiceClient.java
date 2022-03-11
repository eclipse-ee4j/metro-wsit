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
import jakarta.xml.ws.Holder;
import org.xmlsoap.ping.Ping;

import com.sun.xml.ws.security.trust.WSTrustConstants;
import java.net.URL;

public class ServiceClient {
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

            //PreConfigured STS info
            String stsHost = System.getProperty("sts.host");
            String stsPort = System.getProperty("sts.port");
            String stsURLFragment = System.getProperty("sts.url");
            String stsURL =
                "http://" + stsHost + ":" + stsPort + stsURLFragment;
            System.out.println("STS URL=" + stsURL);

            ((BindingProvider)stub).getRequestContext().
                put(jakarta.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL);

            Department dept = new Department();
            dept.setCompanyName("A");
            dept.setDepartmentName("B");

            String balance = stub.getAccountBalance(dept);

            System.out.println("balance=" + balance);

            PingService service1 = new PingService();
            IPingService stub1 = service1.getCustomBindingIPingService();

            // use static stubs to override endpoint property of WSDL
            String service1Host = System.getProperty("endpoint1.host");
            String service1Port = System.getProperty("endpoint1.port");
            String service1URLFragment = System.getProperty("service1.url");
            String service1URL =
                "http://" + service1Host + ":" + service1Port + service1URLFragment;

            System.out.println("Service URL=" + service1URL);

            ((BindingProvider)stub1).getRequestContext().
                put(jakarta.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, service1URL);

             stub1.ping(new Holder("1"), new Holder("sun"), new Holder("Passed!"));

        } catch (Exception ex) {
            System.out.println ("Caught Exception: " + ex.getMessage() );
            ex.printStackTrace();
        }
    }

}
