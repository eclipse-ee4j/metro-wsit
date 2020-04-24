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
 $Id: Wholesaler.java,v 1.7 2010-10-21 14:28:47 snajper Exp $
*/

package pricequote.wholesaler.server;

import jakarta.annotation.Resource;
import javax.imageio.ImageIO;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.handler.MessageContext;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Arun Gupta
 */
@WebService(endpointInterface = "pricequote.wholesaler.server.WholesalerPortType", wsdlLocation = "WEB-INF/wsdl/wholesaler.wsdl")
public class Wholesaler implements WholesalerPortType {

    @Resource
    WebServiceContext context;

    public Quote getQuote(int i) {
        Quote response = new Quote();
        response.setPrice(PRICES[i % 4]);

        ServletContext servletContext = (ServletContext)context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        if (servletContext != null) {

            String carName = "/images/" + carname(i) + ".jpg";

            System.out.println("Car name is: " + carName);
            InputStream is = servletContext.getResourceAsStream(carName);
            try {
                BufferedImage bi = ImageIO.read(is);
                response.setPhoto(bi);
                System.out.println(getClass().getName() + ": Added the photo");
            } catch (IOException e) {
                throw new WebServiceException(e);
            }
        }

        return response;
    }

    private String carname(int pid) {
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

    private static final float[] PRICES = {
        (float)71834.95,
        (float)83450.00,
        (float)75640.00,
        (float)90990.99
    };
}
