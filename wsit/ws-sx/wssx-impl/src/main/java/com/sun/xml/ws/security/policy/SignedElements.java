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
 * Identifies list of elements in the SOAP Message that need to be integrity protected.
 *  <p>
 *      <B>Syntax:
 *          <pre>
 *            &lt;xmp&gt;
 *              &lt;sp:SignedElements XPathVersion="xs:anyURI"? ... &gt;
 *                      &lt;sp:XPath&gt;xs:string&lt;/sp:XPath&gt;+
 *                              ...
 *              &lt;/sp:SignedElements&gt;
 *             &lt;/xmp&gt;
 *           </pre>
 * @author K.Venugopal@sun.com
 */
public interface SignedElements extends Target{
   
    /**
     * returns a {@link java.util.Iterator} over list target elements.
     * @return {@link java.util.Iterator} over list of XPath expressions
     */
    public Iterator<String> getTargets();
}
