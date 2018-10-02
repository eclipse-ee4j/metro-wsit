/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wspolicy.provider.base.client;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.rx.rm.runtime.RmRuntimeVersion;
import com.sun.xml.ws.rx.testing.PacketFilter;
import com.sun.xml.ws.rx.testing.PacketFilteringFeature;
import junit.framework.TestCase;

/**
 *
 * @author Fabian Ritzmann
 */
public class ClientTest extends TestCase {

    public static class TestFilter extends PacketFilter {

        private static volatile RmRuntimeVersion version;

        public Packet filterClientRequest(Packet request) throws Exception {
            if (version == null) {
                version = getRmVersion();
            }
            else {
                assertEquals(version, getRmVersion());
            }
            return request;
        }

        public Packet filterServerResponse(Packet response) throws Exception {
            if (version == null) {
                version = getRmVersion();
            }
            else {
                assertEquals(version, getRmVersion());
            }
            return response;
        }

        public static RmRuntimeVersion getVersion() {
            return version;
        }
    }

    public void testProvider() {
        PacketFilteringFeature filterFeature = new PacketFilteringFeature(TestFilter.class);
        EchoService echoService = new EchoService();
        Echo echo = echoService.getEchoPort(filterFeature);
        String result = echo.echo("Hello");
        assertEquals("Helloellolloloo", result);
        // Make sure that the message exchange actually used the policy configuration
        RmRuntimeVersion version = TestFilter.getVersion();
        assertEquals(RmRuntimeVersion.WSRM200702, version);
    }
}
