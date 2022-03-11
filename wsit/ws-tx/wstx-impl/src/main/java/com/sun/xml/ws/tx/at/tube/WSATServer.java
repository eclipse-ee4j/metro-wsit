/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.tube;

import com.sun.xml.ws.api.message.MessageHeaders;

/**
 * User: paulparkinson
 * Date: Jan 19, 2010
 * Time: 12:19:39 PM
 */
public interface WSATServer {

    void doHandleRequest(MessageHeaders headers, TransactionalAttribute tx);

    void doHandleResponse(TransactionalAttribute transactionalAttribute);

    void doHandleException(Throwable throwable);
}
