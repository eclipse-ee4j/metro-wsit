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

import com.sun.xml.ws.security.opt.impl.message.SOAPBody;
import com.sun.xml.ws.security.opt.crypto.StreamWriterData;

import org.jvnet.staxex.NamespaceContextEx;

/**
 * Representation of SOAP Body data
 * @author K.Venugopal@sun.com
 */

public class SSBData implements StreamWriterData{
    private NamespaceContextEx nsContext;
    private boolean contentOnly;
    private SOAPBody body;

    /** Creates a new instance of SOAPBodyData */
    public SSBData(SOAPBody body,boolean contentOnly,NamespaceContextEx nsContext) {
        this.body = body;
        this.contentOnly = contentOnly;
        this.nsContext = nsContext;
    }

    @Override
    public NamespaceContextEx getNamespaceContext() {
        return nsContext;
    }


    @Override
    public void write(javax.xml.stream.XMLStreamWriter writer) throws javax.xml.stream.XMLStreamException {
        if(contentOnly){
            body.writePayload(writer);
        }else{
            body.writeTo(writer);
        }
    }
}
