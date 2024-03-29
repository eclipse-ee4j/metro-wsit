/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence;

import com.sun.xml.ws.rx.rm.runtime.RmRuntimeVersion;
import com.sun.xml.ws.rx.rm.faults.AbstractSoapFaultException;
import com.sun.xml.ws.rx.rm.runtime.RuntimeContext;
import javax.xml.namespace.QName;
import jakarta.xml.soap.Detail;

/**
 *
 * The Endpoint that generates this fault SHOULD make every reasonable effort to
 * notify the corresponding Endpoint of this decision.
 *
 * Properties:
 * [Code] Sender or Receiver
 * [Subcode] wsrm:SequenceTerminated
 * [Reason] The Sequence has been terminated due to an unrecoverable error.
 * [Detail] {@code <wsrm:Identifier ...> xs:anyURI </wsrm:Identifier>}
 *
 * Generated by: RM Source or RM Destination.
 * Condition : Encountering an unrecoverable condition or detection of violation of the protocol.
 * Action Upon Generation : Sequence termination.
 * Action Upon Receipt : MUST terminate the Sequence if not otherwise terminated.
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class SequenceTerminatedException extends AbstractSoapFaultException {

    private static final long serialVersionUID = -4689338956255310299L;
    //
    private final Code code;
    private final String sequenceId;

    public SequenceTerminatedException(String sequenceId, String message, Code code) {
        super(message, "The Sequence has been terminated due to an unrecoverable error.", true);

        this.code = code;
        this.sequenceId = sequenceId;
    }

    @Override
    public Code getCode() {
        return code;
    }

    @Override
    public QName getSubcode(RmRuntimeVersion rv) {
        return rv.protocolVersion.sequenceTerminatedFaultCode;
    }

    @Override
    public Detail getDetail(RuntimeContext rc) {
        return new DetailBuilder(rc).addSequenceIdentifier(sequenceId).build();
    }
}
