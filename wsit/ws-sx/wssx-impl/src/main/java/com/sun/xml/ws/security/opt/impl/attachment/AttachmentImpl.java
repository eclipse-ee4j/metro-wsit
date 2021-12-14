/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.attachment;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.ws.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jakarta.activation.DataHandler;
import jakarta.xml.soap.AttachmentPart;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class AttachmentImpl implements Attachment {
    
    private final String contentId;
    private byte[] data;
    private final String mimeType;
    
    public AttachmentImpl(@NotNull String contentId, byte[] data, String mimeType){
        this.contentId = contentId;
        this.data = data;
        this.mimeType = mimeType;
    }

    @Override
    public String getContentId() {
        return contentId;
    }

    @Override
    public String getContentType() {
        return mimeType;
    }

    @Override
    public byte[] asByteArray() {
        return data;
    }

    @Override
    public DataHandler asDataHandler() {
        return new DataSourceStreamingDataHandler(new ByteArrayDataSource(data,getContentType()));
    }

    @Override
    public Source asSource() {
        return new StreamSource(asInputStream());
    }

    @Override
    public InputStream asInputStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        os.write(asByteArray());
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        AttachmentPart part = saaj.createAttachmentPart();
        part.setDataHandler(asDataHandler());
        part.setContentId(contentId);
        saaj.addAttachmentPart(part);
    }

}
