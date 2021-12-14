/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.api;

/**
 * represents a SOAP/Security Header that is either signed or encrypted.
 * @author K.Venugopal@sun.com
 */

public interface SecuredHeader {
    /**
     * 
     * @param id true if the SecuredHeader refers to id.
     */
    boolean hasID(String id);
//    /**
//     * 
//     * @param id 
//     * @param outputstream 
//     * @throws com.sun.xml.wss.XWSSecurityException 
//     */
//    public void writeContentWithID(String id, OutputStream outputstream) throws XWSSecurityException ;
//    /**
//     * 
//     * @param id 
//     * @param writer 
//     * @throws com.sun.xml.wss.XWSSecurityException 
//     */
//    public void writeContentWithID(String id, XMLStreamWriter writer) throws XWSSecurityException ;
}
