/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
 *  <B>Syntax:
 * <p>
 *  <pre>
 *   &lt;xmp&gt;
 *       &lt;sp:SignedParts ... &gt;
 *            &lt;sp:Body /&gt;?
 *            &lt;sp:Header Name="xs:NCName"? Namespace="xs:anyURI" ... /&gt;*
 *                  ...
 *       &lt;/sp:SignedParts&gt;
 *   &lt;/xmp&gt;
 * </pre>
 *
 * @author K.Venugopal@sun.com
 */


public interface SignedParts extends Target {
 
    /**
     *
     * @return true if the body is to be integrity protected.
     */
    public boolean hasBody();
    
    /**
     * 
     *  @return true if the attachments are to be integrity protected
     */
    public boolean hasAttachments();
    
    /**
     * 
     * @return the URI of transform to be applied on attachment, the default is
     *  Attachment-Complete-Transform
     */
    public String attachmentProtectionType();
    
    /**
     * {@link java.util.Iterator } over list of Headers that identify targets in the SOAP header
     * to be integrity protected.
     * @return {@link java.util.Iterator }
     */
    public Iterator  getHeaders();  
    
}
