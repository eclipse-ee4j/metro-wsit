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
 $Id: RetailerClient.java,v 1.11 2010-10-21 14:43:39 snajper Exp $
*/

package pricequote.retailer.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;

/**
 * @author Arun Gupta
 */
public class RetailerClient {
    private static final String ENDPOINT = "http://localhost:8080/pricequote/retailer";
    private static final String WSDL_LOCATION = ENDPOINT + "?wsdl";
    private static final QName SERVICE = new QName("http://example.org/retailer", "RetailerQuoteService");
    private static int pid = 10;

    public static void main(String[] args) {
        String endpoint = System.getProperty("endpoint");
        String pid = System.getProperty("pid");
        Quote quote = getQuote(endpoint, pid);
        displayPhotoAndPrice(quote);
    }

    /**
     * Invokes the web service.
     */
    public static Quote getQuote(String endpoint, String spid) {
        RetailerQuoteService service;
        try {
            service = new RetailerQuoteService(new URL(WSDL_LOCATION), SERVICE);
        } catch (MalformedURLException e) {
            throw new WebServiceException(e);
        }
        if (endpoint == null || endpoint.equals("")) {
            endpoint = ENDPOINT;
        }

        if (spid != null && spid != "")
            pid = Integer.valueOf(spid);

        RetailerPortType port = service.getRetailerPort();
        System.out.printf("Invoking endpoint address \"%s\" for product id \"%s\".\n", endpoint, spid);
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

        Quote quote = port.getPrice(pid);
        return quote;
    }

    private static final void displayPhotoAndPrice(Quote quote) {
        if (quote.getPhoto() != null) {
            try {
                String carName = carname(pid);
                File file = new File(carName + ".jpeg");
                ImageIO.write((BufferedImage)quote.getPhoto(), "jpeg", file);
                String imageLocation = file.getAbsolutePath();
                System.out.printf("Photo is copied to \"%s\" file.\n", imageLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No photo received.");
        }
        System.out.println("Quoted price: " + quote.getPrice());
        System.out.println("Success!");
    }

    private static final String carname(int pid) {
        switch (pid % 4) {
            case 1:
                return "AM-Vantage-2k6";
            case 2:
                return "BMW-M3-2k6";
            case 3:
                return "MB-SLR-2k6";
            case 0:
            default:
                return "Porsche-911-2k6";
        }
    }
}
