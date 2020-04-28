/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wssc.sc7.server;
import jakarta.xml.ws.Holder;


@jakarta.jws.WebService (endpointInterface="wssc.sc7.server.IPingService")
@jakarta.xml.ws.BindingType(value="http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
public class PingImpl implements IPingService {
    
   public void ping( Holder<String> scenario,
         Holder<String> origin,
         Holder<String> text){
        System.out.println("The message is here : " + scenario.value + " " +origin.value + " " + text.value);

    }                    
}
