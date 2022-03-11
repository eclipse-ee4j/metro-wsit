/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

// package common;
package wstrust.publickeyn.sts;

import com.sun.xml.wss.impl.callback.PasswordValidationCallback;


public class PlainTextPasswordValidator implements PasswordValidationCallback.PasswordValidator {


        public boolean validate(PasswordValidationCallback.Request request)
            throws PasswordValidationCallback.PasswordValidationException {
            System.out.println("Using configured PlainTextPasswordValidator................");
            PasswordValidationCallback.PlainTextPasswordRequest plainTextRequest =
                (PasswordValidationCallback.PlainTextPasswordRequest) request;
            if ("Alice".equals(plainTextRequest.getUsername()) &&
                "abcd!1234".equals(plainTextRequest.getPassword())) {
                return true;
            }
            return false;
        }
}
