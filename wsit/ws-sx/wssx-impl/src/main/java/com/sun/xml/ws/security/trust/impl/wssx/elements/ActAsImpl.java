/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.elements.ActAs;
import org.w3c.dom.Element;

/**
 *
 * @author Jiandong Guo
 */
public class ActAsImpl implements ActAs {

    private Object obj;

    public ActAsImpl(Token aaToken){
        this.obj = aaToken.getTokenValue();
    }

    public ActAsImpl(Element actAsElement){
        this.obj = actAsElement.getElementsByTagName("*").item(0);
    }

    @Override
    public void setAny(Object obj){
        this.obj = obj;
    }

    @Override
    public Object getAny(){
        return obj;
    }
}

