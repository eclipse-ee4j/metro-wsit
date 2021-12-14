/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.common;

/**
 * This is the common interface implemented by wsat10 and wsat 11 Coordinators endpoints and client proxy
 */
public interface CoordinatorIF<T> {

    /**
     * @param parameters Notification
     */
    void preparedOperation(T parameters);

    /**
     * Aborted response
     * @param parameters Notification
     */
    void abortedOperation(T parameters);

    /**
     * ReadOnly response
     * @param parameters Notification
     */
    void readOnlyOperation(T parameters);

    /**
     * Committed response
     * @param parameters Notification
     */
    void committedOperation(T parameters);

    /**
     * WS-AT 1.0 recovery operation
     * @param parameters  Notification
     */
    void replayOperation(T parameters);

}
