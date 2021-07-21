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
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.runtime.RuntimeContext;
import javax.xml.namespace.QName;
import jakarta.xml.soap.Detail;

/**
 * If the condition listed below is reached, the RM Destination MUST generate this fault.
 *
 * Properties:
 * [Code] Sender
 * [Subcode] wsrm:MessageNumberRollover
 * [Reason] The maximum value for wsrm:MessageNumber has been exceeded.
 * [Detail] {@code <wsrm:Identifier ...> xs:anyURI </wsrm:Identifier>, <wsrm:MaxMessageNumber> wsrm:MessageNumberType </wsrm:MaxMessageNumber>}
 *
 * Generated by: RM Source or RM Destination.
 * Condition : Message number in /wsrm:Sequence/wsrm:MessageNumber of a Received message exceeds the internal limitations of an RM Destination or reaches the maximum value of 9,223,372,036,854,775,807.
 * Action Upon Generation : RM Destination SHOULD continue to accept undelivered messages until the Sequence is closed or terminated.
 * Action Upon Receipt : RM Source SHOULD continue to retransmit undelivered messages until the Sequence is closed or terminated.
 *
 * @author m_potociar
 */
public final class MessageNumberRolloverException extends AbstractSoapFaultException {
    private static final long serialVersionUID = 7692916640741305184L;
    //
    private long messageNumber;
    private String sequenceId;

    public long getMessageNumber() {
        return messageNumber;
    }

    public String getSequenceId() {
        return sequenceId;
    }    
    
    public MessageNumberRolloverException(String sequenceId, long messageNumber) {
        super(
                LocalizationMessages.WSRM_1138_MESSAGE_NUMBER_ROLLOVER(sequenceId, messageNumber),
                "The maximum value for wsrm:MessageNumber has been exceeded.",
                true);
        
        this.messageNumber = messageNumber;
        this.sequenceId = sequenceId;
    }

    @Override
    public Code getCode() {
        return Code.Sender;
    }

    @Override
    public QName getSubcode(RmRuntimeVersion rv) {
        return rv.protocolVersion.messageNumberRolloverFaultCode;
    }

    @Override
    public Detail getDetail(RuntimeContext rc) {
        return new DetailBuilder(rc).addSequenceIdentifier(sequenceId).addMaxMessageNumber(messageNumber).build();
    }
}
