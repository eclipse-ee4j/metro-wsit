/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.security.secconv;

import javax.xml.namespace.QName;


public class WSSecureConversationRuntimeException extends java.lang.RuntimeException  {

    private static final long serialVersionUID = -8356606890633002114L;
    private QName faultcode;

  public WSSecureConversationRuntimeException(QName faultcode,
           String faultstring) {
        super(faultstring);
        this.faultcode = faultcode;
  }

  public QName getFaultCode() {
        return this.faultcode;
  }
}

