/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.c14n;

import org.xml.sax.Attributes;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class Attribute   {
    int position = 0;
    Attributes attributes = null;
    
    /** Creates a new instance of Attribute */
    public Attribute () {
    }
    
    public void setPosition (int pos){
        this.position = pos;
    }
    public void setAttributes (Attributes attrs){
        this.attributes = attrs;
    }
    public String getLocalName (){
        return attributes.getLocalName (position);
    }
    
    public String getNamespaceURI(){
        return attributes.getURI (position);
    }
    
    public String getValue (){
        return attributes.getValue (position);
    }
    public int getPosition(){
        return position;
    }
}
