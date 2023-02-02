/*
 * Copyright (c) 2010, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: AttachmentContentOnlyTransform.java,v 1.2 2010-10-21 15:37:44 snajper Exp $
 */

package com.sun.xml.wss.impl.transform;

import javax.mail.internet.ContentType;

import com.sun.xml.wss.impl.c14n.Canonicalizer;
import com.sun.xml.wss.impl.c14n.CanonicalizerFactory;

import com.sun.xml.wss.impl.resolver.AttachmentSignatureInput;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformationException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.OutputStream;

public class AttachmentContentOnlyTransform extends TransformSpi {

   private static final String implementedTransformURI =
          "http://docs.oasis-open.org/wss/2004/XX/" +
          "oasis-2004XX-wss-swa-profile-1.0#Attachment-Content-Only-Transform";

   protected String engineGetURI() {
       return implementedTransformURI;
   }

   @Override
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream os,
                                                      Element transformElement, String baseURI,
                                                      boolean secureValidation)
           throws IOException, CanonicalizationException, InvalidCanonicalizerException,
           TransformationException, ParserConfigurationException, SAXException {
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

       Canonicalizer canonicalizer =
                             CanonicalizerFactory.
                                   getCanonicalizer(((AttachmentSignatureInput)input).getContentType());

       return canonicalizer.canonicalize(inputContentBytes);
   }

   public boolean wantsOctetStream ()   { return true; }
   public boolean wantsNodeSet ()       { return true; }
   public boolean returnsOctetStream () { return true; }
   public boolean returnsNodeSet ()     { return false; }
}

