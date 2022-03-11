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
 * Specifies list of SOAP Headers that must be present in the SOAP Messages.
 * @author K.Venugopal@sun.com
 */
public interface RequiredTargets extends Target{


    /**
     * {@link java.util.Iterator } over the list of required targetlist.
     * @return {@link java.util.Iterator }
     */
    Iterator getTargets();


    /**
     * returns the XPath Version that is being used.
     * @return XPath Version
     */
    String getXPathExpression();
}
