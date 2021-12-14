/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * JAXWSProcessingContextImpl.java
 *
 * Created on January 30, 2006, 5:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.jaxws.impl;

import com.sun.xml.ws.api.message.Message;
import com.sun.xml.wss.impl.FilterProcessingContext;

/**
 *
 */
public class JAXWSProcessingContextImpl extends FilterProcessingContext implements JAXWSProcessingContext{
    
    private Message _message;
    /** Creates a new instance of JAXWSProcessingContextImpl */
    public JAXWSProcessingContextImpl() {
    }
    
    @Override
    public void setMessage(Message message) {
        this._message = message;
    }
    
    @Override
    public Message getMessage() {
        return _message;
    }
    
}
