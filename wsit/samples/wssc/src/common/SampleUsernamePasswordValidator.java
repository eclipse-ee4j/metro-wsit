/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package common;

import com.sun.xml.wss.impl.callback.PasswordValidationCallback;

public class SampleUsernamePasswordValidator implements PasswordValidationCallback.PasswordValidator {

        public boolean validate(PasswordValidationCallback.Request request)
            throws PasswordValidationCallback.PasswordValidationException {
            System.out.println("Using configured PlainTextPasswordValidator................");

            PasswordValidationCallback.PlainTextPasswordRequest plainTextRequest =
                (PasswordValidationCallback.PlainTextPasswordRequest) request;
            if ("alice".equals(plainTextRequest.getUsername()) &&
                "alice".equals(plainTextRequest.getPassword())) {

                return true;
            }else if ("bob".equals(plainTextRequest.getUsername()) &&
                "bob".equals(plainTextRequest.getPassword())) {
                return true;
            }
            return false;
        }
}
