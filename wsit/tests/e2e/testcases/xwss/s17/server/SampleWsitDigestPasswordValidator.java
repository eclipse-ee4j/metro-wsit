/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

// package common;
package xwss.s17.server;

import com.sun.xml.wss.impl.callback.PasswordValidationCallback;


public class SampleWsitDigestPasswordValidator extends PasswordValidationCallback.WsitDigestPasswordValidator {


        public  void setPassword(PasswordValidationCallback.Request request){

			String passwd = "abcd!1234"; //Get this password from somewhere

            PasswordValidationCallback.DigestPasswordRequest req = (PasswordValidationCallback.DigestPasswordRequest)request;
            req.setPassword(passwd);

		}
}
