/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*

 * DemoClient.java

 */



package rmdemo.client;
import com.sun.xml.ws.Closeable;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This is a demo client to demonstrate the Session support
 * with WS RM implementation.
 * @author Mike Grogan
 */

public class DemoClient {

    public static void main(String[] args) throws Exception {
        RMDemoService service = new RMDemoService();
        RMDemo port = service.getRMDemoPort();

        BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(
                                       System.in));

        System.out.println("Running the RMDemo....\n"+
                "Enter as many strings as you like... \n" +
                "then press <Enter> to see the result and terminate this client\n");

        while (true) {

            String str = reader.readLine();
            if (!str.equals("")) {
                port.addString(str);

            } else {
                System.out.println(port.getResult());
                ((Closeable)port).close();
                System.exit(0);

            }

        }



    }

}

