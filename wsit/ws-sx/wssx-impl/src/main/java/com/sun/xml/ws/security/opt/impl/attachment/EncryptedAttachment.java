/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.attachment;

import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.security.opt.impl.enc.CryptoProcessor;
import com.sun.xml.ws.util.ByteArrayDataSource;
import com.sun.xml.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.swa.MimeConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import jakarta.activation.DataHandler;
import javax.crypto.Cipher;
import jakarta.xml.soap.AttachmentPart;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class EncryptedAttachment implements Attachment {

    private Attachment attachment;
    private String dataAlgo;
    private Key key;
    private byte[] data;

    public EncryptedAttachment(Attachment attachment, String dataAlgo, Key key) throws XWSSecurityException{
        this.attachment = attachment;
        this.dataAlgo = dataAlgo;
        this.key = key;

        doEncryption();
    }

    @Override
    public String getContentId() {
        return attachment.getContentId();
    }

    @Override
    public String getContentType() {
        return MimeConstants.APPLICATION_OCTET_STREAM_TYPE;
    }

    @Override
    public byte[] asByteArray() {
        return data;
    }

    @Override
    public DataHandler asDataHandler() {
        return new DataSourceStreamingDataHandler(new ByteArrayDataSource(data, getContentType()));
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
        part.setContentId(getContentId());
        saaj.addAttachmentPart(part);
    }

    private void doEncryption() throws XWSSecurityException{
        CryptoProcessor dep = new CryptoProcessor(Cipher.ENCRYPT_MODE, dataAlgo, key);
        data = dep.encryptData(attachment.asByteArray());
    }

}
