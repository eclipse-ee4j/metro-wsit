/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wsrm.v1_0.invm.basicorderedoneway.client;

import com.sun.xml.ws.rx.testing.PacketFilteringFeature;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import wsrm.v1_0.invm.basicorderedoneway.common.EvenMessageDelayingFilter;

/**
 *
 * @author Marek Potociar
 */
public class ClientTest extends TestCase {

    private static final Logger LOGGER = Logger.getLogger(ClientTest.class.getName());
    private static final int NUMBER_OF_THREADS = 5;

    public void testAckRequestedInterval() {
        IPing port = null;
        final CountDownLatch latch = new CountDownLatch(NUMBER_OF_THREADS);
        try {
            PingService service = new PingService();
            port = service.getPingPort(new PacketFilteringFeature(
                    EvenMessageDelayingFilter.class //  2
                    //                    EvenMessageDelayingFilter.class, //  4
                    //                    EvenMessageDelayingFilter.class, //  6
                    //                    EvenMessageDelayingFilter.class, //  8
                    //                    EvenMessageDelayingFilter.class, // 10
                    //                    EvenMessageDelayingFilter.class  // 12
                    ));

            final IPing portCopy = port;
            Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);


            for (final AtomicInteger i = new AtomicInteger(1); i.get() <= NUMBER_OF_THREADS; i.incrementAndGet()) {
                executor.execute(new Runnable() {

                    int id = i.get();

                    public void run() {
                        try {
                            LOGGER.info(String.format("Calling web service in runnable [ %d ]", id));
                            portCopy.ping("ping-" + id);
                            LOGGER.info(String.format("Web service call finished in runnable [ %d ]", id));
                        } finally {
                            latch.countDown();
                            LOGGER.info(String.format("Decreasing latch count to %d", latch.getCount()));
                        }
                    }
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Test failed with an unexpected exception.", ex);
            fail(String.format("Test failed with the execption: %s", ex));
        } finally {
            if (port != null) {
                try {
                    LOGGER.info(String.format("Still need to wait for %d threads", latch.getCount()));
                    latch.await(600, TimeUnit.SECONDS);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
                    try {
                        ((java.io.Closeable) port).close();
                    } catch (IOException ioex) {
                        Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ioex);
                    }
                    fail("The test did not finished in 60 seconds. Most likely it is stuck in a deadlock or on sending poisoned messages");
                }
                try {
                    ((java.io.Closeable) port).close();
                } catch (IOException ex) {
                    Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
