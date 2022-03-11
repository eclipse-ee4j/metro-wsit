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
 * $Id: AttachmentCompleteTransform.java,v 1.2 2010-10-21 15:37:44 snajper Exp $
 */

package com.sun.xml.wss.impl.transform;

import com.sun.xml.wss.impl.c14n.Canonicalizer;
import com.sun.xml.wss.impl.c14n.CanonicalizerFactory;
import com.sun.xml.wss.impl.c14n.MimeHeaderCanonicalizer;

import com.sun.xml.wss.impl.resolver.AttachmentSignatureInput;

import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformationException;

public class AttachmentCompleteTransform extends TransformSpi {

   private static final String implementedTransformURI =
          "http://docs.oasis-open.org/wss/2004/XX/" +
          "oasis-2004XX-wss-swa-profile-1.0#Attachment-Complete-Transform";

   @Override
   protected String engineGetURI() {
       return implementedTransformURI;
   }

   @Override
   protected XMLSignatureInput enginePerformTransform(
             XMLSignatureInput input)
             throws TransformationException {
       try {
            return new XMLSignatureInput(_canonicalize(input));
       } catch (Exception e) {
            // log
            throw new TransformationException(e.getMessage(), e);
       }
   }

   private byte[] _canonicalize(XMLSignatureInput input) throws Exception {
       byte[] inputContentBytes = input.getBytes();
       //ContentType contentType = new ContentType(((AttachmentSignatureInput)input).getContentType());

       // rf. RFC822
       MimeHeaderCanonicalizer mHCanonicalizer = CanonicalizerFactory.getMimeHeaderCanonicalizer("US-ASCII");
       byte[] outputHeaderBytes = mHCanonicalizer._canonicalize( ((AttachmentSignatureInput)input).getMimeHeaders().iterator());

       Canonicalizer canonicalizer =
                             CanonicalizerFactory.
                                   getCanonicalizer(((AttachmentSignatureInput)input).getContentType());
       byte[] outputContentBytes = canonicalizer.canonicalize(inputContentBytes);

       byte[] outputBytes = new byte[outputHeaderBytes.length+outputContentBytes.length];
       System.arraycopy(outputHeaderBytes, 0, outputBytes, 0, outputHeaderBytes.length);
       System.arraycopy(outputContentBytes, 0, outputBytes,
                                   outputHeaderBytes.length, outputContentBytes.length);

       return outputBytes;
   }

   public boolean wantsOctetStream ()   { return true; }
   public boolean wantsNodeSet ()       { return true; }
   public boolean returnsOctetStream () { return true; }
   public boolean returnsNodeSet ()     { return false; }
}
