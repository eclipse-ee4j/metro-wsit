/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wspolicy.dispatch.nowsdl.client;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.developer.JAXWSProperties;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.Text;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import junit.framework.TestCase;

/**
 *
 * @author Fabian Ritzmann
 */
public class ClientTest extends TestCase {

    public void testDispatch() throws SOAPException {
        EchoService echoService = new EchoService();
        QName echoServiceName = echoService.getServiceName();
        String targetNamespace = echoServiceName.getNamespaceURI();

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPFactory soapFactory = SOAPFactory.newInstance();

        SOAPMessage message = messageFactory.createMessage();
        SOAPBody messageBody = message.getSOAPBody();

        Name bodyName = soapFactory.createName("echo", "disp", targetNamespace);
        SOAPBodyElement messageEcho = messageBody.addBodyElement(bodyName);
        Name arg0 = soapFactory.createName("arg0");
        SOAPElement messageEchoArg0 = messageEcho.addChildElement(arg0);
        messageEchoArg0.addTextNode("Hello");

        Service service = Service.create(echoServiceName);
        QName portName = new QName(targetNamespace, "EchoPort");
        String echoPortAddress = System.getProperty("echoPortAddress");
        if (echoPortAddress == null) {
            fail("Failed to find echoPortAddress in system properties.");
        }
        service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, echoPortAddress);
        Dispatch dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
        dispatch.getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
        dispatch.getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "http://server.wsdl.dispatch.wspolicy/action/echo");

        SOAPMessage response = (SOAPMessage)dispatch.invoke(message);

        assertNotNull(response);

        // Make sure that the message exchange actually used the policy configuration
        Map<String, Object> responseContext = dispatch.getResponseContext();
        HeaderList headerList = (HeaderList) responseContext.get(JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
        String to = headerList.getTo(AddressingVersion.W3C, SOAPVersion.SOAP_11);
        assertEquals(AddressingVersion.W3C.anonymousUri, to);

        SOAPBody responseBody = response.getSOAPBody();
        assertNotNull(responseBody);
        Iterator elements = responseBody.getChildElements();
        SOAPElement responseElement = (SOAPElement) elements.next();
        assertFalse(elements.hasNext());
        elements = responseElement.getChildElements();
        SOAPElement returnElement = (SOAPElement) elements.next();
        assertFalse(elements.hasNext());
        elements = returnElement.getChildElements();
        Text textNode = (Text) elements.next();
        assertFalse(elements.hasNext());
        String result = textNode.getTextContent();
        assertEquals("Helloellolloloo", result);
    }
}
