/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * Indentifies target elements in the SOAPMessage that need to be confidentiality protected.
 * <p>
 * <pre>{@code
 *  <xmp>
 *      <sp:EncryptedElements XPathVersion="xs:anyURI"? ... > 
 *          <sp:XPath>xs:string</sp:XPath>+ 
 *              ...
 *      </sp:EncryptedElements>
 *  </xmp>
 * }</pre>
 * 
 * @author K.Venugopal@sun.com
 */
public interface EncryptedElements extends Target {
    /**
     * Returns XPath Version in use.
     * @return xpath version 
     */
    String getXPathVersion();
  
    /**
     * targets that needs to be confidentiality
     * protected if present in the message.
     * @return {@link java.util.Iterator }
     */
    Iterator<String> getTargets();
}
