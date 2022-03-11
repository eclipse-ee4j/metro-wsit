/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package common;

import com.sun.xml.ws.security.trust.STSIssuedTokenFeature;
import com.sun.xml.ws.security.trust.impl.client.DefaultSTSIssuedTokenConfiguration;
import java.io.*;
import java.net.*;
import jakarta.annotation.Resource;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.WebServiceRef;

/**
 *
 * @author jiandong guo
 */
public class ClientServlet extends HttpServlet {
    @WebServiceRef(wsdlLocation = "http://localhost:8080/CalculatorApp/CalculatorWSService?wsdl")
    public CalculatorWSService service;

    @Resource
    protected WebServiceContext context;

    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<h2>Servlet ClientServlet at " + request.getContextPath () + "</h2>");
                DefaultSTSIssuedTokenConfiguration config = new DefaultSTSIssuedTokenConfiguration();
                STSIssuedTokenFeature feature = new STSIssuedTokenFeature(config);
                org.me.calculator.client.CalculatorWS port = service.getCalculatorWSPort(new WebServiceFeature[]{feature});

                int i = Integer.parseInt(request.getParameter("value1"));
                int j = Integer.parseInt(request.getParameter("value2"));

                config.setTokenType("urn:oasis:names:tc:SAML:1.0:assertion");
                MyClaims claims = new MyClaims();
                claims.addClaimType(MyClaims.ROLE);
                config.setClaims(claims);
                int result = port.add(i, j);

                out.println("<br/>");
                out.println("Result:");
                out.println("" + i + " + " + j + " = " + result);
                out.println("<br/>");
                config.setTokenType("urn:oasis:names:tc:SAML:2.0:assertion");
                MyClaims claims1 = new MyClaims();
                claims1.addClaimType(MyClaims.LOCALITY);
                config.setClaims(claims1);
                result = port.add(i, j);

                out.println("<br/>");
                out.println("Result:");
                out.println("" + i + " + " + j + " = " + result);
                ((Closeable)port).close();

        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
    * Returns a short description of the servlet.
    */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
