/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wsrm.v1_1.invm.jcapsnack.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar
 */
public class ClientTest extends TestCase {
    private static final Logger LOGGER = Logger.getLogger(ClientTest.class.getName());

    public void testNack() {
        IPing port = null;
        try {
            PingService service = new PingService();
            port = service.getPingPort();

            for (int i = 1; i <= 2; i++) {
                port.ping("Hello " + i);
                LOGGER.info(String.format("Hello %d. message successfully sent.", i));
            }
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

    // See JIRA issue: https://java.net/jira/browse/WSIT-1671
    // Without the fix for this JIRA issue, this test hangs.
    // Needed some test to reproduce Fiber hang to remedy it.
    public void testNegativeCaseJIRAIssue1671() {
        IPing port = null;
        try {
            PingService service = new PingService();
            port = service.getPingPort();
            port.ping("Negative");
            LOGGER.info("Negative message successfully sent.");
        } catch (Exception ex) {
            // Ignore the SOAP fault that comes back.
            // LOGGER.log(Level.SEVERE,
            // "WS proxy invocation failed with an unexpected exception.",
            // ex);
            // fail(String.format("Test failed with the exception: %s", ex));
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
