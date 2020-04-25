/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package externalmetadata.client;

public class BlackboxClient {

    public static void main (String[] args) {
        try {
            BlackboxImpl port = new BlackboxImplService().getBlackboxImplPort();
            System.out.printf("Invoking doSomething\n");
            port.doSomething();
            System.out.printf("Web service call finished successfully.\n");
        } catch (BlackboxException_Exception ex) {
            System.out.printf ("Caught BlackboxException_Exception: %s\n", ex.getFaultInfo ().getDetail ());
        }
    }
}
