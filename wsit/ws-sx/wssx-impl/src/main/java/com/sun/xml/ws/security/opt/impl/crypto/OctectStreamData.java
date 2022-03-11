/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.crypto;

import javax.xml.crypto.Data;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class OctectStreamData implements Data{
    private String data = null;
    /** Creates a new instance of OctectStreamData */
    public OctectStreamData(String data) {
        this.data = data;
    }

    public void write(XMLStreamWriter writer) throws XMLStreamException{
        writer.writeCharacters(data);
    }
}
