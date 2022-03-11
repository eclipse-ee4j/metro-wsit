/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package externalmetadata.server;

//@WebService
public class BlackboxImpl {

    /**
     * @param number1
     * @param number2
     * @return The sum
     * @throws BlackboxException
     *             if any of the numbers to be added is negative.
     */
    //@WebMethod
    public int addNumbers(int number1, int number2) throws BlackboxException {
        if (number1 < 0 || number2 < 0) {
            throw new BlackboxException("Negative number cant be added!",
                "Numbers: " + number1 + ", " + number2);
        }
        return number1 + number2;
    }

    public void doSomething() throws BlackboxException {

    }
}
