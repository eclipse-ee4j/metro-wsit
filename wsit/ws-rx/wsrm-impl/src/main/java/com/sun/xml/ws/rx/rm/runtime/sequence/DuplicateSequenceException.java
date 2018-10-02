/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence;

import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;

/**
 * Inicates that the sequence with given sequence identifier already exists in a given environment.
 * 
 * This exceptions is used under the following conditions:
 *  <ul>
 *      <li>sequence with such {@code sequenceId} is already registered and managed by a given sequence manager</li>
 *  </ul>
 * 
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class DuplicateSequenceException extends RxRuntimeException {
    private static final long serialVersionUID = -4888405115401229826L;
    //
    private final String sequenceId;
    
    /**
     * Constructs an instance of <code>DuplicateSequenceException</code> for the sequence with {@code sequenceId} identifier.
     * @param sequenceId the identifier of the duplicate sequence.
     */
    public DuplicateSequenceException(String sequenceId) {
        super(DuplicateSequenceException.createErrorMessage(sequenceId));
        this.sequenceId = sequenceId;
    }

    /**
     * Returns the identifier of the unknown sequence
     * @return the unknown sequence identifier
     */
    public String getSequenceId() {
        return sequenceId;
    }        
    
    private static String createErrorMessage(String sequenceId) {
        return LocalizationMessages.WSRM_1126_DUPLICATE_SEQUENCE_ID(sequenceId);
    } 
}
