/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wstrust.scenario5.server;


@jakarta.jws.WebService (endpointInterface="wstrust.scenario5.server.IPingService")
@jakarta.xml.ws.BindingType(jakarta.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class PingImpl implements IPingService {
    
    public PingResponseBody ping(Ping ping){
        
        PingResponseBody resp = new PingResponseBody();
        
        String scenario = ping.getScenario();
        System.out.println("scenario = " + scenario);
        resp.setScenario(scenario);
        
        String origin = ping.getOrigin();
        System.out.println("origin = " + origin);
        resp.setOrigin(origin);
        
        String text = ping.getText();
        System.out.println("text = " + text);
        resp.setText(text);
        
        return resp;
    }  
}
