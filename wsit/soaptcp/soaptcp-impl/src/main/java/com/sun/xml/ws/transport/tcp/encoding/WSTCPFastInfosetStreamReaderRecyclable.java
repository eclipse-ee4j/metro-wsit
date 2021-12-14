/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.encoding;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import java.io.InputStream;

/**
 * @author Alexey Stashok
 */
public class WSTCPFastInfosetStreamReaderRecyclable extends StAXDocumentParser implements XMLStreamReaderFactory.RecycleAware {
    private RecycleAwareListener listener;
    
    public WSTCPFastInfosetStreamReaderRecyclable() {
    }
    
    public WSTCPFastInfosetStreamReaderRecyclable(InputStream in, RecycleAwareListener listener) {
        super(in);
        this.listener = listener;
    }
    
    @Override
    public void onRecycled() {
        listener.onRecycled();
    }

    public RecycleAwareListener getListener() {
        return listener;
    }

    public void setListener(RecycleAwareListener listener) {
        this.listener = listener;
    }
    
    public interface RecycleAwareListener {
        void onRecycled();
    }
}
