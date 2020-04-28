/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wsrm.v1_0.invm.dispatch.server;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import jakarta.xml.ws.*;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

@WebServiceProvider(
    portName="WSHttpBinding_IPing",
    serviceName="PingService",
    targetNamespace="http://tempuri.org/",
    wsdlLocation="WEB-INF/wsdl/EchoService.wsdl"
)
@BindingType(value="http://schemas.xmlsoap.org/wsdl/soap/http")
@jakarta.xml.ws.RespectBinding
@ServiceMode(value=jakarta.xml.ws.Service.Mode.MESSAGE)
public class PingProvider implements Provider<SOAPMessage> {

    private static final String helloResponse = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
        "  <S:Header>" +
        "  </S:Header>" +
        "  <S:Body>" +
        "    <echoStringResponse xmlns=\"http://tempuri.org/\">" +
        "      <EchoStringReturn>Returning Hello There! no1Sequenceseq! no1</EchoStringReturn>" +
        "    </echoStringResponse>" +
        "  </S:Body>" +
        "</S:Envelope>";

    public SOAPMessage invoke(SOAPMessage req)  {
	System.out.println("invoke: Request: " + getSOAPMessageAsString(req));
        SOAPMessage res = null;
	try {
            res = makeSOAPMessage(helloResponse);
	} catch (Exception e) {
	    System.out.println("Exception: occurred " + e);
	}
	System.out.println("invoke: Response: " + getSOAPMessageAsString(res));
        return res;
    }

    private String getSOAPMessageAsString(SOAPMessage msg)
    {
	ByteArrayOutputStream baos = null;
	String s = null;
        try {
	    baos = new ByteArrayOutputStream();
            msg.writeTo(baos);
	    s = baos.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
	return s;
    }

    private SOAPMessage makeSOAPMessage(String msg)
    {
	try {
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage message = factory.createMessage();
            message.getSOAPPart().setContent((Source)new StreamSource(new StringReader(msg)));
            message.saveChanges();
            return message;
	}
	catch (Exception e) {
	    return null;
	}
    }
}

