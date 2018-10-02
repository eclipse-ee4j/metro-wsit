/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.commons;

/**
 * Default implementation of {@link MOMRegistrationAware}.
 */
public abstract class AbstractMOMRegistrationAware implements MOMRegistrationAware {

    private boolean atMOM = false;

    public boolean isRegisteredAtMOM() {
        return this.atMOM;
    }

    public void setRegisteredAtMOM(boolean atMOM) {
        this.atMOM = atMOM;
    }

}
