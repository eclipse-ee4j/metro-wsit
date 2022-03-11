/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.crypto;

import com.sun.xml.wss.XWSSecurityException;
import java.io.OutputStream;
import javax.xml.crypto.Data;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.NamespaceContextEx;
import jakarta.xml.bind.JAXBElement;

/**
 *
 * @author K.Venugopal@sun.com
 */
public interface JAXBData extends Data{

    NamespaceContextEx getNamespaceContext();
    void writeTo(XMLStreamWriter writer) throws XWSSecurityException;
    void writeTo(OutputStream os)throws XWSSecurityException;
    JAXBElement getJAXBElement();
}
