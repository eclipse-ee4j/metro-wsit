/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider;

import javax.security.auth.message.MessageInfo;
import jakarta.xml.soap.SOAPMessage;

/**
 *
 * @author Kumar
 */
class AuthParamHelper {

    @SuppressWarnings("unchecked")
    static SOAPMessage getRequest(MessageInfo param) {
        if (param != null) {
            Object obj = param.getRequestMessage();
            if (obj instanceof SOAPMessage) {
                return (SOAPMessage) obj;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static SOAPMessage getResponse(MessageInfo param) {
        if (param != null) {
            Object obj = param.getResponseMessage();
            if (obj instanceof SOAPMessage) {
                return (SOAPMessage) obj;
            }
        }
        return null;
    }
}
