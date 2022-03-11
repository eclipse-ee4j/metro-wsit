/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package metro_sample.client;

import java.io.IOException;

public class AddNumbersClient {

    private static final int NUMBER_1 = 10;
    private static final int NUMBER_2 = 20;
    private static final int EXPECTED_RESULT = 30;

    private final String name;
    private final AddNumbersPortType wsProxy;

    private AddNumbersClient(String clientName, AddNumbersService service) {
        name = clientName;
        wsProxy = service.getAddNumbersPort();
    }

    private void testAddNumbers(int n1, int n2, int expectedResult) {
        System.out.printf("[ %s ]: Adding numbers %d + %d\n", name, n1, n2);
        try {
            int result = wsProxy.addNumbers(n1, n2);
            if (result == expectedResult) {
                System.out.printf("[ %s ]: Result as expected: %d\n", name, result);
            } else {
                System.out.printf("[ %s ]: Unexpected result: %d    Expected: %d\n", name, result, expectedResult);
            }
        } catch (Exception ex) {
            System.err.printf("[ %s ]: Exception occured:\n", name);
            ex.printStackTrace(System.err);
        }
        System.out.printf("\n\n");
    }

    private void releaseWsProxy() {
        try {
            System.out.printf("[ %s ]: Closing WS proxy and releasing RM session...", name);
            ((java.io.Closeable) wsProxy).close();
            System.out.println("DONE.");
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static void main(String[] args) {
        System.out.printf("Maximum RM concurrent sessions sample application");
        System.out.printf("=================================================\n\n");

        AddNumbersService service = new AddNumbersService();

        AddNumbersClient client1 = new AddNumbersClient("Client-1", service);
        client1.testAddNumbers(NUMBER_1, NUMBER_2, EXPECTED_RESULT);

        AddNumbersClient client2 = new AddNumbersClient("Client-2", service);
        client2.testAddNumbers(NUMBER_1, NUMBER_2, EXPECTED_RESULT);

        AddNumbersClient client3 = new AddNumbersClient("Client-3", service);
        try {
            client3.testAddNumbers(NUMBER_1, NUMBER_2, EXPECTED_RESULT);
        } catch (RuntimeException e) {
            System.out.println("Expected exception on the client side:");
            e.printStackTrace(System.out);
        }

        client1.releaseWsProxy();
        client2.releaseWsProxy();

        System.out.printf("Retrying %s\n", client3.name);

        client3.testAddNumbers(NUMBER_1, NUMBER_2, EXPECTED_RESULT);

        System.out.println("SUCCESS!");
        client3.releaseWsProxy();
    }
}
