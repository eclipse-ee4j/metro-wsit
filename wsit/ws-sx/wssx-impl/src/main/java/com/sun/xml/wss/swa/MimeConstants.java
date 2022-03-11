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
 * $Id: MimeConstants.java,v 1.2 2010-10-21 15:38:13 snajper Exp $
 */

package com.sun.xml.wss.swa;

public interface MimeConstants {
   // rfc822, rfc204(5|6|7) charset for mime headers
   String US_ASCII  = "US-ASCII";

   // rfc822 mime headers
   String CONTENT_TRANSFER_ENCODING
                                                     = "Content-Transfer-Encoding";
   String CONTENT_DESCRIPTION    = "Content-Description";
   String CONTENT_DISPOSITION    = "Content-Disposition";
   String CONTENT_ID             = "Content-ID";
   String CONTENT_LOCATION       = "Content-Location";
   String CONTENT_TYPE           = "Content-Type";
   String CONTENT_LENGTH         = "Content-Length";

   // rfc2045/6 canonicalized media type names
   String TEXT_TYPE         = "text";
   String IMAGE_TYPE        = "image";
   String APPLICATION_TYPE  = "application";

   // rfc2045/6 canonicalized media sub-type names
   String XML_TYPE          = "xml";
   String PLAIN_TYPE        = "plain";
   String JPEG_TYPE         = "jpeg";
   String GIF_TYPE          = "gif";
   String OCTET_STREAM_TYPE = "octet-stream";

   // rfc2045/6 canonicalized parameters for text/image/application types
   String CHARSET           = "charset";

   // rfc2045/6 canonicalized parameter names for application type
      // for octet-stream subtype
   String TYPE           = "type";
      String PADDING        = "padding";
      String CONVERSIONS    = "conversions";
      String INTERPRETER    = "interpreter";

   //  rfc2183 canonicalized disposition type values for Content-Disposition
   String INLINE            = "inline";
   String ATTACHMENT        = "attachment";

   //  rfc2183 canonicalized parameter names for Content-Disposition
   String FILENAME          = "filename";
   String CREATION_DATE     = "creation-date";
   String MODIFICATION_DATE = "modification-date";
   String READ_DATE         = "read-date";
   String SIZE              = "size";

   // rfc2045/6 content-types
   String TEXT_XML_TYPE     = TEXT_TYPE + "/" + XML_TYPE;
   String TEXT_PLAIN_TYPE   = TEXT_TYPE + "/" + PLAIN_TYPE;
   String IMAGE_JPEG_TYPE   = IMAGE_TYPE + "/" + JPEG_TYPE;
   String IMAGE_GIF_TYPE    = IMAGE_TYPE + "/" + GIF_TYPE;
   String APPLICATION_OCTET_STREAM_TYPE
                                   = APPLICATION_TYPE + "/" + OCTET_STREAM_TYPE;

}
