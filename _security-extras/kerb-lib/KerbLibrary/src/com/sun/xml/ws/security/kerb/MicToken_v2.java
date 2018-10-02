/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.kerb;

import org.ietf.jgss.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * This class represents the new format of GSS MIC tokens, as specified
 * in draft-ietf-krb-wg-gssapi-cfx-07.txt
 * 
 * MIC tokens = { 16-byte token-header |  HMAC }
 * where HMAC is on { plaintext | 16-byte token-header }
 *
 * @author Seema Malkani
 * @version 1.4, 07/27/06
 */

class MicToken_v2 extends MessageToken_v2 {

    public MicToken_v2(Krb5Context context,
                  byte[] tokenBytes, int tokenOffset, int tokenLen,
                  MessageProp prop)  throws GSSException {
	super(Krb5Token.MIC_ID_v2, context, 
          tokenBytes, tokenOffset, tokenLen, prop);
    }

    public MicToken_v2(Krb5Context context,
                   InputStream is, MessageProp prop)
    throws GSSException {
	super(Krb5Token.MIC_ID_v2, context, is, prop);
    }

    public void verify(byte[] data, int offset, int len) throws GSSException {
	if (!verifySign(data, offset, len))
	    throw new GSSException(GSSException.BAD_MIC, -1, 
                         "Corrupt checksum or sequence number in MIC token");
    }

    public void verify(InputStream data) throws GSSException {

	byte[] dataBytes = null;
	try {
	    dataBytes = new byte[data.available()];
	    data.read(dataBytes);
	} catch (IOException e) {
	    // Error reading application data
 	    throw new GSSException(GSSException.BAD_MIC, -1, 
		"Corrupt checksum or sequence number in MIC token");
	}
	verify(dataBytes, 0, dataBytes.length);
    }

    public MicToken_v2(Krb5Context context, MessageProp prop,
                  byte[] data, int pos, int len)
	throws GSSException {
	super(Krb5Token.MIC_ID_v2, context);

	//	debug("Application data to MicToken verify is [" +
	//	      getHexBytes(data, pos, len) + "]\n");
	if (prop == null) prop = new MessageProp(0, false);
	genSignAndSeqNumber(prop, data, pos, len);
    }

    public MicToken_v2(Krb5Context context, MessageProp prop, InputStream data)
	throws GSSException, IOException {

	super(Krb5Token.MIC_ID_v2, context);
	byte[] dataBytes = new byte[data.available()];
	data.read(dataBytes);

	// debug("Application data to MicToken cons is [" +
	//     getHexBytes(dataBytes) + "]\n");
	if (prop == null) prop = new MessageProp(0, false);
	genSignAndSeqNumber(prop, dataBytes, 0, dataBytes.length);
    }

    public int encode(byte[] outToken, int offset) 
	throws IOException, GSSException {

	// Token  is small
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	super.encode(bos);
	byte[] token = bos.toByteArray();
	System.arraycopy(token, 0, outToken, offset, token.length);
	return token.length;
    }

    public byte[] encode() throws IOException, GSSException {

	// XXX Fine tune this initial size
	ByteArrayOutputStream bos = new ByteArrayOutputStream(50);
	encode(bos);
	return bos.toByteArray();    
    }
}
