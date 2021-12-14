/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.metro.api.config.management;

/**
 * The communication components of the config management system may create instances
 * of this listener that are used then to notify clients when the endpoint was
 * reconfigured.
 *
 * @author Fabian Ritzmann
 */
public interface ReconfigNotifier {

    /**
     * Emit a notification that the endpoint was reconfigured.
     */
    void sendNotification();

}
