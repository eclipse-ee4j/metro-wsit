/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 $Id: WholesalerClient.java,v 1.11 2010-10-21 15:33:34 snajper Exp $
*/

package pricequote.wholesaler.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import jakarta.annotation.Resource;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.WebServiceException;
import javax.xml.namespace.QName;

/**
 * @author Arun Gupta
 */
public class WholesalerClient {
    @Resource
    WebServiceContext wsc;

    WholesalerPortType port = null;
    public static void main(String[] args) {
        String endpoint = "http://localhost:8080/pricequote/wholesaler?wsdl";
        String serviceName = "WholesalerQuoteService";

        String wqs = System.getProperty("wqs");
        if (wqs != null && wqs.equals("ms")) {
            System.out.println("Setting Wholesale Quote Service #2 endpoints");
            String ep = System.getProperty("wqs.endpoint");
            if (ep != null && !ep.equals(""))
                endpoint = ep;
            String sn = System.getProperty("wqs.serviceName");
            if (sn != null && !sn.equals(""))
                serviceName = sn;
        }

        System.out.println("Using endpoints ...");
        System.out.println("endpoint: " + endpoint);
        System.out.println("serviceName: " + serviceName);
        System.out.println(new WholesalerClient(endpoint, new QName("http://example.org/wholesaler", serviceName)).getQuote(10).getPrice());
    }

    public WholesalerClient(String endpoint, QName name) {
        try {
            WholesalerQuoteService service = new WholesalerQuoteService(new URL(endpoint), name);
            port = service.getWholesalerPort();
        } catch (MalformedURLException e) {
            throw new WebServiceException(e);
        }
    }

    public Quote getQuote(int pid) {
        return port.getQuote(pid);
    }

    public void close() {
        if (port != null) {
            try {
                ((Closeable) port).close();
            } catch (IOException ioe){
                System.err.println("Caught IOException: " + ioe.getMessage());
            }
            port = null;
        }
    }
}
