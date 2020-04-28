/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wsrm.v1_0.persistent.dispatch.client;



import java.io.IOException;
import java.util.logging.Level;
import jakarta.xml.bind.*;
import javax.xml.namespace.QName;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.MessageFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.Dispatch;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.StringReader;

import java.util.logging.Logger;

import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class ClientTest extends TestCase {

    private static final Logger LOGGER = Logger.getLogger(ClientTest.class.getName());
    private static final String helloRequest = "<?xml version=\"1.0\" ?>" +
            "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "  <S:Header></S:Header>" +
            "  <S:Body>" +
            "    <echoString xmlns=\"http://tempuri.org/\">" +
            "      <Text>Hello There! no0</Text>" +
            "      <Sequence>seq! no0</Sequence>" +
            "    </echoString>" +
            "  </S:Body>" +
            "</S:Envelope>";

    private static final String NAMESPACEURI = "http://tempuri.org/";
    private static final String SERVICE_NAME = "PingService";
    private static final String PORT_NAME = "WSHttpBinding_IPing";
    private static final QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);
    private static final QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);


    public void testSendEcho() throws Exception {
        PingService service = new PingService();
        Dispatch<SOAPMessage> dispatchMsg = null;
        try {
            dispatchMsg = service.createDispatch(
                    PORT_QNAME,
                    SOAPMessage.class,
                    jakarta.xml.ws.Service.Mode.MESSAGE,
                    new WebServiceFeature[] {new jakarta.xml.ws.RespectBindingFeature()});

            SOAPMessage reqMsg = makeSOAPMessage(helloRequest);

            LOGGER.info(String.format("Sending request message on a dispatch client:%n%s", getSOAPMessageAsString(reqMsg)));
            SOAPMessage resMsg = dispatchMsg.invoke(reqMsg);
            String responseMessage = getSOAPMessageAsString(resMsg);
            LOGGER.info(String.format("Received response message on a dispatch client:%n%s", responseMessage));

            if (!responseMessage.contains(new String("Action"))) {
                fail("The response Message is not as expected");
            }

        } finally {
            if (dispatchMsg instanceof Closeable) {
                try {
                    Closeable.class.cast(dispatchMsg).close();
                    LOGGER.info("Dispatch client successfully closed");
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error closing dispatch", ex);
                }
            }
        }
    }

    private String getSOAPMessageAsString(SOAPMessage msg) throws Exception {
        ByteArrayOutputStream output = null;
        output = new ByteArrayOutputStream();
        msg.writeTo(output);
        return output.toString();
    }

    private SOAPMessage makeSOAPMessage(String msg) throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        message.getSOAPPart().setContent((Source) new StreamSource(new StringReader(msg)));
        message.saveChanges();
        return message;
    }
}
