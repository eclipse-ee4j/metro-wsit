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
 $Id: Retailer.java,v 1.10 2010-10-21 15:33:33 snajper Exp $
*/

package pricequote.retailer.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

import pricequote.wholesaler.client.WholesalerClient;

/**
 * @author Arun Gupta
 */
@WebService(endpointInterface = "pricequote.retailer.server.RetailerPortType", wsdlLocation = "WEB-INF/wsdl/retailer.wsdl")
public class Retailer implements RetailerPortType {
    @Resource
    WebServiceContext wsc;

    private static final Logger logger = Logger.getAnonymousLogger();
    private static final Level level = Level.INFO;
    private static final String WQS_NAMESPACE_URI = "http://example.org/wholesaler";

    public Quote getPrice(int pid) {
        logger.log(level, "Retailer.getListPrice invoked");

        ServletContext sc = (ServletContext)wsc.getMessageContext().get(MessageContext.SERVLET_CONTEXT);

        logger.log(level, "Configuring WSIT client ...");
        WholesalerClient sunClient = new WholesalerClient(getSunEndpointAddress(sc), new QName(WQS_NAMESPACE_URI, getSunServiceName(sc)));
        logger.log(level, "Invoking WSIT's Wholesaler ...");
        pricequote.wholesaler.client.Quote sunQuote = sunClient.getQuote(pid);
        float sunPrice = sunQuote.getPrice();
        logger.log(level, "Sun's wholesaler response received.");
        sunClient.close();

        logger.log(level, "Configuring WSIT#2 client ...");
        WholesalerClient msClient = new WholesalerClient(getMSEndpointAddress(sc), new QName(WQS_NAMESPACE_URI, getMSServiceName(sc)));
        logger.log(level, "Invoking WSIT#2's Wholesaler ...");
        pricequote.wholesaler.client.Quote msQuote = msClient.getQuote(pid);
        float msPrice = msQuote.getPrice();
        logger.log(level, "WSIT#2's wholesaler response received.");
        msClient.close();

        pricequote.wholesaler.client.Quote quote = sunPrice <= msPrice ? sunQuote : msQuote;
        logger.log(level, "Got a better price from \"{0}\" Wholesaler ...",
                   (sunPrice <= msPrice ? "WSIT" : "WSIT#2"));

        // TODO: Calculate the price to be returned back
        // TODO: based upon user's identity and gross margin
        // TODO: For now, the best price from wholesaler is
        // TODO: returned to the consumer

        Quote response = new Quote();
        response.setPrice(quote.getPrice());
        response.setPhoto(quote.getPhoto());

        logger.log(level, "Returning the response.");

        return response;
    }

    private String getSunEndpointAddress(ServletContext sc) {
        String endpoint = sc.getInitParameter("wqs.wsit.endpoint");
        if (endpoint == null || endpoint.equals(""))
            endpoint = "http://localhost:8080/pricequote/wholesaler?wsdl";

        return endpoint;
    }

    private String getSunServiceName(ServletContext sc) {
        String serviceName = sc.getInitParameter("wqs.wsit.serviceName");
        if (serviceName == null || serviceName.equals(""))
            serviceName = "WholesalerQuoteService";

        return serviceName;
    }

    private String getMSEndpointAddress(ServletContext sc) {
        String endpoint = sc.getInitParameter("wqs.wcf.endpoint");
        if (endpoint == null || endpoint.equals("")) {
            //endpoint = "http://131.107.72.15/Wholesaler/WholesalerService.svc?wsdl";
            endpoint = "http://localhost:8080/pricequote-wcf/wholesaler?wsdl";
        }

        return endpoint;
    }

    private String getMSServiceName(ServletContext sc) {
        String serviceName = sc.getInitParameter("wqs.wcf.serviceName");
        if (serviceName == null || serviceName.equals("")) {
            //serviceName = "WholesalerService";
            serviceName = "WholesaleService";
        }

        return serviceName;
    }
}
