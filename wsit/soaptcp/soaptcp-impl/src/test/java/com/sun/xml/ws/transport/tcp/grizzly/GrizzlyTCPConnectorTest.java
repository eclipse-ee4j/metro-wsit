/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.grizzly;

import com.sun.xml.ws.transport.tcp.server.TCPAdapter;
import com.sun.xml.ws.transport.tcp.server.TCPMessageListener;
import com.sun.xml.ws.transport.tcp.server.WSTCPDelegate;
import com.sun.xml.ws.transport.tcp.server.WSTCPModule;
import com.sun.xml.ws.transport.tcp.util.ChannelContext;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import com.sun.xml.ws.transport.tcp.util.WSTCPError;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GrizzlyTCPConnectorTest {

    private final static Logger log = Logger.getLogger("GrizzlyTCPConnectorTest");
    private final Integer PORT = 8036;
    private final String HOST = "localhost";
    private GrizzlyTCPConnector connector;

    private CountDownLatch latch;

    @Before
    public void before() throws IOException {
        final WSTCPDelegate delegate = new WSTCPDelegate();

        new WSTCPModule() {

            public WSTCPModule init() {
                setInstance(this);
                return this;
            }

            @Override
            public void register(String contextPath, List<TCPAdapter> adapters) {
                delegate.registerAdapters(contextPath, adapters);
            }

            @Override
            public void free(String contextPath, List<TCPAdapter> adapters) {
                delegate.freeAdapters(contextPath, adapters);
            }

            @Override
            public int getPort() {
                return PORT;
            }


        }.init();

        connector = new GrizzlyTCPConnector(HOST, PORT, new TCPMessageListener() {
            @Override
            public void onMessage(ChannelContext channelContext) throws IOException {
                final OutputStream stream = channelContext.getConnection().openOutputStream();
                stream.write(0);
                stream.flush();
                stream.close();
                latch.countDown();
            }

            @Override
            public void onError(ChannelContext channelContext, WSTCPError error) {
                latch.countDown();
            }
        });
        connector.listen();
    }

    @After
    public void after() {
        connector.close();
    }

    @Test
    public void testListen() throws Exception {
        latch = new CountDownLatch(1);
        log.info("Starting connection");
        final Socket socket = new Socket(HOST, PORT);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(TCPConstants.PROTOCOL_SCHEMA); // name of the protocol
        writer.write(""); //version mark which is HEX 1010
        writer.flush();
        log.info("Handshake passed");
        writer.flush();
        log.info(String.valueOf(socket.getInputStream().read()));
        writer.flush();
        writer.write("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://servicechannel.tcp.transport.ws.xml.sun.com/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ser:initiateSession/>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>");
        writer.flush();
        log.info(String.valueOf(socket.getInputStream().read()));
        writer.write("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://servicechannel.tcp.transport.ws.xml.sun.com/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ser:openChannel>\n" +
                "         <targetWSURI>/</targetWSURI>\n" +
                "         <negotiatedMimeTypes>text/plain</negotiatedMimeTypes>\n" +
                "      </ser:openChannel>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>");
        writer.flush();
        writer.close();
        socket.close();
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
    }


}