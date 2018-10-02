/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.resolver;

import java.util.Vector;
import java.util.Iterator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.soap.SOAPException;
import javax.xml.soap.AttachmentPart;

import org.apache.xml.security.signature.XMLSignatureInput;

public class AttachmentSignatureInput extends XMLSignatureInput {
   private String _cType = null;
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

   @SuppressWarnings("unchecked")
   public static final Object[] _getSignatureInput(AttachmentPart _part) throws SOAPException, IOException {
       Iterator mhItr = _part.getAllMimeHeaders();

       Vector mhs = new Vector();
       while (mhItr.hasNext()) mhs.add(mhItr.next());        

       // extract Content
       //Object content = _part.getContent();
       OutputStream baos = new ByteArrayOutputStream();  
       _part.getDataHandler().writeTo(baos);          

       return new Object[] {mhs, ((ByteArrayOutputStream)baos).toByteArray()};
   }
}


