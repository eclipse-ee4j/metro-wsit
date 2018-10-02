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

import com.sun.xml.ws.security.opt.api.SecurityElement;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.impl.util.XMLStreamFilter;
import com.sun.xml.ws.security.opt.crypto.StreamWriterData;
import java.util.HashMap;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.security.opt.impl.dsig.SignedMessagePart;
import com.sun.xml.wss.impl.c14n.StAXEXC14nCanonicalizerImpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class SSEData implements StreamWriterData {
    
    private NamespaceContextEx nsContext;
    private boolean contentOnly;
    private SecurityElement data;
    private XMLStreamBuffer buffer = null;
    private HashMap props = new HashMap();
    
    /** Creates a new instance of SSEData */
    public SSEData(SecurityElement se , boolean contentOnly,NamespaceContextEx ns ) {
        this.data = se;
        this.nsContext = ns;
        this.contentOnly = contentOnly;
        //props.put("com.sun.xml.bind.namespacePrefixMapper", new WSSNSPrefixWrapper(JAXBUtil.prefixMapper11));
    }
    
    public SSEData(SecurityElement se , boolean contentOnly,NamespaceContextEx ns, HashMap props ) {
        this.data = se;
        this.nsContext = ns;
        this.contentOnly = contentOnly;
        this.props = props;
    }
    
    public SSEData(XMLStreamBuffer buffer){
        this.buffer = buffer;
    }
    
    public NamespaceContextEx getNamespaceContext() {
        return nsContext;
    }
    
    public SecurityElement getSecurityElement(){
        return data;
    }
    @SuppressWarnings("unchecked")
    public void write(javax.xml.stream.XMLStreamWriter writer) throws javax.xml.stream.XMLStreamException {
        if(buffer != null){
            buffer.writeToXMLStreamWriter(writer);
        }
        
        if(contentOnly){
            XMLStreamWriter fw;
            if(data instanceof SignedMessagePart && writer instanceof StAXEXC14nCanonicalizerImpl){
                SignedMessagePart body = (SignedMessagePart)data;
                List attributeValuePrefixes = body.getAttributeValuePrefixes();
                if(attributeValuePrefixes != null && !attributeValuePrefixes.isEmpty()){
                    List prefixList = ((StAXEXC14nCanonicalizerImpl)writer).getInclusivePrefixList();
                    if(prefixList == null){
                        prefixList = new ArrayList();
                    }
                    prefixList.addAll(attributeValuePrefixes);
                    // remove duplicates by going through a HashSet
                    HashSet set = new HashSet(prefixList);
                    prefixList = new ArrayList(set);
                    ((StAXEXC14nCanonicalizerImpl)writer).setInclusivePrefixList(prefixList);
                }
            }
            fw = new XMLStreamFilter(writer, (com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx)nsContext);
            if(props != null){
                ((SecurityElementWriter)data).writeTo(fw,props);
            }else{
                ((SecurityElementWriter)data).writeTo(fw);
            }
        }else{
            if(props != null){
                ((SecurityElementWriter)data).writeTo(writer,props);
            }else{
                ((SecurityElementWriter)data).writeTo(writer);
            }
        }
    }
    
}
