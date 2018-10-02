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
 * Identifies targets that if present in the message should be confidentiality protected.
 *<p>
 *  <pre>
 *   &lt;xmp&gt;
 *      &lt;sp:EncryptedParts ... &gt;
 *          &lt;sp:Body/&gt;?
 *          &lt;sp:Header Name="xs:NCName"? Namespace="xs:anyURI" ... /&gt;*
 *              ...
 *      &lt;/sp:EncryptedParts&gt;
 *    &lt;/xmp&gt;
 *   </pre>
 * @author K.Venugopal@sun.com
 */
public interface EncryptedParts extends Target {
 
    /**
     *
     * @return true if the body is part of the target list.
     */
    public boolean hasBody();
    
    /**
     * 
     * @return true if attachments are part of the target list.
     */
    public boolean hasAttachments();
   
    /**
     * returns list of SOAP Headers that need to protected.
     * @return {@link java.util.Iterator} over the list of SOAP Headers
     */
    public Iterator getTargets();
    

    /**
     * removes SOAP Body from the list of targets to be confidentiality protected.
     */
    public void removeBody();
}
