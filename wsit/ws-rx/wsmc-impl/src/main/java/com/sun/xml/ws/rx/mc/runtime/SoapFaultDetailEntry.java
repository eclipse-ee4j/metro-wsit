/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.runtime;

import javax.xml.namespace.QName;

final class SoapFaultDetailEntry {

    public SoapFaultDetailEntry(QName name, String value) {
        this.name = name;
        this.value = value;
    }
    public final QName name;
    public final String value;
}
