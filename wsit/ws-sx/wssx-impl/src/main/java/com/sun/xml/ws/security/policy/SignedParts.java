/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;

import java.util.Iterator;



/**
 * This interface identifies targets in the message that need to be integrity protected. The targets if present should be integrity protected.
 * <p>
 *  <B>Syntax:</B>
 * <p>
 *  <pre>{@code
 *   <xmp>
 *       <sp:SignedParts ... >
 *            <sp:Body />?
 *            <sp:Header Name="xs:NCName"? Namespace="xs:anyURI" ... />*
 *                  ...
 *       </sp:SignedParts>
 *   </xmp>
 * }</pre>
 *
 * @author K.Venugopal@sun.com
 */


public interface SignedParts extends Target {

    /**
     *
     * @return true if the body is to be integrity protected.
     */
    boolean hasBody();

    /**
     *
     *  @return true if the attachments are to be integrity protected
     */
    boolean hasAttachments();

    /**
     *
     * @return the URI of transform to be applied on attachment, the default is
     *  Attachment-Complete-Transform
     */
    String attachmentProtectionType();

    /**
     * {@link java.util.Iterator } over list of Headers that identify targets in the SOAP header
     * to be integrity protected.
     * @return {@link java.util.Iterator }
     */
    Iterator  getHeaders();

}
