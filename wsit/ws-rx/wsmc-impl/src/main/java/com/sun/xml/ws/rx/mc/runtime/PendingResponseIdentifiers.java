/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.runtime;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Stores pending message identifiers for a single client
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
class PendingResponseIdentifiers implements Serializable {
    private final Queue<String> messageIdentifiers;

    public PendingResponseIdentifiers() {
        messageIdentifiers = new LinkedList<String>();
    }

    public boolean isEmpty() {
        return messageIdentifiers.isEmpty();
    }

    public String poll() {
        return messageIdentifiers.poll();
    }

    public boolean offer(String messageId) {
        return messageIdentifiers.offer(messageId);
    }

    @Override
    public String toString() {
        return "PendingResponseIdentifiers{" + "messageIdentifiers=" + messageIdentifiers + '}';
    }   
}
