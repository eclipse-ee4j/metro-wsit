/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wsrm.v1_1.invm.dynamic_endpoint_address.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.ws.BindingProvider;
import junit.framework.TestCase;
import com.sun.xml.ws.rx.RxRuntimeException;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class ClientTest extends TestCase {
    private static final Logger LOGGER = Logger.getLogger(ClientTest.class.getName());
    
    public void testOneWay() {
        IPing port = null;
        try {
            PingService service = new PingService();
            port = service.getPingPort();

            ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://test.url:80/Service");

            port.ping("Hello ");
            fail("Invocation was supposed to fail with a RxRuntimeException");
        } catch (RxRuntimeException ex) {
            assertTrue(ex.getMessage().contains("WSRM1128"));
            LOGGER.log(Level.INFO, "WS proxy invocation failed with an EXPECTED exception.", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "WS proxy invocation failed with an unexpected exception.", ex);
            fail(String.format("Test failed with the execption: %s", ex));
        } finally {
            if (port != null) {
                try {
                    ((java.io.Closeable) port).close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error while closing WS proxy", ex);
                }
            }
        }    
    }
}
