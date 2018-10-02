/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.common;


/**
 * 
 * This is the common interface implemented by wsat10 and wsat 11 participants endpoints and client proxy
 */
public interface ParticipantIF<T>{

    /**
     *
     * @param parameters
     */
    public void prepare(T parameters);

    /**
     *
     * @param parameters
     */
    public void commit(T parameters);

    /**
     *
     * @param parameters
     */
    public void rollback(T parameters);


}
