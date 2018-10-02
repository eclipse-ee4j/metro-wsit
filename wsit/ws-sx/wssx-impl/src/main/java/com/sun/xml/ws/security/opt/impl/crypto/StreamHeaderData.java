/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.crypto;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.security.opt.impl.util.XMLStreamFilter;
import com.sun.xml.ws.security.opt.crypto.StreamWriterData;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.NamespaceContextEx;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class StreamHeaderData implements StreamWriterData{
    private NamespaceContextEx nsContext;
    private boolean contentOnly;
    private Header data;
    /** Creates a new instance of HeaderData */
    public StreamHeaderData(Header header,boolean contentOnly,NamespaceContextEx ns) {
        this.data = header;
        nsContext = ns;
        this.contentOnly = contentOnly;
    }
    
    public NamespaceContextEx getNamespaceContext() {
        return nsContext;
    }
    
    public void write(javax.xml.stream.XMLStreamWriter writer) throws javax.xml.stream.XMLStreamException {
        if(contentOnly){
            XMLStreamWriter fw;            
            fw = new XMLStreamFilter(writer, (com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx)nsContext);            
            data.writeTo(fw);
        }else{
            data.writeTo(writer);
        }
    }    
}
