/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.resolver;

import jakarta.xml.soap.AttachmentPart;
import jakarta.xml.soap.SOAPException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.xml.security.signature.XMLSignatureByteInput;

public class AttachmentSignatureInput extends XMLSignatureByteInput {
   private String _cType;
   private Vector _mhs = new Vector();

   public AttachmentSignatureInput(byte[] input) {
       super(input);
   }

   public void setMimeHeaders(Vector mimeHeaders) {
       _mhs = mimeHeaders;
   }

   public Vector getMimeHeaders() {
      return _mhs;
   }

   public void setContentType(String _cType) {
       this._cType = _cType;
   }

   public String getContentType() {
       return _cType;
   }

   public static final Object[] _getSignatureInput(AttachmentPart _part) throws SOAPException, IOException {
       Iterator mhItr = _part.getAllMimeHeaders();

       Vector mhs = new Vector();
       while (mhItr.hasNext()) {
           mhs.add(mhItr.next());
       }

       // extract Content
       //Object content = _part.getContent();
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       _part.getDataHandler().writeTo(baos);

       return new Object[] {mhs, baos.toByteArray()};
   }
}


