/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * SignedMessagePart.java
 *
 * Created on August 24, 2006, 2:19 PM
 */

package com.sun.xml.ws.security.opt.impl.dsig;

import com.sun.xml.ws.security.opt.api.SecurityElement;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SignedData;
import com.sun.xml.ws.security.opt.impl.message.SOAPBody;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class SignedMessagePart implements SecurityElement, SignedData, SecurityElementWriter{
    protected boolean isCanonicalized = false;
    private SecurityElement se = null;
    private SOAPBody body = null;
    private boolean contentOnly = false;
    private List attributeValuePrefixes = null;

    private ByteArrayOutputStream storedStream = new ByteArrayOutputStream();

    protected byte[] digestValue = null;

    /** Creates a new instance of SignedMessagePart */
    public SignedMessagePart(){
    }

    public SignedMessagePart(SecurityElement se) {
        this.se = se;
    }

    public SignedMessagePart(SOAPBody body, boolean contentOnly){
        this.body = body;
        this.contentOnly = contentOnly;
    }

    @Override
    public String getId() {
        if(body != null){
            if(!contentOnly){
                return body.getId();
            } else{
                return body.getBodyContentId();
            }
        }else{
            return se.getId();
        }
    }

    @Override
    public void setId(String id) {
        if(body != null){
            if(!contentOnly){
                body.setId(id);
            } else{
                body.setBodyContentId(id);
            }
        }else{
            se.setId(id);
        }
    }

    @Override
    public String getNamespaceURI() {
        if(body != null){
            if(!contentOnly){
                return body.getSOAPVersion().nsUri;
            } else {
                return body.getPayloadNamespaceURI();
            }
        }else {
            return se.getNamespaceURI();
        }
    }

    @Override
    public String getLocalPart() {
        if(body != null){
            if(!contentOnly){
                return "Body";
            } else {
                return body.getPayloadLocalPart();
            }
        }else {
            return se.getLocalPart();
        }
    }

    @Override
    public javax.xml.stream.XMLStreamReader readHeader() throws javax.xml.stream.XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(OutputStream os) {
        try{
            if(isCanonicalized)
                writeCanonicalized(os);
        } catch(IOException ioe){
            throw new XWSSecurityRuntimeException(ioe);
        }
    }

    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws javax.xml.stream.XMLStreamException {
        if(body != null){
            body.cachePayLoad();//will be replaced with 2nd round of optimization.
            attributeValuePrefixes = body.getAttributeValuePrefixes();
            if(!contentOnly){
                body.writeTo(streamWriter);
            }else{
                body.writePayload(streamWriter);
            }
        }else{
            ((SecurityElementWriter)se).writeTo(streamWriter);
        }

    }

    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter, HashMap props) throws javax.xml.stream.XMLStreamException {
        writeTo(streamWriter);
    }

    public void writeCanonicalized(OutputStream os) throws IOException{
        if(storedStream == null)
            return;
        storedStream.writeTo(os);
    }

    @Override
    public void setDigestValue(byte[] digestValue) {
        this.digestValue = digestValue;
    }

    @Override
    public byte[] getDigestValue() {
        return digestValue;
    }

    public List getAttributeValuePrefixes(){
        return attributeValuePrefixes;
    }
}
