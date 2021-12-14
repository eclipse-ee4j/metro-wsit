/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.jaxws.impl;

import com.sun.xml.ws.api.message.Message;
import com.sun.xml.wss.SecurityProcessingContext;

/**
 * represents security processing context in JAXWS integration path.
 */
public interface JAXWSProcessingContext extends SecurityProcessingContext{
    
    /**
     * sets JAXWS Message representation.
     * @param message {@link com.sun.xml.ws.api.message.Message}
     */
    void setMessage(Message message);
    /**
     * returns JAXWS Message if present else null.
     * @return return {@link com.sun.xml.ws.api.message.Message}  or null
     */
    Message getMessage();
}
