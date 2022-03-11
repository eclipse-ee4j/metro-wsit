/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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

    private static final int[] NUMBER_1 = new int[]{10, 20, 30};
    private static final int[] NUMBER_2 = new int[]{20, 40, 60};
    private static final int[] EXPECTED_RESULT = new int[]{30, 60, 90};

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
            System.out.printf("[ %s ]: Closing WS proxy...", name);
            ((java.io.Closeable) wsProxy).close();
            System.out.println("DONE.");
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static void main(String[] args) {
        System.out.printf("Custom tubes sample application\n");
        System.out.printf("===============================\n\n");

        AddNumbersService service = new AddNumbersService();

        AddNumbersClient client = new AddNumbersClient("Client", service);

        for (int i = 0; i < EXPECTED_RESULT.length; i++) {
            client.testAddNumbers(NUMBER_1[i], NUMBER_2[i], EXPECTED_RESULT[i]);
        }

        client.releaseWsProxy();
    }
}
