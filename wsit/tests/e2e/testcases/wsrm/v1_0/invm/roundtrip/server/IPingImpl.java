/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wsrm.v1_0.invm.roundtrip.server;



import jakarta.jws.WebService;
import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.soap.SOAPBinding;

@WebService(endpointInterface = "wsrm.v1_0.invm.roundtrip.server.IPing")
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class IPingImpl {

    /**
     * @param String
     */
    public PingResponseBodyType echoString(PingRequestBodyType echoString) {
        PingResponseBodyType pr = new ObjectFactory().createPingResponseBodyType();
        JAXBElement<String> val = new JAXBElement<String>(new QName("http://tempuri.org/", "EchoStringReturn"), String.class, new String("Returning hello "));
        JAXBElement<String> text = echoString.getText();
        JAXBElement<String> seq = echoString.getSequence();
        val.setValue("Returning " + text.getValue() + "Sequence" + seq.getValue());
        pr.setEchoStringReturn(val);

        return pr;
    }
}
