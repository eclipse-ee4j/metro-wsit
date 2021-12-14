/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v10;

import com.sun.xml.ws.tx.at.common.NotificationBuilder;
import com.sun.xml.ws.tx.at.v10.types.Notification;

/**
 * This is the  class for building WSAT10 Notifications.
 */
public class NotificationBuilderImpl extends NotificationBuilder<Notification> {
    @Override
    public Notification build() {
        return new Notification();
    }
}
