/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm;

import com.sun.xml.ws.rx.RxException;

/**
 * Subclass of <code>RMException</code> thrown from errors resulting
 *  when a response to close sequence request cannot be satisfied
 * @author Bhakti Mehta
 *
 */
public class CloseSequenceException extends RxException {
    private static final long serialVersionUID = 6938882497563905280L;

    private String sequenceId;

    public CloseSequenceException(String message) {
        super(message);
    }

    public CloseSequenceException(String message, String id) {
        super(message);
        this.sequenceId = id;
    }

    public String getSequenceId() {
        return sequenceId;
    }
}
