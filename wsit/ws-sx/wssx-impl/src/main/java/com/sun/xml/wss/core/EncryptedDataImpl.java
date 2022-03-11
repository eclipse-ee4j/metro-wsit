/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.core;

import com.sun.xml.wss.impl.MessageConstants;
import java.io.ByteArrayOutputStream;

/**
 * Simple EncryptedData for Sign and Encrypt Usecase.
 * @author K.Venugopal@sun.com
 */
public class EncryptedDataImpl extends ByteArrayOutputStream {
    private byte [] iv = null;
    private byte [] encryptedData = null;
    private String id = null;
    private String mimeType = null;
    private String encoding = null;
    private String type = null;
    private KeyInfoHeaderBlock keyInfo = null;
    private String encAlgo = null;
    /** Creates a new instance of EncryptedData */
    private static final byte [] ENCRYPTED_DATA = MessageConstants.ENCRYPTED_DATA_LNAME.getBytes();
    private static final byte [] ENC_PREFIX = MessageConstants.XENC_PREFIX.getBytes();
    private static final byte [] ENC_NS = MessageConstants.XENC_NS.getBytes();
    private static byte [] OPENTAG =  "<".getBytes();
    private static byte [] CLOSETAG =  ">".getBytes();
    private static byte [] ENDTAG =  "</".getBytes();
    private static byte [] CLOSEELEMENT =  "/>".getBytes();
    private static byte [] ENCRYPTION_METHOD = "EncryptionMethod ".getBytes();
    private static byte [] ALGORITHM = "Algorithm ".getBytes();
    private static byte [] XMLNS =  "xmlns".getBytes();
    private static byte [] ID = "Id".getBytes();

    private static byte [] CIPHER_DATA = "CipherData".getBytes();
    private static byte [] CIPHER_VALUE = "CipherValue".getBytes();
    private static byte [] TYPE = "Type".getBytes();
    private static byte [] CONTENT_ONLY = "http://www.w3.org/2001/04/xmlenc#Content".getBytes();

    public EncryptedDataImpl() {
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(byte[] encryptedData) {
        this.encryptedData = encryptedData;
    }

    public KeyInfoHeaderBlock getKeyInfo() {
        return keyInfo;
    }

    public void setKeyInfo(KeyInfoHeaderBlock keyInfo) {
        this.keyInfo = keyInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /*
    public void setXMLSerializer(XMLSerializer xmlWriter){
        xmlSerializer = xmlWriter;
    }


    public void writeTo(OutputStream stream) throws IOException{

        stream.write(OPENTAG);
        stream.write(ENC_PREFIX);
        stream.write(':');
        stream.write(ENCRYPTED_DATA);
        stream.write(' ');
        stream.write(XMLNS);
        stream.write(':');
        stream.write(ENC_PREFIX);

        stream.write('=');
        stream.write('"');
        stream.write(ENC_NS);
        stream.write('"');
        stream.write(' ');
        if(getId() != null){
            stream.write(ID);
            stream.write('=');
            stream.write('"');
            stream.write(getId().getBytes());
            stream.write('"');
        }
        stream.write(' ');
        stream.write(TYPE);
        stream.write('=');
        stream.write('"');
        stream.write(CONTENT_ONLY);
        stream.write('"');
        stream.write(CLOSETAG);
        stream.write(OPENTAG);
        stream.write(ENC_PREFIX);
        stream.write(':');
        stream.write(ENCRYPTION_METHOD);
        stream.write(' ');
        stream.write(ALGORITHM);

        stream.write('=');
        stream.write('"');
        stream.write(getEncAlgo().getBytes());
        stream.write('"');
        stream.write(CLOSEELEMENT);
        stream.write(OPENTAG);
        stream.write(ENC_PREFIX);
        stream.write(':');

        stream.write(CIPHER_DATA);
        stream.write(CLOSETAG);
        try{
            if(keyInfo != null){
                xmlSerializer.setOutputByteStream(stream);
                xmlSerializer.serialize(keyInfo.getAsSoapElement());
                // xmlSerializer.reset();
            }
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
        stream.write(OPENTAG);
        stream.write(ENC_PREFIX);
        stream.write(':');
        stream.write(CIPHER_VALUE);
        stream.write(CLOSETAG);
        //Base64.encodeToStream(getEncryptedData(),Base64.BASE64DEFAULTLENGTH,stream);
        Base64.encodeToStream(new ByteArray(iv,encryptedData),Base64.BASE64DEFAULTLENGTH,stream);
        stream.write(ENDTAG);
        stream.write(ENC_PREFIX);
        stream.write(':');
        stream.write(CIPHER_VALUE);
        stream.write(CLOSETAG);
        stream.write(ENDTAG);
        stream.write(ENC_PREFIX);
        stream.write(':');
        stream.write(CIPHER_DATA);
        stream.write(CLOSETAG);
        stream.write(ENDTAG);
        stream.write(ENC_PREFIX);
        stream.write(':');
        stream.write(ENCRYPTED_DATA);
        stream.write(CLOSETAG);

    }*/

    public String getEncAlgo() {
        return encAlgo;
    }

    public void setEncAlgo(String encAlgo) {
        this.encAlgo = encAlgo;
    }



}
