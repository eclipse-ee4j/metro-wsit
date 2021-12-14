/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.encoding.configurator;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.ws.transport.tcp.encoding.WSTCPFastInfosetStreamReaderRecyclable;

public class DefaultDocumentParserFactory implements DocumentParserFactory {

    @Override
    public StAXDocumentParser newInstance() {
        return new WSTCPFastInfosetStreamReaderRecyclable();
    }
}
