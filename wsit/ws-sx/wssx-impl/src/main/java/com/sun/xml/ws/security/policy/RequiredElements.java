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
 * Used to specify the header elements that the message MUST contain.
 * <p>
 * <pre>{@code
 *  <xmp>
 *      <sp:RequiredElements XPathVersion="xs:anyURI"? ... > 
 *          <sp:XPath>xs:string</sp:XPath>+ 
 *              ...
 *      </sp:RequiredElements>
 *  </xmp>
 * }</pre>
 * @author mayank.mishra@sun.com
 */
public interface RequiredElements extends Target {
    
    /**
     * Returns XPath Version in use.
     * @return xpath version 
     */
    String getXPathVersion();
    
     /**
     * targets header elements message must contains .
     * @return {@link java.util.Iterator }
     */
     Iterator getTargets();
    
}
