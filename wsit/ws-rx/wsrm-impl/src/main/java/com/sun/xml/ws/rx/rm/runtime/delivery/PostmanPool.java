/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.delivery;

/**
 *
 */
public enum PostmanPool {
    INSTANCE;

    private final Postman singletonPostman = new Postman();

    public Postman getPostman() {
        return singletonPostman;
    }
}
