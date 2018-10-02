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

import javax.xml.namespace.QName;

/**
 *
 * @author K.Venugopal@sun.com
 */
public interface Header {
    static final QName NAME = new QName("Name");
    static final QName URI = new QName("Namespace");
    /**
     *  return Local Name of the Header
     */
    public String getLocalName();
    /**
     * return the URI of the Header.
     */
    public String getURI();
}
